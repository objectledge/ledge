package pl.caltha.forms.internal;

import java.util.HashMap;
import java.util.Properties;

import org.dom4j.Document;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.web.HttpContext;
import org.xml.sax.InputSource;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.Form;
import pl.caltha.forms.FormsException;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.ui.UIBuilder;
import pl.caltha.services.xml.LoggingErrorHandler;
import pl.caltha.services.xml.XMLDataReader;
import pl.caltha.services.xml.XMLService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormsServiceImpl.java,v 1.4 2005-02-08 20:33:27 rafal Exp $
 */
public class FormsServiceImpl 
implements FormsService
{
    public static final String LOGGING_FACILITY = "FormsService";

    private Logger log;

    private XMLService xmlService;
    
    /** Form definition objects keyed by their Id's. */
    private HashMap formsById = new HashMap();

    /** Form definition objects keyed by their application given names. */
    private HashMap formsByName = new HashMap();


    /** String containing an URI to form definition schema. */
    private String formSchemaURI;
    /** String containing an URI to ui deifinition schema. */
    private String uiSchemaURI;

    /** TODO: NOT IMPLEMENTED YET!
     * If this flag is set to <code>true</code>, form definition's
     * are reloaded if they are changed on disk.
     * This feature is for development only. Rebuilding form definitions
     * is a CPU intensive operation.
     */
     private boolean reloadFormDefinitions = false;

    //------------------------------------------------------------------------
    // net.labeo.services.Service methods

    /** Called when the broker is starting.
     */
    public FormsServiceImpl(Configuration config, Logger logger, XMLService xmlService
        )
    {
        this.log = logger;
        this.xmlService = xmlService;
        reloadFormDefinitions = config.getChild("form.definition.reload").getValueAsBoolean(false);
        formSchemaURI = config.getChild("uri.schema.form").getValue("classpath:pl/caltha/forms/internal/formtool-form.xsd");
        uiSchemaURI   = config.getChild("uri.schema.ui").getValue("classpath:pl/caltha/forms/internal/formtool-ui.xsd");
        //formSchemaURI = config.getChild("uri.schema.form").getValue("pl/caltha/forms/internal/formtool-form.xsd");
        //uiSchemaURI   = config.getChild("uri.schema.ui").getValue("pl/caltha/forms/internal/formtool-ui.xsd");
        
        log.info("Preloading schemas for 'formtool' service");
        preloadSchema(formSchemaURI);
        preloadSchema(uiSchemaURI);
    }

    private void preloadSchema(String uri)
    {
        log.info("Preloading schema '"+uri+"'");
        try
        {
            xmlService.loadGrammar(uri);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("Cannot load schema with URI '"+uri+"'");
        }
    }

    //------------------------------------------------------------------------
    // pl.caltha.forms.FormsService methods

    private void checkInputValue(String name, String value)
    throws FormsException
    {
        if(value == null || value.length() == 0)
        {
            throw new FormsException(name+" cannot be null or empty");
        }
    }

    /** Returns a Form definition object based on it's definition URI.
     * It also builds and caches such an object. */
    public Form getForm(String formDefinitionURI, String formName)
    throws ConstructionException, FormsException
    {
        // guard from null form definition URIs
        checkInputValue("Form definition URI", formDefinitionURI);

        // guard from null formNames
        checkInputValue("Form name", formName);

        //TODO: if debug set - check for timestamp on form definitionURI - is it possible with Labeo

        Form form = null;

        // get form definition from map
        if(formsByName.containsKey(formName))
        {
            form = (Form)(formsByName.get(formName));

            // check for duplicate formName -> form mapping
            String secondFormDefURI =  form.getDefinitionURI();
            if(secondFormDefURI.equals(formDefinitionURI))
            {
                // store a new name mapping for this form
                formsByName.put(formName, form);
            }
            else
            {
                throw new FormsException("Duplicate name '"+formName
                                +"' for different form definitions: "
                                +formDefinitionURI+" and "+secondFormDefURI);
            }
        }
        // or build it and cache it
        else
        {
            // synchronize to prevent duplicate form definition creation
            synchronized(formsById)
            {
                // WARN: This is the place in which we create formId's.
                // FormId's need to be compliant with XML ID strings
                String formId = formDefinitionURI.replace('/',':');

                // create form
                form = buildForm(formDefinitionURI, formId);

                // store it
                formsById.put(formId, form);
                formsByName.put(formName, form);

                log.info("Added new form definition '"+formDefinitionURI+"' with name '"+formName+"'");
            }
        }

        return form;
    }

    /** Builds a form definition object. */
    private FormImpl buildForm(String formDefinitionURI, String formId)
    throws ConstructionException
    {
        LoggingErrorHandler errorHandler = new LoggingErrorHandler(log);


        // Build Form
        XMLDataReader reader = getXMLDataReader();
        org.xml.sax.InputSource is = getInputSource(formDefinitionURI);
        FormBuilder formBuilder = new FormBuilder(FormsService.ACCEPTED_NS_FORM, formSchemaURI);
        FormImpl form =  new FormImpl(this, xmlService, formDefinitionURI, formId);
        formBuilder.build(form, reader, is, errorHandler);

        // Build DefaultInstance
        // URI is expanded in FormBuilder
        reader = getXMLDataReader();
        is = getInputSource(form.getDefaultInstanceURI());
        //reset errorHandler
        errorHandler.init();
        Document doc = null;
        try
        {
            doc = reader.readDOM4J(is, form.getInstanceSchemaURI(), errorHandler);
            doc.normalize();
        }
        catch(Exception e)
        {
            throw new ConstructionException("Cannot load DefaultInstance document '"+form.getDefaultInstanceURI()+"' from Form definition '"+formDefinitionURI+"'", e);
        }

        if(errorHandler.hadErrors())
        {
            throw new ConstructionException("DefaultInstance document '"+form.getDefaultInstanceURI()+"' had errors");
        }

        form.setDefaultInstance(new pl.caltha.forms.internal.model.DefaultInstance(form, form.getInstanceSchemaURI(), doc));

        // Build UI
        reader = getXMLDataReader();
        String uiURI = form.getUIDefinitionURI();
        // URI is expanded in FormBuilder
        is = getInputSource(uiURI);
        //reset errorHandler
        errorHandler.init();
        UIBuilder uiBuilder = new UIBuilder(FormsService.ACCEPTED_NS_UI, uiSchemaURI);
        UI ui = new UI(form, uiURI);
        uiBuilder.build(ui, reader, is, errorHandler);

        form.init(ui);

        return form;
    }

    /** Get an XMLDataReader for use while building a form definition. */
    private XMLDataReader getXMLDataReader()
    throws ConstructionException
    {
        try
        {
            return xmlService.getXMLDataReader();
        }
        catch(Exception e)
        {
            throw new ConstructionException("Cannot get XMLDataReader", e);
        }
    }

    /** Creates an InputSource from a given URI. */
    private InputSource getInputSource(String definitionURI)
    throws ConstructionException
    {
        try
        {
            return xmlService.getInputSource(definitionURI);
        }
        catch(Exception e)
        {
            throw new ConstructionException("Cannot get InputSource for URI '"+definitionURI+"'", e);
        }
    }

   //-------------------------------------------------------------------------
   // Instance access methods

    /** Returns an {@link pl.caltha.forms.Instance} object
     * depending on RunData parameters. If this object cannot be found it
     * creates one depending on a given {@link pl.caltha.forms.Form}
     * object.
     * @param formName              Form's system wide identifier, this one is used to allow
     *      same form definitions to be used in different site contexts.
     * @param httpContext HttpConext for current request.
     * @throws FormsException    thrown when a found Instance is not an instance
     *      for a given Form definition.
     * @return found or newly created Instance object
     */
    public Instance getInstance(String formName, HttpContext httpContext)
    throws FormsException
    {
        // guard from null formNames
        checkInputValue("Form name", formName);

        if(!formsByName.containsKey(formName))
        {
            throw new FormsException("Form object with name '"+formName+"' cannot be found");
        }

        FormImpl form = (FormImpl)(formsByName.get(formName));

        FormData formData = getFormData(httpContext);
        InstanceImpl instance = (InstanceImpl)(formData.get(formName));

        if(instance == null)
        {
            // create new Instance
            instance = form.createInstance(formName);
            // store it in FormData
            formData.put(instance);
        }

        // check if retrieved instance is connected to a proper Form object
        FormImpl instanceForm = instance.getForm();
        if(instanceForm != form)
        {
            throw new FormsException("Instance retrived for form definition named '"+formName
                +"' is not an instance for form definition '"+form.getDefinitionURI()+"'");
        }

        return (Instance)instance;
    }

    /** Returns an Instance object creating it from a given saved state.
     * @param formName Form's system wide identifier, this one is used to allow
     * same form definitions to be used in different site contexts.
     * @param httpContext HttpConext for current request.
     * @param savedState Serialized Instance data.
     * @throws Exception thrown on problems with deserialization.
     * @return Deserialized Instance object.
     */
    public Instance getInstance(String formName, HttpContext httpContext, byte[] savedState)
        throws Exception
    {
        if(!formsByName.containsKey(formName))
        {
            throw new FormsException("Form object with name '"+formName+"' cannot be found");
        }

        FormImpl form = (FormImpl)(formsByName.get(formName));
        FormData formData = getFormData(httpContext);

        // create new Instance
        InstanceImpl instance = ((FormImpl)form).createInstance(formName, savedState);
        // store it in FormData
        formData.put(instance);

        return (Instance)instance;
    }

    /** Removes an instance from users session - it should be used after instance
     * processing is finished.
     * Otherwise heavy instance data will be kept during whole user session. */
    public void removeInstance(HttpContext httpContext, Instance instance)
    {
        FormData formData = getFormData(httpContext);
        formData.remove(instance);
    }

    /** Key for FormData session object. */
    public static final String FORMDATA_NAME = "formtool.formdata";

    private FormData getFormData(HttpContext httpContext)
    {
        FormData formData = (FormData)(httpContext.getSessionAttribute(FORMDATA_NAME));
        if(formData == null)
        {
            formData = new FormData();
            httpContext.setSessionAttribute(FORMDATA_NAME, formData);
        }
        return formData;
    }


    //------------------------------------------------------------------------
    // Other methods

    public Logger getLogFacility()
    {
        return log;
    }

    public Properties getTidyConfiguration()
    {
        //TODO implement it!
        return new Properties();
        //throw new UnsupportedOperationException("not implemented yet!");
    }
    
    /** FormData is a container for storing form Instances in users session.
     *
     * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
     * @version $Id: FormsServiceImpl.java,v 1.4 2005-02-08 20:33:27 rafal Exp $
     */
    public class FormData
    {
        private HashMap instancesById = new HashMap();

        /** Puts an instance inside this FormData.
         * @param instance Instance to be stored.
         */
        public void put(Instance instance)
        {
            instancesById.put(instance.getId(), instance);
        }

        /** Gets an instance from this FormData.
         * @param id Id of an instance to be retrieved.
         * @return Instance found in this FormData.
         */
        public Instance get(String id)
        {
            return (Instance)(instancesById.get(id));
        }

        /** Removes an instance from this FormData. */
        public void remove(Instance instance)
        {
            instancesById.remove(instance.getId());
        }
    }
}
