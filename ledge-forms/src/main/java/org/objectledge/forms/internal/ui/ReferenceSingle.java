package org.objectledge.forms.internal.ui;

import org.objectledge.forms.internal.model.InstanceImpl;
import org.xml.sax.Attributes;



/**
 * Implements a node with reference to bind element (model) and/or instance
 * part. Covers  Bind All Attributes and  Bind First Attributes from XForms
 * specification.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ReferenceSingle.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceSingle extends Reference
{
    public ReferenceSingle(Attributes atts, Node containerNode, String instRefName)
    {
        super(atts, containerNode, instRefName);
    }

    public ReferenceSingle(Attributes atts, Node containerNode)
    {
        // ref - InstanceReference
        this(atts, containerNode, "ref");
    }

    //------------------------------------------------------------------------
    // ReferenceSingle methods
    //
    public org.dom4j.Node getContextNode(InstanceImpl instance)
    {
        return (org.dom4j.Node)getContextNodeInternal(instance);
    }

    protected Object evaluateInstanceReference(org.dom4j.Node contextNode)
    {
        return instanceReference.getNode(contextNode);
    }
}

