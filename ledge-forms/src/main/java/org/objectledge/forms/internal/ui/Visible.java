package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.util.CSSParseException;
import org.objectledge.forms.internal.util.CSSParser;
import org.objectledge.forms.internal.util.CSSStyle;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Implements a node with visibility attributes - Common UI Attributes.
 *
 * @see org.objectledge.forms.internal.ui.Control
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Visible.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class Visible extends Node
{
    public Visible(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // Attributes
        // Common UI Attributes
        cssClass = Util.getSAXAttributeVal(atts, "class");
        cssStyle = Util.getSAXAttributeVal(atts, "style");
        if(cssStyle != null)
        {
            try
            {
                CSSParser cssParser = new CSSParser();
                cssStyleObject = cssParser.parse(cssStyle);
            }
            catch(CSSParseException e)
            {
                throw new ConstructionException("Invalid 'style' attribute", e);
            }
        }
        accessKey = Util.getSAXAttributeVal(atts, "accessKey");
        navigationIndex = Util.getSAXAttributeVal(atts, "navIndex");
    }

    //-----------------------------------------------------------------------
    // attributes

    /** Element's cssClass. */
    protected String cssClass;
    /** Element's css style. */
    protected String cssStyle;
    /** Element's css style as an object. */
    protected CSSStyle cssStyleObject;
    /** Element's accessKey. */
    protected String accessKey;
    /** Element's navigation index. */
    protected String navigationIndex;

    //-----------------------------------------------------------------------
    // access methods for attributes

    public String getCSSStyle()
    {
        return cssStyle;
    }

    public CSSStyle getStyle()
    {
        return cssStyleObject;
    }

    public String getCSSClass()
    {
        return cssClass;
    }

    public String getAccessKey()
    {
        return accessKey;
    }

    public String getNavIndex()
    {
        return navigationIndex;
    }
}

