package pl.caltha.forms.internal;

import org.xml.sax.SAXException;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.util.Util;

/**
 * Builds form-tool Form objects based on SAX events.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormBuilder.java,v 1.2 2005-02-10 17:49:43 rafal Exp $
 */
public class FormBuilder
extends pl.caltha.forms.internal.util.AbstractBuilder
{
    /** Form object that is being built. */
    private FormImpl buildForm;

    public FormBuilder(String acceptedNamespace, String schemaURI)
    {
        super(acceptedNamespace, schemaURI);
    }

    public void startBuild(Object builtObject)
    throws ConstructionException
    {
        //Log.info(MSG_PREFIX+"Start building Form.");
        buildForm = (FormImpl)builtObject;
    }
    
    public void endBuild(Object builtObject)
    throws ConstructionException
    {
        //Log.info(MSG_PREFIX+"End building Form.");
    }

    protected void startElement(String elementName, org.xml.sax.Attributes atts) throws SAXException
    {
        // WARN: Expanding relative definition URIs
        String uri = Util.expandURI(definitionURI, Util.getSAXAttributeVal(atts, "href"));

        if("model".equals(elementName))
        {
            buildForm.instanceSchemaURI = uri;
        }
        else if("instance".equals(elementName))
        {
            buildForm.defaultInstanceURI = uri;
        }
        else if("interface".equals(elementName))
        {
            buildForm.uiURI = uri;
        }
        else if("bind".equals(elementName))
        {
            try
            {
                buildForm.addBindElement(new pl.caltha.forms.internal.model.Bind(atts));
            }
            catch(ConstructionException e)
            {
                throw new SAXException("Error adding bind element ("+locator.getSystemId()+" "+locator.getLineNumber()+":"+locator.getColumnNumber()+")", e);
            }
        }
        else if("submitInfo".equals(elementName))
        {
            buildForm.submitInfo = new pl.caltha.forms.internal.model.SubmitInfo(atts);
        }
    }
}
