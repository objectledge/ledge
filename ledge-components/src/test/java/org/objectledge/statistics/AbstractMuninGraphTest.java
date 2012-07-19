package org.objectledge.statistics;

import java.util.Map;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.LedgeTestCase;

public class AbstractMuninGraphTest
    extends LedgeTestCase
{
    private Foo foo;

    public void setUp()
    {
        foo = new Foo(getFileSystem());
    }
    
    public void tearDown()
        throws Exception
    {
        super.tearDown();
        foo = null;
    }

    public void testGetConfig()
    {
        String config = foo.getConfig();
        assertNotNull(config);
    }

    public void testGetVariables()
    {
        String variables[] = foo.getVariables();
        assertEquals(3, variables.length);
        assertEquals("foo", variables[0]);
        assertEquals("bar", variables[1]);
        assertEquals("baz", variables[2]);
    }
    
    public void testGetValue()
    {
        Number v; 
        v = foo.getValue("foo");
        assertEquals(v, 1);
        v = foo.getValue("bar");
        assertEquals(v, 1l);
        v = foo.getValue("baz");
        assertEquals(v, 1.0d);
    }
    
    public void testGetValues()
    {
        Map<String, Number> values = foo.getValues();
        assertEquals(foo.getVariables().length, values.size());
        for(String variable : foo.getVariables())
        {
            assertEquals(foo.getValue(variable), values.get(variable));
        }
    }
    
    public void testBadVariable()
    {
        try
        {
            foo.getValue("baka");
            fail("should throw exception");            
        }
        catch(Exception e)
        {
            // OK
        }
    }

    public class Foo
        extends AbstractMuninGraph
    {
        public Foo(FileSystem fs)
        {
            super(fs);
        }

        public String getId()
        {
            return "foo";
        }
        
        public int getFoo()
        {
            return 1;
        }

        public long getBar()
        {
            return 1l;
        }

        public double getBaz()
        {
            return 1.0d;
        }
    }
}
