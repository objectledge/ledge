package org.objectledge.forms.internal.ui;

import org.objectledge.forms.internal.model.InstanceImpl;

/** Represents a node which may have appended actions.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ActionNode.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public interface ActionNode
{
    /** Passes an event to this UI node (control). */
    public void dispatchEvent(UI ui, InstanceImpl instance, ActionEvent evt);
    /** Returns <code>true</code> if this action node has a binded action. */
    public boolean hasAction();
}
