package pl.caltha.forms.internal.util;

import org.w3c.tidy.Tidy;

/** Wrapper for jTidy objects that allows pooling of those.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TidyWrapper.java,v 1.1 2005-01-19 06:55:37 pablo Exp $
 */
public class TidyWrapper
extends net.labeo.services.pool.RecyclableObject
{
    private Tidy tidy;
    
    /** Creates a new instance of TidyWrapper */
    public TidyWrapper()
    {
        tidy = new org.w3c.tidy.Tidy();
    }
    
    public void reset()
    {
        tidy.setErrout(null);
        tidy.setErrfile(null);
    }
    
    /** Getter for property tidy.
     * @return Value of property tidy.
     */
    public Tidy getTidy()
    {
        return tidy;
    }
}
