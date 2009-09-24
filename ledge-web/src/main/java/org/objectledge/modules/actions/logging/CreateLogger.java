package org.objectledge.modules.actions.logging;

import org.apache.log4j.LogManager;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.builders.PolicyProtectedAction;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * Creates a Log4j logger with the specified name.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class CreateLogger
    extends PolicyProtectedAction
{
    /**
     * Creates a new CreateLogger action instance.
     * 
     * @param policySystemArg the PolicySystem component.
     */
    public CreateLogger(PolicySystem policySystemArg)
    {
        super(policySystemArg);
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        RequestParameters requestParameters = RequestParameters.getRequestParameters(context);
        String id = requestParameters.get("id");
        if(id.equals("root") || LogManager.exists(id) != null)
        {
            throw new ProcessingException("logger "+id+" already exists");
        }
        LogManager.getLogger(id);
    }
}
