package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;

/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ReferenceSingleForm.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ReferenceSingleForm extends ReferenceSingle
{
    public ReferenceSingleForm(Attributes atts, NodeForm containerNode)
    throws ConstructionException
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

