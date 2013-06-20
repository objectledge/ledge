package org.objectledge.web;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.objectledge.web.rewrite.RewriteInfo;
import org.objectledge.web.rewrite.RewriteInfoBuilder;
import org.objectledge.web.rewrite.UrlRewriter;
import org.picocontainer.PicoContainer;

public class LedgeRewriteFilter
    implements Filter
{
    public static final String REWRITER_KEY = "rewriter";

    private final Logger log = Logger.getLogger(LedgeServlet.class);

    private String filterName;

    private UrlRewriter rewriter;

    @Override
    public void init(final FilterConfig filterConfig)
        throws ServletException
    {
        final PicoContainer container = (PicoContainer)filterConfig.getServletContext()
            .getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);

        if(container == null)
        {
            throw new ServletException(LedgeServletContextListener.CONTAINER_CONTEXT_KEY
                + " is missing from SerlvetContext");
        }
        final String rewriterName = filterConfig.getInitParameter(REWRITER_KEY);
        rewriter = (UrlRewriter)container.getComponentInstance(rewriterName != null ? rewriterName
            : UrlRewriter.class);
        if(rewriter == null)
        {
            throw new ServletException("UrlRewriter component "
                + (rewriterName != null ? rewriterName + " " : " ") + "is missing");
        }

        filterName = filterConfig.getFilterName();
        log.info("Filter " + filterName + " initialized.");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
        final FilterChain chain)
        throws IOException, ServletException
    {
        final RewriteInfo original = RewriteInfoBuilder.fromRequest((HttpServletRequest)request)
            .build();
        if(rewriter.matches(original))
        {
            final ServletRequest target = rewriter.rewrite(original).getRequest();
            chain.doFilter(target, response);
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy()
    {
        rewriterRef.lazySet(null);
        log.info("Filter " + filterName + " destroyed.");
    }
}
