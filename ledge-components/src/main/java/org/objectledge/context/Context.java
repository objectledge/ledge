/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 18, 2003
 */
package org.objectledge.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Context gives the application an ability to reference arbitrary objects using String names.
 * 
 * <p>The bindings are local the calling thread. This is a convinient way of passing data 
 * associated with a stream of processing without putting them into method attributes. This is 
 * especialy convinient with data that are not needed at a specific stage of processing, but may 
 * be needed by the nested stages - for example security credentials.</p>
 * 
 * <p><b>Note!</b>It is important to clear the context before the thread departs from within 
 * application boundaries - otherwise the references may prevent the objects (and as a result 
 * large graphs of objects including classes and classloaders) from being garbage collected, while
 * being otherwise being eligible to collection.</p>  
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Context.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public class Context
{
    /** storage of the context attributes, specific to a thread. */
    private static InheritableThreadLocal threadAttributes = new InheritableThreadLocal();

    /** singleton instance used by the {@link #getContext()} static method. */
    private static Context staticContext = new Context();

    /**
     * Return the value of a context attribute.
     * 
     * @param name the attribute name.
     * @return the value of the attribute.
     */
    public Object getAttribute(String name)
    {
        return getAttributes().get(name); 
    }

    /**
     * Sets a new value of a context attribute.
     * 
     * @param name the name of the attribute.
     * @param value the new value of the attribute.
     * @return the old value of the attribute.
     */    
    public Object setAttribute(String name, Object value)
    {
        return getAttributes().put(name, value);
    }
    
    /**
     * Removes a context attribute.
     * 
     * @param name the name of the attribute.
     * @return the old value of the attribute.
     */
    public Object removeAttribute(String name)
    {
        return getAttributes().remove(name);
    }
    
    /**
     * Removes all context attributes.
     */
    public void clearAttributes()
    {
        getAttributes().clear();
    }
    
    // - static access ------------------------------------------------------
    
    /**
     * Returns an instance of the Context.
     * 
     * <p>It is recommended that components are passed an instance of the
     * context class by their instantiators. In the places where this is
     * not apropriate this static method may be used to access the context
     * attributes.</p>
     * 
     * @return an instance of the Context.
     */
    public static Context getContext()
    {
        return staticContext;
    }
    
    // - implementation -----------------------------------------------------
    
    private Map getAttributes()
    {
        Map attributes = (Map)threadAttributes.get();
        if(attributes == null)
        {
            attributes = new HashMap();
            threadAttributes.set(attributes);
        }
        return attributes;
    }
}
