/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 25, 2003
 */
package org.objectledge.filesystem.impl;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LocalFileSystemProviderTest.java,v 1.1 2003-11-26 09:23:56 mover Exp $
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
