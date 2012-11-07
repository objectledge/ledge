package org.objectledge.web.rest;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.objectledge.authentication.ServerApiRestrictionProvider;
import org.objectledge.authentication.ServerApiRestrictions;
import org.objectledge.authentication.ServerApiRestrictions.AutorizationStatus;
import org.objectledge.web.LedgeServletContextListener;
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

        AutorizationStatus response = AutorizationStatus.UNDEFINED;
        for(ServerApiRestrictionProvider provider : jerseyFileProviders)
        {
            ServerApiRestrictions serverApiRestrictions = provider.getServerApiRestrictions();
            if(serverApiRestrictions == null)
                continue;

            response = serverApiRestrictions.validateApiRequest(containerRequest.getPath(),
                containerRequest.getMethod(), containerRequest.getHeaderValue("Authorization"),
                httpServletRequest.getRemoteAddr(), containerRequest.isSecure());

            if(!AutorizationStatus.UNDEFINED.equals(response))
                break;
        }
        if(AutorizationStatus.UNAUTHORIZED.equals(response))
        {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        else if(AutorizationStatus.UNDEFINED.equals(response))
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
