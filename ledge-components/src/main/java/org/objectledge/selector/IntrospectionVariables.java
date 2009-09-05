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
package org.objectledge.selector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * An implementation of Variables interface that is able to introspect properties of Java objects
 * recursively.
 * 
 * <p>Variable names are tokenized on '.' characters. Each token must specify a valid property
 * name of the object that was the result of the evaluation of the preceeding token.</p>
 * 
 * <p>The attempts to evaluate a property named 'foo' are made in the following order:</p>
 * <p>
 *   <ol>
 *     <li>public non-void method 'getFoo'</li>
 *     <li>public method 'isFoo' returning Boolean or boolean</li>
 *     <li>public field 'foo'</li>
 *     <li>public non-void method get(String), called with "foo" parmeter</li>
 *     <li>public non-void method get(Object), called with "foo" parameter</li>
 *   </ol>
 * </p>
 * <p>The first successful evaluation in the above list winns. If none of the succeed, 
 * UndefinedVariableException is thrown.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: IntrospectionVariables.java,v 1.6 2005-02-21 16:28:01 zwierzem Exp $
 */
public class IntrospectionVariables
    implements Variables
{
    private Object object;
    
    /**
     * Creates an instance of the IntrospectionVariables.
     * 
     * @param object the object to introspect.
     */
    public IntrospectionVariables(Object object)
    {
        this.object = object;
    }
    
    /**
     * Creates an instance of the IntrospectionVariables operating on multiple objects.
     *  
     * <p>The keys in the map are used as the first tokens of the variable names.</p>
     * 
     * @param objects the objects to introspect.
     */
    public IntrospectionVariables(Map objects)
    {
        this((Object)objects);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
        throws EvaluationException
    {
        StringTokenizer st = new StringTokenizer(name,".");
        Object value = object;        
        while(st.hasMoreTokens() && value != null)
        {
            try
            {
                value = getProperty(value, st.nextToken());
            }
            catch(UndefinedVariableException e)
            {
                return false;
            }
        }
        return !st.hasMoreTokens();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object get(String name)
        throws UndefinedVariableException, EvaluationException
    {
        StringTokenizer st = new StringTokenizer(name,".");
        Object value = object;   
        StringBuilder buff = new StringBuilder(); 
        boolean first = true;    
        while(st.hasMoreTokens() && value != null)
        {
            String property = st.nextToken();
            if(first)
            {
                first = false;
            }
            else
            {
                buff.append('.');
            }
            buff.append(property);
            try
            {
                value = getProperty(value, property);
            }
            catch(UndefinedVariableException e)
            {
                throw new UndefinedVariableException(buff.toString());
            }
        }
        return value;
    }
    
    private Object getProperty(Object object, String property)
        throws UndefinedVariableException, EvaluationException
    {
        String capitalizedProperty = Character.toUpperCase(property.charAt(0))+
            property.substring(1);
        Class<?>[] emptyArgsSpec = new Class[0];
        Object[] args = new Object[0];
        Class<?> clazz = object.getClass();
        Method method;
        try
        {
            method = clazz.getMethod("get"+capitalizedProperty, emptyArgsSpec);
        }
        catch(NoSuchMethodException e)
        {
            try
            {
                method = clazz.getMethod("is"+capitalizedProperty, emptyArgsSpec);
                if(!method.getReturnType().equals(Boolean.class) && 
                   !method.getReturnType().equals(Boolean.TYPE))
                {
                    method = null;
                }
            }
            catch(NoSuchMethodException ee)
            {
                method = null;
            }
        }
        if(method != null && !method.getReturnType().equals(Void.TYPE))
        {
            try
            {
                return method.invoke(object, args);
            }
            catch(InvocationTargetException e)
            {
                throw new EvaluationException("exception while evaluating variable", e.getCause());
            }
            catch(Exception e)
            {
                ///CLOVER:OFF
                throw new EvaluationException("unexpected exception while evaluating variable", e);
                ///CLOVER:ON
            }
        }
        args = new Object[] { property };
        try
        {
            method = clazz.getMethod("get", new Class[] { String.class });
        }
        catch(NoSuchMethodException e)
        {
            try
            {
                method = clazz.getMethod("get", new Class[] { Object.class });
            }
            catch(NoSuchMethodException ee)
            {
                method = null;
            }
        }
        if(method != null && !method.getReturnType().equals(Void.TYPE))
        {
            try
            {
                return method.invoke(object, args);
            }
            catch(InvocationTargetException e)
            {
                throw new EvaluationException("exception while evaluating variable", e.getCause());
            }
            catch(Exception e)
            {
                ///CLOVER:OFF
                throw new EvaluationException("unexpected exception while evaluating variable", e);
                ///CLOVER:ON
            }
        }
        throw new UndefinedVariableException(property);
    }
}
