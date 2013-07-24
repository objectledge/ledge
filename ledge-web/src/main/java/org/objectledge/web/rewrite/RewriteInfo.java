package org.objectledge.web.rewrite;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface RewriteInfo
{
    HttpServletRequest getRequest();

    String getServletPath();

    String getPathInfo();

    /**
     * Query string represented as list of String pairs.
     * 
     * @return
     */
    List<String[]> getQueryParameters();

    /**
     * Request parameters represented as a multi-map.
     * 
     * @return
     */
    Map<String, List<String>> getParameters();
}
