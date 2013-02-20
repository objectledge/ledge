package org.objectledge.pico;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        Class<?>[] interfaces = adapter.getComponentImplementation().getInterfaces();
        return Proxy.newProxyInstance(cl, interfaces, new ComponentInvocationHandler(container, adapter));
    }

    private static class ComponentInvocationHandler
        implements InvocationHandler
    {
        private final LazyValue<Object> instance;

        public ComponentInvocationHandler(final PicoContainer container, final ComponentAdapter adapter)
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
            return method.invoke(instance.get(), args);
        }
    }
}
