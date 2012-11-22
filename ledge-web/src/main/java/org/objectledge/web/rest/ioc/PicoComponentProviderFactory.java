package org.objectledge.web.rest.ioc;


import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Marek Lewandowski <marek.m.lewandowski@gmail.com>
 * @since 11/18/12
 *        Time: 7:31 PM
 */
public class PicoComponentProviderFactory implements IoCComponentProviderFactory
{

    private final PicoContainer container;
    private Collection<Pattern> patterns;

    public static void initialize(ResourceConfig rc, Collection<String> packageNames, PicoContainer container)
    {
        Collection<Pattern> patterns = new ArrayList<>();
        for(String packageName : packageNames)
        {
            patterns.add(Pattern.compile(packageName + ".*"));
        }
        rc.getSingletons().add(new PicoComponentProviderFactory(container, patterns));
    }

    public PicoComponentProviderFactory(PicoContainer container, Collection<Pattern> patterns)
    {
        this.container = container;
        this.patterns = patterns;
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c)
    {
        return getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c)
    {
        String name = c.getName();
        for(Pattern pattern : patterns)
        {
            if(pattern.matcher(name).matches())
            {
                Object object = container.getComponentInstanceOfType(c);
                return new PicoManagedComponentProvider(object);
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
