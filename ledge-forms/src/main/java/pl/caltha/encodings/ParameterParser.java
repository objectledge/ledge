package pl.caltha.encodings;

/**
 * Provides an unified interface for querying HTTP request parameters.
 * Converts all parameters using HTMLEntityDecoder.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ParameterParser.java,v 1.1 2005-01-19 06:55:41 pablo Exp $
 */
public class ParameterParser
    extends net.labeo.internal.webcore.ParameterParser
{
    protected HTMLEntityDecoder decoder = new HTMLEntityDecoder();
    
    /**
     * Converts the parameter value unsing the HTMLEntityDecoder.
     *
     * @param name parameter name
     * @return converted name
     */
    protected String convert(String name)
    {
        name = super.convert(name);
        name = decoder.decode(name);
        return name;
    }
}
