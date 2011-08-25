package org.objectledge.modules.views.system;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

public class CompositeCache
    extends PolicyProtectedBuilder
{
    private final CacheFactory cacheFactory;
    
    private final int DEFAULT_LIMIT = 100;

    public CompositeCache(Context context, PolicySystem policySystem, CacheFactory cacheFactroy)
    {
        super(context, policySystem);
        this.cacheFactory = cacheFactroy;
    }

    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        Parameters parameters = context.getAttribute(RequestParameters.class);
        String cacheName = parameters.get("cache");
        templatingContext.put("cache", cacheName);
        int limit = parameters.getInt("limit", DEFAULT_LIMIT);
        templatingContext.put("limit", limit);
        Map<? , ? > cache = cacheFactory.getInstance(cacheName);
        if(isCompositeCache(cache))
        {
            List<NestedCache> nestedCaches = new ArrayList<NestedCache>(cache.size());
            for(Map.Entry<? , ? > e : cache.entrySet())
            {
                nestedCaches.add(new NestedCache(e));
            }
            Collections.sort(nestedCaches, Collections.reverseOrder(NestedCacheComparator.INSTANCE));
            if(nestedCaches.size() > limit)
            {
                nestedCaches = nestedCaches.subList(0, limit);
            }
            templatingContext.put("nestedCaches", nestedCaches);            
        }
    }

    public static class NestedCache
    {
        private final String key;

        private final int elementCount;

        public NestedCache(Map.Entry<? , ? > e)
        {
            this.key = e.getKey().toString();
            if(e.getValue().getClass().isArray())
            {
                elementCount = Array.getLength(e.getValue());
            }
            else 
            {
                try
                {
                    Method sizeMethod = e.getValue().getClass().getMethod("size", new Class<?>[0]);
                    try
                    {
                        elementCount = (Integer)sizeMethod.invoke(e.getValue(), new Object[0]);
                    }
                    catch(Exception ex)
                    {
                        throw new RuntimeException("failed to invoke " + sizeMethod.toString(), ex);
                    }                    
                }
                catch(NoSuchMethodException ex)
                {
                    throw new IllegalArgumentException("unexpected value class "
                        + e.getValue().getClass());                    
                }                
            }
        }

        public String getKey()
        {
            return key;
        }

        public int getElementCount()
        {
            return elementCount;
        }
    }

    private static class NestedCacheComparator
        implements Comparator<NestedCache>
    {

        public static NestedCacheComparator INSTANCE = new NestedCacheComparator();

        private NestedCacheComparator()
        {
            // use INSTANCE
        }

        @Override
        public int compare(NestedCache o1, NestedCache o2)
        {
            return o1.getElementCount() - o2.getElementCount();
        }
    }
    
    public static boolean isCompositeCache(Map<?,?> m)
    {
        List<Map.Entry<?, ?>> entries = new ArrayList<Map.Entry<?, ?>>(m.entrySet());
        if(entries.size() > 0)
        {
            Object value = entries.get(0).getValue();
            if(value.getClass().isArray())
            {
                return true;
            }
            try
            {
                Method sizeMethod = value.getClass().getMethod("size", new Class<?>[0]);
                try
                {
                    sizeMethod.invoke(value, new Object[0]);
                    return true;
                }
                catch(Exception ex)
                {
                    return false;
                }                    
            }
            catch(NoSuchMethodException ex)
            {
                return false;                  
            }  
        }
        return false;
    }
}
