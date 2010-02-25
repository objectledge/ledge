package org.objectledge.forms.internal.ui;

import java.util.List;

import org.objectledge.forms.internal.model.InstanceImpl;
import org.xml.sax.Attributes;


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
    {
        super(atts, containerNode);
    }

    //------------------------------------------------------------------------
    // ReferenceMultiple methods
    //
    protected org.dom4j.Node getParentContextNodeForChild(InstanceImpl instance, Node child)
    {
        List<Node> instChildren = ((NodeRepeat)containerNode).getChildren(instance);
        int repeatIndex = ((NodeRepeat)containerNode).getIndex(instance);

        int childIndex = instChildren.indexOf(child);
        if(childIndex == -1)
        {
            return null;
        }
        else
        {
            int index = repeatIndex + instChildren.indexOf(child) - 1;
            return getContextNodes(instance).get(index);
        }
    }
}

