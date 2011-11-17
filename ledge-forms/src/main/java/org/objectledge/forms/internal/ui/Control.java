package org.objectledge.forms.internal.ui;

import org.objectledge.forms.Instance;

/** Represents common properties of form-tool controls.
 * <ul>
 *    <li>Common UI Children -- {@link org.objectledge.forms.internal.ui.DescriptionAll}</li>
 *    <li>Bind Attributes -- {@link org.objectledge.forms.internal.model.InstanceReference},
 *        {@link org.objectledge.forms.internal.model.Bind}</li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Control.java,v 1.2 2005-02-08 20:33:30 rafal Exp $
 */
public interface Control extends ActionNode
{
    /** Returns value associated with this Control. */
    public Object getValue(Instance instance);
    /** Returns <code>true</code> if this control has a value. */
    public boolean hasValue(Instance instance);
    /** Returns true if value associated with this Control is invalid. */
    public boolean hasError(Instance instance);
}

