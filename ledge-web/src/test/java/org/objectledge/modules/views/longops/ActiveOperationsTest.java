package org.objectledge.modules.views.longops;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jcontainer.dna.Logger;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.longops.impl.LongRunningOperationRegistryImpl;
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

    private final Context context = new Context();

    private final HttpContext httpContext = new HttpContext(servletRequest, servletResponse);

    private final LongRunningOperationRegistry registry = new LongRunningOperationRegistryImpl();

    private final ActiveOperations view = new ActiveOperations(registry, context, logger);

    private final ByteArrayOutputStream buff = new ByteArrayOutputStream();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setUp()
    {
        context.setAttribute(HttpContext.class, httpContext);
        checking(new Expectations()
            {
            {
                    oneOf(servletRequest).getHeader("Content-Type");
                    will(returnValue(null));

                    oneOf(servletRequest).getCharacterEncoding();
                    will(returnValue("UTF-8"));

                    oneOf(servletResponse).setContentType("application/json");

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
                }
            });
    }

    public void testAllOperations()
        throws Exception
    {
        LongRunningOperation op = registry.register("op", null, null, 3);
        JsonNode tree = render();
        assertEquals(1, tree.size());
        assertTrue(tree.isArray());
        assertTrue(tree.get(0).has("identifier"));
        assertEquals(op.getIdentifier(), tree.get(0).get("identifier").getTextValue());
    }

    protected JsonNode render()
        throws IOException, JsonProcessingException, BuildException, ProcessingException
    {
        view.build(null, "");
        return mapper.readTree(new ByteArrayInputStream(buff.toByteArray()));
    }
}
