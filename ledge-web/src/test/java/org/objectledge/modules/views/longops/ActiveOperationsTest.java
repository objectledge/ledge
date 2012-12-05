package org.objectledge.modules.views.longops;

import static org.hamcrest.core.IsNot.not;
import static org.objectledge.longops.matchers.LongRunningOperationCodeMatcher.opCodeMatching;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.jcontainer.dna.Logger;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.DummyUserManager;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.LongRunningOperationSecurityCallback;
import org.objectledge.longops.impl.LongRunningOperationRegistryImpl;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.BuildException;

public class ActiveOperationsTest
    extends MockObjectTestCase
{
    @Mock
    private Logger logger;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private LongRunningOperationSecurityCallback securityCallback;

    private UserManager userManager = new DummyUserManager();

    private final Context context = new Context();

    private final HttpContext httpContext = new HttpContext(servletRequest, servletResponse);

    private final AuthenticationContext authContext = new AuthenticationContext(null, false);

    private final LongRunningOperationRegistry registry = new LongRunningOperationRegistryImpl();

    private final ActiveOperations view = new ActiveOperations(registry, userManager, context,
        logger);

    private final ByteArrayOutputStream buff = new ByteArrayOutputStream();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setUp()
    {
        context.setAttribute(HttpContext.class, httpContext);
        context.setAttribute(AuthenticationContext.class, authContext);
        checking(new Expectations()
            {
                {
                    oneOf(servletRequest).getHeader("Content-Type");
                    will(returnValue(null));

                    oneOf(servletRequest).getCharacterEncoding();
                    will(returnValue("UTF-8"));

                    oneOf(servletResponse).setContentType("application/json");

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

                    try
                    {
                        oneOf(servletResponse).getOutputStream();
                        will(returnValue(new ServletOutputStream()
                            {
                                @Override
                                public void write(int b)
                                    throws IOException
                                {
                                    buff.write(b);
                                }
                            }));
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }

                    ignoring(logger);
                }
            });
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

    private JsonNode render(final String queryString)
        throws IOException, JsonProcessingException, BuildException, ProcessingException
    {
        checking(new Expectations()
            {
                {
                    oneOf(servletRequest).getQueryString();
                    will(returnValue(queryString));
                }
            });
        context.setAttribute(RequestParameters.class, new RequestParameters(servletRequest));
        view.build(null, "");
        if(buff.size() > 0)
        {
            return mapper.readTree(new ByteArrayInputStream(buff.toByteArray()));
        }
        else
        {
            return null;
        }
    }

    public void testAllOperations()
        throws Exception
    {
        LongRunningOperation op = registry.register("op", null, null, 3);

        JsonNode response = render("");
        assertEquals(1, response.size());
        assertTrue(response.isArray());
        assertTrue(response.get(0).has("identifier"));
        assertEquals(op.getIdentifier(), response.get(0).get("identifier").getTextValue());
    }

    public void testOperationsByUser()
        throws Exception
    {
        Principal user1 = userManager.getUserByLogin("user1");
        Principal user2 = userManager.getUserByLogin("user2");
        LongRunningOperation op1 = registry.register("op1", null, user1, 3);
        @SuppressWarnings("unused")
        LongRunningOperation op2 = registry.register("op2", null, user2, 3);

        JsonNode response = render("uid=user1");
        assertEquals(1, response.size());
        assertTrue(response.isArray());
        assertTrue(response.get(0).has("identifier"));
        assertEquals(op1.getIdentifier(), response.get(0).get("identifier").getTextValue());
        assertEquals(user1.getName(), response.get(0).get("user").get("name").getTextValue());
    }

    public void testOperationsByInvalidUser()
        throws Exception
    {
        expectError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
        render("uid=MISSING");
    }

    public void testOperationsByCode()
        throws Exception
    {
        Principal user1 = userManager.getUserByLogin("user1");
        LongRunningOperation op1 = registry.register("op.a.1", null, user1, 3);
        @SuppressWarnings("unused")
        LongRunningOperation op2 = registry.register("op.b.1", null, user1, 3);

        JsonNode response = render("code=op.a");
        assertEquals(1, response.size());
        assertTrue(response.isArray());
        assertTrue(response.get(0).has("identifier"));
        assertEquals(op1.getIdentifier(), response.get(0).get("identifier").getTextValue());
    }

    public void testOperationsByUserAndCode()
        throws Exception
    {
        Principal user1 = userManager.getUserByLogin("user1");
        LongRunningOperation op1 = registry.register("op.a.1", null, user1, 3);
        @SuppressWarnings("unused")
        LongRunningOperation op2 = registry.register("op.b.1", null, user1, 3);
        Principal user2 = userManager.getUserByLogin("user2");
        @SuppressWarnings("unused")
        LongRunningOperation op3 = registry.register("op.a.1", null, user2, 3);

        JsonNode response = render("code=op.a&uid=user1");
        assertEquals(1, response.size());
        assertTrue(response.isArray());
        assertTrue(response.get(0).has("identifier"));
        assertEquals(op1.getIdentifier(), response.get(0).get("identifier").getTextValue());
    }

    public void testOperationsAuthFiltered()
        throws Exception
    {
        final Principal user1 = userManager.getUserByLogin("user1");
        final Principal user2 = userManager.getUserByLogin("user2");
        checking(new Expectations()
            {
                {
                    oneOf(securityCallback).canView(with(opCodeMatching("op\\.1\\..")),
                        with(same(user1)));
                    will(returnValue(true));
                    oneOf(securityCallback).canView(with(not(opCodeMatching("op\\.1\\.."))),
                        with(same(user1)));
                    will(returnValue(false));
                }
            });
        authContext.setUserPrincipal(user1, true);

        LongRunningOperation op1a = registry.register("op.1.a", null, user1, 3, securityCallback);
        registry.register("op.1.b", null, user2, 3, securityCallback);
        registry.register("op.2", null, user1, 3, securityCallback);

        JsonNode response = render("uid=user1");
        assertEquals(1, response.size());
        assertTrue(response.isArray());
        assertTrue(response.get(0).has("identifier"));
        assertEquals(op1a.getIdentifier(), response.get(0).get("identifier").getTextValue());
    }
}
