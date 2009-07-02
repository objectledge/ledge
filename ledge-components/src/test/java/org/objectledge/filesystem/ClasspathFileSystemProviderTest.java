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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ClasspathFileSystemProviderTest.java,v 1.9 2008-02-28 16:56:15 rafal Exp $
 */
public class ClasspathFileSystemProviderTest
    extends TestCase
{
    protected ClasspathFileSystemProvider provider;

    /**
     * Constructor for LocalFileProviderTest.
     * 
     * @param arg0
     */
    public ClasspathFileSystemProviderTest(String arg0)
    {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        provider = new ClasspathFileSystemProvider("classpath", ClasspathFileSystemProvider.class
            .getClassLoader(), Collections.EMPTY_LIST);
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
        ;
        assertFalse("should not exist", provider.exists("nonexistent_file"));
    }

    public void testIsFile()
        throws IOException
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
        List<String> list = new ArrayList<String>(provider.list("/org/objectledge/filesystem/impl"));
        Collections.sort(list);
        // Filter out Clover generated classes
        Iterator<String> i = list.iterator();
        while(i.hasNext())
        {
            if(i.next().contains("$__CLR"))
            {
                i.remove();
            }
        }
        assertEquals(4, list.size());
        assertEquals("LocalRandomAccessFile.class", list.get(0));
        assertEquals("ReadOnlyFileSystemProvider.class", list.get(1));
        assertEquals("URLConnectionImpl.class", list.get(2));
        assertEquals("URLStreamHandlerImpl.class", list.get(3));
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
        throws IOException
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