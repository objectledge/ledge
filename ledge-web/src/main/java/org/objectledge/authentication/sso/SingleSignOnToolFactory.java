package org.objectledge.authentication.sso;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.tools.ContextToolFactory;
import org.objectledge.web.HttpContext;

public class SingleSignOnToolFactory
    implements ContextToolFactory
{
    public static final String SSO_TOOL_KEY = "ssoTool";

    private final Context context;
    
    private final SingleSignOnService singleSignOnService;

    public SingleSignOnToolFactory(Context context, SingleSignOnService singleSignOnService)
    {
        this.context = context;
        this.singleSignOnService = singleSignOnService;        
    }
    
    @Override
    public Object getTool()
        throws ProcessingException
    {
        return new SingleSignOnTool(context, singleSignOnService);
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
