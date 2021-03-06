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

import org.jcontainer.dna.Configuration;
import org.objectledge.LedgeWebTestCase;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.test.views.foo.Bar;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: NameSequenceFactoryTest.java,v 1.16 2005-10-10 14:06:45 rafal Exp $
 */
public class NameSequenceFactoryTest extends LedgeWebTestCase
{
    private FileSystem fs;   
    
    /**
     * Constructor for NameSequenceFactoryTest.
     */
    public void setUp() throws Exception
    {
        fs = getFileSystem("src/test/resources/view-sequences");
    }
    
    public void testLookups()
        throws Exception
    {
        NameSequenceFactory factory = getNameSequenceFactory("standard");
        
        Sequence classSequence = factory.getClassNameSequence("views", "foo.Bar", true, false);
        assertEquals("org.objectledge.test.views.foo.Bar", classSequence.next());
        assertEquals("org.objectledge.test.views.foo.Default", classSequence.next());
        assertEquals("org.objectledge.test.views.Default", classSequence.next());
        assertEquals(false, classSequence.hasNext());

        classSequence = factory.getClassNameSequence("views", "foo.Bar", false, false);
        assertEquals("org.objectledge.test.views.foo.Bar", classSequence.next());
        assertEquals(false, classSequence.hasNext());

        Sequence templateSequence = factory.
            getTemplateNameSequence("views", "foo.Bar", true, false);
        assertEquals("views/foo/Bar", templateSequence.next());
        assertEquals("views/foo/Default", templateSequence.next());
        assertEquals("views/Default", templateSequence.next());
        assertEquals(false, templateSequence.hasNext());

        templateSequence = factory.getTemplateNameSequence("views", "foo.Bar", false, false);
        assertEquals("views/foo/Bar", templateSequence.next());
        assertEquals(false, templateSequence.hasNext());

        templateSequence = factory.getTemplateNameSequence("views", "foo", true, false);
        assertEquals("views/foo/Default", templateSequence.next());
        assertEquals("views/Default", templateSequence.next());
        assertEquals(false, templateSequence.hasNext());

        templateSequence = factory.getTemplateNameSequence("views", "foo.Default", true, false);
        assertEquals("views/foo/Default", templateSequence.next());
        assertEquals("views/Default", templateSequence.next());
        assertEquals(false, templateSequence.hasNext());
    }
    
    public void testDots()
        throws Exception
    {
        NameSequenceFactory factory = getNameSequenceFactory("dots");
        Sequence classSequence = factory.getClassNameSequence("views", "foo.Bar", true, false);
        assertEquals("org.objectledge.test.views.foo.Bar", classSequence.next());
        assertEquals("org.objectledge.test.views.foo.Default", classSequence.next());
        assertEquals("org.objectledge.test.views.Default", classSequence.next());
        assertEquals(false, classSequence.hasNext());
    }
    
    public void testReverseLookups()
        throws Exception
    {
        NameSequenceFactory factory = getNameSequenceFactory("standard");
        assertEquals("foo.Bar", factory.getView("views", Bar.class));
        try
        {
            factory.getView("views", NameSequenceFactory.class);
            fail("exception should have been thrown");
        }
        catch(Exception e)
        {
            // success
        }
        Templating templating = getTemplating();
        Template barTemplate = templating.getTemplate("views/foo/Bar");
        assertEquals("foo.Bar", factory.getView("views", barTemplate));
        try
        {
            factory.getView("components", barTemplate);
            fail("exception should have been thrown");
        }
        catch(Exception e)
        {
            // success
        }
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
        Configuration config = getConfig(fs,
            "org.objectledge.web.mvc.finders.NameSequenceFactory-"+configVariant+".xml");         
        return new NameSequenceFactory(config);
    }
    
    public Templating getTemplating() throws Exception
    {
        Configuration config = getConfig(fs,"org.objectledge.templating.Templating.xml");
        LoggerFactory loggerFactory = new LoggerFactory(new LoggingConfigurator());
        return new VelocityTemplating(config, loggerFactory.getLogger(Templating.class), fs);
    }
}
