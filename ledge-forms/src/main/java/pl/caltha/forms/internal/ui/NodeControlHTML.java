package pl.caltha.forms.internal.ui;

import java.io.StringWriter;

import org.dom4j.Document;
import org.objectledge.encodings.HTMLEntityEncoder;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;
import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/**
 * HTML input control. Includes:
 * <ul>
 *      <li><code>htmlarea</code></li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControlHTML.java,v 1.2 2005-01-20 16:44:52 pablo Exp $
 */
public class NodeControlHTML extends NodeControl
{
    private HTMLService htmlService;
    
    private String cleanupProfile;
    
    public NodeControlHTML(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // Optional, may be null. Null value is accepted by HTMLService.
        cleanupProfile = Util.createAttribute(atts, "cleanupProfile", null);
    }

    //------------------------------------------------------------------------
    // Control methods
    //
    /** Key for Tidy error log state value. */
    private String TIDY_ERROR_LOG = "htmlarea.tidyErrorLog";

    void setValue(InstanceImpl instance, String value)
    {
        // clean last error log
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);
        instance.setStateValue(contextNode, TIDY_ERROR_LOG, null);
        // set up buffers
        StringWriter outputWriter = new StringWriter(value.length() + 256);
        StringWriter errorWriter = new StringWriter(256);
        String outputValue;
        // 1.5. run cleanup
        Document dom4jDoc = null;
        try
        {
            dom4jDoc = htmlService.textToDom4j(value, errorWriter, cleanupProfile);
            if(dom4jDoc != null)
            {
                htmlService.dom4jToText(dom4jDoc, outputWriter, true);
            }
        }
        catch(HTMLException e)
        {
            errorWriter.append(e.getMessage());
            if(e.getCause() != null)
            {
                errorWriter.append(": ").append(e.getCause().getMessage());
            }            
            // in case validation passes but serialization fails
            dom4jDoc = null;
        }
        // 2.1. check tidy if there were any errors.
        if(dom4jDoc != null)
        {
            // 2.2. get cleaned HTML as String
            outputValue = outputWriter.toString(); 
        }
        else
        {
            // user must correct the HTML
            outputValue = value;
            // set error information in state because ErrorCollector
            // does not allow to store errors explicitly
            HTMLEntityEncoder encoder = new HTMLEntityEncoder();
            String errorLog = encoder.encodeAttribute(errorWriter.toString(), "UTF-8");
            instance.setStateValue(contextNode, TIDY_ERROR_LOG, errorLog);
			// make sure validation of the form fails
            instance.setError(contextNode, "HTML_VALIDATION_ERROR");
        }        
        super.setValue(instance, outputValue);
    }
    
    public boolean hasError(InstanceImpl instance)
    {
        return (getTidyErrorLog(instance) != null);
    }

    public String getTidyErrorLog(InstanceImpl instance)
    {
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);
        return (String)(instance.getStateValue(contextNode, TIDY_ERROR_LOG));
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder

    /** Gets a Tidy instance which will be used by this control.
     */
    protected void init(UI ui)
        throws ConstructionException
    {
        super.init(ui);
        htmlService = ui.getForm().getHtmlService();
    }
}
