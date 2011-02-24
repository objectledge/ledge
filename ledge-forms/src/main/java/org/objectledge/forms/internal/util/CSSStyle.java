package org.objectledge.forms.internal.util;

// For CSS2
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

/**
 * Gives access to the CSS properties.
 *
 * @version $Revision: 1.2 $
 * @author <a href="mailto:dgajda@elka.pw.edu.pl>Damian Gajda</a>
 */
public class CSSStyle
{
    private CSSStyleDeclaration fCSSStyle;
    
    public CSSStyle(CSSStyleDeclaration cssStyle)
    {
        fCSSStyle = cssStyle;
    }
    
    /**
     * Returns the number of defined properites.
     * @return 				Number of properties.
     */
    public int getLength()
    {
        if ( fCSSStyle != null )
        {
            return fCSSStyle.getLength();
        }
        return 0;
    }
    
    /**
     * Returns the name of a property.
     * @return 				Number of properties.
     */
    public String getPropertyName(int i)
    {
        if ( fCSSStyle != null )
        {
            return fCSSStyle.item(i);
        }
        return "";
    }
    
    /**
     * Returns the specified property's CSSValue.
     * @param propertyName  The name of the property to return.
     * @return 				The specified property's CSSValue.
     */
    public CSSValue getPropertyCSSValue( String propertyName )
    {
        CSSValue v = null;
        if ( fCSSStyle != null )
        {
            v = fCSSStyle.getPropertyCSSValue( propertyName );
        }
        return v;
    }
    
    /**
     * Returns the specified property's CSSValue.
     * @param propertyName	The name of the property to return.
     * @return 				The specified property's CSSValue.
     */
    public String getProperty( String propertyName )
    {
        return fCSSStyle.getPropertyValue(propertyName);
    }
}
