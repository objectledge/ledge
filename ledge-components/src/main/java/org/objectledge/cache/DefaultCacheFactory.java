//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.cache;

import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.ConfigurableValueFactory;
import org.objectledge.cache.spi.DistributedMap;
import org.objectledge.cache.spi.FactoryMap;
import org.objectledge.cache.spi.LRUMap;
import org.objectledge.cache.spi.LayeredMap;
import org.objectledge.cache.spi.SoftMap;
import org.objectledge.cache.spi.StatisticsMap;
import org.objectledge.cache.spi.TimeoutMap;
import org.objectledge.context.Context;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.notification.Notification;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

/**
 * DefaultCacheFactory component.
 * 
 * <p>An instance declaration is a list of map layer declarations.
 * A map layer declaration is composed of a map type name (both
 * standard and custom types may be used) and a pair of parentheses. Optional
 * configuration string may be placed betweene the parens. The meaning of the
 * string is depenant upon the implementation class. If there is a non-empty
 * configuration string, the map implementation is required to support {@link
 * ConfigurableMap} interface. Layers declared first are the deeper than those
 * declared later. The implemenation of map types used on layers deeper than
 * layer one are required to support {@link LayeredMap} interface. The layer
 * number <i>n</i> becomes the delegate of the layer <i>n+1</i>.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DefaultCacheFactory.java,v 1.2 2004-12-23 07:17:08 rafal Exp $
 */
