package org.objectledge.web.rest;

import java.util.Map;
import java.util.Map.Entry;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.objectledge.pico.ComponentProxyFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

class LedgeBinder
    extends AbstractBinder
{

    private PicoContainer container;

    public LedgeBinder(MutablePicoContainer container)
    {
        super();
        this.container = container.getParent();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void configure()
    {
        final Map<Class<?>, Object> proxies = ComponentProxyFactory.proxies(container, getClass().getClassLoader());
        for(Entry<Class<?>, Object> entry : proxies.entrySet())
        {
            final Class key = entry.getKey();
            bind(entry.getValue()).to(key);
        }
    }
}
