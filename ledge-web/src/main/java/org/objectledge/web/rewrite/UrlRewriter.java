package org.objectledge.web.rewrite;

public interface UrlRewriter
{
    boolean matches(RewriteInfo request);

    RewriteInfo rewrite(RewriteInfo request);
}
