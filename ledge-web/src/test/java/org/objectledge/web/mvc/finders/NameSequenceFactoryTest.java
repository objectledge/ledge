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
package org.objectledge.web.mvc.finders;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.test.views.foo.Bar;
import org.objectledge.xml.XMLValidator;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: NameSequenceFactoryTest.java,v 1.3 2004-01-19 13:44:44 fil Exp $
 */
public class NameSequenceFactoryTest extends TestCase
{
    private FileSystem fs;
    
    private ConfigurationFactory configFactory;
    
    /**
     * Constructor for NameSequenceFactoryTest.
     * @param arg0
     */
    public NameSequenceFactoryTest(String arg0)
        throws Exception
    {
        super(arg0);
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
            "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        fs = FileSystem.getStandardFileSystem(root+"/view-sequences");
        XMLValidator validator = new XMLValidator();        
        configFactory = new ConfigurationFactory(fs, validator, ".");
    }
    
    public void testLookups()
        throws Exception
    {
        NameSequenceFactory factory = getNameSequenceFactory("standard");
        Sequence classSequence = factory.getClassNameSequence("views.foo.Bar");
        assertEquals("org.objectledge.test.views.foo.Bar", classSequence.next());
        assertEquals("org.objectledge.test.views.foo.Default", classSequence.next());
        assertEquals("org.objectledge.test.views.Default", classSequence.next());
        assertEquals("org.objectledge.test.Default", classSequence.next());
        assertEquals(false, classSequence.hasNext());
        Sequence templateSequence = factory.getTemplateNameSequence("views.foo.Bar");
        assertEquals("views/foo/Bar", templateSequence.next());
        assertEquals("views/foo/Default", templateSequence.next());
        assertEquals("views/Default", templateSequence.next());
        assertEquals("Default", templateSequence.next());
        assertEquals(false, templateSequence.hasNext());
    }
    
    public void testReverseLookups()
        throws Exception
    {
        NameSequenceFactory factory = getNameSequenceFactory("standard");
        assertEquals("views.foo.Bar", factory.getView(Bar.class));
        try
        {
            factory.getView(NameSequenceFactory.class);
            fail("exception should have been thrown");
        }
        catch(Exception e)
        {
            // success
        }
        Templating templating = getTemplating();
        Template barTemplate = templating.getTemplate("views/foo/Bar");
        assertEquals("views.foo.Bar", factory.getView(barTemplate));
    }

    public void testOverlap()
    {
        try
        {
            getNameSequenceFactory("overlap");
            fail("exception should have been thrown");
        }
        catch(Exception e)
        {
            // success        
        }
    }
    
    public NameSequenceFactory getNameSequenceFactory(String configVariant)
        throws Exception
    {
        Configuration config = configFactory.
            getConfig("org.objectledge.web.mvc.finders.NameSequenceFactory:"+configVariant, 
                NameSequenceFactory.class);
        return new NameSequenceFactory(config);
    }
    
    public Templating getTemplating()
    {
        Configuration config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory();
        return new VelocityTemplating(config, loggerFactory.getLogger(Templating.class), fs);
    }
}
