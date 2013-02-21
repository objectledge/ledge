package org.objectledge.web.rest;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.objectledge.authentication.api.ServerApiRestrictionProvider;
import org.objectledge.authentication.api.ServerApiRestrictions;
import org.objectledge.authentication.api.ServerApiRestrictions.AutorizationStatus;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;


public class JerseyRestAuthenticationFilter
    implements ContainerRequestFilter
{
    private final ServerApiRestrictionProvider[] providers;

    private final Context context;

    public JerseyRestAuthenticationFilter(ServerApiRestrictionProvider[] providers,
        Context context)
    {
        this.providers = providers;
        this.context = context;
    }

    @Override
    public void filter(ContainerRequestContext requestContext)
        throws IOException
    {
        AutorizationStatus response = AutorizationStatus.UNDEFINED;
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        for(ServerApiRestrictionProvider provider : providers)
        {
            ServerApiRestrictions serverApiRestrictions = provider.getServerApiRestrictions();
            if(serverApiRestrictions == null)
                continue;

            response = serverApiRestrictions.validateApiRequest(requestContext.getUriInfo()
                .getPath(), requestContext.getMethod(), requestContext
                .getHeaderString("Authorization"), httpContext.getRequest().getRemoteAddr(),
                requestContext.getSecurityContext().isSecure());

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
    }
}
