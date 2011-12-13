package org.objectledge.pico;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.ThreadLocalCyclicDependencyGuard;

/**
 * This class is here to to circumvent a problem with ThreadLocalCyclicDependencyGuard thread local
 * reference not being cleared after ConstructorInjectionComponentAdapter.getComponentInstance
 * method is completed.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class ConstructorInjectionComponentAdapter
    extends org.picocontainer.defaults.ConstructorInjectionComponentAdapter
{
    public ConstructorInjectionComponentAdapter(Object componentKey, Class componentImplementation)
        throws AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        super(componentKey, componentImplementation);
    }

    public ConstructorInjectionComponentAdapter(Object componentKey, Class componentImplementation,
        Parameter[] parameters)
    {
        super(componentKey, componentImplementation, parameters);
    }

    public ConstructorInjectionComponentAdapter(Object componentKey, Class componentImplementation,
        Parameter[] parameters, boolean allowNonPublicClasses)
    {
        super(componentKey, componentImplementation, parameters, allowNonPublicClasses);
    }

    private static abstract class Guard
        extends ThreadLocalCyclicDependencyGuard
    {
        protected PicoContainer guardedContainer;

        private void setArguments(PicoContainer container)
        {
            this.guardedContainer = container;
        }
    }

    private transient Guard instantiationGuard;

    public Object getComponentInstance(PicoContainer container)
        throws PicoInitializationException, PicoIntrospectionException,
        AssignabilityRegistrationException, NotConcreteRegistrationException
    {
        if(instantiationGuard == null)
        {
            instantiationGuard = new Guard()
                {
                    public Object run()
                    {
                        final Constructor constructor;
                        try
                        {
                            constructor = getGreediestSatisfiableConstructor(guardedContainer);
                        }
                        catch(AmbiguousComponentResolutionException e)
                        {
                            e.setComponent(getComponentImplementation());
                            throw e;
                        }
                        try
                        {
                            Object[] parameters = getConstructorArguments(guardedContainer,
                                constructor);
                            return newInstance(constructor, parameters);
                        }
                        catch(InvocationTargetException e)
                        {
                            if(e.getTargetException() instanceof RuntimeException)
                            {
                                throw (RuntimeException)e.getTargetException();
                            }
                            else if(e.getTargetException() instanceof Error)
                            {
                                throw (Error)e.getTargetException();
                            }
                            throw new PicoInvocationTargetInitializationException(
                                e.getTargetException());
                        }
                        catch(java.lang.InstantiationException e)
                        {
                            throw new PicoInitializationException("Should never get here");
                        }
                        catch(IllegalAccessException e)
                        {
                            throw new PicoInitializationException(e);
                        }
                    }
                };
        }
        instantiationGuard.setArguments(container);
        Object componentInstance = instantiationGuard.observe(getComponentImplementation());
        instantiationGuard.remove();
        return componentInstance;
    }
}
