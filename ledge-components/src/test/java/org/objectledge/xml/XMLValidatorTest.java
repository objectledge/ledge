/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 2, 2003
 */
package org.objectledge.xml;

import junit.framework.TestCase;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.impl.ClasspathFileSystemProvider;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: XMLValidatorTest.java,v 1.2 2004-01-08 12:50:52 fil Exp $
 */
public class XMLValidatorTest extends TestCase
{
    /**
     * Constructor for XMLValidatorTest.
     * @param arg0
     */
    public XMLValidatorTest(String arg0)
    {
        super(arg0);
    }

    public void testValidate()
        throws Exception
    {
        ClasspathFileSystemProvider cps = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fileSystem = new FileSystem(new FileSystemProvider[] { cps }, 4096, 4096);
        fileSystem.start();
        XMLValidator xmlValidator = new XMLValidator(fileSystem);
        xmlValidator.validate(XMLValidator.RELAXNG_SCHEMA, XMLValidator.RELAXNG_SCHEMA);
    }
}
