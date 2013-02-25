package org.objectledge.web.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.objectledge.authentication.api.ServerApiRestrictionProvider;
import org.objectledge.authentication.api.ServerApiRestrictions;
import org.objectledge.authentication.api.ServerApiRestrictions.AutorizationStatus;
import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

@ApiRestrictions
public class JerseyRestAuthenticationFilter
    implements ContainerRequestFilter
{
    @Inject
    private ServerApiRestrictionProvider provider;

    @Override
    public void filter(ContainerRequestContext requestContext)
        throws IOException
    {
        AutorizationStatus response = AutorizationStatus.UNDEFINED;

        HttpContext httpContext = new Context().getAttribute(HttpContext.class);
        ServerApiRestrictions serverApiRestrictions = provider.getServerApiRestrictions();
        response = serverApiRestrictions.validateApiRequest(requestContext.getUriInfo().getPath(),
            requestContext.getMethod(), requestContext.getHeaderString("Authorization"),
            httpContext.getRequest().getRemoteAddr(), requestContext.getSecurityContext()
                .isSecure());

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
