package org.objectledge.web.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.objectledge.authentication.api.ServerApiRestrictionsProvider;
import org.objectledge.authentication.api.ServerApiRestrictions;
import org.objectledge.authentication.api.ServerApiRestrictions.AutorizationStatus;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class JerseyRestAuthenticationFilter
    implements ContainerRequestFilter
{
    private final ServerApiRestrictionsProvider[] providers;

    private final Context context;

    public JerseyRestAuthenticationFilter(ServerApiRestrictionsProvider[] providers,
        Context context)
    {
        this.providers = providers;
        this.context = context;
    }

    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        AutorizationStatus response = AutorizationStatus.UNDEFINED;
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        for(ServerApiRestrictionsProvider provider : providers)
        {
            ServerApiRestrictions serverApiRestrictions = provider.getServerApiRestrictions();
            if(serverApiRestrictions == null)
                continue;

            response = serverApiRestrictions.validateApiRequest(containerRequest.getPath(),
                containerRequest.getMethod(), containerRequest.getHeaderValue("Authorization"),
                httpContext.getRequest().getRemoteAddr(), containerRequest.isSecure());

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
}
