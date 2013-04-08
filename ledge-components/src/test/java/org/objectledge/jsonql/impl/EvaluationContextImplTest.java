package org.objectledge.jsonql.impl;

import java.util.Set;

import junit.framework.TestCase;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.jsonql.EvaluationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EvaluationContextImplTest
    extends TestCase
{
    private static FileSystem fs = FileSystem.getClasspathFileSystem();

    private static ObjectMapper mapper = new ObjectMapper();

    private EvaluationContext context;

    @Override
    public void setUp()
        throws Exception
    {
        JsonNode data = mapper.readTree(fs.read("/jsonql/context.json", "UTF-8"));
        context = new EvaluationContextImpl(data);
    }

    private void assertErrors(String... errors)
    {
        Set<String> reported = context.getErrors();
        for(String error : errors)
        {
            boolean found = false;
            for(String r : reported)
            {
                if(r.contains(error))
                {
                    found = true;
                }
            }
            if(!found)
            {
                fail("error " + error + " not reported. reported errors: " + reported.toString());
            }
        }
    }

    public void testGetValue()
    {
        String value = context.getField("a").getValue();
        assertNotNull(value);
        assertEquals("1", value);
    }

    public void testGetValueNested()
    {
        String value = context.getField("b").getField("b1").getValue();
        assertNotNull(value);
        assertEquals("1", value);
    }

    public void testArray()
    {
        String value = context.getField("c").getElement(0).getValue();
        assertNotNull(value);
        assertEquals("a", value);
    }

    public void testNumber()
    {
        String value = context.getField("n").getValue();
        assertNotNull(value);
        assertEquals("7", value);
    }

    public void testNull()
    {
        String value = context.getField("z").getValue();
        assertNull(value);
        assertErrors("z is null");
    }

    public void testUndefined()
    {
        assertNull(context.getField("u").getValue());
        assertNull(context.getField("b").getField("u").getValue());
        assertNull(context.getField("c").getElement(4).getValue());
        assertNull(context.getField("a").getField("x").getValue());
        assertNull(context.getField("a").getElement(4).getValue());
        assertNull(context.getField("c").getValue());
        assertErrors("u is undefined", "b does not have field u", "c does not have element 4",
            "a is not an object", "a is not an array", "c is a container node");
    }
}
