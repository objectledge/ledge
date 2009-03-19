package pl.caltha.forms.internal;

import java.util.HashMap;

import org.objectledge.html.HTMLService;
import org.objectledge.parameters.Parameters;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.Form;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.internal.model.Bind;
import pl.caltha.forms.internal.model.DefaultInstance;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.model.SubmitInfo;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.ui.UIConstants;
import pl.caltha.services.xml.XMLService;

/**
 * Represents Form definition.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormImpl.java,v 1.4 2008-07-24 17:06:56 rafal Exp $
 */
public class FormImpl implements Form
{
    /** FormsService. */
    private FormsService formToolService;

    /** XMLService is used to get validators. */
    private XMLService xmlService;

    /** Used for HTML manipulation in WYSIWIG text edit control. */
    private HTMLService htmlService;
    
    /** I18nService is used to localise strings. */
    //private I18nService i18nService;

    /** Keeps ID for this form definition.. */
    private String id;

    /** Keeps URI for form definition.. */
    private String definitionURI;

    /** Keeps model/bind elements. */
    private HashMap bindElements = new HashMap();

    /** Keeps Form's SubmitInfo. */
    protected SubmitInfo submitInfo;

    /** Keeps URI for UI definition.. */
    protected String uiURI;
    /** Keeps UI object. */
    private UI ui;

    /** Keeps Instance schema URI. */
    protected String instanceSchemaURI;
    /** Keeps Instance URI. */
    protected String defaultInstanceURI;
    /** Keeps an object representing default Instance. */
    private DefaultInstance defaultInstance;
    /** WYSIWIG editor name */
    private String editorName = "kupu";
    
    //private Parameters parameters;

    /** Creates new Form */
    public FormImpl(FormsService formToolService, XMLService xmlService, HTMLService htmlService,
        String definitionURI, String id)
    {
        this.definitionURI = definitionURI;
        this.id = id;
        this.formToolService = formToolService;
        this.xmlService = xmlService;
        this.htmlService = htmlService;
    }

    //------------------------------------------------------------------------
    // pl.caltha.forms.Form mehods

    /** Processes a user request merging it's data with an Instance object.
     * <p>Processing consists of following steps:</p>
     * <ol>
     *  <li><b>Value extraction (from the request) and application on an Instance.</b>
     *      <p>This step is first because form-tool is a server-side tool.
     *      All values must be applied before Instance is validated, or
     *      any actions, including setting values dynamically are executed
     *      (If for instance setValue action would be executed before
     *       applying values, the result of this action could be lost).
     *      </p>
     *      <p>This action triggers bind property cache clean up
     *         ({@link pl.caltha.forms.internal.model.InstanceImpl#preprocessInit()})
     *      </p>
     *      <p>This action also triggers submit requested flag clean up
     *         ({@link pl.caltha.forms.internal.model.InstanceImpl#preprocessInit()})
     *      </p>
     *      <p>This action also triggers error collector clean up
     *         ({@link pl.caltha.forms.internal.model.InstanceImpl#preprocessInit()})
     *      </p>
     *  </li>
     *  <li><b>Event extraction (from request) and dispatching.</b>
     *      <p>Event dispatching triggers actions connected to controls.
     *      These actions may also trigger another events and so on.</p>
     *      <p>TODO: Implement event - action loop protection.</p>
     *
     *      <p>Right now only two kinds of events are extracted:</p>
     *      <ol>
     *          <li><code>activate</code> - control activation,
     *          in this case button control activation.</li>
     *          <li><code>submit</code> - submit button control activation.</li>
     *      </ol>
     *  </li>
     *  <li><b>Instance validation</b>
     *      <p>In client-side XForms implementation this step should be
     *      triggered by a changed value. In server-side processing value
     *      comparison would have to be implemented to check if any of them
     *      was changed. It is not very wise to do value comparison because
     *      there will be cases in which it will be faster to do validation
     *      than to compare all values in the Instance. That's why validation
     *      is always executed.
     *      </p>
     *  </li>
     * </ol>
     */
    public void process(pl.caltha.forms.Instance inst, Parameters parameters)
    throws Exception
    {
        InstanceImpl instance = (InstanceImpl)inst;
        instance.setEditorName(this.getEditorName());
        // 1. Apply new values
        // Apply new values dispatches:
        //  - errorCollector init
        //  - bind expression cache should be cleared (implemented below)
        //  - selectValues should be cleared (implemented in NodeSelect)
        //  - submit requested clearing
        // 1.1.1. errorCollector
        // 1.1.2 Clear bind property cache.
        // 1.1.2 Clear submit requested.
        instance.preprocessInit();

        // 1.2. Set new values only if there are values sent from user's browser
        // 2. dispatch events & execute actions
        //
        // SetValues and dispatchEvents is not being called
        // for the first time instance processing.
        // This avoids Select fields clearing.
        String instanceId = parameters.get(UIConstants.INSTANCE_ID_NAME,null);
        if(instanceId != null && instanceId.equals(instance.getId()))
        {
            ui.setValues(instance, parameters);
            ui.dispatchEvents(instance, parameters);
        }

        // 3. validate
        // 3.1. Schema validate
        instance.validate( xmlService.getDOM4JValidator() );
        // 3.2. Check if required values are inputed
            // Required fields checking is only for Programmers using Instance
            // objects!!
            // Together with submit requested it gives information on form
            // Instance being ready or not.
        boolean hasRequired = ui.hasRequired(instance);
        instance.setHasRequired(hasRequired);
    }

