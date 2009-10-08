package org.objectledge.forms.internal.util;

/**
 *
 * @author  damian
 */
public class CSSParseException
extends org.objectledge.forms.ConstructionException
{
    public CSSParseException()
    {
        super();
    }
    
    public CSSParseException(String msg)
    {
        super(msg);
    }

    public CSSParseException(String msg, Exception e)
    {
        super(msg, e);
    }
}
