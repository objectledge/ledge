package org.objectledge.xmlrpc;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;

/**
 * Abstract base class for Views supporting XmlRpc communication.
 */
public abstract class XmlRpcView
    extends AbstractBuilder
{
    private final XmlRpcServlet xmlRpcServlet;

    private Logger log;

    public XmlRpcView(Context context, Logger log)
        throws ServletException
    {
        super(context);
        this.log = log;
        this.xmlRpcServlet = new CustomXmlRpcServlet();
        xmlRpcServlet.init(new DummyServletConfig());
    }

    @Override
    public final EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }

    @Override
    public final String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        HttpServletRequest request = httpContext.getRequest();
        HttpServletResponse response = httpContext.getResponse();
        try
        {
            if(request.getMethod().equals("POST"))
            {
                xmlRpcServlet.doPost(request, response);
            }
            else
            {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "XmlRpc requires HTTP POST");
            }
        }
        catch(Exception e)
        {
            log.error("XmlRpc server error", e);
        }
        // direct response
        return null;
    }

    /**
     * Returns XmlRpc handler mapping.
     * <p>
     * Must be overridden to define available XmlRpc operations.
     * </p>
     * <p>
     * Typically, {@link POJOHandlerMapping} will be used passing the instance of concrete
     * XmlRpcView's {@code this} reference as the handler object.
     * </p>
     * 
     * @return
     * @throws XmlRpcException
     */
    protected abstract XmlRpcHandlerMapping newXmlRpcHandlerMapping()
        throws XmlRpcException;

    /**
     * Returns the XmlRpc server configuration.
     * <p>
     * May be overridden in order to customize server behavior.
     * </p>
     * 
     * @return XmlRpcServerConfigImpl object.
     */
    protected XmlRpcServerConfigImpl getXmlRpcServerConfig()
    {
        return new XmlRpcServerConfigImpl();
    }

    private class CustomXmlRpcServlet
        extends XmlRpcServlet
    {
        /**
         * serialVersionUID as required by java.io.Serializable
         */
        private static final long serialVersionUID = 215404969958383955L;

        @Override
        protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
            throws XmlRpcException
        {
            return XmlRpcView.this.newXmlRpcHandlerMapping();
        }

        @Override
        protected XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig)
            throws XmlRpcException
        {
            XmlRpcServletServer server = new CustomXmlRpcServletServer();
            server.setConfig(XmlRpcView.this.getXmlRpcServerConfig());
            return server;
        }
    }

    private static class CustomXmlRpcServletServer
        extends XmlRpcServletServer
    {
        @Override
        protected org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl newConfig(
            HttpServletRequest pRequest)
        {
            return new XmlRpcHttpRequestConfigImpl();
        }

        @Override
        protected org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl getConfig(
            HttpServletRequest pRequest)
        {
            XmlRpcHttpRequestConfigImpl requestConfig = (XmlRpcHttpRequestConfigImpl)super
                .getConfig(pRequest);
            requestConfig.setRemoteAddr(pRequest.getRemoteAddr());
            requestConfig.setRemoteHost(pRequest.getRemoteHost());
            requestConfig.setRemotePort(pRequest.getRemotePort());
            requestConfig.setSecure(pRequest.isSecure());
            return (org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl)requestConfig;
        }
    }

    private static class DummyServletConfig
        implements ServletConfig
    {
        @Override
        public String getServletName()
        {
            return null;
        }

        @Override
        public ServletContext getServletContext()
        {
            return null;
        }

        @Override
        public String getInitParameter(String name)
        {
            return null;
        }

        @Override
        public Enumeration<String> getInitParameterNames()
        {
            return Collections.enumeration(Collections.<String>emptyList());
        }
    }
}
