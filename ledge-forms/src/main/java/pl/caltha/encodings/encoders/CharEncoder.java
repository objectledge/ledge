package pl.caltha.encodings.encoders;

/**
 * Base class for Encoder classes. Defines masks and encoding method.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CharEncoder.java,v 1.1 2005-01-19 06:55:15 pablo Exp $
 */
public abstract class CharEncoder
{
    protected final int prefixMask = 0xff00;
    protected final int suffixMask = 0x00ff;
    protected final int prefixShift = 8;

    protected String encodingName = null;
    
    /** Returns this encoding name. */
    public String getEncoding()
    {
        return encodingName;
    }

    /** Encodes a char in a given encoding. */
    public char[] encode(char c)
    {
        return suffixIndex[ prefixIndex[(c & prefixMask) >> prefixShift] + (c & suffixMask) ];
    }

    /** Description of the Field */
    protected int[] prefixIndex = null;

    /** Description of the Field */
    protected char[][] suffixIndex = null;
}

