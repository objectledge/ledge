package org.objectledge.modules.actions.logging;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.builders.PolicyProtectedAction;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * Removes an appender from a logger.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class DeleteAppender
    extends PolicyProtectedAction
{
    /**
     * Creates an new instance of DeleteAppender action.
     * 
     * @param policySystemArg the PolicySystem component.
     */
    public DeleteAppender(PolicySystem policySystemArg)
    {
        super(policySystemArg);
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        RequestParameters parameters = RequestParameters.getRequestParameters(context);
        String id = parameters.get("id");
        Logger logger;
        if(id.equals("root"))
        {
            logger = LogManager.getRootLogger();
        }
        else
        {
            if(LogManager.exists(id) == null)
            {
                throw new ProcessingException("invalid logger id "+id);
            }
            logger = LogManager.getLogger(id);
        }
        String appenderName = parameters.get("appender");
        Appender appender = logger.getAppender(appenderName);
        if(appender != null)
        {
            logger.removeAppender(appender);
        }
        else
        {
            throw new ProcessingException("invalid appender name "+appenderName);
        }
    }
}
