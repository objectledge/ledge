package pl.caltha.forms.internal.ui;

import pl.caltha.forms.ConstructionException;

/**
 * Caption description element container.
 *
 * @see pl.caltha.forms.internal.ui.Control
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DescriptionCaption.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class DescriptionCaption extends Parent
{
    public DescriptionCaption()
    {
    }

    //------------------------------------------------------------------------
    // associations
    /** Object containing caption text for this control. */
    protected VisibleText caption;

    //------------------------------------------------------------------------
    // Description methods
    //
    /** Description element getter for Velocity. */
    public VisibleText getCaption()
    {
        return caption;
    }

    //------------------------------------------------------------------------
    // Overriden Parent methods

    protected void addChild(Node child)
    throws ConstructionException
    {
        if(child instanceof VisibleText &&
           UIConstants.CAPTION == child.type)
        {
            caption = (VisibleText)child;
        }
        else
        {
           throw new ConstructionException("Invalid child for DescriptionCaption");
        }
    }
}

