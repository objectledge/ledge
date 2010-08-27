package org.objectledge.forms.internal.ui.actions;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.ui.ActionEvent;
import org.objectledge.forms.internal.ui.NodeSelectable;
import org.objectledge.forms.internal.ui.NodeSelectableContainer;
import org.objectledge.forms.internal.ui.ReferenceSingle;
import org.objectledge.forms.internal.ui.UI;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/** Toggle action for switch/case and form/page elements.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Toggle.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public class Toggle extends org.objectledge.forms.internal.ui.Action
{
    public Toggle(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        childNodeId = Util.getSAXAttributeVal(atts, "case");
    }

    private NodeSelectableContainer containerNode;

    private String childNodeId;

    /** This method performs the action, ie. changes UI state values in
     * Instance. This action works like this:
     * <ol>
     *  <li>gets the event target</li>
     *  <li>gets target's repeat subtree root</li>
     *  <li>if root is null, changes all switch/cases or form/pages
     *      with {@link #childNodeId} in UI</li>
     *  <li>else is changes all switch/cases or form/pages
     *      with {@link #childNodeId} in subtree</li>
     * </ol>
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        org.dom4j.Node contextNode = ((ReferenceSingle)(containerNode.getRef())).getContextNode(instance);
        instance.setStateValue(contextNode, containerNode.getCurrentChildKey(), childNodeId);
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder in the same sequence they are called

    /** Inits a toggle action node - connects a referenced <code>case</code>
     * element..
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        NodeSelectable childNode = (NodeSelectable)(getNodeById(ui, childNodeId));
        containerNode = (NodeSelectableContainer)(childNode.getParent());
    }
}

