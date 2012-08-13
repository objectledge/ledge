package org.objectledge.web.rest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.objectledge.web.HttpDispatcher;
import org.objectledge.web.LedgeServlet;

public class RestServlet extends LedgeServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig servletConfig) throws ServletException
    {
        Logger log = Logger.getLogger(LedgeServlet.class);
    	configure(servletConfig); //throws ServletException
    	
        dispatcher = (HttpDispatcher)container.getContainer().
            getComponentInstance("restDispatcher");
        if(dispatcher == null)
        {
            log.error("restDispatcher dispatcher component is missing");
            throw new ServletException("restDispatcher dispatcher component is missing");
        }
    }

}
