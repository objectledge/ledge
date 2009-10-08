package org.objectledge.forms.internal.ui;

import java.util.Collection;
import java.util.HashMap;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeSelectableContainer.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeSelectableContainer extends NodeCaptionReference
{
    public NodeSelectableContainer(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        CURRENT_CHILD = "currentChild"+definitionUiPath;

        defaultChildId = Util.getSAXAttributeVal(atts, "default");
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are copied and cleared:</p>
     * <ul>
     *  <li>{@link #childrenById} - it must be a new HashMap so that it indexes
     *  only nodes from a cloned subtree</li>
     * </ul>
     */
    protected Object clone()
    {
        NodeSelectableContainer next = (NodeSelectableContainer)(super.clone());
        next.childrenById = new HashMap();
        return next;
    }

    //------------------------------------------------------------------------
    // associations
    public HashMap childrenById = new HashMap();
    private String defaultChildId;

    //------------------------------------------------------------------------
    // Node methods
    /** Indexes children by their XML IDs, gets a value
     * of {@link #defaultChildId}. */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        // 1. Index children
        for(int i = 0, s = children.size(); i < s; i++)
        {
            NodeSelectable child = (NodeSelectable)(children.get(i));
            // 1. Get default child id
            // Schema checking takes care of the appropriate node type.
            if(defaultChildId == null && child.getSelected())
            {
                defaultChildId = child.id;
            }
            // 2. Add to childrenById.
            if(!childrenById.containsValue(child))
            {
                childrenById.put(child.id, child);
            }
        }

        // 2. Check if there is a default child defined.
        if(defaultChildId == null)
        {
            // 1. Set a default page
            // This should be not empty - schema covers that.
            if(this.childrenById.size() > 0)
            {
                Collection childrenCol = childrenById.values();
                defaultChildId = ((Node)(childrenCol.iterator().next())).id;
            }
        }
    }

    //------------------------------------------------------------------------
    // ChildrenByIdContainer methods
    //

    /** Key for state hash map. */
    protected String CURRENT_CHILD;

    /** Returns a key for current child ID state value.
     * It is used in actions.
     */
    public String getCurrentChildKey()
    {
        return CURRENT_CHILD;
    }

    /** Returns current child, depending on Instance. */
    protected NodeSelectable getCurrentChild(InstanceImpl instance)
    {
        // 1. Get children
        // 2. Get actual state from Instance.
        // 3. Get actual child Id from State
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);
        Object childId = instance.getStateValue(contextNode, CURRENT_CHILD);
        if(childId == null)
        {
            childId = defaultChildId;
            instance.setStateValue(contextNode, CURRENT_CHILD, childId);
        }
        return (NodeSelectable)(childrenById.get(childId));
    }
}
