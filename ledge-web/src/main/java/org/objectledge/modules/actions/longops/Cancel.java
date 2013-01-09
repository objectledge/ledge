package org.objectledge.modules.actions.longops;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

public class Cancel
    implements Valve
{
    private final LongRunningOperationRegistry registry;

    public Cancel(LongRunningOperationRegistry registry)
    {
        this.registry = registry;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        Parameters parameters = context.getAttribute(RequestParameters.class);
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        String id = parameters.get("id");
        final Principal requestor = authContext.getUserPrincipal();
        try
        {
            try
            {
                LongRunningOperation op = registry.getOperation(id, requestor);
                registry.cancel(op, requestor);
            }
            catch(IllegalArgumentException e)
            {
                httpContext.getResponse().sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "invalid operation identifier");
            }
            catch(SecurityException e)
            {
                httpContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN,
                    "operation not permitted");
            }
        }
        catch(IOException e)
        {
            throw new ProcessingException("failed to send error reponse to client", e);
        }
    }
}
