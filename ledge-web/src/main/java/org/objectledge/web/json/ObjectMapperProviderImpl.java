package org.objectledge.web.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProviderImpl
    implements ObjectMapperProvider
{

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ObjectMapper provide()
    {
        return mapper;
    }

}
