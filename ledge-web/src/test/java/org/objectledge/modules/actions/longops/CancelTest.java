package org.objectledge.modules.actions.longops;

import static org.objectledge.longops.matchers.LongRunningOperationCodeMatcher.opCodeMatching;
import static org.objectledge.longops.matchers.LongRunningOperationEventMatcher.event;

import java.io.IOException;
import java.security.Principal;
import java.util.EnumSet;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.core.StringContains;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.DummyUserManager;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationEvent;
import org.objectledge.longops.LongRunningOperationListener;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.LongRunningOperationSecurityCallback;
import org.objectledge.longops.impl.LongRunningOperationRegistryImpl;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

public class CancelTest
    extends MockObjectTestCase
{

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private LongRunningOperationSecurityCallback securityCallback;

    @Mock
    private LongRunningOperationListener listener;

    private final LongRunningOperationRegistry registry = new LongRunningOperationRegistryImpl();

    private final Context context = new Context();

    private final Cancel action = new Cancel(registry);

    private final HttpContext httpContext = new HttpContext(servletRequest, servletResponse);

    private final AuthenticationContext authContext = new AuthenticationContext(null, false);

    private UserManager userManager = new DummyUserManager();

    @Override
    public void setUp()
    {
        context.setAttribute(HttpContext.class, httpContext);
        context.setAttribute(AuthenticationContext.class, authContext);
        checking(new Expectations()
            {
                {
                    oneOf(servletRequest).getParameterNames();
                    will(returnValue(new Enumeration<String>()
                        {
                            @Override
                            public boolean hasMoreElements()
                            {
                                return false;
                            }

                            @Override
                            public String nextElement()
                            {
                                return null;
                            }
                        }));
                    oneOf(servletRequest).getPathInfo();
                    will(returnValue(null));
                }
            });
    }

    protected void invoke(final String queryString)
        throws ProcessingException
    {
        checking(new Expectations()
            {
                {
                    oneOf(servletRequest).getQueryString();
                    will(returnValue(queryString));
                }
            });
        context.setAttribute(RequestParameters.class, new RequestParameters(servletRequest));
        action.process(context);
    }

    private void expectError(final int code, final String messageSubstring)
    {
        checking(new Expectations()
            {
                {
                    try
                    {
                        oneOf(servletResponse).sendError(with(equal(code)),
                            with(StringContains.containsString(messageSubstring)));
                        allowing(servletResponse).isCommitted();
                        will(returnValue(true));
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
    }

    public void testCancel()
        throws Exception
    {
        final Principal user = userManager.getUserByLogin("user");
        authContext.setUserPrincipal(user, false);
        LongRunningOperation op = registry.register("op", null, null, 3, securityCallback);
        registry.addListener(listener, EnumSet.of(LongRunningOperationEvent.Type.CANCELLED), "");
        checking(new Expectations()
            {
                {
                    oneOf(securityCallback).canView(with(any(LongRunningOperation.class)),
                        with(same(user)));
                    will(returnValue(true));
                    oneOf(securityCallback).canCancel(with(any(LongRunningOperation.class)),
                        with(same(user)));
                    will(returnValue(true));
                    oneOf(listener)
                        .receive(
                            with(event(LongRunningOperationEvent.Type.CANCELLED,
                                opCodeMatching("op"))));
                }
            });
        invoke("id=" + op.getIdentifier());
    }

    public void testCancelInvalidId()
        throws ProcessingException
    {
        expectError(HttpServletResponse.SC_BAD_REQUEST, "");
        invoke("id=INVALID");
    }

    public void testCancelNotAuthorized()
        throws Exception
    {
        final Principal user = userManager.getUserByLogin("user");
        authContext.setUserPrincipal(user, false);
        LongRunningOperation op = registry.register("op", null, null, 3, securityCallback);
        checking(new Expectations()
            {
                {
                    oneOf(securityCallback).canView(with(any(LongRunningOperation.class)),
                        with(same(user)));
                    will(returnValue(true));
                    oneOf(securityCallback).canCancel(with(any(LongRunningOperation.class)),
                        with(same(user)));
                    will(returnValue(false));
                }
            });
        expectError(HttpServletResponse.SC_FORBIDDEN, "");
        invoke("id=" + op.getIdentifier());
    }

    public void testCancelNotAuthorizedView()
        throws Exception
    {
        final Principal user = userManager.getUserByLogin("user");
        authContext.setUserPrincipal(user, false);
        LongRunningOperation op = registry.register("op", null, null, 3, securityCallback);
        checking(new Expectations()
            {
                {
                    oneOf(securityCallback).canView(with(any(LongRunningOperation.class)),
                        with(same(user)));
                    will(returnValue(false));
                }
            });
        expectError(HttpServletResponse.SC_FORBIDDEN, "");
        invoke("id=" + op.getIdentifier());
    }
}
