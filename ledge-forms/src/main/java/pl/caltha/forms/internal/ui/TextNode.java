package pl.caltha.forms.internal.ui;


/**
 * Represents a node which definition has text content.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: TextNode.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public interface TextNode
{
    /** Used by the builder. */
    public void setText(String text);
}

