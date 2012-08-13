package org.objectledge.web.rest;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

public class LedgeServletContainer extends ServletContainer  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	private ServletContext context;

	public LedgeServletContainer(Application app) {
		super(app);
	}
	
    public ServletConfig getServletConfig() {
    	return config;
    }
	
	public void setServletConfig(ServletConfig sc) {
		config = sc;		
	}
	
	public void setServletContext(ServletContext sc) {
		context = sc;		
	}	


//	@Override
//	public void init() throws ServletException {
//		this.init(new WebConfig() {    	   
//	   		@Override
//            public String getName() {
//                return LedgeServletContainer.this.getServletName();
//            }
//
//            @Override
//            public String getInitParameter(String name) {
//                return LedgeServletContainer.this.getInitParameter(name);
//            }
//
//            @Override
//            public Enumeration getInitParameterNames() {
//                return LedgeServletContainer.this.getInitParameterNames();
//            }
//
//            @Override
//            public ServletContext getServletContext() {
//                return LedgeServletContainer.this.getServletContext();
//            }
//
//			@Override
//			public ConfigType getConfigType() {
//				return ConfigType.ServletConfig;
//			}
//
//			@Override
//			public ResourceConfig getDefaultResourceConfig(
//					Map<String, Object> props) throws ServletException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//       });
//	}
}
