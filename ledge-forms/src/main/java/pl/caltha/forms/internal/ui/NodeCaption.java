package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;

/** A base implementation for a node of the UI tree structure.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeCaption.java,v 1.2 2005-02-08 20:33:30 rafal Exp $
 */
public class NodeCaption extends Node
{
    /** Basic constuctor, extracts Node properties from SAX Attributes object.
     *
     * @param type name of the defining XML element.
     * @param atts attributes of node's XML definition.
     * @throws ConstructionException Thrown on problems with this Node's construction.
     */
    public NodeCaption(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        visible = new Visible(type, atts);
        desc = new DescriptionCaption();
        actions = new Actions(UIConstants.ACTIONS, new org.xml.sax.helpers.AttributesImpl());
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are left unchanged during and after cloning:</p>
     * <ul>
     *  <li>{@link #desc} - this objects have no Instance references and are shallow copied</li>
     *  <li>{@link #visible} - this objects have no Instance references and are shallow copied</li>
     * </ul>
     * <p>Fields which are deep copied:</p>
     * <ul>
     *  <li>{@link #actions} - some actions may have Instance references</li>
     * </ul>
     */
    protected Object clone()
    {
        NodeCaption next = (NodeCaption)(super.clone());
        // some actions have References so they have to be copied
        next.actions = (Actions)(actions.clone());
        return next;
    }

    //------------------------------------------------------------------------
    // associations
    protected Visible visible;
    protected DescriptionCaption desc;
    /** Actions container - It is used in subclasses that implement ActionNode
     * interface. */
    protected Actions actions;

    //------------------------------------------------------------------------
    // ControlCaption methods

    /** Getter for property visible.
     * @return Value of property visible.
     */
    public Visible getVis()
    {
        return visible;
    }

    /** Getter for property desc.
     * @return Value of property desc.
     */
    public DescriptionCaption getDesc()
    {
        return desc;
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder

    /** Adds a child Node to this Node.
     * @param child A new child Node for this Node.
     * @throws ConstructionException Thrown when this type child Node is not supported.
     */
    protected void addChild(Node child)
    throws ConstructionException
    {
        if(child instanceof VisibleText)
        {
            desc.addChild(child);
        }
        else if(child instanceof Action)
        {
            actions.addChild(child);
        }
        else
        {
            super.addChild(child);
        }
    }

    protected void setParent(UI ui, Node parentNode)
    throws ConstructionException
    {
        super.setParent(ui, parentNode);
        actions.setParent(ui, this);
    }

    /** Inits actions. */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        actions.init(ui);
    }
}

