package org.objectledge.forms.internal.ui;

import org.objectledge.forms.internal.model.InstanceImpl;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ReferenceSingleForm.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceSingleForm extends ReferenceSingle
{
    public ReferenceSingleForm(Attributes atts, NodeForm containerNode)
    {
        super(atts, containerNode);
    }

    //------------------------------------------------------------------------
    // ReferenceSingle methods
    public org.dom4j.Node getContextNode(InstanceImpl instance)
    {
        if(instanceReference == null)
        {
            // WARN: No instance reference for this form element -
            // global state storage == document
            return instance.getDocument();
        }
        else
        {
            return super.getContextNode(instance);
        }
    }
}

