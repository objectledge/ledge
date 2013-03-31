package org.objectledge.web.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class ObjectMapperProviderImpl
    implements ObjectMapperProvider
{

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ObjectMapper provide()
    {
        return mapper;
    }

    @Override
    public <T> Collection<T> readAsCollectionOf(String json, Class<T> clazz)
        throws IOException
    {
        final CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(
            ArrayList.class, clazz);
        return mapper.readValue(json, collectionType);
    }

}
