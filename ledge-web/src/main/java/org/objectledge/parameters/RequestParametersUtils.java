// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.parameters;

import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Uses bean introspection to set writable properties of bean from
 * the parameters.
 * 
 * <p>This class includes code from Jakarta Turbine project and parts of it are subject to
 * Apache Software Foundation License included in ObjectLedge distribution
 * (file /ASFLicense.txt).</p>
 *
 * @author <a href="maito:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @created 2005-07-26
 * @version $Id: RequestParametersUtils.java,v 1.1 2005-08-26 07:14:15 zwierzem Exp $
 */
public class RequestParametersUtils
{
    /**
     * Uses bean introspection to set writable properties of bean from
     * the parameters, where a (case-insensitive) name match between
     * the bean property and the parameter is looked for.
     *
     * @param bean An Object.
     * @throws Exception 
     * @exception Exception a generic exception.
     */
    public static void setProperties(RequestParameters params, Object bean) 
    throws Exception 
    {
        
        Class<? extends Object> beanClass = bean.getClass();
        PropertyDescriptor[] props
                = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();

        for (int i = 0; i < props.length; i++)
        {
            String propname = props[i].getName();
            Method setter = props[i].getWriteMethod();
            if (setter != null &&
                    (containsKey(params, propname)     ))
//TODO                    
//                    ||
//                    containsDateSelectorKeys(propname) ||
//                    containsTimeSelectorKeys(propname)))
            {
                setProperty(params, bean, props[i]);
            }
        }
    }   


    /**
     * Uses bean introspection to set writable properties of bean from
     * the parameters, where a (case-insensitive) name match between
     * the bean property and the parameter is looked for.
     *
     * @param bean An Object.
     * @throws Exception 
     * @exception Exception a generic exception.
     */
    //TODO
    private static void setPropertiesForSearch(RequestParameters params, Object bean) 
    throws Exception 
    {
        
        Class<? extends Object> beanClass = bean.getClass();
        PropertyDescriptor[] props
                = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();

        for (int i = 0; i < props.length; i++)
        {
            String propname = props[i].getName();
            Method setter = props[i].getWriteMethod();
            if (setter != null &&
                    containsKey(params, propname)     )
            {
                setProperty(params, bean, props[i]);
            }
        }
    }    

    
    /**
     * Determine whether a given key has been inserted.  All keys are
     * stored in lowercase strings, so override method to account for
     * this.
     *
     * @param key An Object with the key to search for.
     * @return True if the object is found.
     */
    private static boolean containsKey(RequestParameters params, Object key)
    {
        String tmp = convert((String)key);
        return params.isDefined(tmp) && !params.get(tmp).equals("");
    }    
    
    /**
     * Trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    private static String convert(String value)
    {
        return value.trim();
        //.toLowerCase();
    }    
    

    /**
     * Set the property 'prop' in the bean to the value of the
     * corresponding parameters.  Supports all types supported by
     * getXXX methods plus a few more that come for free because
     * primitives have to be wrapped before being passed to invoke
     * anyway.
     *
     * @param bean An Object.
     * @param prop A PropertyDescriptor.
     * @exception Exception a generic exception.
     */
    private static void setProperty(RequestParameters params, Object bean,
                               PropertyDescriptor prop)
            throws Exception
    {
        if (prop instanceof IndexedPropertyDescriptor)
        {
            throw new Exception(prop.getName() +
                    " is an indexed property (not supported)");
        }

        Method setter = prop.getWriteMethod();
        if (setter == null)
        {
            throw new Exception(prop.getName() +
                    " is a read only property");
        }

        Class<?> propclass = prop.getPropertyType();
        Object[] args = {null};

        if (propclass == String.class)
        {
            args[0] = params.get(prop.getName());
        }
        else if (propclass == Integer.class || propclass == Integer.TYPE)
        {
            args[0] = params.getInt(prop.getName());
        }
        else if (propclass == Long.class || propclass == Long.TYPE)
        {
            args[0] = new Long(params.getLong(prop.getName()));
        }
        else if (propclass == Short.class || propclass == Short.TYPE)
        {
            args[0] = new Short((short)params.getLong(prop.getName()));
        }
        else if (propclass == Byte.class || propclass == Byte.TYPE)
        {
            args[0] = new Byte((byte)params.getLong(prop.getName()));
        }           
        else if (propclass == Character.class || propclass == Character.TYPE)
        {
            args[0] = new Character(params.get(prop.getName()).charAt(0));
        }           
        else if (propclass == Boolean.class || propclass == Boolean.TYPE)
        {
            args[0] = params.getBoolean(prop.getName());
        }
        else if (propclass == Double.class || propclass == Double.TYPE)
        {
            //FIXME
//            args[0] = new Double(getDouble(prop.getName()));
            args[0] = new Double(params.getFloat(prop.getName()));
        }
        else if (propclass == BigDecimal.class)
        {
            //FIXME
//            args[0] = getBigDecimal(prop.getName());
            args[0] = new BigDecimal(params.getFloat(prop.getName()));
        }
        else if (propclass == String[].class)
        {
            args[0] = params.getStrings(prop.getName());
        }
//        else if (propclass == Object.class)
//        {
//            args[0] = params.getObject(prop.getName());
//        }
        else if (propclass == int[].class)
        {
            args[0] = params.getInts(prop.getName());
        }
        else if (propclass == Integer[].class)
        {
//            args[0] = getIntObjects(prop.getName());
            args[0] = new Integer(params.getInt(prop.getName()));
        }
        else if (propclass == Date.class)
        {
            args[0] = params.getDate(prop.getName());
        }
//        else if (propclass == NumberKey.class)
//        {
//            args[0] = getNumberKey(prop.getName());
//        }
//        else if (propclass == StringKey.class)
//        {
//            args[0] = getStringKey(prop.getName());
//        }
        else
        {
            throw new Exception("property "
                    + prop.getName()
                    + " is of unsupported type "
                    + propclass.toString());
        }

        setter.invoke(bean, args);
    }
    
  
}

