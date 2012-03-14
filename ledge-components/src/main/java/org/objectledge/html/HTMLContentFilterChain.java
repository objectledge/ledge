package org.objectledge.html;

import org.dom4j.Document;
import org.objectledge.pipeline.ProcessingException;

public class HTMLContentFilterChain
    implements HTMLContentFilter
{
    private final HTMLContentFilter[] contentFilters;

    public HTMLContentFilterChain(HTMLContentFilter ... contentFilters)
    {
        this.contentFilters = contentFilters;
    }

    @Override
    public Document filter(Document dom)
        throws ProcessingException
    {
        for(HTMLContentFilter contentFilter : contentFilters)
        {
            dom = contentFilter.filter(dom);
        }
        return dom;
    }
}