public class DefaultCacheFactory
    implements CacheFactorySPI, CacheFactory
{
    // constants ////////////////////////////////////////////////////////////
    /** Type constant for HashMap. */
    public static final String HASH_MAP_TYPE = "HashMap";

    /** Type constant for TimeoutMap. */
    public static final String TIMEOUT_MAP_TYPE = "TimeoutMap";

    /** Type constant for LRUMap. */
    public static final String LRU_MAP_TYPE = "LRUMap";

    /** Type constant for SoftMap. */
    public static final String SOFT_MAP_TYPE = "SoftMap";

    /** Type constant for FactoryMap. */
    public static final String FACTORY_MAP_TYPE = "FactoryMap";

    /** Type constant for DistributedMap. */
    public static final String DISTRIBUTED_MAP_TYPE = "DistributedMap";

    /** Type constant for StatisticsMap. */
    public static final String STATISTICS_MAP_TYPE = "StatisticsMap";
    
    /** The default implementation HashMap implementation. */
    public static final String HASH_MAP_CLASS_DEFALUT =
        "java.util.HashMap";

    /** The default implementation TimeoutMap implementation. */
    public static final String TIMEOUT_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.TimeoutMapImpl";

    /** The default implementation LRUMap implementation. */
    public static final String LRU_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.LRUMapImpl";
    
    /** The default implementation SoftMap implementation. */
    public static final String SOFT_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.SoftMapImpl";

    /** The default implementation DistributedMap implementation. */
    public static final String DISTRIBUTED_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.DistributedMapImpl";

    /** The default implementation FactoryMap implementation. */
    public static final String FACTORY_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.FactoryMapImpl";

    /** The default implementation StatisticsMap implementation. */
    public static final String STATISTICS_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.StatisticsMapImpl";

    // member objects ////////////////////////////////////////////////////////
    
    /** The registered StatisticsMaps */
    private List statistics = new ArrayList();

    /** Mapping of *_TYPE constants into configured implementation classes. */
    private Map implClasses = new HashMap();

    /** instance prepared configurations map. */
    private Map instanceConfigurations = new HashMap();
    
    /** Factory configurations */
    private Map factoryConfigurations = new HashMap();

    /** Configured map instances. */
    private Map instances = new HashMap();

    /** DelayedUpdate queue (target update time -&gt; object)*/
    private SortedMap queue = new TreeMap();

    /** DelayedUpdate queue helper map (object -&gt; target update time)*/
    private Map queueHelper = new HashMap();

    /** The configuration */
    private Configuration config;
    
    /** The logging facility. */
    private Logger logger;
    
    /** The thread pool */
    private ThreadPool threadPool;
    
    /** The notification */
    private Notification notification;
    
    /** The persistence */
    private Persistence persistence;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Component constructor.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool.
     * @param notification the notification.
     * @param persistence the persistence.
     * @throws ConfigurationException thrown if configuration is invalid.
     * @throws ClassNotFoundException thrown if one of the class not found.
     */
    public DefaultCacheFactory(Configuration config, Logger logger, 
                    ThreadPool threadPool, Notification notification,
                    Persistence persistence)
        throws ConfigurationException, ClassNotFoundException
    {
        this.config = config;
        this.logger = logger;
        this.threadPool = threadPool;
        this.notification = notification;
        this.persistence = persistence;
        
        Map classMap = new HashMap();
        classMap.put(HASH_MAP_TYPE, HASH_MAP_CLASS_DEFALUT);
        classMap.put(TIMEOUT_MAP_TYPE, TIMEOUT_MAP_CLASS_DEFALUT);
        classMap.put(LRU_MAP_TYPE, LRU_MAP_CLASS_DEFALUT);
        classMap.put(SOFT_MAP_TYPE, SOFT_MAP_CLASS_DEFALUT);
        classMap.put(DISTRIBUTED_MAP_TYPE, DISTRIBUTED_MAP_CLASS_DEFALUT);
        classMap.put(FACTORY_MAP_TYPE, FACTORY_MAP_CLASS_DEFALUT);
        classMap.put(STATISTICS_MAP_TYPE, STATISTICS_MAP_CLASS_DEFALUT);
        
        Map ifaceMap = new HashMap();
        ifaceMap.put(HASH_MAP_TYPE, Map.class);
        ifaceMap.put(TIMEOUT_MAP_TYPE, TimeoutMap.class);
        ifaceMap.put(LRU_MAP_TYPE, LRUMap.class);
        ifaceMap.put(SOFT_MAP_TYPE, SoftMap.class);
        ifaceMap.put(DISTRIBUTED_MAP_TYPE, DistributedMap.class);
        ifaceMap.put(FACTORY_MAP_TYPE, FactoryMap.class);
        ifaceMap.put(STATISTICS_MAP_TYPE, StatisticsMap.class);
        
        Configuration[] custom = config.getChildren("implementation");
        for(int i=0; i<custom.length; i++)
        {
            String type = custom[i].getAttribute("type");
            String clazz = custom[i].getAttribute("class");
            classMap.put(type,clazz);
        }
        Iterator it = classMap.keySet().iterator();
        while(it.hasNext())
        {
            String type = (String)it.next();
            String clazz = (String)classMap.get(type);
            Class iface = (Class)ifaceMap.get(type);
            if(iface == null)
            {
                iface = Map.class;
            }
            initImpl(type, clazz, iface);    
        }
        
        Configuration[] aliases = config.getChildren("alias");
        for(int i = 0; i < aliases.length; i++)
        {
            String name = aliases[i].getAttribute("name");
            instanceConfigurations.put(name,aliases[i]);
        }
        Configuration[] factories = config.getChildren("factory");
        for(int i = 0; i < factories.length; i++)
        {
            String name = factories[i].getAttribute("name");
            factoryConfigurations.put(name,factories[i]);
        }
        Configuration[] instanceNodes = config.getChildren("instance");
        for(int i =0; i < instanceNodes.length;i++)
        {
            String name = instanceNodes[i].getAttribute("name");
            Configuration instanceConfig = instanceNodes[i];
            String alias = instanceNodes[i].getAttribute("alias",null);
            if(alias != null)
            {
                instanceConfig = (Configuration)instanceConfigurations.get(alias);
                if(instanceConfig == null)
                {
                    throw new ComponentInitializationError(
                        "cannot find conifured alias: '"+alias+"'");
                }
            }
            Map map = buildInstance(name, instanceConfig);
            instances.put(name, map);
        }
        threadPool.runDaemon(new DelayedUpdateTask());
    }                          

    // CacheFactorySPI interface ////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public Map getMap(String type)
    {
        Class cl = (Class)implClasses.get(type);
        if(cl == null)
        {
            throw new IllegalArgumentException("unknown map type "+type);
        }
        try
        {
            return (Map)cl.newInstance();
        }
        ///CLOVER:OFF
        catch(VirtualMachineError t)
        {
            throw t;
        }
        catch(ThreadDeath t)
        {
            throw t;
        }
        catch(Throwable t)
        {
            throw new RuntimeException("failed to instantaite map of type "+type, t);
        }
        ///CLOVER:ON
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Map getInstance(String name)
    {
        Map map = (Map)instances.get(name);
        if(map == null)
        {
            throw new IllegalArgumentException("configuration "+name+
                                                " not defined in config file");
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Map getInstance(String name, String configAlias)
        throws ConfigurationException
    {
        Map map = (Map)instances.get(name);
        if(map == null)
        {
            Configuration instanceConfiguration = 
                (Configuration)instanceConfigurations.get(configAlias);
            if(instanceConfiguration == null)
            {
                throw new IllegalArgumentException("configuration "+configAlias+
                                                   " not defined in config file");
            }
            map = buildInstance(name, instanceConfiguration);
            instances.put(name, map);
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public ValueFactory getValueFactory(String factory, String map)
    {
        Configuration factoryConfig = (Configuration)factoryConfigurations.get(factory);
        String fclass = factoryConfig.getAttribute("class",null);
        Object obj;
        if(fclass == null)
        {
            throw new IllegalArgumentException("factory "+factory+" not defined in config file");
        }
        try
        {
            obj = Class.forName(fclass).newInstance();
        }
        catch(ClassNotFoundException e)
        {
            throw new IllegalArgumentException(fclass+" not found");
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException(fclass+" not found");
        }
        
        if(!(obj instanceof ConfigurableValueFactory))
        {
            throw new IllegalArgumentException(fclass+" does not implement " +
                                               "ConfigurableValueFactory interface");
        } 
        ((ConfigurableValueFactory)obj).configure(this, map, factoryConfig);
        return (ValueFactory)obj;
    }

    /**
     * {@inheritDoc}
     */
    public Map getHashMap()
    {
        Map map = getMap(HASH_MAP_TYPE);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public Map getTimeoutMap(long timeoutMillis)
    {
        TimeoutMap map = (TimeoutMap)getMap(TIMEOUT_MAP_TYPE);
        map.setTimeout(timeoutMillis);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map getLRUMap(int capacity)
    {
        LRUMap map = (LRUMap)getMap(LRU_MAP_TYPE);
        map.setCapacity(capacity);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map getSoftMap(int protect)
    {
        SoftMap map = (SoftMap)getMap(SOFT_MAP_TYPE);
        map.setProtect(protect);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    public org.objectledge.cache.DistributedMap getDistributedMap(String name, Map delegate)
    {
        DistributedMap map = (DistributedMap)getMap(DISTRIBUTED_MAP_TYPE);
        map.setDelegate(delegate);
        map.attach(notification, name);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public Map getFactoryMap(ValueFactory factory, Map delegate)
    {
        FactoryMap map = (FactoryMap)getMap(FACTORY_MAP_TYPE);
        map.setDelegate(delegate);
        map.setFactory(factory);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map getStatisticsMap(String name, Map delegate)
    {
        StatisticsMap map = (StatisticsMap)getMap(STATISTICS_MAP_TYPE);
        map.setDelegate(delegate);
        map.setName(name);
        addStatisticsMap(map);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public ValueFactory getPersitenceValueFactory(Class valueClass)
    {
        PersistenceValueFactory factory = new PersistenceValueFactory();
        factory.init(valueClass, persistence);
        return factory;
    }

    /**
     * {@inheritDoc}
     */
    public void register(DelayedUpdate object)
    {
        synchronized(queue)
        {
            // queue helper provides a reverse mapping, so we don't need have
            // to perform liner search
            Long target = (Long)queueHelper.get(object);
            Set set;
            if(target != null)
            {
                set = (Set)queue.get(target);
                set.remove(object);
                if(set.isEmpty())
                {
                    queue.remove(target);
                }
            }
            target = new Long(System.currentTimeMillis()+object.getUpdateLatency());
            set = (Set)queue.get(target);
            if(set == null)
            {
                set = new HashSet();
                queue.put(target, set);
                queueHelper.put(object, target);
            }
            set.add(object);
            queue.notify();
        }
    }

    /**
     * Prints the report on the specified PrintWriter.
     *
     * @param out the PrintWriter to print report into.
     */
    public void getStatus(PrintWriter out)
    {
        synchronized(statistics)
        {
            for(int i=0; i<statistics.size(); i++)
            {
                out.print(((StatisticsMap)statistics.get(i)).getStatistics());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addStatisticsMap(StatisticsMap map)
    {
        synchronized(statistics)
        {
            statistics.add(map);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Notification getNotification()
    {
        return notification;
    }

    /**
     * {@inheritDoc}
     */
    public Persistence getPersistence()
    {
        return persistence;
    }
    // implementation ////////////////////////////////////////////////////////

    /**
     * Initializes an entry in the {@link #implClasses} map.
     *
     * @param type the map type.
     * @param className the implementation class name.
     * @param iface the interface the implementation class must support.
     * @throws ClassNotFoundException if class couldn't be found.
     */
    private void initImpl(String type, String className, Class iface)
        throws ClassNotFoundException
    {
        Class cl = Class.forName(className);
        String reason = null;
        int mod = cl.getModifiers();
        if(Modifier.isInterface(mod) || Modifier.isAbstract(mod))
        {
            reason =  "it is an abstract or interface class";
        }
        try
        {
            cl.getConstructor( new Class[] {} );
        }
        catch(NoSuchMethodException e)
        {
            reason = " it has no public no-arg constructor";
        }                      
        if(!iface.isAssignableFrom(cl))
        {
            reason = " it does not implement "+iface.getName()+" interface";
        }
        if(reason != null)
        {
            throw new ComponentInitializationError(className+" can't be used as "+type+
                                         " implementation because "+reason);
        }
        implClasses.put(type, cl);
    }

    /**
     * Creates a new configurable map instance.
     *
     * @param name instance's name.
     * @param conf inststance's configuration elements.
     */ 
    private Map buildInstance(String name, Configuration conf)
        throws ConfigurationException
    {
        Map previous = null;
        Map current = null;
        Configuration[] mapConfigs = conf.getChildren("config"); 
        for(int i=0; i < mapConfigs.length; i++)
        {
            String num = (i == 0) ? "st" :
                ( (i == 1) ? "nd" : 
                  ( (i == 2) ? "rd" : "th" ) );
            num = (i+1)+num;
                
            String s = mapConfigs[i].getValue().trim();
            int op = s.indexOf('(');
            int cp = (op > 0) ? s.indexOf(')',op) : -1;
            if(op < 1 || cp < 0 || cp < s.length() - 1)
            {
                throw new IllegalArgumentException(num+" entry in "+name+" configuration "+
                                             "is malformed: MapType([config]) expected");
            }
            String type = s.substring(0,op);
            String mapConfig = s.substring(op+1,cp);
            current = getMap(type);
            if(i > 0)
            {
                if(current instanceof LayeredMap)
                {
                    ((LayeredMap)current).setDelegate(previous);
                }
                else
                {
                    throw new IllegalArgumentException(type+" was specified as the "+
                        num+" entry in "+name+" configuration, but it's "+
                        "implementation does not support LayeredMap interface");
                }
            }
            if(current instanceof ConfigurableMap)
            {
                ((ConfigurableMap)current).configure(this,name,mapConfig);
            }
            else
            {
                if(mapConfig.length() != 0)
                {
                    throw new ComponentInitializationError("configuration was specified " +                        "for the "+num+ " entry in "+name+" configuration, but "+
                        type+" implementation does not support ConfigurableMap interface");
                }
            }
            previous = current;
        }
        return current;
    }

    /**
     * The task that performs delayed updates.
     */
    private class DelayedUpdateTask
        extends Task
    {
        public String getName()
        {
            return "Delayed update sheduler";
        }
        
        public void process(Context context)
        {
            synchronized(queue)
            {
                long now;
                loop: while(!Thread.interrupted())
                {
                    // queue is empty - wait indefinetely
                    if(queue.size() == 0)
                    {
                        try
                        {
                            queue.wait();
                        }
                        catch(InterruptedException e)
                        {
                            break loop;
                        }
                    }
                    // there is something in the queue
                    now = System.currentTimeMillis();
                    Long first = (Long)queue.firstKey();
                    while(!queue.isEmpty() && first.longValue() <= now)
                    {
                        // the first element of the que has reached or passed
                        // it's target time
                        Set set = (Set)queue.remove(first);
                        Iterator i = set.iterator();
                        while(i.hasNext())
                        {
                            DelayedUpdate obj = (DelayedUpdate)i.next();
                            queueHelper.remove(obj);
                            try
                            {
                                obj.update();
                            }
                            ///CLOVER:OFF
                            catch(VirtualMachineError e)
                            {
                                throw e;
                            }
                            catch(ThreadDeath e)
                            {
                                throw e;
                            }
                            catch(Throwable e)
                            {
                                logger.error("exception in delayed update thread", e);
                            }
                            ///CLOVER:ON
                        }
                        if(!queue.isEmpty())
                        {
                            first = (Long)queue.firstKey();
                        }
                    }
                    // wait for the first element's target time
                    if(!queue.isEmpty())
                    {
                        try
                        {
                            queue.wait(first.longValue() - now);
                        }
                        catch(InterruptedException e)
                        {
                            break loop;
                        }
                    }
                }
            }
        }
        
        public void terminate(Thread t)
        {
            t.interrupt();
        }
    }
}
