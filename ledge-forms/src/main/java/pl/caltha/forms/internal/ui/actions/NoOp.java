package pl.caltha.forms.internal.ui.actions;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.ActionEvent;
import pl.caltha.forms.internal.ui.UI;

/** Noop action for watching value changes on controls.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NoOp.java,v 1.2 2005-02-10 17:46:49 rafal Exp $
 */
public class NoOp extends pl.caltha.forms.internal.ui.Action
{
    public NoOp(String type, Attributes atts) throws ConstructionException
    {
        super(type, atts);
    }

    /** This method performs the action, ie. does nothing.
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        // do nothing very thoroughly
    }
}

