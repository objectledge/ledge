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

package org.objectledge.event;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.ArrayList;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

/**
 * Provides a common facility for associating event generators with
 * receivers. 
 *
 * <p>This component was created to remove the burden of managing listener lists
 * from objects that generate events.</p>
 *
 * <p>'Events' that are dealt with here are experssed with method calls on
 * concrete objects. Propagating these events to other JVMs will usually
 * require some additional effort, like <code>java.rmi.Remote</code>
 * listeners, or listeners generating messages and propagating them to other
 * JVMs using <code>Notification</code>, with notification listeners
 * parsing messages, and firing events on the other local
 * <code>EventSystem</code>.</p>
 *
 * <p>The {@link EventSystem} implements {@link EventForwarder}
 * interface, thus this component acts as the ledge instance wide event
 * forwarder. If need arsises, you can create additional private event
 * forwarders separate from the global one. See also {@link
 * InboundEventForwarder} and {@link OutboundEventForwarder}.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EventSystem.java,v 1.3 2004-02-12 11:43:05 pablo Exp $
 */
public class EventSystem implements EventForwarder
{
    // Memeber objects ///////////////////////////////////////////////////////

    /** The logger */
    private Logger logger;
    
    /** The event forwarder. */
    private EventForwarder forwarder;

    /** Asynchronous mode flag. */
    private boolean asynchronous;
    
    /** The event queue */
    private ArrayList queue;

    /**
     * Component constructor.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool component.
     */
    public EventSystem(Configuration config, Logger logger, ThreadPool threadPool)
    {
        this.logger = logger;
        forwarder = new DefaultEventForwarder(this, logger);
        asynchronous = config.getChild("asynchronous").getValueAsBoolean(false);
        if(asynchronous)
        {
            threadPool.runDaemon(new DispatcherTask());
        }
    }

    /**
     * Creates a private event forwarder.
     *
     * @return a new event forwarder.
     */
    public EventForwarder getForwarder()
    {
        return new DefaultEventForwarder(this, logger);
    }


    /**
     * {@inheritDoc}
     */
    public void addListener(Class iface, Object listener, Object object)
        throws IllegalArgumentException
    {
        forwarder.addListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(Class iface, Object listener, Object object)
    {
        forwarder.removeListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void addRemoteListener(Class iface, Remote listener, Object object)
        throws IllegalArgumentException
    {
        forwarder.addRemoteListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRemoteListener(Class iface, Remote listener, Object object)
        throws IllegalArgumentException
    {
        forwarder.removeRemoteListener(iface, listener, object);
    }

    /**
     * {@inheritDoc}
     */
    public void fireEvent(Method method, Object[] args, Object object)
    {
        forwarder.fireEvent(method, args, object);
    }

    /**
     * Returns <code>true</code> if the event service is running in asynchronos
     * mode. 
     *
     * @return <code>true</code> if the event service is running in asynchronos
     * mode. 
     */
    boolean isAsynchronous()
    {
        return this.asynchronous;
    }

    /**
    * Enqueues an event for asynchronous forwarding.
    *
    * @param forwarder the forwarder.
    * @param method the method.
    * @param args the argumentds.
    * @param object the trigger object. 
    */
    void enqueueEvent(DefaultEventForwarder forwarder, Method method, Object[] args, Object object)
    {
        if (!asynchronous)
        {
            throw new IllegalStateException("asynchronous mode disabled");
        }
        Event event = new Event(forwarder, method, args, object);
        synchronized (queue)
        {
            queue.add(event);
            queue.notify();
        }
    }

    // private ///////////////////////////////////////////////////////////////

    /**
     * Removes events from the queue and dispatches them with apropriate
     * forwaders. 
     */
    private class DispatcherTask extends Task
    {
        public String getName()
        {
            return "Event dispatcher";
        }

        /**
         * Main processing loop.
         */
        public void process(Context context)
        {
            Event event;
            loop : while (!Thread.interrupted())
            {
                synchronized (queue)
                {
                    if (queue.size() == 0)
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException e)
                        {
                            break loop;
                        }
                    }
                    event = (Event)queue.remove(0);
                }
                event.getForwarder().dispatchEvent(event.getMethod(), event.getArgs(),
                                                   event.getObject());
            }
        }

        /**
         * Terminates the task.
         */
        public synchronized void terminate(Thread thread)
        {
            thread.interrupt();
        }
    }

    /**
     * Represents an event enqueued for asynchronous forwarding.
     */
    private class Event
    {
        // member objects ////////////////////////////////////////////////////////

        /** The forwarder that enqueued this event. */
        private DefaultEventForwarder forwarder;

        /** The method to call. */
        private Method method;

        /** The arguments of the call. */
        private Object[] args;

        /** The trigger object. */
        private Object object;

        // initialization ////////////////////////////////////////////////////////

        /**
         * Contstructs an event.
         *
         * @param forwarder the forwarder.
         * @param method the method.
         * @param args the argumentds.
         * @param object the trigger object.
         */
        public Event(DefaultEventForwarder forwarder, Method method, Object[] args, Object object)
        {
            this.forwarder = forwarder;
            this.method = method;
            this.args = args;
            this.object = object;
        }

        // member object access //////////////////////////////////////////////////

        /**
         * Returns the event forwarder.
         *
         * @return the event forwarder.
         */
        public DefaultEventForwarder getForwarder()
        {
            return this.forwarder;
        }

        /**
         * Returns the method.
         * 
         * @return the method.
         */
        public Method getMethod()
        {
            return this.method;
        }

        /**
         * Returns the arguments.
         *
         * @return the arguments.
         */
        public Object[] getArgs()
        {
            return this.args;
        }

        /**
         * Returns the trigger object.
         *
         * @return the trigger object.
         */
        public Object getObject()
        {
            return this.object;
        }
    }
}
