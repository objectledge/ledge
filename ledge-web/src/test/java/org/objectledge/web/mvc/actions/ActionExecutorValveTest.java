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

package org.objectledge.web.mvc.actions;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.LedgeWebTestCase;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;
import org.objectledge.web.mvc.finders.NameSequenceFactory;
import org.objectledge.web.mvc.security.SecurityHelper;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class ActionExecutorValveTest extends LedgeWebTestCase
{
    private Context context;

    private ActionExecutorValve actionExecutorValve;

    public void setUp() throws Exception
    {
        context = new Context();
        FileSystem fs = getFileSystem("src/test/resources/actions");
        Configuration config = getConfig(fs, NameSequenceFactory.class, NameSequenceFactory.class);
        NameSequenceFactory nameSequenceFactory = new NameSequenceFactory(config);
        config = getConfig(fs, Templating.class, VelocityTemplating.class);
        LoggerFactory loggerFactory = new LoggerFactory(null);
        Logger logger = loggerFactory.getLogger(Templating.class);
        Templating templating = new VelocityTemplating(config, logger, fs);
        logger = loggerFactory.getLogger(MVCFinder.class);
        MutablePicoContainer container = new DefaultPicoContainer();
        container.registerComponentImplementation(Context.class);
        MVCFinder finder = new MVCFinder(container, logger, templating, nameSequenceFactory);
        SecurityHelper securityHelper = new SecurityHelper();
        actionExecutorValve = new ActionExecutorValve(finder, securityHelper);
        MVCContext mvcContext = new MVCContext();
        mvcContext.setAction(null);
        context.setAttribute(MVCContext.class, mvcContext);
    }

    public void testProcess() throws Exception
    {
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        actionExecutorValve.process(context);
        mvcContext.setAction("foo.TestAction");
        actionExecutorValve.process(context);
        mvcContext.setAction("Foo");
        try
        {
            actionExecutorValve.process(context);
            fail("Should throw the exception");
        }
        catch (ProcessingException e)
        {
            //ok!
        }
    }

}
