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

import java.io.IOException;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ClasspathFileSystemProviderTest.java,v 1.2 2004-03-18 09:37:37 pablo Exp $
 */
public class ClasspathFileSystemProviderTest extends TestCase
{
    protected ClasspathFileSystemProvider provider;

    /**
     * Constructor for LocalFileProviderTest.
     * @param arg0
     */
    public ClasspathFileSystemProviderTest(String arg0)
    {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        provider = new ClasspathFileSystemProvider("classpath", ClasspathFileSystemProvider.class.getClassLoader());
    }

    public void testGetName()
    {
        assertEquals(provider.getName(), "classpath");
    }

    public void testIsReadOnly()
    {
        assertTrue("Provider is read only", provider.isReadOnly());
    }

    public void testExists()
    {
        assertTrue("File does not exist - check test resources!",
                   provider.exists("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
        assertFalse("File exists - check test resources!", provider.exists("nofile"));
    }

    public void testIsFile()
    {
        assertTrue("The resource is not a file - check test resources!", 
        			provider.isFile("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
        //assertFalse("The resource is a file - check test resources!", 
        //  			provider.isFile("org/objectledge/filesystem/"));
    }

    public void testIsDirectory()
    {
        //assertTrue("The resource is not a directory - check test resources!", 
        //			provider.isDirectory("org/objectledge/filesystem/"));
        assertFalse("The resource is a directory - check test resources!", 
        			provider.isDirectory("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }

    public void testCanRead()
    {
		assertTrue("The resource is not readable - check test resources!", 
					provider.canRead("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
		assertFalse("The resource is readable - check test resources!", 
					provider.canRead("nofile"));
    }

    public void testCanWrite()
    {
		assertFalse("The resource is writable - check test resources!", 
					provider.canWrite("nofile"));
    }

    public void testList()  
        throws Exception
    {
		//String[] list = provider.list("/org/");
        //assertEquals(1, list.length);
    }

    public void testCreateNewFile()
    	throws Exception 
    {
		try
		{
			provider.createNewFile("foo");
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
            provider.mkdirs("foo");
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
            provider.delete("foo");
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
            provider.rename("foo","foo");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            // expected
        }
    }

    public void testGetInputStream()
    {
        //TODO Implement getInputStream().
    }

    public void testGetOutputStream()
    {
        assertNull(provider.getOutputStream("",true));
    }

    public void testGetRandomAccess()
    {
        assertNull(provider.getRandomAccess("",""));
    }

    public void testLastModified()
    {
        assertEquals(-1L, provider.lastModified(""));
        //assertEquals(true, 1079100713000L == provider.lastModified("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }

    public void testLength()
    {
        assertEquals(-1L, provider.length(""));
        //assertEquals(true, 12345L == provider.length("org/objectledge/filesystem/ClasspathFileSystemProvider.class"));
    }
}
