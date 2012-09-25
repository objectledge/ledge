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
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.ConfigurableValueFactory;
import org.objectledge.cache.spi.DistributedMap;
import org.objectledge.cache.spi.FactoryMap;
import org.objectledge.cache.spi.GuavaCache;
import org.objectledge.cache.spi.LRUMap;
import org.objectledge.cache.spi.LayeredMap;
import org.objectledge.cache.spi.SoftMap;
import org.objectledge.cache.spi.StatisticsMap;
import org.objectledge.cache.spi.TimeoutMap;
import org.objectledge.context.Context;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.notification.Notification;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.statistics.AbstractMuninGraph;
import org.objectledge.statistics.MuninGraph;
import org.objectledge.statistics.StatisticsProvider;
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
 * @version $Id: DefaultCacheFactory.java,v 1.12 2008-01-20 15:17:50 rafal Exp $
 */
public class DefaultCacheFactory
    implements CacheFactorySPI, CacheFactory, StatisticsProvider
{
    // constants ////////////////////////////////////////////////////////////
    /** Type constant for HashMap. */
    public static final String HASH_MAP_TYPE = "HashMap";
    
    /** Type constant for LongKeyOpenHashMap. */
    public static final String LONG_KEY_OPEN_HASH_MAP_TYPE = "LongKeyOpenHashMap";

    /** Type constant for LongKeyOpenHashMap. */
    public static final String LONG_KEY_CHAINED_HASH_MAP_TYPE = "LongKeyChainedHashMap";

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

    /** Type constant for ForgetFullMap. */
    public static final String FORGETFULL_MAP_TYPE = "ForgetfullMap";
    
    /** Type contstant for GuavaCache. */
    public static final String GUAVA_CACHE_TYPE = "GuavaCache";
    
    /** The default implementation HashMap implementation. */
    public static final String HASH_MAP_CLASS_DEFALUT =
        "java.util.HashMap";

    /** The default LongKeyOpenHashMap implementation */
    public static final String LONG_KEY_OPEN_HASH_MAP_DEFAULT = 
        "org.objectledge.cache.impl.LongKeyOpenHashMapImpl";

    /** The default LongKeyChainedHashMap implementation */
    public static final String LONG_KEY_CHAINED_HASH_MAP_DEFAULT = 
        "org.objectledge.cache.impl.LongKeyChainedHashMapImpl";
    
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

    /** The default implementation StatisticsMap implementation. */
    public static final String FORGETFULL_MAP_CLASS_DEFALUT =
        "org.objectledge.cache.impl.ForgetfullMapImpl";
    
    /** The implementation of GuavaCache */
    public static final String GUAVA_CACHE_CLASS_DEFAULT = 
         "org.objectledge.cache.impl.GuavaCacheImpl";
    
    // member objects ////////////////////////////////////////////////////////
    
    /** The registered StatisticsMaps */
    private List<StatisticsMap<?,?>> statistics = new ArrayList<StatisticsMap<?,?>>();

    /** Mapping of *_TYPE constants into configured implementation classes. */
    private Map<String,Class<?>> implClasses = new HashMap<String,Class<?>>();

    /** instance prepared configurations map. */
    private Map<String,Configuration> instanceConfigurations = new HashMap<String,Configuration>();
    
    /** Factory configurations */
    private Map<String,Configuration> factoryConfigurations = new HashMap<String,Configuration>();

    /** Configured map instances. */
    private Map<String,Map<?,?>> instances = new HashMap<String,Map<?,?>>();

    /** DelayedUpdate queue (target update time -&gt; object)*/
    private SortedMap<Long,Set<DelayedUpdate>> queue = new TreeMap<Long,Set<DelayedUpdate>>();

    /** DelayedUpdate queue helper map (object -&gt; target update time)*/
    private Map<DelayedUpdate,Long> queueHelper = new HashMap<DelayedUpdate,Long>();

    /** Registered WeakHashMap objects. */
    private List<WeakReference<WeakHashMap<?,?>>> weakHashMaps = new LinkedList<WeakReference<WeakHashMap<?,?>>>();
    
    /** The logging facility. */
    private Logger logger;
    
    /** The notification */
    private Notification notification;
    
    /** The persistence */
    private Persistence persistence;
    
    /** Configured Munin graphs */
    private final MuninGraph[] graphs;
    
    // initialization ////////////////////////////////////////////////////////

    /**
     * Component constructor.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param threadPool the thread pool.
     * @param notification the notification.
     * @param persistence the persistence.
     * @param fileSystem TODO
     * @throws ConfigurationException thrown if configuration is invalid.
     * @throws ClassNotFoundException thrown if one of the class not found.
     */
    public DefaultCacheFactory(Configuration config, Logger logger, 
                    ThreadPool threadPool, Notification notification,
                    Persistence persistence, FileSystem fileSystem)
        throws ConfigurationException, ClassNotFoundException
    {
        this.logger = logger;
        this.notification = notification;
        this.persistence = persistence;
        
        Map<String, String> classMap = new HashMap<String, String>();
        classMap.put(HASH_MAP_TYPE, HASH_MAP_CLASS_DEFALUT);
        classMap.put(LONG_KEY_OPEN_HASH_MAP_TYPE, LONG_KEY_OPEN_HASH_MAP_DEFAULT);
        classMap.put(LONG_KEY_CHAINED_HASH_MAP_TYPE, LONG_KEY_CHAINED_HASH_MAP_DEFAULT);
        classMap.put(TIMEOUT_MAP_TYPE, TIMEOUT_MAP_CLASS_DEFALUT);
        classMap.put(LRU_MAP_TYPE, LRU_MAP_CLASS_DEFALUT);
        classMap.put(SOFT_MAP_TYPE, SOFT_MAP_CLASS_DEFALUT);
        classMap.put(DISTRIBUTED_MAP_TYPE, DISTRIBUTED_MAP_CLASS_DEFALUT);
        classMap.put(FACTORY_MAP_TYPE, FACTORY_MAP_CLASS_DEFALUT);
        classMap.put(STATISTICS_MAP_TYPE, STATISTICS_MAP_CLASS_DEFALUT);
        classMap.put(FORGETFULL_MAP_TYPE, FORGETFULL_MAP_CLASS_DEFALUT);
        classMap.put(GUAVA_CACHE_TYPE, GUAVA_CACHE_CLASS_DEFAULT);
        
        Map<String, Class<?>> ifaceMap = new HashMap<String, Class<?>>();
        ifaceMap.put(TIMEOUT_MAP_TYPE, TimeoutMap.class);
        ifaceMap.put(LRU_MAP_TYPE, LRUMap.class);
        ifaceMap.put(SOFT_MAP_TYPE, SoftMap.class);
        ifaceMap.put(DISTRIBUTED_MAP_TYPE, DistributedMap.class);
        ifaceMap.put(FACTORY_MAP_TYPE, FactoryMap.class);
        ifaceMap.put(STATISTICS_MAP_TYPE, StatisticsMap.class);
        ifaceMap.put(GUAVA_CACHE_TYPE, GuavaCache.class);
        
        Configuration[] custom = config.getChildren("implementation");
        for(int i=0; i<custom.length; i++)
        {
            String type = custom[i].getAttribute("type");
            String clazz = custom[i].getAttribute("class");
            classMap.put(type,clazz);
        }
        Iterator<String> it = classMap.keySet().iterator();
        while(it.hasNext())
        {
            String type = it.next();
            String clazz = classMap.get(type);
            Class<?> iface = ifaceMap.get(type);
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
        List<MuninGraph> graphList = new ArrayList<MuninGraph>();
        Configuration[] instanceNodes = config.getChildren("instance");
        for(int i =0; i < instanceNodes.length;i++)
        {
            String name = instanceNodes[i].getAttribute("name");
            Configuration instanceConfig = instanceNodes[i];
            String alias = instanceNodes[i].getAttribute("alias",null);
            if(alias != null)
            {
                instanceConfig = instanceConfigurations.get(alias);
                if(instanceConfig == null)
                {
                    throw new ComponentInitializationError(
                        "cannot find conifured alias: '"+alias+"'");
                }
            }
            Map<?, ?> map = buildInstance(name, instanceConfig);
            instances.put(name, map);
            boolean graphRequested  = instanceNodes[i].getAttributeAsBoolean("graph", false);
            if(graphRequested && fileSystem != null)
            {
                if(map instanceof StatisticsMap<?, ?>)
                {
                    graphList.add(new CacheSizeGraph(name, (StatisticsMap<?, ?>)map, fileSystem));
                    graphList.add(new CacheRequestsGraph(name, (StatisticsMap<?, ?>)map, fileSystem));
                    graphList.add(new CacheEfficiencyGraph(name, (StatisticsMap<?, ?>)map, fileSystem));
                }
                else
                {
                    throw new ConfigurationException("graph requested for instance " + name
                        + "but outermost map is not an StatisticsMap", instanceNodes[i].getPath(),
                        instanceNodes[i].getLocation());
                }
            }
        }
        graphs = graphList.toArray(new MuninGraph[graphList.size()]);
        threadPool.runDaemon(new DelayedUpdateTask());
        threadPool.runDaemon(new WeakHashMapExpungeTask());        
    }      
    
    // StatisticsProvider interface /////////////////////////////////////////////
    
    public MuninGraph[] getGraphs()
    {
        return graphs;
    }

    // CacheFactorySPI interface ////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String type)
    {
        Class<?> cl = implClasses.get(type);
        if(cl == null)
        {
            throw new IllegalArgumentException("unknown map type "+type);
        }
        try
        {
            return (Map<K, V>)cl.newInstance();
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
    @SuppressWarnings("unchecked")
    public synchronized <K, V> Map<K, V> getInstance(String name)
    {
        Map<K, V> map = (Map<K, V>)instances.get(name);
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
    @SuppressWarnings("unchecked")
    public synchronized <K, V> Map<K, V> getInstance(String name, String configAlias)
        throws ConfigurationException
    {
        Map<K, V> map = (Map<K, V>)instances.get(name);
        if(map == null)
        {
            Configuration instanceConfiguration = 
                instanceConfigurations.get(configAlias);
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
    public Set<String> getInstanceNames()
    {
        return instances.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> ValueFactory<K, V> getValueFactory(String factory, String map)
    {
        Configuration factoryConfig = factoryConfigurations.get(factory);
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
        
        if(!(obj instanceof ConfigurableValueFactory<?, ?>))
        {
            throw new IllegalArgumentException(fclass+" does not implement " +
                                               "ConfigurableValueFactory interface");
        } 
        ((ConfigurableValueFactory<K, V>)obj).configure(this, map, factoryConfig);
        return (ValueFactory<K, V>)obj;
    }

    /**
     * {@inheritDoc}
     */
    public <K, V> Map<K, V> getHashMap()
    {
        Map<K, V> map = getMap(HASH_MAP_TYPE);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getTimeoutMap(long timeoutMillis)
    {
        TimeoutMap<K, V> map = (TimeoutMap<K, V>)getMap(TIMEOUT_MAP_TYPE);
        map.setTimeout(timeoutMillis);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getLRUMap(int capacity)
    {
        LRUMap<K, V> map = (LRUMap<K, V>)getMap(LRU_MAP_TYPE);
        map.setCapacity(capacity);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getSoftMap(int protect)
    {
        SoftMap<K, V> map = (SoftMap<K, V>)getMap(SOFT_MAP_TYPE);
        map.setProtect(protect);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> org.objectledge.cache.DistributedMap<K, V> getDistributedMap(String name, Map<K, V> delegate)
    {
        DistributedMap<K, V> map = (DistributedMap<K, V>)getMap(DISTRIBUTED_MAP_TYPE);
        map.setDelegate(delegate);
        map.attach(notification, name);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getFactoryMap(ValueFactory<K, V> factory, Map<K, V> delegate)
    {
        FactoryMap<K, V> map = (FactoryMap<K, V>)getMap(FACTORY_MAP_TYPE);
        map.setDelegate(delegate);
        map.setFactory(factory);
        return map;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getStatisticsMap(String name, Map<K, V> delegate)
    {
        StatisticsMap<K, V> map = (StatisticsMap<K, V>)getMap(STATISTICS_MAP_TYPE);
        map.setDelegate(delegate);
        map.setName(name);
        addStatisticsMap(map);
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public <K extends Number, V extends Persistent> ValueFactory<K, V> getPersitenceValueFactory(Class<V> valueClass)
    {
        PersistenceValueFactory<K, V> factory = new PersistenceValueFactory<K, V>();
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
            Long target = queueHelper.get(object);
            Set<DelayedUpdate> set;
            if(target != null)
            {
                set = queue.get(target);
                set.remove(object);
                if(set.isEmpty())
                {
                    queue.remove(target);
                }
            }
            target = Long.valueOf(System.currentTimeMillis()+object.getUpdateLatency());
            set = queue.get(target);
            if(set == null)
            {
                set = new HashSet<DelayedUpdate>();
                queue.put(target, set);
                queueHelper.put(object, target);
            }
            set.add(object);
            queue.notify();
        }
    }
    
    public void registerForPeriodicExpunge(WeakHashMap<?, ?> map)
    {
        synchronized(weakHashMaps)
        {
            weakHashMaps.add(new WeakReference<WeakHashMap<?, ?>>(map));
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
                out.print(statistics.get(i).getStatistics());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public <K, V> void addStatisticsMap(StatisticsMap<K, V> map)
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
    private void initImpl(String type, String className, Class<?> iface)
        throws ClassNotFoundException
    {
        Class<?> cl = Class.forName(className);
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
    private <K, V> Map<K, V> buildInstance(String name, Configuration conf)
        throws ConfigurationException
    {
        Map<K, V> previous = null;
        Map<K, V> current = null;
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
                if(current instanceof LayeredMap<?, ?>)
                {
                    ((LayeredMap<K, V>)current).setDelegate(previous);
                }
                else
                {
                    throw new IllegalArgumentException(type+" was specified as the "+
                        num+" entry in "+name+" configuration, but it's "+
                        "implementation does not support LayeredMap interface");
                }
            }
            if(current instanceof ConfigurableMap<?, ?>)
            {
                ((ConfigurableMap<K, V>)current).configure(this,name,mapConfig);
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
                    Long first = queue.firstKey();
                    while(!queue.isEmpty() && first.longValue() <= now)
                    {
                        // the first element of the que has reached or passed
                        // it's target time
                        Set<DelayedUpdate> set = queue.remove(first);
                        Iterator<DelayedUpdate> i = set.iterator();
                        while(i.hasNext())
                        {
                            DelayedUpdate obj = i.next();
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
                            first = queue.firstKey();
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
    
    private class WeakHashMapExpungeTask extends Task
    {
        public String getName()
        {
            return "WeakHashMap periodic expunge";
        }
        
        public void terminate(Thread t)
        {
            t.interrupt();
        }

        public void process(Context context)
            throws ProcessingException
        {
            synchronized(weakHashMaps)
            {
                while(!Thread.interrupted())
                {
                    try
                    {
                        // sleep while allowing registerForPeriodcExpunge method to work
                        weakHashMaps.wait(60 * 1000);
                    }
                    catch(InterruptedException e)
                    {
                        return;
                    }
                    expunge();
                }
            }
        }

        private void expunge()
        {
            logger.info("starting expunge of " + weakHashMaps.size() + " maps");
            int collected = 0;
            long time = System.currentTimeMillis();
            Iterator<WeakReference<WeakHashMap<?, ?>>> i = weakHashMaps.iterator();
            while(i.hasNext())
            {
                WeakHashMap<?, ?> map = i.next().get();
                if(map != null)
                {
                    // trigger expungeStaleEntries
                    map.size();
                }
                else
                {
                    // map has gone weakly reachable itself, get rid of the ref
                    i.remove();
                    collected++;
                }
            }
            time = System.currentTimeMillis() - time;
            logger.info("expunge done in " + time + "ms, " + collected + " maps were collected since last run");
        }
    }
    
    public abstract class AbstractCacheStatisticsGraph
        extends AbstractMuninGraph
    {
        protected final String name;
        protected final StatisticsMap<?, ?> map;
    
        public AbstractCacheStatisticsGraph(String name, StatisticsMap<?, ?> map, FileSystem fileSystem)
        {
            super(fileSystem);
            this.name = name;
            this.map = map;
        }
        
        public String getConfig()
        {
            String config = super.getConfig();
            return config.replace("${name}", name);
        }
    }
    
    public class CacheEfficiencyGraph
        extends AbstractCacheStatisticsGraph
    {
        public CacheEfficiencyGraph(String name, StatisticsMap<?, ?> map, FileSystem fileSystem)
        {
            super(name, map, fileSystem);
        }
        
        public String getId()
        {
            return "cache_" + name + "_efficiency";
        }
        
        public double getHit()
        {
            if(map.getRequestCount() == 0)
            {
                return 0.0d;
            }
            else
            {
                return 100.0d * map.getHitCount() / map.getRequestCount();
            }
        }
        
        public double getMiss()
        {
            return 100.0d - getHit();
        }
    }
    
    public class CacheSizeGraph
        extends AbstractCacheStatisticsGraph
    {
        public CacheSizeGraph(String name, StatisticsMap<?, ?> map, FileSystem fileSystem)
        {
            super(name, map, fileSystem);
        }
        
        public String getId()
        {
            return "cache_" + name + "_size";
        }
        
        public int getSize()
        {
            return map.size();
        }
    }
    
    public class CacheRequestsGraph
        extends AbstractCacheStatisticsGraph
    {
        public CacheRequestsGraph(String name, StatisticsMap<?, ?> map, FileSystem fileSystem)
        {
            super(name, map, fileSystem);
        }
        
        public String getId()
        {
            return "cache_" + name + "_demand";
        }
        
        public int getCount()
        {
            return map.getRequestCount();
        }
    }       
}
