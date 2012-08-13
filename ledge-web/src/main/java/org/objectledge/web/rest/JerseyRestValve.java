package org.objectledge.web.rest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.core.Context;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.container.servlet.WebServletConfig;


public class JerseyRestValve implements RestProcessor, Valve {	
    
    private LedgeServletContainer jerseyContainer;	
    
    Logger logger;

	private String restPackageNames;

    
    /**
     * Creates a new jersey REST dispatcher.
     * 
     * @param pipeline the pipeline
     * @param context the thread context
     * @throws ConfigurationException if the configuration is malformed.
     * @throws ServletException 
     */
	public JerseyRestValve(Logger logger, Configuration config) throws ConfigurationException, ServletException {
       this.logger = logger;
       restPackageNames = config.getChild("restPackageNames").getValue();  
	}

	@Override
	public void process(Context context) throws ProcessingException {
		HttpContext httpContext = (HttpContext)context.getAttribute(HttpContext.class);
	    HttpServletRequest request = httpContext.getRequest();
 	    HttpServletResponse response = httpContext.getResponse(); 	    
 	    ServletConfig svconfig = (ServletConfig)context.getAttribute(ServletConfig.class);
 	    
        try {
            PackagesResourceConfig app = new PackagesResourceConfig(restPackageNames);
            jerseyContainer = new LedgeServletContainer(app);
            jerseyContainer.setServletConfig(svconfig);
            jerseyContainer.init();               	
        	jerseyContainer.service(request, response);
        	httpContext.setDirectResponse(true);
        	
		} catch (ServletException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

}
