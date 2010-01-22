package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeSwitch.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeSwitch extends NodeSelectableContainer
{
    public NodeSwitch(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    //------------------------------------------------------------------------
    // Switch methods
    //
    /** Returns actual page, depending on Instance. */
    public NodeSelectable getCase(InstanceImpl instance)
    {
        return getCurrentChild(instance);
    }
}
