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
package org.objectledge.threads.impl;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.threads.Task;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: WorkerPool.java,v 1.1 2004-01-30 15:52:27 fil Exp $
 */
public class WorkerPool
{
    private Logger log;

    private Context context;
    
    private Valve cleanup;
    
    private ObjectPool pool;
    
    private ThreadGroup threadGroup;
    
    private int priority;
    
    private int counter = 1;

    /**
     * Creates a daemon thread.
     * 
     * @param priority the task's priority (see {@link java.lang.Thread} description).
     * @param threadGroup the thread group where the thread should belong.
     * @param log the logger to use.
     * @param context thread's processing context.
     * @param cleanup cleanup valve to invoke, should the task terminate.
     */
    public WorkerPool(int priority, ThreadGroup threadGroup, 
        Logger log, Context context, Valve cleanup)
    {
        this.priority = priority;
        this.threadGroup = threadGroup;
        this.log = log;
        this.context = context;
        this.cleanup = cleanup;
        this.pool = new GenericObjectPool(new ObjectFactory());
    }

    /**
     * Dispatches a Task using a Worker
     * 
     * @param task the task to dispatch.
     * @return the Worker running the task.
     * @throws ProcessingException if there is a proble with obtaing the worker.
     */
    public Worker dispatch(Task task)
        throws ProcessingException
    {
        try
        {
            Worker worker = (Worker)pool.borrowObject();
            worker.dispatch(task);
            return worker;
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to dispatch task", e);
        }
    }
    
    /**
     * Interface to commons-pool package. 
     */    
    private class ObjectFactory
        extends BasePoolableObjectFactory
    {
        /**
         * {@inheritDoc}
         */
        public Object makeObject()
        {
            return new Worker("worker #"+(counter++), priority, pool, threadGroup, 
                log, context, cleanup);
        }
    }
}
