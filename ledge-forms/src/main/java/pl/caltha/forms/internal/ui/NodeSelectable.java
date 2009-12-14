package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.util.Util;

/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeSelectable.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeSelectable extends NodeCaptionReference
{
    public NodeSelectable(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        selected = Util.createBooleanAttribute(atts, "selected", false);
    }

    private boolean selected;

    /** Getter for property selected.
     * @return Value of property selected.
     */
    boolean getSelected()
    {
        return selected;
    }
}
