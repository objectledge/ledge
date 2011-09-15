package org.objectledge.html;

import org.dom4j.Document;

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
    {
        for(HTMLContentFilter contentFilter : contentFilters)
        {
            dom = contentFilter.filter(dom);
        }
        return dom;
    }
}
