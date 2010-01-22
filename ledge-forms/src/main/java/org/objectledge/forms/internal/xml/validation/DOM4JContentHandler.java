package org.objectledge.forms.internal.xml.validation;

/**
 * Interface for reporting DOM4J tree Nodes when traversing the tree.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DOM4JContentHandler.java,v 1.1 2005-01-20 16:44:51 pablo Exp $
 */
public interface DOM4JContentHandler
{
    /** Reports a start of an element node. */
    void startElementNode(org.dom4j.Element node);
    /** Reports an end of en element node. */
    void endElementNode(org.dom4j.Element node);    
    /** Reports a current node which is not an Element node. */
    void setCurrentNode(org.dom4j.Node node);
}
