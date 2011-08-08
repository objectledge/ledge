package org.objectledge.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

/**
 * XmlRpcHandlerMapping suitable for singleton POJO XmlRpc handlers.
 * <p>
 * All methods in a selected interface implemented by the handler are registered.
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
    /** Handler object */
    private final Object handler;

    /**
     * Creates a new POJOHandlerMapping.
     * 
     * @param handler
     * @param publicInterfaces
     * @throws XmlRpcException
     */
    public POJOHandlerMapping(Object handler, Class<? >... publicInterfaces)
        throws XmlRpcException
    {
        this.handler = handler;
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

    @Override
    public RequestProcessorFactoryFactory getRequestProcessorFactoryFactory()
    {
        return new POJORequestProcessorFactoryFactory();
    }

    private class POJORequestProcessorFactoryFactory
        extends RequestProcessorFactoryFactory.StatelessProcessorFactoryFactory
    {
        @SuppressWarnings("rawtypes")
        @Override
        protected Object getRequestProcessor(Class pClass)
            throws XmlRpcException
        {
            return POJOHandlerMapping.this.handler;
        }
    }
}
