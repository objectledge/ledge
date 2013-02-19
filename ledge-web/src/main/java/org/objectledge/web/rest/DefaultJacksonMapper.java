package org.objectledge.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

class DefaultJacksonMapper
    implements JacksonMapper
{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean providesFor(Class<?> clazz)
    {
        return true;
    }

    @Override
    public ObjectMapper getMapper()
    {
        return objectMapper;
    }

}
