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

import java.net.MalformedURLException;

import org.hibernate.HibernateException;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

/**
 * The hibernate session factory component.
 * 
 * <p>
 * An example configuration follows:
 * </p>
 * <pre>
 * &lt;?xml version='1.0' encoding='utf-8'?>
 * &lt;!DOCTYPE hibernate-configuration PUBLIC
 *       &quot;-//Hibernate/Hibernate Configuration DTD 3.0//EN&quot;
 *       &quot;http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd&quot;>
 * &lt;hibernate-configuration>
 *   &lt;session-factory>
 *     &lt;!-- Database connection settings -->
 *     &lt;property name="connection.driver_class">org.postgresql.Driver&lt;/property>
 *     &lt;property name="connection.url">jdbc:postgresql://localhost/test_db&lt;/property>
 *     &lt;property name="connection.username">test&lt;/property>
 *     &lt;property name="connection.password">test&lt;/property>
 * 
 *     &lt;!-- JDBC connection pool (use the built-in) -->
 *     &lt;property name="connection.pool_size">1&lt;/property>
 * 
 *     &lt;!-- SQL dialect -->
 *     &lt;property name="dialect">org.hibernate.dialect.PostgreSQLDialect&lt;/property>
 * 
 *     &lt;!-- Echo all executed SQL to stdout -->
 *     &lt;property name="show_sql">true&lt;/property>
 * 
 *     &lt;property name="query.substitutions">true 1, false 0&lt;/property>
 * 
 *     &lt;!--     Hibernate Security Service -->
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernatePermission"
 *         resource="org/objectledge/security/hibernate/HibernatePermission.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateResourceGroup"
 *         resource="org/objectledge/security/hibernate/HibernateResourceGroup.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateRole"
 *         resource="org/objectledge/security/hibernate/HibernateRole.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateRolePermission"
 *         resource="org/objectledge/security/hibernate/HibernateRolePermission.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateUser"
 *         resource="org/objectledge/security/hibernate/HibernateUser.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateUserGroupRole"
 *         resource="org/objectledge/security/hibernate/HibernateUserGroupRole.hbm.xml"/>
 *     &lt;mapping class="org.objectledge.security.hibernate.HibernateUserResourceGroupRo"
 *         resource="org/objectledge/security/hibernate/HibernateUserResourceGroupRo.hbm.xml"/>
 *   &lt;/session-factory>
 * &lt;/hibernate-configuration>
 *</pre>
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: BasicHibernateSessionFactory.java,v 1.1 2006-01-17 13:40:27 zwierzem Exp $
 */
public class BasicHibernateSessionFactory 
extends AbstractHibernateSessionFactory
{
    public BasicHibernateSessionFactory(Logger logger, final FileSystem fs,
        InterceptorFactory interceptorFactory)
        throws ConfigurationException
    {
        super(logger, fs, new HibernateConfigurator()
            {
                public void configure(org.hibernate.cfg.Configuration cfg) throws ConfigurationException
                {
                    String xmlPath = "/config/org.objectledge.hibernate.HibernateSessionFactory.xml";  
                    try
                    {
                        cfg.configure(fs.getResource(xmlPath));
                    }
                    catch(HibernateException e)
                    {
                        throw new ConfigurationException("cannot configure hibernate", e);
                    }
                    catch(MalformedURLException e)
                    {
                        throw new ConfigurationException("cannot access hibernate configuration file", e);
                    }
                }
            },
            interceptorFactory);
    }

    public BasicHibernateSessionFactory(Logger logger, final FileSystem fs)
        throws ConfigurationException 
    {
        this(logger, fs, null);
    }
}
