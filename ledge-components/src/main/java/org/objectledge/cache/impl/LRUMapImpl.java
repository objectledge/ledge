package org.objectledge.cache.impl;

import java.util.LinkedHashMap;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.LRUMap;

/**
 * An implementation of LRUMap interface using java.util.LinkedHashMap
 * 
 * @author Rafał Krzewski
 */
public class LRUMapImpl<K, V>
    extends DelegateMap<K, V>
    implements LRUMap<K, V>, ConfigurableMap<K, V>
{
    /** Default capacity o the map (1000) */
    private static final int CAPACITY_DEFAULT = 1000;

    /** Default load factor of the map (0.75) */
    private static final float LOAD_FACTOR_DEFAULT = 0.75f;

    /** Strongly typed delegate method reference, for setCapacity()/getCapacity() access */
    private Storage<K, V> map;

    /**
     * Creates an instance of the map with the default parameters.
     */
    public LRUMapImpl()
    {
        super(new Storage<K, V>(CAPACITY_DEFAULT, LOAD_FACTOR_DEFAULT));
        map = (Storage<K,V>)getDelegate();
    }

    // ConfigurableMap interface /////////////////////////////////////////////////////////////////

    /**
     * Configures the map.
     * <p>
     * Two configuration options are supported
     * <ul>
     * <li><i>capacity</i></li>
     * <li><i>capacity</i>,<i>loadFactor</i></li>
     * </ul>
     * Where <i>capacity</i> is a positive integer, and loadFactor is a floating point number from
     * the range (0,1)
     * </p>
     */
    public void configure(CacheFactorySPI caching, String name, String config)
    {
        try
        {
            if(config.contains(","))
            {
                String[] c = config.split(",");
                if(c.length != 2)
                {
                    throw new IllegalArgumentException("invalid spec \"" + config
                        + "\" expected size or size,loadFactor");
                }
                int capacity = Integer.parseInt(c[0]);
                float loadFactor = Float.parseFloat(c[1]);
                setDelegate(new Storage<K, V>(capacity, loadFactor));
                map = (Storage<K,V>)getDelegate();
            }
            else
            {
                setCapacity(Integer.parseInt(config));
            }
        }
        catch(NumberFormatException e)
        {
            throw new IllegalArgumentException("invalid capacity spec '" + config + "' for '"
                + name + "'");
        }
    }

    // LRUMap SPI interface //////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public int getCapacity()
    {
        return map.getCapacity();
    }

    /**
     * {@inheritDoc}
     */
    public void setCapacity(int capacity)
    {
        map.setCapacity(capacity);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    
    private static class Storage<K, V>
        extends LinkedHashMap<K, V>
    {
        /** Map capacity */
        private int capacity;

        /**
         * Creates Storage instance
         * 
         * @param capacity map capacity.
         * @param loadFactor map load factor.
         */
        public Storage(int capacity, float loadFactor)
        {
            // Create the superclass with accessOrder = true
            super(capacity, loadFactor, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest)
        {
            return size() >= capacity + 1;
        }

        /**
         * Returns current map capacity.
         * 
         * @return map capacity.
         */
        public int getCapacity()
        {
            return capacity;
        }

        /**
         * Sets new map capacity.
         * 
         * @param capacity map capacity.
         */
        public void setCapacity(int capacity)
        {
            this.capacity = capacity;
        }
    }
}
