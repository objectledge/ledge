package pl.caltha.forms.internal.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.w3c.tidy.Tidy;
import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.TidyWrapper;

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
    public NodeControlHTML(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    /** Tidy configuration. */
    private static Properties tidyConfiguration;
    /** PoolService is used to pool jTidy objects. */

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

        // do HTML processing
        // 1. clean up the value using jTidy
        // 1.1. get tidy
        TidyWrapper tidyWrap = new TidyWrapper();

        try
        {
            // 1.2. setup tidy
            Tidy tidy = tidyWrap.getTidy();
            tidy.setConfigurationFromProps(tidyConfiguration);
            tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);
            tidy.setXHTML(true);
            tidy.setShowWarnings(false);
            // 1.3. setup streams
            ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes("UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(value.length()+256);
            // 1.4. setup error information writer
            StringWriter errorWriter = new StringWriter(256);
            tidy.setErrout(new PrintWriter(errorWriter));
            // 1.5. run cleanup
            tidy.parse(inputStream, outputStream);
            // 2. set the value
            String outputValue;
            // 2.1. check tidy if there were any errors.
            if(tidy.getParseErrors() > 0)
            {
                // user must correct the HTML
                outputValue = value;
                // set error information in state because ErrorCollector
                // does not allow to store errors explicitly
                String errorLog = errorWriter.toString();
                instance.setStateValue(contextNode, TIDY_ERROR_LOG, errorLog);
            }
            else
            // 2.2. get cleaned HTML as String
            {
              outputValue = outputStream.toString("UTF-8");
            }
            // 2.3. Remove HTML headers and footers
            outputValue = stripHTMLHead(outputValue);
            
            super.setValue(instance, outputValue);
        }
        catch(java.io.UnsupportedEncodingException e)
        {
            // should never happen
        }
        
        // return tidy wrapper to the pool
        
    }

    /** Removes everything but <code>&lt;body&gt;</code> tag contents. */
    private String stripHTMLHead(String htmlDoc)
    {
        int bodyStartIndex = htmlDoc.indexOf("<body");
        int bodyEndIndex = htmlDoc.indexOf("</body>");

        if(bodyStartIndex > -1)
        {
            for(int i = bodyStartIndex; i < bodyEndIndex; i++)
            {
                if(htmlDoc.charAt(i) == '>')
                {
                    bodyStartIndex = i+1;
                    break;
                }
            }

            if(bodyStartIndex < bodyEndIndex)
            {
                return htmlDoc.substring(bodyStartIndex, bodyEndIndex);
            }
        }
        
        return htmlDoc;
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
        FormsService formToolService = ui.getForm().getFormToolService();
        if(tidyConfiguration == null)
        {
            tidyConfiguration = formToolService.getTidyConfiguration();
        }
    }
}
