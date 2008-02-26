package org.objectledge.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class ClasspathFileSystemWithListingTest
    extends TestCase
{
    protected ClasspathFileSystemProvider provider;

    public void setUp()
        throws Exception
    {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL listing1 = classLoader.getResource("org/objectledge/filesystem/listing1.lst");
        provider = new ClasspathFileSystemProvider("classpath", classLoader, listing1);
    }

    public void testExists()
    {
        assertTrue("should exist", provider.exists("/dir"));
        assertTrue("should exist", provider.exists("/dir/empty"));
        assertTrue("should exist", provider.exists("/dir/file1"));
        assertFalse("should not exist", provider.exists("/missing_file"));
        assertFalse("should not exist", provider.exists("/dir/missing_file"));
    }

    public void testIsFile()
    {
        assertTrue("should be a file", provider.isFile("/dir/file1"));
        assertTrue("should be a file", provider.isFile("/dir/file2"));
        assertFalse("should not be a file", provider.isFile("/dir"));
        assertFalse("should not be a file", provider.isFile("/"));
        assertFalse("should not be a file", provider.isFile("/missing_file"));
        assertFalse("should not be a file", provider.isFile("/dir/missing_file"));
    }

    public void testIsDirectory()
    {
        assertFalse("should not be a directory", provider.isDirectory("/dir/file1"));
        assertFalse("should not be a directory", provider.isDirectory("/dir/file2"));
        assertTrue("should be a directory", provider.isDirectory("/"));
        assertTrue("should be a directory", provider.isDirectory("/dir"));
        assertTrue("should be a directory", provider.isDirectory("/dir/empty"));
        assertFalse("should not be a directory", provider.isDirectory("/missing_file"));
        assertFalse("should not be a directory", provider.isDirectory("/dir/missing_file"));
    }

    public void testCanRead()
    {
        assertTrue("should be able to read", provider.canRead("/"));
        assertTrue("should be able to read", provider.canRead("/dir"));
        assertTrue("should be able to read", provider.canRead("/dir/empty"));
        assertTrue("should be able to read", provider.canRead("/dir/file1"));
        assertFalse("should not be able to read", provider.canRead("/missing_file"));
        assertFalse("should not be able to read", provider.canRead("/dir/missing_file"));
    }

    public void testCanWrite()
    {
        assertFalse("should not be able to write", provider.canWrite("/"));
        assertFalse("should not be able to write", provider.canWrite("/dir"));
        assertFalse("should not be able to write", provider.canWrite("/dir/empty"));
        assertFalse("should not be able to write", provider.canWrite("/dir/file1"));
        assertFalse("should not be able to write", provider.canWrite("/missing_file"));
        assertFalse("should not be able to write", provider.canWrite("/dir/missing_file"));
    }

    public void testList()
        throws IOException
    {
        List<String> items = new ArrayList<String>(provider.list("/dir"));
        Collections.sort(items);
        assertEquals(3, items.size());
        assertEquals("empty", items.get(0));
        assertEquals("file1", items.get(1));
        assertEquals("file2", items.get(2));
        items = new ArrayList<String>(provider.list("/dir/empty"));
        assertEquals(0, items.size());
        try
        {
            provider.list("/dir/file1");
            fail("should throw exception");
        }
        catch(IOException e)
        {
            // expected
        }
        try
        {
            provider.list("/missing_directory");
            fail("should throw exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testCreateNewFile()
        throws Exception
    {
        try
        {
            provider.createNewFile("/new_file");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testMkdirs()
        throws IOException
    {
        try
        {
            provider.mkdirs("/new_directory");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testDelete()
    {
        try
        {
            provider.delete("/dir/file1");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testRename()
    {
        try
        {
            provider.rename("/dir/file1",
                "new_name");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testGetInputStream()
        throws IOException
    {
        InputStream is = provider
            .getInputStream("org/objectledge/filesystem/ClasspathFileSystemProvider.class");
        assertNotNull(is);
        assertTrue("should read at least a byte", is.read() >= 0);
    }

    public void testGetOutputStream()
    {
        assertNull(provider.getOutputStream("/dir/file1", true));
    }

    public void testGetRandomAccess()
    {
        assertNull(provider.getRandomAccess("/dir/file1", ""));
    }
    
    public void testLenght()
    {
        assertEquals(100, provider.length("/dir/file1"));
        assertEquals(0, provider.length("/dir/file2"));
        assertEquals(-1L, provider.length("/missing_file"));
    }
    
    public void testLastModified()
    {
        assertEquals(1204069131000L, provider.lastModified("/dir/file1"));
        assertEquals(-1L, provider.lastModified("/missing_file"));
    }
}
