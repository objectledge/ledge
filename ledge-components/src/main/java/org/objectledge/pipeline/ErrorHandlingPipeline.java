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

package org.objectledge.pipeline;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;

/**
 * A Pipeline composed of sequences of Valves that provides error handling along the lines of Java 
 * try/catch/finally.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: ErrorHandlingPipeline.java,v 1.1 2004-01-23 08:47:26 fil Exp $
 */
public class ErrorHandlingPipeline
    implements Valve
{
    /** key to store the exception in the context */
    public static final String PIPELINE_EXCEPTION = "org.objectledge.pipeline.Pipeline.exception";
    
    private Logger logger;
    
    private Valve[] tryValves;
    
    private Valve[] catchValves;
    
    private Valve[] finallyValves;
    
    /**
     * Constructs a new instance of the pipeline.
     * 
     * @param logger the logger.
     * @param tryValves the valves to be used in the try stage.
     * @param catchValves the valves to be used in the catch stage.
     * @param finallyValves the valves to be used in the finaly stage.
     */
    public ErrorHandlingPipeline(Logger logger, Valve[] tryValves, Valve[] catchValves, 
        Valve[] finallyValves)
    {
        this.logger = logger;
        this.tryValves = tryValves;
        this.catchValves = catchValves;
        this.finallyValves = finallyValves;
    }

    /**
     * Runs the connected valves.
     * 
     * @param context the context.
     */    
    public void process(Context context)
    {
    	try
        {
        	for(int i = 0; i < tryValves.length; i++)
        	{
		   		tryValves[i].process(context);
        	}
        }
        ///CLOVER:OFF
        catch(VirtualMachineError e)
        {
            throw e;
        }
        catch(ThreadDeath e)
        {
            throw e;
        }
        ///CLOVER:ON
        catch(Throwable e)
        {
            logger.error("Exception in try section", e);
            context.setAttribute(PIPELINE_EXCEPTION, e);
            try
            {
                for(int i = 0; i < catchValves.length; i++)
                {
                    catchValves[i].process(context);
                }
            }
            ///CLOVER:OFF
            catch(VirtualMachineError ee)
            {
                throw ee;
            }
            catch(ThreadDeath ee)
            {
                throw ee;
            }
            ///CLOVER:ON
            catch(Throwable ee)
            {
                logger.error("Exception in catch section", ee);
            }
        }
        finally
        {
			for(int i = 0; i < finallyValves.length; i++)
			{
                try
                {
                    finallyValves[i].process(context);
                }
                ///CLOVER:OFF
                catch(VirtualMachineError ee)
                {
                    throw ee;
                }
                catch(ThreadDeath ee)
                {
                    throw ee;
                }
                ///CLOVER:ON
                catch(Throwable ee)
                {
                    logger.error("Exception in finally section", ee);
                }
			}
            context.removeAttribute(PIPELINE_EXCEPTION);
        }
    }
}
