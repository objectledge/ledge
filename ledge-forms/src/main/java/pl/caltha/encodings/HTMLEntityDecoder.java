package pl.caltha.encodings;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import pl.caltha.encodings.encoders.CharEncoderHTMLEntity;

/**
 * Tool for parsing text with characters encoded as HTML entities. All recognized entities
 * (including numeric ones) are converted to single unicode characters. 
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: HTMLEntityDecoder.java,v 1.1 2005-01-19 06:55:41 pablo Exp $
 */
public class HTMLEntityDecoder
{
    /** Entity encoder is used to reverse encode detected named and numeric entities. */
    private CharEncoderHTMLEntity entityEncoder = new CharEncoderHTMLEntity();

    /** Default automata state - characters pass through. */ 
    private static final int DEFAULT = 0;
    /** Maybe entity automata state - depending on a character we may start a named or numeric
     * entity or get back to default state. */ 
    private static final int MAYBE_ENTITY = 1;
    /** Maybe start numeric entity automata state - depending on a character we may start a decimal
     * or hexadecimal numeric entity or get back to default state. */ 
    private static final int MAYBE_START_NUMERIC_ENTITY = 2;
    /** Maybe in a hexadecimal numeric entity automata state - depending on a character we may
     * collect characters of a hexadecimal numeric entity or get back to default state. */ 
    private static final int MAYBE_IN_NUMHEX_ENTITY = 3;
    /** Maybe in a decimal numeric entity automata state - depending on a character we may collect
     * characters of a decimal numeric entity or get back to default state. */ 
    private static final int MAYBE_IN_NUMDEC_ENTITY = 4;
    /** Maybe in a named entity automata state - depending on a character we may collect
     * characters of a named entity or get back to default state. */ 
    private static final int MAYBE_IN_NAMED_ENTITY = 5;

    /** 
     * Deodes a given string and returns a string with entities converted to unicode characters.
     * This operation compresses strings. Example:
     * <pre>
     * input  = " &amp;amp; &amp;quot; &amp;euml; &amp;Oacute; &amp;#211; &amp;sdsd&amp;#1233&amp;amp; &amp;euml &amp;notsgmlentity; "
     * output = " &amp; " � � � &amp;sdsd&amp;#1233&amp; &amp;euml &amp;notsgmlentity; "
     * </pre>
     * 
     * @param input input string with characters encoded as named or numeric entities.
     * @return string with entities converted to single unicode characters.
     */
    public String decode(String input)
    {
        return decode(input, false);
    }

    /** 
     * Deodes a string and returns a string with entities converted to unicode characters,
     * avoids converting core XML entities.
     * This operation compresses strings. Example:
     * <pre>
     * input  = " &amp;amp; &amp;quot; &amp;lt; &amp;euml; &amp;Oacute; &amp;#211; &amp;sdsd&#1233&amp;amp; &amp;euml &amp;notsgmlentity; "
     * output = " &amp;amp; &amp;quot; &amp;lt; � � � &amp;sdsd&amp;#1233&amp;amp; &amp;euml &amp;notsgmlentity; "
     * </pre>
     * 
     * @param input input string with characters encoded as named or numeric entities.
     * @return string with entities converted to single unicode characters.
     */
    public String decodeXML(String input)
    {
        return decode(input, true);
    }

    private String decode(String input, boolean keepXmlEntities)
    {
        try
        {
            StringWriter writer = new StringWriter(input.length());
            decode(new StringReader(input), writer, keepXmlEntities);
            return writer.toString();
        }
        catch (IOException e)
        {
            // should not happen
            return null;
        }
    }

    /** 
     * Deodes a given character stream and writes a string with entities converted to unicode
     * characters into the given writer.
     * @see HTMLEntityParser#parse(String) 
     * 
     * @param inputReader input character stream with characters encoded as named or numeric
     *  entities, this method CLOSES the given reader.
     * @param outputWriter output character stream with entities converted to single unicode
     *  characters.
     * @throws IOException on errors in reading or writing characters
     */
    public void decode(Reader inputReader, Writer outputWriter)
        throws IOException
    {
        decode(inputReader, outputWriter, false);
    }

