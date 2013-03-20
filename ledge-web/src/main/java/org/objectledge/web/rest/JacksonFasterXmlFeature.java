package org.objectledge.web.rest;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class JacksonFasterXmlFeature
    implements Feature
{

    @Override
    public boolean configure(final FeatureContext context)
    {
        context.register(JacksonJaxbJsonProvider.class, MessageBodyReader.class,
            MessageBodyWriter.class);
        return true;
    }
}
