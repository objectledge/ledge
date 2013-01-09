package org.objectledge.web.rest;


import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.jcontainer.dna.Logger;
import org.picocontainer.MutablePicoContainer;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;

/**
 * @author Marek Lewandowski <marek.m.lewandowski@gmail.com>
 * @since 11/18/12
 *        Time: 7:31 PM
 */
public class PicoComponentProviderFactory implements IoCComponentProviderFactory
{
    private final MutablePicoContainer container;

    private final Collection<Pattern> patterns = new ArrayList<>();

    private final Logger logger;

    public PicoComponentProviderFactory(MutablePicoContainer container,
        Collection<String> packageNames, Logger logger)
    {
        this.container = container;
        this.logger = logger;
        for(String packageName : packageNames)
        {
            patterns.add(Pattern.compile(packageName.replace(".", "\\.") + ".*"));
        }
        patterns.add(Pattern.compile("org\\.objectledge\\.web\\.rest\\.[^.]+"));
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c)
    {
        return getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> clazz)
    {
        String name = clazz.getName();
        for(Pattern pattern : patterns)
        {
            if(pattern.matcher(name).matches())
            {
                if(container.getComponentAdapter(clazz) == null)
                {
                    logger.warn("component " + name
                        + " is not registered, attempting to wire it automatically");
                    container.registerComponentImplementation(clazz);
                }
                Object instance = container.getComponentInstance(clazz);
                return new PicoManagedComponentProvider(instance);
            }
        }
        return null;
    }

    private static class PicoManagedComponentProvider implements IoCFullyManagedComponentProvider
    {
        private final Object o;

        PicoManagedComponentProvider(Object o) {
            this.o = o;
        }

        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        public Object getInstance() {
            return o;
        }
    }
}
