package pl.caltha.forms.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.util.Util;

/** A base class for a node of the UI tree structure.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Node.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class Node extends Parent
implements Cloneable
{
    /** Basic constuctor, extracts Node properties from SAX Attributes object.
     *
     * @param type name of the defining XML element.
     * @param atts attributes of node's XML definition.
     * @throws ConstructionException Thrown on problems with this Node's construction.
     */
    public Node(String type, Attributes atts)
    throws ConstructionException
    {
        this.type = type;
        this.id = Util.getSAXAttributeVal(atts, "id");
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are left unchanged during and after cloning:</p>
     * <ul>
     *  <li>{@link #type} - it comes directly from XML definition file</li>
     *  <li>{@link #id} - it comes directly from XML definition file</li>
     *  <li>{@link #definitionUiPath} - it comes indirectly from XML definition file</li>
     * </ul>
     * <p>Fields which are deep copied:</p>
     * <ul>
     *  <li>{@link #children}</li>
     * </ul>
     */
    protected Object clone()
    {
        Node next = null;
        // 1. Bitwise object copy
        try
        {
            next = (Node)(super.clone());
        }
        catch(CloneNotSupportedException e)
        {
            // this should never happen
        }
        // 2. Clone children collection
        int childrenSize = children.size();
        // 2.1. Create an array
        next.children = new ArrayList(childrenSize);
        // 2.2. Clone children objects
        for(int i = 0; i < childrenSize; i++)
        {
            Node child = (Node)(children.get(i));
            Node cloneChild = (Node)(child.clone());
            next.children.add(i, cloneChild);
        }
        // 3. Set cloned flag
        next.cloned = true;
        return next;
    }

    //------------------------------------------------------------------------
    //attributes
    /** Information about the way Node object was created. */
    protected boolean cloned = false;
    /** Node's unique XML ID. Should be constrained by XML validation. */
    protected String id;
    /** Node's type - XML element name. */
    protected String type;

    /** UI path for this node - this is a path in memory structure.
     * Internally this value is also being used as a name of a HTML
     * control, because it is unique through all the form. */
    protected String uiPath;

    /** UI path for this node - similar to uiPath, but pointing to
     * node's location in UI's XML definition file.
     * Unlike uiPath it can be non unique because repeat elements make
     * copies of it's subtrees.
     */
    protected String definitionUiPath;

    //------------------------------------------------------------------------
    // associations
    /** Node's children. */
    protected List children = new ArrayList();
    /** Node's parent node. */
    protected Node parent;
    /** Node's parent NodeRepeatSubTree node - the root of this node's repeat subtree. */
    protected NodeRepeatSubTree repeatSubTreeRoot;

    //------------------------------------------------------------------------
    // Node methods

    /** Getter for XML ID of this Node.
     * @return XML ID of this Node.
     */
    public String getId()
    {
        return id;
    }
    /** Getter for XML element name of this Node - it's type.
     * @return Type of this Node - XML element name.
     */
    public String getType()
    {
        return type;
    }
    /** Getter for HTML name of this node - it's UIPath, which is unique through
     * a whole UI structure.
     * @return HTML name of this Node.
     */
    public String getName()
    {
        return this.uiPath;
    }

    /** Getter for List of this Node's children Nodes.
     * @return List of this Node's children Nodes.
     */
    public List getChildren()
    {
        return children;
    }

    /** Returns parent node of this node, can be <code>null</code>. */
    public Node getParent()
    {
        return parent;
    }

    /** Returns repeat subtree element which is the ancestor of this node.
     * @return repeat subtree ancestor of this node.
     */
    public NodeRepeatSubTree getParentRepeatSubTree()
    {
        return repeatSubTreeRoot;
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder in the same sequence they are called

    /** Adds a child Node to this Node.
     * @param child next child Node for this Node.
     * @throws ConstructionException Thrown when this type of child Node is not supported.
     */
    protected void addChild(Node child)
    throws ConstructionException
    {
        if (!children.contains(child))
        {
            children.add(child);
        }
    }

    /** Sets a parent node for this node.
     * Calculates UI path and definition UI path.
     * Adds nodes to special UI collections, like nodes by ID etc.
     */
    protected void setParent(UI ui, Node parentNode)
    throws ConstructionException
    {
        parent = parentNode;

        // path
        if(parent != null)
        {
            uiPath = parent.uiPath+"."+type+"-"+parent.children.indexOf(this);
        }
        else
        {
            uiPath = type;
        }
        // UI checks if uiPath => node mapping is unique
        ui.addNodeByUIPath(this);

        // definitnion UI path and ID
        if(definitionUiPath == null)
        {
            // WARN: node wasn't created using clone operation
            definitionUiPath = uiPath;
        }

        // to distinguish repeated (copied) elements we use definitionUiPath
        ui.addNodeById(this);

        // attach this RepeatSubTree's root - it can be null
        Node node = parent;
        while(node != null && !(node instanceof NodeRepeatSubTree))
        {
            node = node.parent;
        }
        repeatSubTreeRoot = (NodeRepeatSubTree)node;

        // index this node in a subtree
        if(repeatSubTreeRoot != null)
        {
            repeatSubTreeRoot.addNodeById(this);
        }
    }

    /** Inits a Node.
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
    }
}
