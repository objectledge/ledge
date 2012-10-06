// 
// Copyright (c) 2006, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * The hibernate interceptor for creation of persistent objects using the container.
 * The container for objects is created per request.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PicofyingInterceptor.java,v 1.6 2006-01-26 14:25:34 zwierzem Exp $
 */
public class PicofyingInterceptor
    extends EmptyInterceptor
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private SessionFactory sessionFactory;
    private NonCachingPicoObjectInstantiator objectInstantiator;
    private Map<String, Class<?>> classByName = new HashMap<String, Class<?>>();
    
    /**
     * Creates a new <code>HibernatePicofier</code> object.
     * 
     * @param objectInstantiator the pico based object instantiator.
     * @param sessionFactory the Hibernate session factory. This is used to get meta data
     *        information, including the id property name, of new entities.
     */
    public PicofyingInterceptor(NonCachingPicoObjectInstantiator objectInstantiator, SessionFactory sessionFactory)
    {
        this.objectInstantiator = objectInstantiator;
        this.sessionFactory = sessionFactory;
    }

    /**
     * Instantiates a new persistent object with the given id. Uses Pico to inject dependencies into
     * the new object.
     * 
     * @param entityName the name of the class of the persistent object to be instantiated.
     * @param id the id of the object.
     * @return the newly instantiated (and Picofied) object.
     * @throw CallbackException if an error occurs
     */
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
    {
        try
        {
            // get persistent object
            Class<?> clazz = classByName.get(entityName);
            if(clazz == null)
            {
                clazz = Class.forName(entityName);
                classByName.put(entityName, clazz);
            }
            Object instance = objectInstantiator.instantiate(clazz);
            // set peristent object's id
            sessionFactory.getClassMetadata(clazz).setIdentifier(instance, id, (SessionImplementor)sessionFactory.getCurrentSession());
            return instance;
        }
        catch(ClassNotFoundException e)
        {
            throw new IllegalArgumentException("not a clas name: "+entityName, e);
        }
    }
}

