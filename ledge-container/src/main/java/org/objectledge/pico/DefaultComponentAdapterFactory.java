package org.objectledge.pico;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * This class is here to to circumvent a problem with ThreadLocalCyclicDependencyGuard thread local
 * reference not being cleared after ConstructorInjectionComponentAdapter.getComponentInstance
 * method is completed.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class DefaultComponentAdapterFactory
    extends org.picocontainer.defaults.DefaultComponentAdapterFactory
{
    public ComponentAdapter createComponentAdapter(Object componentKey,
        Class componentImplementation, Parameter[] parameters)
        throws PicoIntrospectionException, AssignabilityRegistrationException,
        NotConcreteRegistrationException
    {
        // note: org.objectledge.pico.ConstructorInjectionComponentAdapter is used
        return new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(componentKey,
            componentImplementation, parameters));
    }
}
