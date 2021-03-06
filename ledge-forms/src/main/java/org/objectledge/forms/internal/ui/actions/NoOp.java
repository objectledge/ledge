package org.objectledge.forms.internal.ui.actions;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.Instance;
import org.objectledge.forms.internal.ui.ActionEvent;
import org.objectledge.forms.internal.ui.UI;
import org.xml.sax.Attributes;


/** Noop action for watching value changes on controls.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NoOp.java,v 1.2 2005-02-10 17:46:49 rafal Exp $
 */
public class NoOp extends org.objectledge.forms.internal.ui.Action
{
    public NoOp(String type, Attributes atts) throws ConstructionException
    {
        super(type, atts);
    }

    /** This method performs the action, ie. does nothing.
     */
    public void execute(UI ui, Instance instance, ActionEvent evt)
    {
        // do nothing very thoroughly
    }
}

