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
 * @version $Id: LocalFileSystemProviderTest.java,v 1.2 2004-06-28 10:08:46 fil Exp $
 */
public class LocalFileSystemProviderTest extends TestCase
{
    protected LocalFileSystemProvider provider;

    /**
     * Constructor for LocalFileProviderTest.
     * @param arg0
     */
    public LocalFileSystemProviderTest(String arg0)
    {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        provider = new LocalFileSystemProvider("local", "src/test/resources");
    }

	protected void tearDown() throws Exception
	{
		super.tearDown();
		String[] tab = new String[] { "var/new.file", "var/new.directory1", "var/new.directory2" }; 
		for (int i = 0; i < tab.length; i++)
        {
            String element = tab[i];
            if(provider.exists(element))
            {
				provider.delete(element);
            }
        }
	}

    public void testGetName()
    {
        assertEquals(provider.getName(), "local");
    }

    public void testIsReadOnly()
    {
        assertFalse("Provider is not read only", provider.isReadOnly());
    }

    public void testExists()
    {
        assertTrue("File does not exist - check test resources!", provider.exists("file"));
        assertFalse("File exists - check test resources!", provider.exists("nofile"));
    }

    public void testIsFile()
    {
        assertTrue("The resource is not a file - check test resources!", 
        			provider.isFile("file"));
        assertFalse("The resource is a file - check test resources!", 
        			provider.isFile("directory"));
    }

    public void testIsDirectory()
    {
        assertTrue("The resource is not a directory - check test resources!", 
        			provider.isDirectory("directory"));
        assertFalse("The resource is a directory - check test resources!", 
        			provider.isDirectory("file"));
    }

    public void testCanRead()
    {
		assertTrue("The resource is not readable - check test resources!", 
					provider.canRead("file"));
		assertFalse("The resource is readable - check test resources!", 
					provider.canRead("nofile"));
		assertTrue("The resource does not exist - check test resources!", 
					provider.exists("unreadablefile"));
		assertFalse("The resource is readable - check test resources!", 
					provider.canRead("unreadablefile"));
    }

    public void testCanWrite()
    {
		assertTrue("The resource is not writable - check test resources!", 
					provider.canWrite("file"));
		assertFalse("The resource is writable - check test resources!", 
					provider.canWrite("nofile"));
		assertTrue("The resource does not exist - check test resources!", 
					provider.exists("unwritablefile"));
		assertFalse("The resource is writable - check test resources!", 
					provider.canWrite("unwritablefile"));
    }

    public void testList()
    {
		//TODO Implement list().
    }

    public void testCreateNewFile()
    	throws Exception 
    {
		assertTrue("This resource shouldn't exist - check test resources!", 
					provider.createNewFile("var/new.file"));
		assertFalse("This resource should exist - check test resources!", 
					provider.createNewFile("file"));
		try
		{
			provider.createNewFile("var/new.directory1/new.file");
			fail("The directory shouldn't exist - check test resources!");
		}
		catch(IOException e)
		{
			// expected
		}
	}
		
    public void testMkdirs()
    throws IOException
    {
		provider.mkdirs("var/new.directory2");
		assertTrue("The directory should just have been created!", 
		provider.exists("var/new.directory2"));
    }

    public void testDelete()
    {
        //TODO Implement delete().
    }

    public void testRename()
    {
        //TODO Implement rename().
    }

    public void testGetInputStream()
    {
        //TODO Implement getInputStream().
    }

    public void testGetOutputStream()
    {
        //TODO Implement getOutputStream().
    }

    public void testGetRandomAccess()
    {
        //TODO Implement getRandomAccess().
    }

    public void testLastModified()
    {
        //TODO Implement lastModified().
    }

    public void testLength()
    {
        //TODO Implement length().
    }

    public void testGetFile()
    {
        //TODO Implement getFile().
    }

}
