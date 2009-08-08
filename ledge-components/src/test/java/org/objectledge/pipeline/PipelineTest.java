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

package org.objectledge.pipeline;

import org.jcontainer.dna.Logger;
import org.jmock.Mock;
import org.objectledge.context.Context;
import org.objectledge.utils.LedgeTestCase;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class PipelineTest extends LedgeTestCase
{
    private Context context;
    private Mock loggerMock;
    private Mock tryValveMock;
    private Mock catchValveMock;
    private Mock finallyValveMock;

    private Logger logger;
    private Valve tryValve;
    private Valve catchValve;
    private Valve finallyValve;
    private Valve pipe; 

    public void setUp()
    {
        context = new Context();
        loggerMock = mock(Logger.class);
        tryValveMock = mock(Valve.class);
        catchValveMock = mock(Valve.class);
        finallyValveMock = mock(Valve.class);

        logger = (Logger)loggerMock.proxy();
        tryValve = (Valve)tryValveMock.proxy();
        catchValve = (Valve)catchValveMock.proxy();
        finallyValve = (Valve)finallyValveMock.proxy();
        pipe = new ErrorHandlingPipeline(logger, 
            new Valve[] { tryValve }, new Valve[] { catchValve }, new Valve[] { finallyValve });
    }

    public void testRun() throws Exception
    {
        tryValveMock.expects(once()).method("process");
        finallyValveMock.expects(once()).method("process");
        pipe.process(context);
    }
    
    public void testRun2() throws Exception
    {
        tryValveMock.expects(once()).method("process").
            will(throwException(new ProcessingException("foo")));
        catchValveMock.expects(once()).method("process");
        finallyValveMock.expects(once()).method("process");
        loggerMock.expects(once()).method("debug");
        pipe.process(context);
    }

    public void testRun3() throws Exception
    {
        tryValveMock.expects(once()).method("process").
            will(throwException(new ProcessingException("foo")));
        catchValveMock.expects(once()).method("process").
            will(throwException(new ProcessingException("foo")));
        loggerMock.expects(once()).method("debug");
        loggerMock.expects(once()).method("error");
        finallyValveMock.expects(once()).method("process");
        pipe.process(context);
    }
        
    public void testRun4() throws Exception
    {        
        tryValveMock.expects(once()).method("process");
        loggerMock.expects(once()).method("error");
        finallyValveMock.expects(once()).method("process").
            will(throwException(new ProcessingException("foo")));
        pipe.process(context);
    }
}
