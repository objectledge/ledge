package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.xml.sax.Attributes;


/**
 * Button controls implementation.
 * Includes:
 * <ul>
 *      <li><code>submit</code></li>
 *      <li><code>button</code></li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeButton.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeButton extends NodeCaptionReference
implements ActionNode
{
    public NodeButton(String type, Attributes atts) throws ConstructionException
    {
        super(type, atts);
        // has all description elements
        desc = new DescriptionAll();
    }

    //------------------------------------------------------------------------
    // ActionNode methods

    /** Passes an event to this UI node (control). */
    public void dispatchEvent(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        actions.execute(ui, instance, evt);
    }

    /** Returns <code>true</code> if this action node has a binded action. */
    public boolean hasAction()
    {
        return (actions.getChildren().size() > 0);
    }
}

