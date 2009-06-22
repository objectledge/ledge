package pl.caltha.forms.internal.ui.actions;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.model.InstanceReference;
import pl.caltha.forms.internal.ui.ActionEvent;
import pl.caltha.forms.internal.ui.Node;
import pl.caltha.forms.internal.ui.ReferenceSingle;
import pl.caltha.forms.internal.ui.TextNode;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.util.Util;

/** Action that set a value in Instance.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: SetValue.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public class SetValue extends BaseReferenceAction
implements TextNode
{
    public SetValue(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // where to put the value
        ref = new ReferenceSingle(atts, (Node)this);
        // from where to get it
        String xPath = Util.getSAXAttributeVal(atts, "value");
        if(xPath != null)
        {
            instanceReference = new InstanceReference("value", xPath);
        }
    }

    //------------------------------------------------------------------------
    //attributes
    private InstanceReference instanceReference;
    private String fixedValue;

    //------------------------------------------------------------------------
    // Action methods

    /** This method performs the action, ie. sets a value in Instance.
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        org.dom4j.Node contextNode = ((ReferenceSingle)ref).getContextNode(instance);

        String value;
        if(instanceReference != null)
        {
            value = instanceReference.getStringValue(instance);
        }
        else
        {
            value = fixedValue;
        }
        // set the value and mark instance dirty
        ref.setValue(contextNode, value);
        instance.setDirty(true);
    }

    //------------------------------------------------------------------------
    // methods used during building

    // TextNode methods

    /** Used by the builder. */
    public void setText(String text)
    {
        if(fixedValue == null) // secure from evil Velocimacros
        {
            fixedValue = text;
        }
    }
}
