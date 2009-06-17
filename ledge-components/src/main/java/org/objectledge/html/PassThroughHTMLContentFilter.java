/*
 * Created on 2003-11-12
 */
package org.objectledge.html;

import org.dom4j.Document;

/**
 * A pass through implementation of content filter. 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PassThroughHTMLContentFilter.java,v 1.1 2005-01-12 20:44:39 pablo Exp $
 */
public class PassThroughHTMLContentFilter implements HTMLContentFilter
{
    /* (non-Javadoc)
     * @see net.cyklotron.cms.documents.HTMLContentFilter#filter(org.dom4j.Document)
     */
    public Document filter(Document dom)
    {
        return (Document)(dom.clone());
    }
}
