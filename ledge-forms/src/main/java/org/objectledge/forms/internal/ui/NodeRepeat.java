package org.objectledge.forms.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Implementation of <code>repeat</code> control functionality.
 * This class gives a possiblity to build forms with repeating blocks
 * (with many controls). Repeat blocks may be nested.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeRepeat.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */

public class NodeRepeat extends NodeCaptionReference
{
    public NodeRepeat(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        ref = new ReferenceMultipleRepeat(atts, this);
        startIndex = Util.createIntAttribute(atts, "startIndex", 1);
        number = Util.createIntAttribute(atts, "number", 1);

        firstSubTree = new NodeRepeatSubTree(UIConstants.REPEATSUBTREE, new org.xml.sax.helpers.AttributesImpl());
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which cleared:</p>
     * <ul>
     *  <li>{@link #firstSubTree}</li>
     * </ul>
     */
    protected Object clone()
    {
        NodeRepeat next = (NodeRepeat)(super.clone());
        next.firstSubTree = null;
        return next;
    }

    private NodeRepeatSubTree firstSubTree;

    private int startIndex;
    private int number;

    //-----------------------------------------------------------------------
    // Methods used in Velocity macros.
    //
    /** Getter for property number.
     * @return Value of property number.
     */
    public int getNumber()
    {
        // number == children.size()
        return number;
    }

    private String INDEX;

    public int getIndex(InstanceImpl instance)
    {
        // get parent contextNode - repeat cannot us contextNode because it
        // does not have one
        org.dom4j.Node contextNode = ref.getParentContextNode(instance);
        // get value from state
        Integer index = (Integer)(instance.getStateValue(contextNode, INDEX));
        // no state value - get one from default
        if(index == null)
        {
            index = new Integer( calcIndex(instance, startIndex) );
            // store index val in state
            instance.setStateValue(contextNode, INDEX, index);
        }

        return index.intValue();
    }

    private int calcIndex(InstanceImpl instance, int index)
    {
        // check index for sanity
        int indexVal = index;

        List contextNodes = ((ReferenceMultipleRepeat)ref).getContextNodes(instance);
        int ctxNodesNumber = contextNodes.size();
        // number of contextNodes is smaller than repeat size (number)
        // we must start on first child
        if(number > ctxNodesNumber || indexVal < 1)
        {
            indexVal = 1;
        }
        // number of contextNodes is bigger than repeat size (number)
        // we have to check if we are out of bounds
        else if(indexVal > ctxNodesNumber - number)
        {
            // WARN is it ok
            indexVal = ctxNodesNumber - number + 1;
        }

        return indexVal;
    }

    public List getChildren(InstanceImpl instance)
    {
        List contextNodes = ((ReferenceMultipleRepeat)ref).getContextNodes(instance);
        int size = Math.min(contextNodes.size(), number);
        ArrayList instChildren = new ArrayList(size);
        for(int i=0; i<size; i++)
        {
            instChildren.add(i, children.get(i));
        }
        return instChildren;
    }

    public void flushChildrenState(InstanceImpl instance)
    {
        ref.clearContextNodeCache(instance);
    }

    //-----------------------------------------------------------------------
    // Methods used by UI Builder
    protected void addChild(Node child)
    throws ConstructionException
    {
        // connect first subtree container
        // this is only called for SAX constructed repeat
        if(children.size() == 0)
        {
            super.addChild(firstSubTree);
        }
        firstSubTree.addChild(child);
    }

    protected void cloneRepeatSubTree(UI ui)
    throws ConstructionException
    {
        for(int i = 1; i < number; i++)
        {
            NodeRepeatSubTree cloneSubTree = (NodeRepeatSubTree)(firstSubTree.clone());
            super.addChild(cloneSubTree);
        }
    }

    protected void setParent(UI ui, Node parentNode)
    throws ConstructionException
    {
        super.setParent(ui, parentNode);
        INDEX = "index."+definitionUiPath;
    }

    /**
     * Inits an insert/delete action node - connects a referenced <code>repeat</code>
     * element..
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);

        // preform form definition quality check only for defined nodes
        // this way we avoid non existant copntextNodes,
        // Context nodes number may be smaller than repeat sub trees number.
        // So we could get null contextNode for nestged repeats.

        // //////////////// escape from cloned repeats

        if(this.cloned)
        {
            return;
        }

        // //////////////// perform the check

        InstanceImpl instance = ui.getForm().getDefaultInstance();

        org.dom4j.Node parentContextNode = ((ReferenceMultipleRepeat)ref).getParentContextNode(instance);
        // guard from problems with insert and delete actions
        if(parentContextNode == null)
        {
            throw new ConstructionException("Cannot get a parent context node for repeat with id='"+id+"'");
        }

        if( !(parentContextNode instanceof org.dom4j.Element) )
        {
            throw new ConstructionException("Parent context node for repeat with id='"+id+"' is not an element");
        }

        List contextNodes = ((ReferenceMultipleRepeat)ref).getContextNodes(instance);
        // WARN: guard from badly designed forms
        if(contextNodes.size() > 0)
        {
            org.dom4j.Node contextNode = (org.dom4j.Node)(contextNodes.get(0));
            if(contextNode.getParent() != parentContextNode)
            {
                throw new ConstructionException("Parent context node for repeat with id='"+id+"'"+
                    " must be equal to parent node of nodes belonging to repeat's 'nodeset'.");
            }
        }
        else
        {
            throw new ConstructionException("Repeat with id='"+id+"'"+
                " has an empty 'nodeset'.");
        }
    }
}
