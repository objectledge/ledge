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

package org.objectledge.filesystem.impl;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LocalFileSystemProviderTest.java,v 1.2 2003-12-03 14:40:42 mover Exp $
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
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. use -Dledge.root=.../ledge-components/src/test/resources");
        }
        provider = new LocalFileSystemProvider("local", root);
    }

    public void testGetName()
    {
        assertEquals(provider.getName(), "local");
    }

    public void testIsReadOnly()
    {
        assertEquals(provider.isReadOnly(), false);
    }

    public void testExists()
    {
        assertEquals(provider.exists("file"), true);
        assertEquals(provider.exists("nofile"), false);
    }

    public void testIsFile()
    {
        assertEquals(provider.isFile("file"), true);
        assertEquals(provider.isFile("directory"), false);
    }

    public void testIsDirectory()
    {
        assertEquals(provider.isDirectory("directory"), true);
        assertEquals(provider.isDirectory("file"), false);
    }

    public void testCanRead()
    {
		assertEquals(provider.canRead("file"), true);
		assertEquals(provider.canRead("nofile"), false);
		assertEquals(provider.exists("unreadablefile"), true);
		assertEquals(provider.canRead("unreadablefile"), false);
    }

    public void testCanWrite()
    {
		assertEquals(provider.canWrite("file"), true);
		assertEquals(provider.canWrite("nofile"), false);
		assertEquals(provider.exists("unwritablefile"), true);
		assertEquals(provider.canWrite("unwritablefile"), false);
    }

    public void testList()
    {
        //TODO Implement list().
    }

    public void testCreateNewFile()
    {
        //TODO Implement createNewFile().
    }

    public void testMkdirs()
    {
        //TODO Implement mkdirs().
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
