package org.objectledge.encodings.encoders;

/**
 * Encoder for UTF-16 character set.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CharEncoderUnicode.java,v 1.1 2004-02-02 18:59:00 zwierzem Exp $
 */
public class CharEncoderUnicode extends CharEncoderUTF
{
    /** Constructor. */
    public CharEncoderUnicode()
    {
        this.encodingName = "Unicode";
    }
}
