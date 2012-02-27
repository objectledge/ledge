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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;

/**
 * Default event forwarder implementation.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DefaultEventWhiteboard.java,v 1.8 2008-01-01 22:53:36 rafal Exp $
 */
public class DefaultEventWhiteboard implements EventWhiteboard
{
    /** The logger. */
    private Logger logger;

    /**
     * The Map Listener interface -> ( Map trigger object -> Set of Listeners )
     */
    private Map<Class<?>, WeakHashMap<Object, WeakHashMap<Object, Object>>> interfaceMap = new HashMap<Class<?>, WeakHashMap<Object, WeakHashMap<Object, Object>>>();

    /** Created proxies for <code>Remote</code> objects. */
    private Map<Remote, Map<Class<?>, Object>> proxies = new WeakHashMap<Remote, Map<Class<?>, Object>>();

    /** The EventWhiteboardFactory */
    private EventWhiteboardFactory eventSystem;

    /** The CacheFactory. */
    private final CacheFactory cacheFactory;

    // Initailization ////////////////////////////////////////////////////////

    /**
     * Crates an EventWhiteboard instance.
     * 
     * @param eventSystem the event system component.
     * @param logger the logger.
     */
    public DefaultEventWhiteboard(EventWhiteboardFactory eventSystem, CacheFactory cacheFactory,
        Logger logger)
    {
        this.cacheFactory = cacheFactory;
        this.logger = logger;
        this.eventSystem = eventSystem;
        cacheFactory.registerForPeriodicExpunge((WeakHashMap<?,?>)proxies);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addListener(Class<?> iface, Object listener, Object object)
        throws IllegalArgumentException
    {
        if (!iface.isInterface())
        {
            throw new IllegalArgumentException(iface.getName() + " is not an interface type");
        }
        if (!iface.isInstance(listener))
        {
            throw new IllegalArgumentException("Handler Class<?> " + listener.getClass().getName() + 
                                                " does not implement " + iface.getName());
        }
        WeakHashMap<Object, WeakHashMap<Object, Object>> triggerMap = interfaceMap.get(iface);
        if (triggerMap == null)
        {
            triggerMap = new WeakHashMap<Object, WeakHashMap<Object, Object>>();
            cacheFactory.registerForPeriodicExpunge(triggerMap);
            interfaceMap.put(iface, triggerMap);
        }
        WeakHashMap<Object, Object> handlers = triggerMap.get(object);
        if (handlers == null)
        {
            handlers = new WeakHashMap<Object, Object>();
            cacheFactory.registerForPeriodicExpunge(handlers);
            triggerMap.put(object, handlers);
        }
        handlers.put(listener, null);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void removeListener(Class<?> iface, Object listener, Object object)
    {
        WeakHashMap<Object, WeakHashMap<Object, Object>> triggerMap = interfaceMap.get(iface);
        if (triggerMap != null)
        {
            WeakHashMap<Object, Object> handlers = triggerMap.get(object);
            if (handlers != null)
            {
                handlers.remove(listener);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addRemoteListener(Class<?> iface, Remote listener, Object object)
        throws IllegalArgumentException
    {
        InvocationHandler handler = new RemoteInvocationHandler(iface, listener);
        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                              new Class<?>[] { iface }, handler);
        Map<Class<?>,Object> ifMap = proxies.get(listener);
        if(ifMap == null)
        {
            ifMap = new HashMap<Class<?>,Object>();
            proxies.put(listener, ifMap);
        }
        ifMap.put(iface, proxy);
        addListener(iface, proxy, object); 
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void removeRemoteListener(Class<?> iface, Remote listener, Object object)
    {
        Map<Class<?>, Object> ifMap = proxies.get(listener);
        if(ifMap != null)
        {
            Object proxy = ifMap.get(iface);
            if(proxy != null)
            {
                removeListener(iface, proxy, object);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void fireEvent(Method method, Object[] args, Object object)
    {
        if(eventSystem.isAsynchronous())
        {
            eventSystem.enqueueEvent(this, method, args, object);
        }
        else
        {
            dispatchEvent(method, args, object);
        }
    }

    void dispatchEvent(Method method, Object[] args, Object object)
    {
        Class<?> iface = method.getDeclaringClass();
        Set<Object> currentHandlers = null;
        synchronized(this)
        {
            WeakHashMap<Object, WeakHashMap<Object, Object>> triggerMap = interfaceMap.get(iface);
            if(triggerMap != null)
            {
                WeakHashMap<Object, Object> handlers = triggerMap.get(object);
                if(handlers != null)
                {
                    currentHandlers = new HashSet<Object>(handlers.keySet());
                }
            }
        }
        if(currentHandlers != null)
        {
            Iterator<Object> i = currentHandlers.iterator();
            while(i.hasNext())
            {
                Object handler = i.next();
                try
                {
                    method.invoke(handler, args);
                }
                catch(ThreadDeath t)
                {
                    throw t;
                }
                catch(VirtualMachineError t)
                {
                    throw t;
                }
                catch(InvocationTargetException e)
                {
                    logger.error("Failed to invoke handler", e.getTargetException());
                }
                catch(Throwable t)
                {
                    logger.error("Failed to invoke handler", t);
                }
            }
        }
    }
}
