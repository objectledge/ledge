package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/** Class that represents a selection item in select controls.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControlSelectItem.java,v 1.2 2005-02-08 20:33:30 rafal Exp $
 * @see pl.caltha.forms.internal.ui.NodeControlSelect
 */

public class NodeControlSelectItem extends NodeCaption
{
    /** The only constructor, common to all UI nodes.
     * Takes item's value attribute and adds this item to
     * it's select via
     * {@link pl.caltha.forms.internal.ui.NodeControlSelect#addItemByValue(NodeControlSelectItem)}.
     * @param type element type of this UI node. In this case <code>item</code>.
     * @param atts SAX attributes collection. In this case containing a <code>value</code> attribute.
     * @throws ConstructionException thrown on problems on UI node's construction.
     */
    public NodeControlSelectItem(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        value = Util.getSAXAttributeVal(atts, "value");
    }

    /** Value provided by this item element.
     */
    private String value;
    /** Select element to which this item element belongs.
     * Because of choices element, select is not item's parent,
     * but it is it's ancestor.
     */
    private NodeControlSelect select;

    /** Getter for property value.
     * @return Value of property value.
     */
    public String getValue()
    {
        return value;
    }

    /** This method returns state of this item's selection depending on given
     * Instance.
     * @param instance InstanceImpl of formtool form.
     * @return <code>true</code> if this item is selected.
     */
    public boolean getSelected(InstanceImpl instance)
    {
        return select.getSelected(instance, this);
    }

    //------------------------------------------------------------------------
    // methods used during building
    //
    protected void setParent(UI ui, Node parentNode)
    throws ConstructionException
    {
        super.setParent(ui, parentNode);
        select = getParentSelect();
        select.addItemByValue(this);
    }


    /** Returns select element which is the ancestor of this item.
     * @return select ancestor of this item.
     */
    private NodeControlSelect getParentSelect()
    {
        Node node = parent;
        while(node != null && !(node instanceof NodeControlSelect))
        {
            node = node.parent;
        }
        // what about null
        return (NodeControlSelect)node;
    }
}

