package org.objectledge.forms.internal.ui;

import java.util.List;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/** Base implementation of an action node.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Action.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public abstract class Action extends Node
{
    public Action(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // extract attribute event
        eventType = Util.getSAXAttributeVal(atts, "event");
    }

    private String eventType;

    /** Getter for event type.
     * @return Value of event type.
     */
    public String getEventType()
    {
        return eventType;
    }

    public abstract void execute(UI ui, InstanceImpl instance, ActionEvent evt);

    /** This method is used in action which reference nodes by their XML ID.
     * It is used to find the referenced node in a closest RepeatSubTree in which
     * action is defined.
     */
    protected Node getNodeById(UI ui, String nodeId)
    throws ConstructionException
    {
        // find a referenced node
        List<Node> nodes = null;

        // try to find it in this ui subtree
        NodeRepeatSubTree _repeatSubTree = this.getParentRepeatSubTree();
        while(_repeatSubTree != null && nodes == null)
        {
            nodes = _repeatSubTree.getNodesById(nodeId);
            _repeatSubTree = _repeatSubTree.getParentRepeatSubTree();
        }

        // if not - find it on top level
        if(nodes == null)
        {
            nodes = ui.getNodesById(nodeId);
        }

        // could not find any node with this id
        if(nodes == null)
        {
            throw new ConstructionException(
                "Cannot find a node with id='"+nodeId+
                "' which is referenced by +'"+type+"' action (uiPath='"+uiPath+"')");
        }

        // if size > 1 it means that schema ID checking failed and there
        // is more than one node with the same id.
        if(nodes.size() > 1)
        {
            throw new ConstructionException(
                "There is more than one node with id='"+nodeId+
                "' which is referenced by '"+type+"' action (uiPath='"+uiPath+"')");
        }
        else
        {
            return nodes.get(0);
        }
    }
}

