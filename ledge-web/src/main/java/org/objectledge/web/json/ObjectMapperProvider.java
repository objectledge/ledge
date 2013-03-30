package org.objectledge.web.json;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider
{
    ObjectMapper provide();
}
