package org.objectledge.hibernate;

import java.net.MalformedURLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.Startable;

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
 * @version $Id: HibernateSessionFactory.java,v 1.2 2005-07-29 14:39:24 rafal Exp $
 */
public class HibernateSessionFactory 
implements Startable
{
    private SessionFactory sessionFactory;

    public Session openHibernateSession() {
        return  sessionFactory.openSession();
    }
    
    public HibernateSessionFactory(Configuration config, Logger logger,
            FileSystem fs) 
    throws ConfigurationException, ClassNotFoundException, HibernateException, 
            MalformedURLException {

        logger.info("HibernateConfig starting...");
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        
        String xmlPath = "/config/org.objectledge.hibernate.HibernateSessionFactory.xml";  
        
        cfg.configure(fs.getResource(xmlPath));
       
        sessionFactory = cfg.buildSessionFactory();

        logger.info("HibernateConfig started");
    }

    public void start()
    {
        // nothing to do
    }

    public void stop()
    {
        // nothing to do
    }
}
