package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;

/**
* Represents a stylable description element. Includes:
 * <ul>
 *      <li><code>caption</code></li>
 *      <li><code>hint</code></li>
 *      <li><code>help</code></li>
 *      <li><code>alert</code></li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: VisibleText.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class VisibleText extends Visible
implements TextNode
{

    public VisibleText(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    /** Text value of node. */
    protected String text;

    //------------------------------------------------------------------------
    //access methods for attributes
    public String getText()
    {
        return text;
    }

    //------------------------------------------------------------------------
    // TextNode methods

    /** Used by the builder. */
    public void setText(String text)
    {
        if(this.text == null) // secure from evil Velocimacros
        {
            this.text = text;
        }
    }
}
