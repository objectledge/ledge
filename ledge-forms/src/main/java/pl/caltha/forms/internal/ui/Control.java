package pl.caltha.forms.internal.ui;

import pl.caltha.forms.internal.model.InstanceImpl;

/** Represents common properties of form-tool controls.
 * <ul>
 *    <li>Common UI Children -- {@link pl.caltha.forms.internal.ui.DescriptionAll}</li>
 *    <li>Bind Attributes -- {@link pl.caltha.forms.internal.model.InstanceReference},
 *        {@link pl.caltha.forms.internal.model.Bind}</li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Control.java,v 1.2 2005-02-08 20:33:30 rafal Exp $
 */
public interface Control extends ActionNode
{
    /** Returns value associated with this Control. */
    public Object getValue(InstanceImpl instance);
    /** Returns <code>true</code> if this control has a value. */
    public boolean hasValue(InstanceImpl instance);
    /** Returns true if value associated with this Control is invalid. */
    public boolean hasError(InstanceImpl instance);
}

