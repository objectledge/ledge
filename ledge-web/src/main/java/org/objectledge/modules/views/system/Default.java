package org.objectledge.modules.views.system;

import org.objectledge.context.Context;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * A class that enforces policy protection for this view.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class Default
    extends PolicyProtectedBuilder
{
    /**
     * Creates a Default view instance.
     * 
     * @param context the Context component. 
     * @param policySystemArg the PolicySystem component.
     */
    public Default(Context context, PolicySystem policySystemArg)
    {
        super(context, policySystemArg);
    }
}
