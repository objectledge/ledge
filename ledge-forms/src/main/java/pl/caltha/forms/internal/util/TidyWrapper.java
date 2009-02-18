package pl.caltha.forms.internal.util;

import org.w3c.tidy.Tidy;

/** Wrapper for jTidy objects that allows pooling of those.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TidyWrapper.java,v 1.2 2005-01-20 16:44:55 pablo Exp $
 */
public class TidyWrapper
{
    private Tidy tidy;
    
    /** Creates a new instance of TidyWrapper */
    public TidyWrapper()
    {
        tidy = new org.w3c.tidy.Tidy();
    }
    
    /** Getter for property tidy.
     * @return Value of property tidy.
     */
    public Tidy getTidy()
    {
        return tidy;
    }
}
