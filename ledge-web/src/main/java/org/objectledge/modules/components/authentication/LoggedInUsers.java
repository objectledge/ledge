/**
 * 
 */
package org.objectledge.modules.components.authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserTrackingValve;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.components.AbstractComponent;

/**
 * @author fil
 *
 */
public class LoggedInUsers
    extends AbstractComponent
{
    private final UserTrackingValve userTracker;
    private final UserManager userManager;

    /**
     * Creates a LoggedInUsers visual component instance.
     * 
     * @param context the request context.
     * @param userTracker the UserTrackinValve component.
     * @param userManager the UserManagerComponent.
     */
    public LoggedInUsers(Context context, UserTrackingValve userTracker, UserManager userManager)
    {
        super(context);
        this.userTracker = userTracker;
        this.userManager = userManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        try
        {
            Set<Principal> principals = userTracker.getLoggedInUsers();
            Map<Principal,String> login = new HashMap<Principal,String>(principals.size());
            Map<Principal, Parameters> personalData = new HashMap<Principal,Parameters>(
                principals.size());
            Map<Principal,Long> idleTime = new HashMap<Principal,Long>(principals.size());
            Date now = new Date();
            for(Principal p : principals)
            {
                login.put(p, userManager.getLogin(p));
                personalData.put(p, new DirectoryParameters(userManager.getPersonalData(p)));
                idleTime.put(p, now.getTime() - userTracker.getLastClickTime(p).getTime());
            }
            List<Principal> users = new ArrayList<Principal>(principals);
            Collections.sort(users, new Comparator<Principal>() {
                public int compare(Principal o1, Principal o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            templatingContext.put("users", users);
            templatingContext.put("login", login);
            templatingContext.put("personalData", personalData);
            templatingContext.put("idleTime", idleTime);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve user information", e);
        }
    }
}
