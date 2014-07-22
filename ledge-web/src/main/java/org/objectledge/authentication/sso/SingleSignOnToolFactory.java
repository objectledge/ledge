package org.objectledge.authentication.sso;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.tools.ContextToolFactory;

public class SingleSignOnToolFactory
    implements ContextToolFactory
{
    public static final String SSO_TOOL_KEY = "ssoTool";

    private final Context context;
    
    private final SingleSignOnService singleSignOnService;

    private final Logger log;

    public SingleSignOnToolFactory(Context context, SingleSignOnService singleSignOnService,
        Logger log)
    {
        this.context = context;
        this.singleSignOnService = singleSignOnService;
        this.log = log;
    }
    
    @Override
    public Object getTool()
        throws ProcessingException
    {
        return new SingleSignOnTool(context, singleSignOnService, log);
    }

    @Override
    public void recycleTool(Object tool)
        throws ProcessingException
    {
        // does nothing
    }

    @Override
    public String getKey()
    {
        return SSO_TOOL_KEY;
    }
}
