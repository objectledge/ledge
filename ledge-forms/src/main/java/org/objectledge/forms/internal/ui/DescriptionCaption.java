package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;

/**
 * Caption description element container.
 *
 * @see org.objectledge.forms.internal.ui.Control
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DescriptionCaption.java,v 1.2 2005-02-10 17:49:14 rafal Exp $
 */
public class DescriptionCaption extends Parent
{
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

