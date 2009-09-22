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
 * @version $Id: ReferenceMultiple.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceMultiple extends Reference
{
    public ReferenceMultiple(Attributes atts, Node containerNode, String instRefName)
    throws ConstructionException
    {
        super(atts, containerNode, instRefName);
    }

    public ReferenceMultiple(Attributes atts, Node containerNode)
    throws ConstructionException
    {
        // nodeset - InstanceReference for Grouping controls
        this(atts, containerNode, "nodeset");
    }

    //------------------------------------------------------------------------
    // ReferenceMultiple methods
    //
    public List getContextNodes(InstanceImpl instance)
    {
        return (List)getContextNodeInternal(instance);
    }

    protected Object evaluateInstanceReference(org.dom4j.Node contextNode)
    {
        List nodes = instanceReference.getNodes(contextNode);
        return nodes;
    }
}