    /** 
     * Deodes a given character stream and writes a string with entities converted to unicode
     * characters into the given writer, avoids converting core XML entities.
     * @see HTMLEntityParser#parseXML(String) 
     * 
     * @param inputReader input character stream with characters encoded as named or numeric
     *  entities, this method CLOSES the given reader.
     * @param outputWriter output character stream with entities converted to single unicode
     *  characters.
     * @throws IOException on errors in reading or writing characters
     */
    public void decodeXML(Reader inputReader, Writer outputWriter)
        throws IOException
    {
        decode(inputReader, outputWriter, true);
    }

    private void decode(Reader inputReader, Writer outputWriter, boolean keepXmlEntities)
        throws IOException 
    {
        StringBuffer entity = new StringBuffer(16); // sufficient for all entities
        
        PushbackReader reader = new PushbackReader(inputReader);
        int state = DEFAULT;
        int chr;
        while((chr = reader.read()) != -1)
        {
            char c = (char)chr;
            switch(state)
            {
                case DEFAULT:
                    if(c == '&')
                    {
                        entity.append(c);
                        state = MAYBE_ENTITY;
                    }
                    else
                    {
                        outputWriter.write(c);
                    }
                break;
                case MAYBE_ENTITY:
                    entity.append(c);
                    if(c == '#')
                    {
                        state = MAYBE_START_NUMERIC_ENTITY;
                    }
                    else if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
                    {
                        state = MAYBE_IN_NAMED_ENTITY;
                    }
                    else
                    {
                        state = notAnEntity(reader, c, entity, outputWriter);
                    }
                break;
                case MAYBE_START_NUMERIC_ENTITY:
                    entity.append(c);
                    if(c >= '0' && c <= '9')
                    {
                        state = MAYBE_IN_NUMDEC_ENTITY;
                    }
                    else if(c == 'x')
                    {
                        state = MAYBE_IN_NUMHEX_ENTITY;
                    }
                    else
                    {
                        state = notAnEntity(reader, c, entity, outputWriter);
                    }
                break;
                case MAYBE_IN_NUMDEC_ENTITY:
                    entity.append(c);
                    if(c == ';')
                    {
                        state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
                    }
                    else if(entity.length() > 8 || !(c >= '0' && c <= '9'))
                    {
                        state = notAnEntity(reader, c, entity, outputWriter);
                    }
                break;
                case MAYBE_IN_NUMHEX_ENTITY:
                    entity.append(c);
                    if(c == ';')
                    {
                        state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
                    }
                    else if(entity.length() > 8 || 
                        !(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F'))
                    {
                        state = notAnEntity(reader, c, entity, outputWriter);
                    }
                break;
                case MAYBE_IN_NAMED_ENTITY:
                    entity.append(c);
                    if(c == ';')
                    {
                        state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
                    }
                    else if(entity.length() > 12 || // should be 10
                        !(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'))
                    {
                        state = notAnEntity(reader, c, entity, outputWriter);
                    }
                break;
                default:
                    throw new IllegalStateException("the automata should not enter this state");
            }
        }
        
        if(entity.length() > 0)
        {
            outputWriter.write(entity.toString());
        }
        
        inputReader.close(); // TODO should we do it?
    }

    private int notAnEntity(PushbackReader reader, char c, StringBuffer entity, Writer writer)
        throws IOException
    {
        reader.unread(c);
        entity.setLength(entity.length()-1);
        writer.write(entity.toString());
        entity.setLength(0);  // clear out entity buffer
        return DEFAULT;
    }

    private int dumpEntity(PushbackReader reader, char c, StringBuffer entity, Writer writer,
        boolean keepXmlEntities)
        throws IOException
    {
        int eC = entityEncoder.entityCode(entity.toString());
        if(eC != -1)
        {
            if(keepXmlEntities && (eC == '"' || eC == '\'' || eC == '&' || eC == '<' || eC == '>'))
            {
                return notAnEntity(reader, c, entity, writer);
            }
            writer.write((char)eC);
            entity.setLength(0); // clear out entity buffer
            return DEFAULT;                        
        }
        else
        {
            return notAnEntity(reader, c, entity, writer);
        }
    }

    // implementation -----------------------------------------------------------------------------

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
