package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;

/**
 * Base Control implementation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControl.java,v 1.2 2005-03-24 14:27:41 zwierzem Exp $
 */
public class NodeControl extends NodeCaptionReference
implements Control
{
    public NodeControl(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // has all description elements
        desc = new DescriptionAll();
    }

    //------------------------------------------------------------------------
    // Control methods
    //
    /**
     * Grabs context node and returns value, stores cached
     * value and context node in NodeState.
     */
    public Object getValue(InstanceImpl instance)
    {
        // 1. Get contextNode.
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);

        // 2.2. Get value.
        return ref.getValue(contextNode);
    }

    public boolean hasValue(InstanceImpl instance)
    {
        Object value = getValue(instance);
        boolean hasValue = (value != null);
        if(hasValue && value instanceof String)
        {
            return ((String)value).length() > 0; // disallow empty strings 
        }
        return hasValue;
    }

    public boolean hasError(InstanceImpl instance)
    {
        //FIXME: Add Bind.required checking (??)
        return instance.hasError(((ReferenceSingle)ref).getContextNode(instance));
    }

    void setValue(InstanceImpl instance, String value)
    {
        // 1. Get contextNode.
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);

        // 2. Set value.
        ref.setValue(contextNode, value);
    }

    //------------------------------------------------------------------------
    // ActionNode methods

    /** Passes an event to this UI node (control). */
    public void dispatchEvent(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        actions.execute(ui, instance, evt);
    }

    /** Returns <code>true</code> if this action node has a binded action. */
    public boolean hasAction()
    {
        return (actions.getChildren().size() > 0);
    }
}
