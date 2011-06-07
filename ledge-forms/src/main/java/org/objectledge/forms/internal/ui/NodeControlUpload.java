package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControlUpload.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeControlUpload extends NodeControl
{
    public NodeControlUpload(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        mediaType = Util.getSAXAttributeVal(atts, "mediaType");
    }

    //------------------------------------------------------------------------
    //attributes

    /** Media type for this upload control. */
    private String mediaType;

    //------------------------------------------------------------------------
    //access methods for attributes

    public String getMediaType()
    {
        return mediaType;
    }
}

