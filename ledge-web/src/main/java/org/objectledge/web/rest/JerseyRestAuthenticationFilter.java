package org.objectledge.web.rest;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.objectledge.authentication.sso.ServerApiRestrictionProvider;
import org.objectledge.authentication.sso.ServerApiRestrictions;
import org.objectledge.authentication.sso.ServerApiRestrictions.Status;
import org.objectledge.web.LedgeServletContextListener;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class JerseyRestAuthenticationFilter
    implements ContainerRequestFilter
{
    @javax.ws.rs.core.Context
    private ServletContext context;

    @javax.ws.rs.core.Context
    private HttpServletRequest httpServletRequest;

    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        final List<ServerApiRestrictionProvider> jerseyFileProviders = getJerseyFileProviders();
        Status status = Status.UNDEFINED;

        for(ServerApiRestrictionProvider provider : jerseyFileProviders)
        {
            ServerApiRestrictions serverApiRestrictions = provider.getServerApiRestrictions();
            if(serverApiRestrictions == null)
                continue;

            status = serverApiRestrictions.validateApiRequest(containerRequest.getPath(),
                containerRequest.getMethod(), containerRequest.getHeaderValue("Authorization"),
                httpServletRequest.getRemoteAddr(), containerRequest.isSecure());

            if(!Status.UNDEFINED.equals(status))
                break;
        }
        if(!Status.AUTHORIZED.equals(status))
        {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        
        return containerRequest;
    }

    private List<ServerApiRestrictionProvider> getJerseyFileProviders()
    {
        final PicoContainer container = (PicoContainer)context
            .getAttribute(LedgeServletContextListener.CONTAINER_CONTEXT_KEY);
        final List<ServerApiRestrictionProvider> providers = container
            .getComponentInstancesOfType(ServerApiRestrictionProvider.class);
        return providers;
    }
}
