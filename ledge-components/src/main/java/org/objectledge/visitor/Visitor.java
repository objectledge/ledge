// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * General purpose visitor that is capable of traversing arbitrary object graphs.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Visitor.java,v 1.1 2005-03-18 10:26:48 rafal Exp $
 */
public abstract class Visitor<T>
{
    private final MethodMap methodMap;
    
    /**
     * Creates new Visitor instance.
     */
    public Visitor()
    {
        methodMap = MethodMap.getInstance(getClass());
    }    

    /**
     * Visit an object.
     * 
     * <p>An overloaded visit method apropriate for the object type will be looked up and 
     * invoked. If not exact match between method argument type and visited object type is found,
     * the methods will be matched sequentially against the object type. The order of matching
     * in such case can be controlled using the {@link DispatchOrder} annotations. If no
     * inexact match is found eiter, visitOther method will be invoked.</p>
     * 
     * @param o the visited object.
     */
    public final void visit(Object o)
    {
        methodMap.invoke(this, o);
    }

    /**
     * Invoked when no visit method applicable for object type is found.
     * 
     * <p>Default implementation does nothing.</p>
     * 
     * @param o the visited object.
     */
    protected void visitOther(T o)
    {
        
    }

    /**
     * Returns the successors of the objects in the traversal.
     *  
     * @param o the object.
     * @return iterator over objects's successors.
     */
    protected abstract Iterator<T> successors(T o);
 
    /**
     * Traverses object graph depth first - the successors will be visited before their 
     * predecessor.
     * 
     * @param o traversal origin.
     * @param v the visitor.
     */
    public static <T, OT extends T> void traverseDepthFirst(OT o, Visitor<T> v)
    {
        traverseDepthFirst(o, v, new HashSet());
    }
    
    /**
     * Traverses object graph breadth first - the successors will be visited after their 
     * predecessor.
     * 
     * @param o traversal origin.
     * @param v the visitor.
     */
    public static <T, OT extends T> void traverseBreadthFirst(OT o, Visitor<T> v)
    {
        traverseBreadthFirst(o, v, new HashSet());        
    }
    
    /**
     * Traverses object graph depth first - the successors will be visited before their predecessor.
     * 
     * @param o traversal origin.
     * @param v the visitor.
     * @param s seen objects set.
     */
    private static <T, OT extends T> void traverseDepthFirst(OT o, Visitor<T> v, Set s)
    {
        if(!s.contains(o))
        {
            s.add(o);
            Iterator<T> i = v.successors(o);
            while(i.hasNext())
            {
                traverseDepthFirst(i.next(), v, s);
            }
            v.visit(o);
        }
    }
    
    /**
     * Traverses object graph breadth first - the successors will be visited after their 
     * predecessor.
     * 
     * @param o traversal origin.
     * @param v the visitor.
     * @param s seen objects set.
     */
    private static <T, OT extends T> void traverseBreadthFirst(OT o, Visitor<T> v, Set s)
    {
        if(!s.contains(o))
        {
            s.add(o);
            v.visit(o);
            Iterator<T> i = v.successors(o);
            while(i.hasNext())
            {
                traverseBreadthFirst(i.next(), v, s);
            }
        }
    }

    /**
     * Helper class for managing overloaded visit methods.
     */
    private static class MethodMap
    {
        private final Map<Class, Method> methodMap = new LinkedHashMap<Class, Method>();
        
        /**
         * Prepares method map for the specific class.
         * 
         * <p>This constructor should not be called directly, use factory method {@link 
         * #getInstance(Class)}.</p>
         * 
         * @param c the class.
         */
        private MethodMap(Class c)
        {
            Method[] methods = c.getMethods();
            List<Method> ordered = new ArrayList<Method>();
            Map<Class, Method> unordered = new HashMap();
            for(int i = 0; i < methods.length; i++)
            {
                if(methods[i].getName().equals("visit")
                    && methods[i].getParameterTypes().length == 1
                    && !methods[i].getDeclaringClass().equals(Visitor.class))
                {
                    if(methods[i].isAnnotationPresent(DispatchOrder.class))
                    {
                        ordered.add(methods[i]);
                    }
                    else
                    {
                        unordered.put(methods[i].getParameterTypes()[0], methods[i]);
                    }
                }
            }
            Collections.sort(ordered, new Comparator<Method>() {
                public int compare(Method o1, Method o2)
                {
                    DispatchOrder do1 = o1.getAnnotation(DispatchOrder.class);
                    DispatchOrder do2 = o2.getAnnotation(DispatchOrder.class);
                    return do1.value() - do2.value();
                }
            });
            for(Method m : ordered)
            {
                methodMap.put(m.getParameterTypes()[0], m);
            }
            methodMap.putAll(unordered);
        }
        
        /**
         * Invokes the most apropriate method for the object's class.
         * 
         * @param v the visitor instance.
         * @param o the object.
         */
        public void invoke(Visitor v, Object o)
        {
            Method m = methodMap.get(o.getClass());
            if(m == null)
            {
                // look for inexact match
                loop: for(Class c : methodMap.keySet())
                {
                    if(c.isAssignableFrom(o.getClass()))
                    {
                        m = methodMap.get(c);
                        break loop;
                    }
                }
            }
            if(m != null)
            {
                try
                {
                    m.invoke(v, o);
                }
                catch(InvocationTargetException e)
                {
                    throw new RuntimeException(e.getTargetException());
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                v.visitOther(o);
            }
        }
 
        private static final Map<Class, MethodMap> CLASS_MAPS = new HashMap<Class, MethodMap>(); 
        
        public static synchronized MethodMap getInstance(Class c)
        {
            MethodMap map = CLASS_MAPS.get(c);
            if(map == null)
            {
                map = new MethodMap(c);
                CLASS_MAPS.put(c, map);
            }
            return map;
        }
    }
}
