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

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.SessionFactory;
import org.picocontainer.MutablePicoContainer;

/**
 * The hibernate interceptor for creation of persistent objects using the container.
 * The container for objects is created per request.
 * 
 * TODO: Consider using a pool of containers??
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PicofyingInterceptor.java,v 1.1 2006-01-11 16:57:58 zwierzem Exp $
 */
public class PicofyingInterceptor
    extends EmptyInterceptor
{
    private MutablePicoContainer container;

    private SessionFactory sessionFactory;

    /**
     * Creates a new <code>HibernatePicofier</code> object.
     * 
     * @param container the Pico container.
     * @param sessionFactory the Hibernate session factory. This is used to get meta data
     *        information, including the id property name, of new entities.
     */
    public PicofyingInterceptor(MutablePicoContainer parentContainer, SessionFactory sessionFactory)
    {
        this.container = parentContainer;
        this.sessionFactory = sessionFactory;
    }

    /**
     * Instantiates a new persistent object with the given id. Uses Pico to inject dependencies into
     * the new object.
     * 
     * @param serializable the id of the object.
     * @return the newly instantiated (and Picofied) object.
     * @throw CallbackException if an error occurs
     */
    public Object instantiate(Class clazz, Serializable id)
        throws CallbackException
    {
/*        MutablePicoContainer tempContainer = new DefaultPicoContainer(container);
        tempContainer.registerComponentImplementation(clazz);

        Object newEntity = tempContainer.getComponentInstance(clazz);
        ReflectionUtil.setProperty(newEntity, getIdProperty(clazz), id);
        return newEntity;
        */
        return null;
    }

/**    private String getIdProperty(Class clazz)
        throws CallbackException
    {
        try
        {
            return sessionFactory.getClassMetadata(clazz).getIdentifierPropertyName();
        }
        catch(HibernateException e)
        {
            throw new CallbackException("Error getting identifier property for class " + clazz, e);
        }
    }*/
}
