package pl.caltha.forms.internal.ui.actions;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.ActionEvent;
import pl.caltha.forms.internal.ui.ActionNode;
import pl.caltha.forms.internal.ui.Node;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.util.Util;

/** Toggle action for switch/case and form/page elements.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Dispatch.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public class Dispatch extends pl.caltha.forms.internal.ui.Action
{
    public Dispatch(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        eventName = Util.getSAXAttributeVal(atts, "name");
        targetNodeId = Util.getSAXAttributeVal(atts, "target");
        bubbles = Util.createBooleanAttribute(atts, "bubbles", true);
        cancelable = Util.createBooleanAttribute(atts, "cancelable", true);
    }

    private ActionNode targetNode;

    private String eventName;
    private String targetNodeId;
    private boolean bubbles;
    private boolean cancelable;

    /** This method performs the action, ie. dispatches an event
     * to a given target.
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        targetNode.dispatchEvent(ui, instance, new ActionEvent(eventName, (Node)(targetNode)));
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder in the same sequence they are called

    /** Inits a dispatch action node - connects a referenced <code>target</code>
     * element..
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        targetNode = (ActionNode)(getNodeById(ui, targetNodeId));
    }
}
