package org.objectledge.hibernate;

import java.io.File;

import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;

import org.picocontainer.Startable;

/**
 * The hibernate session factory component.
 * 
 * <p>
 * An example configuration follows:
 * </p>
 * <pre>
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
 *
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionFactory.java,v 1.1 2005-07-22 17:19:39 pablo Exp $
 */
public class HibernateSessionFactory 
implements Startable
{

	private SessionFactory sessionFactory;

	public Session openHibernateSession()
    {
		return 	sessionFactory.openSession();
	}
	
    private Logger logger;
    
	private static final int SESSION_FACTORY = 0;
	private static final int TOP = 0;
	private static final int SECURITY = 1;
	private static final int PROPERTY = 1;
	private static final int MAPPING = 2;
	private static final int CLASS_CACHE = 3;
	private static final int COLLECTION_CACHE = 4;
	private static final int LISTENER = 5;
	private static final int GRANT = 1;
	
	private static final String KEYS [][] = new String[][] 
    {
		{
			"session-factory",  //atributes=(name[opp])[m]
			"property",         //atributes=(name[m]) [opp] 
			"mapping",          //atributes=(resource[opp],file[opp],jar[opp],
								//           package[opp],class[opp])[opp][empty]
			"class-cache",      //atributes=(class[m],region[opp], 
								//           usage[m]{read-only,read-write,
								//				nonstrict-read-write,transactional}) [opp-1][empty]
			"collection-cache", //atributes=(collection[m],region[opp],usage[m]{read-only,read-write,
								//				nonstrict-read-write,transactional})[opp-1][empty]
			"listener"          //atributes=(type[m]{...},class[m])[opp][empty]
		},
		{
			"security",         //atributes=(context[m])[opp]
			"grant"             //atributes=(role[m], entity-name[m], actions[m])[empty]
		}
	};
	
	private static final String ROOT_CONFIG = "hibernate-configuration";

	private static String mappingAttrNames[] = new String[] {
		"resource", "file", "jar", "package", "class" 
	};
	private static final int RESOURCE = 0;	
	private static final int FILE = 1;	
	private static final int JAR = 2;	
	private static final int PACKAGE = 3;	
	private static final int CLASS = 4;	
	
    private void setProperty(org.hibernate.cfg.Configuration config, Configuration property) 
    throws ConfigurationException 
    {
        config.setProperty("hibernate."+property.getAttribute("name"), 
                property.getValue(""));
    }
    
	private void setMapping(org.hibernate.cfg.Configuration config, Configuration mapping) 
	throws MappingException, ConfigurationException, ClassNotFoundException
	{
		for( int i=0; i<mappingAttrNames.length; i++)
		{
			String str = mapping.getAttribute(mappingAttrNames[i], ""); 
			if( !str.equals("")) 
			{
				switch(i)
				{
					case RESOURCE:	
						config.addResource(str);
                        return;
					case FILE: 
					    config.addFile(str);
                        return;
					case JAR:
						config.addJar(new File(str));
                        return;
					case PACKAGE:
						throw new ConfigurationException("CAnnot map package", 
                                "hibernate-configuration > session-factory > maping > package", 
                                mapping.getLocation());
					case CLASS:
						config.addClass(Class.forName(str));
                        return;
				}
			}
		}
	}

	private void setClassCache(org.hibernate.cfg.Configuration config, Configuration classCache) 
    throws MappingException, ConfigurationException
	{
        //TODO: what about "region"[opp]
		config.setCacheConcurrencyStrategy(classCache.getAttribute("class"),
                classCache.getAttribute("usage"));
	}

	private void setCollectionCache(org.hibernate.cfg.Configuration config, Configuration collectionCache) 
    throws MappingException, ConfigurationException
	{
        //TODO: what about "region"[opp]
		config.setCollectionCacheConcurrencyStrategy(collectionCache.getAttribute("class"),
                collectionCache.getAttribute("usage"));
	}

	private void setListener(org.hibernate.cfg.Configuration config, Configuration listener) 
    throws ConfigurationException, ClassNotFoundException
	{
		config.setListener(listener.getAttribute("type"), 
                Class.forName(listener.getAttribute("class")));
	}

	private void setGrant(org.hibernate.cfg.Configuration config, Configuration grant) 
    throws ConfigurationException
	{
		throw new ConfigurationException("Cannot set grant", "security > grant", grant.getLocation());
	}
	
	
	public HibernateSessionFactory(Configuration config, Logger logger) 
	throws ConfigurationException, MappingException, ClassNotFoundException {
        this.logger = logger; 

        logger.info("HibernateConfig starting...");
        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        
        
		Configuration sessionFactoryCfg = config.getChild(KEYS[SESSION_FACTORY][TOP]);
		for(int k=PROPERTY; k<=LISTENER; k++)
		{
			Configuration properties[] = sessionFactoryCfg.getChildren(KEYS[SESSION_FACTORY][k]);
			for(int i=0; i<properties.length; i++) 
			{
				switch(k) 
				{
					case PROPERTY:
						setProperty(cfg, properties[i]);
                        break;
					case MAPPING:
						setMapping(cfg, properties[i]);
                        break;
					case CLASS_CACHE:
						setClassCache(cfg, properties[i]);
                        break;
					case COLLECTION_CACHE:
						setCollectionCache(cfg, properties[i]);;
                        break;
					case LISTENER:
						setListener(cfg, properties[i]);
                        break;
				}
			}
		}
		sessionFactoryCfg = null;

		Configuration security = config.getChild(KEYS[SECURITY][TOP]);
		
		Configuration grants[] = security.getChildren();
		for( int k=0; k<grants.length; k++)
		{
			setGrant(cfg, grants[k]);
		}
        
        sessionFactory = cfg.buildSessionFactory();

        logger.info("HibernateConfig started");
        
	}

    public void start()
    {
        // TODO Auto-generated method stub
        
    }

    public void stop()
    {
        // TODO Auto-generated method stub
        
    }
	
}
