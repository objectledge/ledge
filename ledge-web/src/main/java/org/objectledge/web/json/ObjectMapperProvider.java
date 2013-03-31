package org.objectledge.web.json;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider
{
    ObjectMapper provide();

    <T> Collection<T> readAsCollectionOf(String json, Class<T> clazz)
        throws IOException;
}
