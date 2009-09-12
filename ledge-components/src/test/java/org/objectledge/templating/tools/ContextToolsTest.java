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

package org.objectledge.templating.tools;

import org.jmock.Mock;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.velocity.VelocityContext;
import org.objectledge.test.LedgeTestCase;

/**
 * Context tools test.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class ContextToolsTest extends LedgeTestCase
{
    private Mock toolFactoryMock = mock(ContextToolFactory.class);
    private ContextToolFactory toolFactory = (ContextToolFactory)toolFactoryMock.proxy();
    private TemplatingContext templatingContext;
    

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
		super.setUp();
    }

	public void testLoadTools()
        throws Exception
	{
        templatingContext = new VelocityContext();
	   	ContextTools contextTools = new ContextTools(new ContextToolFactory[]{toolFactory});
        toolFactoryMock.expects(once()).method("getKey").will(returnValue("foo"));
        toolFactoryMock.expects(once()).method("getTool").will(returnValue("bar"));
        contextTools.populateTools(templatingContext);
        assertEquals("bar", templatingContext.get("foo"));
        toolFactoryMock.expects(once()).method("getKey").will(returnValue("foo"));
        toolFactoryMock.expects(once()).method("recycleTool");
        contextTools.recycleTools(templatingContext);
    }
}
