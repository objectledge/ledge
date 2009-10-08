package org.objectledge.forms.internal.ui;

import java.util.HashMap;
import java.util.List;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeRepeatSubTree.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeRepeatSubTree extends NodeCaptionReference
{
    public NodeRepeatSubTree(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are copied and cleared:</p>
     * <ul>
     *  <li>{@link #nodesById} - it must be a new HashMap so that it indexes
     *  only nodes from a cloned subtree</li>
     * </ul>
     */
    protected Object clone()
    {
        NodeRepeatSubTree next = (NodeRepeatSubTree)(super.clone());
        next.nodesById = new HashMap();
        return next;
    }

    private HashMap nodesById = new HashMap();

    /** Returns node(s) with a given ID. */
    public List getNodesById(String id)
    {
        return (List)(nodesById.get(id));
    }

    //------------------------------------------------------------------------
    // methods used during building

    void addNodeById(Node node)
    throws ConstructionException
    {
        // we do not have to check for ID identity
        // it is already done in UI
        Util.insertMultipleIntoHash(node.id, node, nodesById);
    }
}
