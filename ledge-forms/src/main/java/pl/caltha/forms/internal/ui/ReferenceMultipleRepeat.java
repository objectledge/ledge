package pl.caltha.forms.internal.ui;

import java.util.List;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;

/**
 * Implements a node with reference to bind element (model) and/or instance
 * part. Covers  Bind All Attributes and  Bind First Attributes from XForms
 * specification.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ReferenceMultipleRepeat.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceMultipleRepeat extends ReferenceMultiple
{
    public ReferenceMultipleRepeat(Attributes atts, NodeRepeat containerNode)
    throws ConstructionException
    {
        super(atts, containerNode);
    }

    //------------------------------------------------------------------------
    // ReferenceMultiple methods
    //
    protected org.dom4j.Node getParentContextNodeForChild(InstanceImpl instance, Node child)
    {
        List instChildren = ((NodeRepeat)containerNode).getChildren(instance);
        int repeatIndex = ((NodeRepeat)containerNode).getIndex(instance);

        int childIndex = instChildren.indexOf(child);
        if(childIndex == -1)
        {
            return null;
        }
        else
        {
            int index = repeatIndex + instChildren.indexOf(child) - 1;
            return (org.dom4j.Node)(getContextNodes(instance).get(index));
        }
    }
}

