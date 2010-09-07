package org.objectledge.forms.internal;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.SAXException;


/**
 * Builds form-tool Form objects based on SAX events.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormBuilder.java,v 1.3 2005-03-23 07:52:16 zwierzem Exp $
 */
public class FormBuilder
extends org.objectledge.forms.internal.util.AbstractBuilder
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
        buildForm = (FormImpl)builtObject;
    }
    
    public void endBuild(Object builtObject)
    throws ConstructionException
    {
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
                buildForm.addBindElement(new org.objectledge.forms.internal.model.Bind(atts));
            }
            catch(ConstructionException e)
            {
                throw new SAXException("Error adding bind element ("+locator.getSystemId()+" "+locator.getLineNumber()+":"+locator.getColumnNumber()+")", e);
            }
        }
        else if("submitInfo".equals(elementName))
        {
            buildForm.submitInfo = new org.objectledge.forms.internal.model.SubmitInfo(atts);
        }
    }
}
