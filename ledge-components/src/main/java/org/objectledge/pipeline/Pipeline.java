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
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Pipeline.java,v 1.7 2003-12-30 14:59:39 pablo Exp $
 */
public class Pipeline
    implements Runnable
{
    private static final String CONTEXT_EXCEPTIONS = "org.objectledge.pipeline.Pipeline.exceptions";
    
    private Context context;
    
    private Logger logger;
    
    private Runnable[] tryValves;
    
    private Runnable[] catchValves;
    
    private Runnable[] finallyValves;
    
    /**
     * Constructs a new instance of the pipeline.
     * 
     * @param context the context.
     * @param logger the logger.
     * @param tryValves the valves to be used in the try stage.
     * @param catchValves the valves to be used in the catch stage.
     * @param finallyValves the valves to be used in the finaly stage.
     */
    public Pipeline(Context context, Logger logger, Runnable[] tryValves, Runnable[] catchValves, 
        Runnable[] finallyValves)
    {
        this.context = context;
        this.logger = logger;
        this.tryValves = tryValves;
        this.catchValves = catchValves;
        this.finallyValves = finallyValves;
    }

    /**
     * Runs the connected valves.
     */    
    public void run()
    {
    	try
        {
        	for(int i = 0; i < tryValves.length; i++)
        	{
		   		tryValves[i].run();
        	}
        }
        catch(Exception e)
        {
        	// TODO - remove this exception and log and handle it.
        	logger.error("Exception occured",e);
        	for(int i = 0; i < catchValves.length; i++)
		    {
				catchValves[i].run();
		    }
        }
        finally
        {
			for(int i = 0; i < finallyValves.length; i++)
			{
				finallyValves[i].run();
			}
        }
    }
    
    /**
     * Get catch valves
     * 
     * @return the catch valves.
     */
    public Runnable[] getCatchValves()
    {
        return catchValves;
    }

    /**
     * Get finally valves
     * 
     * @return the finally valves.
     */
    public Runnable[] getFinallyValves()
    {
        return finallyValves;
    }

    /**
     * Get try valves
     * 
     * @return the try valves.
     */
    public Runnable[] getTryValves()
    {
        return tryValves;
    }

}