    /** Returns ID for this form's definition file. This is a form definition URI
     * ({@link Form#getDefinitionURI}) changed to be compatible with XHTML IDs. */
    public String getId()
    {
        return id;
    }

    public String getDefinitionURI()
    {
        return definitionURI;
    }

    public String getUIDefinitionURI()
    {
        return uiURI;
    }

    public String getDefaultInstanceURI()
    {
        return defaultInstanceURI;
    }

    public String getInstanceSchemaURI()
    {
        return instanceSchemaURI;
    }

    //------------------------------------------------------------------------
    // Methods used during processing - runtime

    /** Returns a default instance object. */
    public DefaultInstance getDefaultInstance()
    {
        return defaultInstance;
    }

    /** Creates a new instance object. */
    InstanceImpl createInstance(String instanceId)
    {
        return defaultInstance.createInstance(instanceId);
    }

    /** Creates a new instance object from a saved instance data. */
    InstanceImpl createInstance(String instanceId, byte[] savedState)
    throws Exception
    {
        return defaultInstance.createInstance(instanceId, savedState);
    }

    // methods used in UI
    public Bind getBind(String id)
    throws ConstructionException
    {
        if(bindElements.containsKey(id))
        {
            return (Bind)(bindElements.get(id));
        }
        throw new ConstructionException("Cannot find bind element with id='"+id+"'");
    }

    public SubmitInfo getSubmitInfo()
    {
        return submitInfo;
    }

    public UI getUI()
    {
        return ui;
    }

    public FormsService getFormToolService()
    {
        return formToolService;
    }
    
    public HTMLService getHtmlService()
    {
        return htmlService;
    }
    
    public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

/*    public I18nService getI18nService()
    {
        return i18nService;
    }
*/
    //------------------------------------------------------------------------
    // Form building methods
    void setDefaultInstance(DefaultInstance defaultInstance)
    {
        this.defaultInstance = defaultInstance;
    }

    void addBindElement(Bind bind)
    throws ConstructionException
    {
        // guard from bind elements with no id
        String id = bind.getId();
        if(id == null || id.equals(""))
        {
            throw new ConstructionException("Cannot add Bind element with no id");
        }

        if(!bindElements.containsKey(id))
        {
            bindElements.put(id, bind);
        }
        // duplicate ids
        else
        {
            throw new ConstructionException("Duplicate bind element id='"+id+"'");
        }
    }

    void init(UI ui)
    throws ConstructionException
    {
        this.ui = ui;
    }
}

