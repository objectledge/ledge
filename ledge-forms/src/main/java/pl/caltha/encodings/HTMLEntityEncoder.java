package pl.caltha.encodings;

import pl.caltha.encodings.encoders.CharEncoder;
import pl.caltha.encodings.encoders.CharEncoderHTMLEntity;

/**
 * Tool for encoding HTML text to a text which supports a chosen encoding using HTML entities.
 * If an encoding does not support a character code, a HTML or numeric entity is being generated
 * for this character, if a character is supported it is not changed.
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: HTMLEntityEncoder.java,v 1.1 2005-01-19 06:55:41 pablo Exp $
 */
public class HTMLEntityEncoder
{
    private CharEncoderHTMLEntity entityEncoder;
    private CharEncoder charsetEncoder;

    /**
     * Encodes a given text as an attribute with given encoding.
     *
     * @param text  Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @param doubleQuoteQuote set to <code>true</code> if attribute is quoted
     *      using " (double quote character) set to <code>false</code>
     *      if attribute is quoted using ' (apostrophe character)
     * @return          encoded text
     */
    public String encodeAttribute( String text, String encodingName, boolean doubleQuoteQuote )
    {
        if(text == null || text.length() == 0)
        {
            return text;
        }
        
        initEncoders(encodingName);

        // START: convert string
        StringBuffer buf = new StringBuffer(text.length());
        for (int i=0, s=text.length(); i < s; i++)
        {
            // get a character from input String
            char c = text.charAt(i);
            // check if character is a special XML attribute character 
            if(c == '"' && doubleQuoteQuote ||
               c == '\'' && !doubleQuoteQuote ||
               c == '&' || c == '<' || c == '>')
            {
                encodeEntity(buf, c);
            }
            else
            // try to encode using normal encoding 
            if(charsetEncoder != null && charsetEncoder.encode(c) == null)
            {
                // if not, encode it using entity encoding
                encodeEntity(buf, c);
            }
            else
            {
                // return it as is - in this case a character will be encoded while
                // encoding a string to a byte array
                buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * Encodes a given text as an attribute with given encoding and default quote character -
     * <code>&quot;</code>.
     *
     * @param text  Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @return          encoded text
     */
    public String encodeAttribute( String text, String encodingName )
    {
        return encodeAttribute(text, encodingName, true);
    }


    /**
     * Encodes a given text with given encoding.
     *
     * @param htmlText  Text to be encoded
     * @param encodingName name of a chosen encoding.
     * @return          encoded text
     */
    public String encodeHTML( String htmlText, String encodingName )
    {
        if(htmlText == null || htmlText.length() == 0)
        {
            return htmlText;
        }

        initEncoders(encodingName);
                
        // pass for unknown encodings
        if(charsetEncoder == null)
        {
            return htmlText;
        }
        
        // START: convert string
        StringBuffer buf = new StringBuffer(htmlText.length());
        for (int i=0, s=htmlText.length(); i < s; i++)
        {
            // get a character from input String
            char c = htmlText.charAt(i);
            // check if it is posible to encode it using current charset
            if(charsetEncoder.encode(c) == null)
            {
                // if not, try to encode it using entity encoding
                encodeEntity(buf, c);
            }
            else
            {
                // return it as is - in this case a character will be encoded while
                // encoding a string to a byte array
                buf.append(c);
            }
        }
        
        return buf.toString();
    }

    // implementation -----------------------------------------------------------------------------

    private void initEncoders(String encodingName)
    {
        // get entity encoder
        if(entityEncoder == null)
        {
            entityEncoder = new CharEncoderHTMLEntity();
        }

        // get charset encoder
        encodingName = EncodingMap.getIANA2JavaMapping(encodingName);
        if(charsetEncoder == null || !charsetEncoder.getEncoding().equals(encodingName))
        {
            charsetEncoder = (CharEncoder)
                ObjectUtils.instantiate("pl.caltha.encodings.encoders.CharEncoder"+encodingName);
        }
    }

    private void encodeEntity(StringBuffer buf, char c)
    {
        // encode it using entity encoding
        char[] encodedChar = entityEncoder.encode(c);

        buf.append('&');
        if(encodedChar != null)
        {
            buf.append(encodedChar);
        }
        else
        {   
            // return it as a numeric entity
            buf.append('#');
            buf.append(Short.toString((short)c));
        }
        buf.append(';');
    }

    /**
     * Resets the object's internal state. <p>
     *
     * This method prepares the object instance for reuse. After this method returns, the object
     * should be equivalent to a newly created one. </p>
     */
    public void reset()
    {
    }
}

