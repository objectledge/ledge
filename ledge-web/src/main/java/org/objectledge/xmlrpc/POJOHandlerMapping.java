package org.objectledge.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

/**
 * XmlRpcHandlerMapping suitable for singleton POJO XmlRpc handlers.
 * <p>
 * All methods in a selected interfaces implemented by the handler are registered.
 * </p>
 * <p>
 * All requests are processed using the same handler instance. Handler needs to be implemented in a
 * thread safe way.
 * </p>
 * <p>
 * Public methods of {@link AbstractReflectiveHandlerMapping} can be used to customize the mapping.
 * </p>
 */
public class POJOHandlerMapping
    extends AbstractReflectiveHandlerMapping
{    
    /**
     * Creates a new POJOHandlerMapping.
     * 
     * @param handler the singleton handler object.
     * @param voidMethodsEnabled should void methods be exposed {@see AbstractReflectiveHandlerMapping#setVoidMethodEnabled(boolean)}.
     * @param publicInterfaces interfaces implemented by the handled object that should be exposed trough XmlRpc.
     * @throws XmlRpcException
     */
    public POJOHandlerMapping(Object handler, Class<? >... publicInterfaces)
        throws XmlRpcException
    {
        this(handler, false, publicInterfaces);
    }
    
    /**
     * Creates a new POJOHandlerMapping.
     * 
     * @param handler the singleton handler object.
     * @param voidMethodsEnabled should void methods be exposed {@see AbstractReflectiveHandlerMapping#setVoidMethodEnabled(boolean)}.
     * @param publicInterfaces interfaces implemented by the handled object that should be exposed trough XmlRpc.
     * @throws XmlRpcException
     */
    public POJOHandlerMapping(Object handler, boolean voidMethodsEnabled, Class<? >... publicInterfaces)
        throws XmlRpcException
    {        
        setRequestProcessorFactoryFactory(new POJORequestProcessorFactoryFactory(handler));
        setVoidMethodEnabled(voidMethodsEnabled);
        for(Class<? > publicInterface : publicInterfaces)
        {
            if(!publicInterface.isAssignableFrom(handler.getClass()))
            {
                throw new IllegalArgumentException(handler.getClass().getName()
                    + " does not implement " + publicInterface.getName());
            }
            registerPublicMethods(publicInterface.getName(), publicInterface);
        }
    }

    private static class POJORequestProcessorFactoryFactory
        extends RequestProcessorFactoryFactory.StatelessProcessorFactoryFactory
    {
        /** Handler object */
        private final Object handler;

        POJORequestProcessorFactoryFactory(Object handler)
        {
            this.handler = handler;
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected Object getRequestProcessor(Class pClass)
            throws XmlRpcException
        {
            return handler;
        }
    }
}
