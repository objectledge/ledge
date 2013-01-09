package org.objectledge.modules.views.longops;

import static org.objectledge.longops.LongRunningOperationOrdering.sortOperations;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.json.AbstractJsonView;

import com.fasterxml.jackson.databind.JsonNode;

public class ActiveOperations
    extends AbstractJsonView
{
    private final LongRunningOperationRegistry registry;

    private final UserManager userManager;

    public ActiveOperations(LongRunningOperationRegistry registry, UserManager userManager,
        Context context, Logger log)
    {
        super(context, log);
        this.registry = registry;
        this.userManager = userManager;
    }

    @Override
    protected JsonNode buildJsonTree()
        throws ProcessingException
    {
        Parameters parameters = context.getAttribute(RequestParameters.class);
        String uid = parameters.get("uid", null);
        String code = parameters.get("code", null);
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        Principal requestor = authContext.getUserPrincipal();
        Collection<LongRunningOperation> activeOperations;
        try
        {
            if(uid != null)
            {
                Principal user = userManager.getUserByLogin(uid);
                if(code != null)
                {
                    activeOperations = registry.getActiveOperations(code, user, requestor);
                }
                else
                {
                    activeOperations = registry.getActiveOperations(user, requestor);
                }
            }
            else
            {
                if(code != null)
                {
                    activeOperations = registry.getActiveOperations(code, requestor);
                }
                else
                {
                    activeOperations = registry.getActiveOperations(requestor);
                }
            }
            return objectMapper.valueToTree(sortOperations(activeOperations));
        }
        catch(IllegalArgumentException | AuthenticationException e)
        {
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            try
            {
                httpContext.getResponse().sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());
            }
            catch(IOException e1)
            {
                log.error("failed to send error response to client", e);
            }
            throw new ProcessingException(e);
        }
    }
}
