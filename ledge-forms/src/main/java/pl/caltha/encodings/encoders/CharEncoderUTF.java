package pl.caltha.encodings.encoders;

/**
 * Base encoder for UTF (Unicode) character sets.
 *
 * @author    <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version   $Id: CharEncoderUTF.java,v 1.1 2005-01-19 06:55:15 pablo Exp $
 */
public class CharEncoderUTF extends CharEncoder
{
    protected char[] res = new char[1];
    
    public char[] encode(char c)
    {
        res[0] = c;
        return res;
    }
}
