// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.web.mvc.components;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.finders.MVCFinder;
import org.objectledge.web.mvc.finders.NameSequenceFactory;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class ComponentsTest extends TestCase
{
    private Context context;
    
    private ComponentToolFactory componentToolFactory;
    
    private ComponentTool componentTool;
    /**
     * Constructor for ActionExecutorValveTest.
     * @param arg0
     */
    public ComponentsTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {

        try
        {
            context = new Context();
            
            //prepare test
            String root = System.getProperty("ledge.root");
            if (root == null)
            {
                throw new Error("system property ledge.root undefined. " +
                     "use -Dledge.root=.../ledge-container/src/test/resources");
            }
            
            FileSystem fs = FileSystem.getStandardFileSystem(root + "/components");
            XMLValidator validator = new XMLValidator();
            ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");
            Configuration config = configFactory.
                        getConfig(NameSequenceFactory.class, NameSequenceFactory.class);
            NameSequenceFactory nameSequenceFactory = new NameSequenceFactory(config);
            config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
            LoggerFactory loggerFactory = new LoggerFactory();
            Logger logger = loggerFactory.getLogger(Templating.class);
            Templating templating = new VelocityTemplating(config, logger, fs);
            logger = loggerFactory.getLogger(MVCFinder.class);
            MutablePicoContainer container = new DefaultPicoContainer();
            container.registerComponentImplementation(Context.class);
            MVCFinder finder = new MVCFinder(container, logger, templating, nameSequenceFactory);
            container.registerComponentInstance(MVCFinder.class,finder);
            componentToolFactory = new ComponentToolFactory(context,finder,finder);
            MVCContext mvcContext = new MVCContext();
            mvcContext.setAction(null);
            context.setAttribute(MVCContext.class, mvcContext);
        }
        catch (Exception e)
        {
            throw new Error(e);
        }
    }
    
    public void testFactory() throws Exception
    {
        
        componentTool = (ComponentTool)componentToolFactory.getTool();
        componentToolFactory.recycleTool(componentTool);
        assertNotNull(componentTool);
        assertEquals(componentToolFactory.getKey(),"component");
    }
    
    public void testTool() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        componentTool = (ComponentTool)componentToolFactory.getTool();
        assertNull(componentTool.embed("DefaultComponent"));
        try
        {
            componentTool.embed("Foo");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
        assertEquals(componentTool.embed("Bar"),"bar");
        assertEquals(componentTool.embed("TestComponent"),"bar");
        try
        {
            TemplatingContext tContext = TemplatingContext.getTemplatingContext(context);
            tContext.put("test", this);
            componentTool.embed("Exc");
            System.out.println("RES:"+componentTool.embed("Exc"));
            fail("should throw the exception");
        }
        catch(BuildException e)
        {
            //ok!
        }
        
    }
    
    public void throwException() throws Exception
    {
        throw new Exception("test");
    }
}
