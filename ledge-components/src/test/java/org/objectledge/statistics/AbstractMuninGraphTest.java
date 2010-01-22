package org.objectledge.statistics;

import junit.framework.TestCase;

import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;

public class AbstractMuninGraphTest
    extends TestCase
{
    private Foo foo;

    public void setUp()
    {
        FileSystem fs = new FileSystem(new FileSystemProvider[] { new ClasspathFileSystemProvider(
            "classpath", getClass().getClassLoader()) }, 4096, 4096);
        foo = new Foo(fs);
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
