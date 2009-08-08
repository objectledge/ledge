package org.objectledge.html;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Text;
import org.dom4j.VisitorSupport;

/**
 * Class for collecting text from HTML parsed into DOM4J Document.
 * It is used for indexing the CMS documents.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLTextCollectorVisitor.java,v 1.3 2005-02-21 16:28:56 zwierzem Exp $
 */
public class HTMLTextCollectorVisitor extends VisitorSupport
{
    private StringBuilder buf = new StringBuilder(8129);

    /** Returns the collected text. */
    public String getText()
    {
        return buf.toString();
    }

    public void visit(Element node)
    {
        String name = node.getName();
        if(HTMLTagTypes.isSpaceAddTag(name))
        {
            buf.append(' ');
        }
    }

    public void visit(Attribute node)
    {
        String name = node.getName();
        if(HTMLTagTypes.isTextAttribute(name))
        {
            buf.append(' ');
            buf.append(node.getText());
            buf.append(' ');
        }
    }

    public void visit(Text node)
    {
        buf.append(node.getText());
    }

    public void visit(CDATA node)
    {
        buf.append(node.getText());
    }

    public void visit(Comment node)
    {
        // ignore
    }

    public void visit(Entity node)
    {
        // ignore
    }
}
