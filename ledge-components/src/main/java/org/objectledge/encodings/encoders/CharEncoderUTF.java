package org.objectledge.encodings.encoders;

/**
 * Base encoder for UTF (Unicode) character sets.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CharEncoderUTF.java,v 1.1 2004-02-02 18:59:00 zwierzem Exp $
 */
public class CharEncoderUTF extends CharEncoder
{
    private char[] res = new char[1];

	/**
	 * {@inheritDoc}
	 */
    public char[] encode(char c)
    {
        res[0] = c;
        return res;
    }
}
