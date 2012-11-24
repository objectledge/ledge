// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import java.io.IOException;
import java.util.HashMap;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The hibernate session factory component.
 * 
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractHibernateSessionFactory.java,v 1.2 2006-03-13 18:22:50 zwierzem Exp $
 */
public abstract class AbstractHibernateSessionFactory 
implements HibernateSessionFactory
{
    protected SessionFactory sessionFactory;
    protected Interceptor interceptor;

    public AbstractHibernateSessionFactory(Logger logger, final FileSystem fs, HibernateConfigurator configurator,
        InterceptorFactory interceptorFactory) throws ConfigurationException
    {
        logger.info("HibernateConfig starting...");
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        cfg.setEntityResolver(new EntityResolver()
        {
            private HashMap<String, String> mapping = new HashMap<String, String>();
            {
                mapping.put("-//Hibernate/Hibernate Configuration DTD 3.0//EN",
                    "hibernate-configuration-3.0.dtd");
                mapping.put("-//Hibernate/Hibernate Mapping DTD 3.0//EN",
                    "hibernate-mapping-3.0.dtd");
            }
            
            public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException
            {
                InputSource is = new InputSource(
                     fs.getInputStream("/org/hibernate/"+mapping.get(publicId)));
                is.setPublicId(publicId);
                //is.setSystemId()
                return is;
            }
        });
        
        configurator.configure(cfg);
       
        sessionFactory = cfg.buildSessionFactory();

        if(interceptorFactory != null)
        {
            Interceptor interceptor = interceptorFactory.createInterceptor(sessionFactory);
            if(interceptor != null)
            {
                this.interceptor = interceptor;
            }
        }       
        logger.info("HibernateConfig started");
    }

    public AbstractHibernateSessionFactory(Logger logger, FileSystem fs,
        HibernateConfigurator configurator)
        throws ConfigurationException 
    {
        this(logger, fs, configurator, null);
    }

    /**
     * Opens the hibernate session.
     * 
     * @return the newly open hibernate session.
     */
    public Session openHibernateSession()
    {
        if(interceptor != null)
        {
            return sessionFactory.withOptions().interceptor(interceptor).openSession();
        }
        return sessionFactory.openSession();
    }

    public void start()
    {
        // nothing to do
    }

    public void stop()
    {
        // nothing to do
    }
    
    /**
     * Configurator configures the hibernate session factory, to be implemented by concrete
     * implementations.
     */
    protected interface HibernateConfigurator
    {
        /**
         * Provides the configuration data and calls one of <code>configure()</code> methods on
         * hibernate configuration object.
         * 
         * @param cfg the hibernate configuration object.
         * @throws ConfigurationException thrown on configuration problems.
         */
        public void configure(org.hibernate.cfg.Configuration cfg) throws ConfigurationException;
    }
}
