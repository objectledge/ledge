package pl.caltha.forms.internal.util;

// For CSS2
import java.io.IOException;
import java.io.StringReader;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Reads in a style string. $Id: CSSParser.java,v 1.2 2005-02-10 17:49:15 rafal Exp $
 * 
 * @author <a href="mailto:zwierzem@ngo.pl>Damian Gajda</a>
 */
public class CSSParser
{
    /** Parser. */
    private CSSOMParser fParser = null;

    /** Method for parsing short style declarations embedded in a XML document. */
    public CSSStyle parse(String s)
        throws CSSParseException
    {
        CSSStyle cssStyle = null;

        // Check for no style
        if(s == null || s == "")
        {
            throw new CSSParseException("No style defined.");
        }

        InputSource source = new InputSource(new StringReader(s));

        if(fParser == null)
        {
            fParser = new CSSOMParser();
            fParser.setErrorHandler(new ErrorHandlerImpl());
        }

        try
        {
            cssStyle = new CSSStyle(fParser.parseStyleDeclaration(source));
        }
        catch(IOException e)
        {
            throw new CSSParseException("Error in CSS style '" + s + "'", e);
        }
        return cssStyle;
    }

    /** test only */
    static public void main(String[] args)
    {
        CSSParser cssParser = new CSSParser();

        String[] styleDecl = { "background: black; width: 20px; list-ui: checkbox;",
                        "dizasto-d: klac; minko: 20; lig: bank;",
                        "fdfs: 54mm; yui: 20px; bui: listbox;", };

        for(int k = 0; k < styleDecl.length; k++)
        {
            try
            {
                CSSStyle style = cssParser.parse(styleDecl[k]);

                System.out.println();
                System.out.println("Style --- " + k);

                for(int i = 0, j = style.getLength(); i < j; i++)
                {
                    String name = style.getPropertyName(i);

                    System.out.println(name + ": " + style.getProperty(name) + ";");
                }
            }
            catch(CSSParseException e)
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }

    private static class ErrorHandlerImpl
        implements ErrorHandler
    {
        @Override
        public void warning(org.w3c.css.sac.CSSParseException arg0)
            throws CSSException
        {
            // ignore
        }

        @Override
        public void fatalError(org.w3c.css.sac.CSSParseException arg0)
            throws CSSException
        {
            throw arg0;
        }

        @Override
        public void error(org.w3c.css.sac.CSSParseException arg0)
            throws CSSException
        {
            throw arg0;
        }
    }
}
