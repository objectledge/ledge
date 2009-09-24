//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

/**
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 *
 * @version $Id: ClasspathFileSystemProviderTest.java,v 1.3 2009-09-07 21:00:50 mgolebsk Exp $
 */
public class ClasspathFileSystemProviderTest
    extends TestCase
{
    protected ClasspathFileSystemProvider provider;
    
    /**Test resource provider.*/
    protected FileSystemProvider lfs;

    /**
     * Constructor for LocalFileProviderTest.
     * 
     * @param arg0
     */
    public ClasspathFileSystemProviderTest(String arg0)
    {
        super(arg0);
        lfs = new LocalFileSystemProvider("local", "src/test/resources");
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        provider = new ClasspathFileSystemProvider("classpath", ClasspathFileSystemProvider.class
            .getClassLoader(), new ArrayList<URL>());
    }

    public void testGetName()
    {
        assertEquals("classpath", provider.getName());
    }

    public void testIsReadOnly()
    {
        assertTrue(provider.isReadOnly());
    }

    public void testExists()
    {
        assertTrue("should exist", provider
            .exists("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));        
        assertFalse("should not exist", provider.exists("nonexistent_file"));
    }

    public void testIsFile()
    {
        assertTrue("should be a file", provider
            .isFile("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
        assertFalse("should not be a file", provider.isFile("org/objectledge/filesystem/"));
        assertFalse("should not be a file", provider.isFile("nonexistent_file"));
    }

    public void testIsDirectory()
    {
        assertTrue("should be a directory", provider.isDirectory("org/objectledge/filesystem/"));
        assertFalse("should be a directory", provider
            .isDirectory("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }
    
    public void testExistInJar()
    {
        assertTrue("should exist", provider.exists("java/io/File.class"));
    }
    
    public void testIsFileInJar()
    {
        assertTrue("should be a file", provider.isFile("java/io/File.class"));
    }

    public void testCanRead()
    {
        assertTrue("should be readable", provider
            .canRead("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
        assertFalse("should not be readable", provider.canRead("nonexistent_file"));
    }

    public void testCanWrite()
    {
        assertFalse("should not be writable", provider.canWrite("nonexistent_file"));
        assertFalse("should not be writable", provider
            .canWrite("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
        assertFalse("should not be writable", provider.canWrite("org/objectledge/filesystem/"));
    }

    public void testList()
        throws Exception
    {
        String location = "jar:file:/some/path/ledge-tutorial-webapp/WEB-INF/lib/dojo-1.1.1.jar!/files.lst";
        
        InputStream is = lfs.getInputStream("filesystem/files.lst");
        Method processListingMethod = 
            ReadOnlyFileSystemProvider.class.getDeclaredMethod("processListing", 
                    new Class[] {String.class, InputStream.class});
        processListingMethod.setAccessible(true);
        Object[] args = new Object[] {location, is};
        
        processListingMethod.invoke(provider, args);
        
        String[] dirPaths = new String[] {
                "/dojo/", 
                "/dojo/some  double  spaced  path/"};
        String[] filePaths = new String[] {
                "/dojo/dojo.css",
                "/dojo/some  double  spaced  path/dojo.css", 
                "/dojo/some  double  spaced  path/double  spaced  filename.css"};
        
        for(final String dirPath : dirPaths)
        {
            assertTrue("The resource should be a directory", provider.isDirectory(dirPath));
            assertFalse("The resource should NOT be a directory", provider.isFile(dirPath));
            assertEquals("Modification time should equal -1 for directory", -1,  provider.lastModified(dirPath));
            assertEquals("Length should equal -1 for directory", -1,  provider.length(dirPath));
        }
        
        int length = 1842;
        long lastModified = 1251794098;
        for(final String filePath : filePaths)
        {
            assertFalse("The resource should NOT be a file", provider.isDirectory(filePath));
            assertTrue("The resource should be a file", provider.isFile(filePath));
            assertEquals("The file lenght is incorrect", length++, provider.length(filePath));
            assertEquals("The file modification date is incorrect", lastModified++, provider.lastModified(filePath));
        }
    }

    public void testCreateNewFile()
        throws Exception
    {
        try
        {
            provider.createNewFile("new_file");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testMkdirs()
    {
        try
        {
            provider.mkdirs("new_file");
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
            provider.delete("new_file");
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
            provider.rename("org/objectledge/filesystem/ClasspathFileSystemProvider.class",
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
        assertNull(provider.getOutputStream("", true));
    }

    public void testGetRandomAccess()
    {
        assertNull(provider.getRandomAccess("", ""));
    }

    public void testLastModified()
        throws URISyntaxException
    {
        URL url = provider
            .getResource("org/objectledge/filesystem/ClasspathFileSystemProvider.class");
        File f = new File(url.toURI());
        assertEquals(f.lastModified(), provider
            .lastModified("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }

    public void testLength() throws URISyntaxException
    {
        URL url = provider
            .getResource("org/objectledge/filesystem/ClasspathFileSystemProvider.class");
        File f = new File(url.toURI());
        assertEquals(f.length(), provider
            .length("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }
}
