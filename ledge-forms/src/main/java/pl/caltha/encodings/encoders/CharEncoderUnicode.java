package pl.caltha.encodings.encoders;

/**
 * Encoder for UTF-16 character set.
 *
 * @author    <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version   $Id: CharEncoderUnicode.java,v 1.1 2005-01-19 06:55:15 pablo Exp $
 */
public class CharEncoderUnicode extends CharEncoderUTF
{
    /** Constructor. */
    public CharEncoderUnicode()
    {
        this.encodingName = "Unicode";
    }
}
