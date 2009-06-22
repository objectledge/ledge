package pl.caltha.forms.internal.ui;

import pl.caltha.forms.ConstructionException;

/**
 * Description elements container. Contains all of them, together
 * with caption inherited from {@link DescriptionCaption}.
 * <p>Description elements contained by objects of this class:</p>
 * <ul>
 *  <li><code>hint</code></li>
 *  <li><code>alert</code></li>
 *  <li><code>help</code></li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DescriptionAll.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class DescriptionAll extends DescriptionCaption
{
    public DescriptionAll()
    {
        super();
    }

    //------------------------------------------------------------------------
    // associations

    /** Object containing hint text for this control. */
    protected VisibleText hint;
    /** Object containing on error alert for this control. */
    protected VisibleText alert;
    /** Object containing help text for this control. */
    protected VisibleText help;

    //------------------------------------------------------------------------
    // Description methods
    //
    /** Description element getter for Velocity. */
    public VisibleText getHint()
    {
        return hint;
    }

    /** Description element getter for Velocity. */
    public VisibleText getAlert()
    {
        return alert;
    }

    /** Description element getter for Velocity. */
    public VisibleText getHelp()
    {
        return help;
    }

    //------------------------------------------------------------------------
    // Overriden Parent methods
    protected void addChild(Node child)
    throws ConstructionException
    {
        if(child instanceof VisibleText)
        {
            VisibleText text = (VisibleText)child;
            if(UIConstants.ALERT == text.type)
            {
                alert = text;
            }
            else
            if(UIConstants.HELP == text.type)
            {
                help = text;
            }
            else
            if(UIConstants.HINT == text.type)
            {
                hint = text;
            }
            else
            {
                super.addChild(child);
            }
        }
        else
        {
           throw new ConstructionException("Invalid child for DescriptionAll");
        }
    }
}

