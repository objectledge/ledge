package pl.caltha.encodings;

import net.labeo.services.ServiceBroker;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ContextTool;
import net.labeo.webcore.RunData;

/**
 * Tool for encoding HTML text to a text which supports a chosen encoding using HTML entities.
 * If an encoding does not support a character code, a HTML or numeric entity is being generated
 * for this character, if a character is supported it is not changed.
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: HTMLEntityEncoderTool.java,v 1.1 2005-01-19 06:55:41 pablo Exp $
 */
public class HTMLEntityEncoderTool extends HTMLEntityEncoder implements ContextTool
{
    private RunData data;

    public String encodeAttribute( String text, boolean doubleQuoteQuote )
    {
        return encodeAttribute(text, data.getEncoding(), doubleQuoteQuote);
    }

    public String encodeAttribute( String text )
    {
        return encodeAttribute(text, data.getEncoding());
    }

    /**
     * Description of the Method
     *
     * @param htmlText  Description of Parameter
     * @return          Description of the Returned Value
     */
    public String encodeHTML( String htmlText )
    {
        return encodeHTML(htmlText, data.getEncoding());
    }

    /**
     * Initializes the context tool. <p>
     *
     * Initialization specific to tool definition is performed here.</p>
     *
     * @param broker  Labeo service broker
     * @param conf    tool's configuration
     */
    public void init( ServiceBroker broker, Configuration conf )
    {
    }


    /**
     * Prepares the context tool instance for work witin a specific request.
     *
     * @param data  the RunData.
     */
    public void prepare( RunData data )
    {
        this.data = data;
    }


    /**
     * Resets the object's internal state. <p>
     *
     * This method prepares the object instance for reuse. After this method returns, the object
     * should be equivalent to a newly created one. </p>
     */
    public void reset()
    {
        super.reset();
        data = null;
    }
}

