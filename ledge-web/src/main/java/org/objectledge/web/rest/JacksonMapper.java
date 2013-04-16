package org.objectledge.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JacksonMapper is a specific configuration for mapping your objects if default is not what your
 * into. Create your own mappers and configure them in {@link CompositeJacksonMapper}
 * 
 * @author Marek Lewandowski
 * @since 2013-02-19
 */
public interface JacksonMapper
{
    /**
     * Returns true if this mapper has configuration for this type
     * 
     * @param type - type of object for which configuration is sought
     * @return <code>true</code> if this mapper has configuration for this type, <code>false</code>
     *         otherwise
     */
    boolean providesFor(Class<?> type);

    /**
     * Returns configured ObjectMapper
     * 
     * @return objectMapper the ObjectMapper with specific configuration for asked type
     */
    ObjectMapper getMapper();
}
