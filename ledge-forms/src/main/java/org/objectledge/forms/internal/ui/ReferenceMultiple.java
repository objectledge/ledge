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
 * @version $Id: ReferenceMultiple.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceMultiple extends Reference
{
    public ReferenceMultiple(Attributes atts, Node containerNode, String instRefName)
    {
        super(atts, containerNode, instRefName);
    }

    public ReferenceMultiple(Attributes atts, Node containerNode)
    {
        // nodeset - InstanceReference for Grouping controls
        this(atts, containerNode, "nodeset");
    }

    //------------------------------------------------------------------------
    // ReferenceMultiple methods
    //
    @SuppressWarnings("unchecked")
    public List<org.dom4j.Node> getContextNodes(InstanceImpl instance)
    {
        return (List<org.dom4j.Node>)getContextNodeInternal(instance);
    }

    protected Object evaluateInstanceReference(org.dom4j.Node contextNode)
    {
        List<org.dom4j.Node> nodes = instanceReference.getNodes(contextNode);
        return nodes;
    }
}

