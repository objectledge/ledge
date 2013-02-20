package org.objectledge.pico;

import junit.framework.TestCase;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class ComponentProxyFactoryTest
    extends TestCase
{
    public interface Contract
    {
        int fun();
    }

    public static class Impl
        implements Contract
    {
        public int fun()
        {
            return 1;
        }
    }

    public void testComponentProxy()
    {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Contract.class, Impl.class);
        Contract proxy = (Contract)ComponentProxyFactory.proxy(
            pico.getComponentAdapter(Contract.class), pico, getClass().getClassLoader());
        assertEquals(1, proxy.fun());
    }
}
