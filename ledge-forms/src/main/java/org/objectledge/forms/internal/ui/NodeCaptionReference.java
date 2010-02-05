package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.xml.sax.Attributes;


/** A base implementation for a node of the UI tree structure.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeCaptionReference.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeCaptionReference extends NodeCaption
implements ReferenceNode
{
    /** Basic constuctor, extracts Node properties from SAX Attributes object.
     *
     * @param type name of the defining XML element.
     * @param atts attributes of node's XML definition.
     * @throws ConstructionException Thrown on problems with this Node's construction.
     */
    public NodeCaptionReference(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        ref = new ReferenceSingle(atts, this);
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are deep copied:</p>
     * <ul>
     *  <li>{@link #ref} - ref is also set a new container Node, see
     *  {@link Reference#clone()}</li>
     * </ul>
     */
    protected Object clone()
    {
        NodeCaptionReference next = (NodeCaptionReference)(super.clone());
        // References must be copied
        next.ref = (Reference)(ref.clone());
        // set a new container node
        next.ref.setContainerNode(next);
        return next;
    }

    //------------------------------------------------------------------------
    //attributes
    protected Reference ref;

    //------------------------------------------------------------------------
    // ReferenceNode methods
    public Reference getRef()
    {
        return ref;
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder

    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        ref.init(ui);
    }
}

