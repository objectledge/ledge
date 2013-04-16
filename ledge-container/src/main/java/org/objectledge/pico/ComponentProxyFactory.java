package org.objectledge.pico;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.concurrent.LazyValue;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

public class ComponentProxyFactory
{
    public static boolean canProxy(ComponentAdapter adapter)
    {
        Class<?>[] interfaces = adapter.getComponentImplementation().getInterfaces();
        return interfaces.length > 0;
    }

    public static Object proxy(ComponentAdapter adapter, PicoContainer container, ClassLoader cl)
    {
        final Class<?> c = adapter.getComponentImplementation();
        Class<?>[] interfaces = c.getInterfaces();
        if(interfaces.length > 0)
        {
            return Proxy.newProxyInstance(cl, interfaces, new ComponentInvocationHandler(container,
                adapter));
        }
        else
        {
            throw new IllegalArgumentException(c + " does not expose any interfaces");
        }
    }

    public static Map<Class<?>, Object> proxies(PicoContainer container, ClassLoader cl)
    {
        Map<Class<?>, Object> proxies = new HashMap<>();
        for(ComponentAdapter adapter : (Collection<ComponentAdapter>)container
            .getComponentAdapters())
        {
            final Class<?> clazz = adapter.getComponentImplementation();
            Collection<Class<?>> interfaces = new ArrayList<>();
            interfaces = getInheritedInterfaces(interfaces, clazz);
            final Class<?>[] declaredInterfaces = clazz.getInterfaces();
            interfaces.addAll(Arrays.asList(declaredInterfaces));
            if(interfaces.size() > 0)
            {
                Object proxy = Proxy.newProxyInstance(cl,
                    interfaces.toArray(new Class[interfaces.size()]),
                    new ComponentInvocationHandler(container, adapter));
                for(Class<?> iface : interfaces)
                {
                    proxies.put(iface, proxy);
                }
            }
        }
        return proxies;
    }

    private static Collection<Class<?>> getInheritedInterfaces(
        Collection<Class<?>> inheritedInterfaces, Class<?> clazz)
    {
        final Class<?> superclass = clazz.getSuperclass();
        if(superclass != null && !superclass.equals(Object.class))
        {
            inheritedInterfaces.addAll(Arrays.asList(superclass.getInterfaces()));
            return getInheritedInterfaces(inheritedInterfaces, superclass);
        }
        else
        {
            return inheritedInterfaces;
        }
    }

    private static class ComponentInvocationHandler
        implements InvocationHandler
    {
        private final LazyValue<Object> instance;

        public ComponentInvocationHandler(final PicoContainer container,
            final ComponentAdapter adapter)
        {
            instance = new LazyValue<Object>()
                {
                    @Override
                    protected Object compute()
                        throws Exception
                    {
                        return adapter.getComponentInstance(container);
                    }
                };
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
        {
            try
            {
                return method.invoke(instance.get(), args);
            }
            catch(InvocationTargetException e)
            {
                throw e.getTargetException();
            }
        }
    }
}
