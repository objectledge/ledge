package pl.caltha.forms.internal.util;

// For CSS2
import java.io.StringReader;

import com.steadystate.css.CSS2Parser;
import com.steadystate.css.ParseException;

/**
 * Reads in a style string.
 *
 * $Id: CSSParser.java,v 1.2 2005-02-10 17:49:15 rafal Exp $
 * @author <a href="mailto:zwierzem@ngo.pl>Damian Gajda</a>
 */
public class CSSParser
{
    /** Parser. */
    private CSS2Parser fParser = null;

    /** Method for parsing short style declarations embedded in a XML document. */
    public CSSStyle parse(String s)
    throws CSSParseException
    {
        CSSStyle cssStyle = null;

        // Check for no style
        if ( s == null || s == "" )
        {
            throw new CSSParseException("No style defined.");
        }

        StringReader reader = new StringReader("{" + s + "}");

        if (fParser == null)
        {
            fParser = new CSS2Parser(reader);
        }
        else
        {
            fParser.ReInit(reader);
        }

        try
        {
            cssStyle = new CSSStyle(fParser.styleDeclaration());
        }
        catch (ParseException pe)
        {
            throw new CSSParseException("Error in CSS style '"+s+"'", pe);
        }
        return cssStyle;
    }

    /** test only */
    static public void main( String[] args )
    {
        CSSParser cssParser = new CSSParser();

        String[] styleDecl =
        { "background: black; width: 20px; list-ui: checkbox;",
          "dizasto-d: klac; minko: 20; lig: bank;",
          "fdfs: 54mm; yui: 20px; bui: listbox;",
        };

        for(int k=0; k<styleDecl.length; k++)
        {
            try
            {
                CSSStyle style = cssParser.parse( styleDecl[k] );

                System.out.println();
                System.out.println("Style --- "+k);

                for(int i=0, j=style.getLength(); i<j; i++)
                {
                    String name = style.getPropertyName(i);

                    System.out.println(name+": "+style.getProperty(name)+";");
                }
            }
            catch (CSSParseException e)
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }
}
