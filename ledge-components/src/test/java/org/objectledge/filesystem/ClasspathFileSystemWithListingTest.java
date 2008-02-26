package org.objectledge.filesystem;

import java.net.URL;

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
}
