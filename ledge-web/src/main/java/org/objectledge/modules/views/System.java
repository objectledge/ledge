package org.objectledge.modules.views;

import org.objectledge.context.Context;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * A silly redirector view.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class System
    extends PolicyProtectedBuilder
{
    /**
     * Creates a System view instance.
     * 
     * @param context the Context component. 
     * @param policySystemArg the PolicySystem component.
     */
    public System(Context context, PolicySystem policySystemArg)
    {
        super(context, policySystemArg);
    }

    /**
     * Route to system.Default view.
     * 
     * @param thisViewName the view name used to locate this builder.
     * @return routed view name.
     */
    @Override
    public String route(String thisViewName)
    {
        return "system.Default";
    }
}
