package org.objectledge.forms;

/**
 * Thrown on problems with form definition objects {@link Form} construction.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ConstructionException.java,v 1.1 2005-01-19 06:55:23 pablo Exp $
 */
public class ConstructionException
extends FormsException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ConstructionException()
    {
        super();
    }
    
    public ConstructionException(String msg)
    {
        super(msg);
    }

    public ConstructionException(String msg, Exception e)
    {
        super(msg, e);
    }
}
