package org.objectledge.web.json;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;

/**
 * Abstract base class for views speaking JSON using org.codehasus.jackson package.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public abstract class AbstractJsonView
    extends AbstractBuilder
{
    /**
     * JsonGenerator object. It may only be used by {@link #buildJsonStream()} method
     * implementation.
     */
    protected JsonGenerator jsonGenerator;

    /**
     * JsonParser object. When request content type is not {@code application/json}, jsonParser
     * fields is {@code null}. The object may only be used by {@link #buildJsonStream()} and
     * {@link #buildJsonTree()} method implementations.
     */
    protected JsonParser jsonParser;

    /**
     * JSON Object reader that will be used for parsing request data.
     */
    protected ObjectMapper objectMapper;

    /** The logger. */
    protected final Logger log;

    /**
     * Creates an new AbstractJsonView instnace.
     * 
     * @param context the request context.
     * @param log the logger.
     */
    public AbstractJsonView(Context context, Logger log)
    {
        super(context);
        this.log = log;
    }

    /**
     * JSON views generate a complete response body, hence the enclosing view is always TOP
     */
    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }

    /**
     * Entry point for BuilderExecutorValve.
     * <p>
     * Implementations are not expected to override this method.
     * </p>
     * 
     * @param template not used
     * @param embeddedBuildResults not used
     */
    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        HttpContext httpContext = getHttpContext();
        JsonFactory factory = newJsonFactory();
        objectMapper = new ObjectMapper(factory);
        try
        {
            String requestContentType = httpContext.getRequest().getHeader("Content-Type");
            if(requestContentType != null && requestContentType.equals("application/json"))
            {
                jsonParser = factory.createJsonParser(httpContext.getRequest().getInputStream());
            }
        }
        catch(IOException e)
        {
            log.error("failed to create JSON parser", e);
            return null;
        }

        String callbackParameterName = getCallbackParameterName();
        String callback = null;
        if(callbackParameterName != null)
        {
            callback = getRequestParameters().get(callbackParameterName, null);
            if(callback != null)
            {
                httpContext.setContentType("text/javascript");
            }
        }
        if(callbackParameterName == null || callback == null)
        {
            httpContext.setContentType("application/json");
        }

        try
        {
            jsonGenerator = factory.createJsonGenerator(httpContext.getPrintWriter());
        }
        catch(IOException e)
        {
            log.error("failed to create JSON generator", e);
            return null;
        }

        try
        {
            buildResponseHeaders(httpContext);
            try
            {
                if(callback != null)
                {
                    httpContext.getPrintWriter().append(callback).append("(");
                }
                JsonNode tree = null;
                tree = buildJsonTree();
                if(tree == null)
                {
                    buildJsonStream();
                }
                if(tree != null)
                {
                    jsonGenerator.setCodec(objectMapper);
                    jsonGenerator.writeTree(tree);
                }
                jsonGenerator.flush();
                if(callback != null)
                {
                    httpContext.getPrintWriter().append(");");
                    httpContext.getPrintWriter().flush();
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("Exception while serializing JSON tree", e);
            }
            catch(IOException e)
            {
                log.error("Exception while sending results to client", e);
            }
        }
        catch(ProcessingException e)
        {
            log.error("Exception during JSON view processing", e);
            try
            {
                if(!httpContext.getResponse().isCommitted())
                {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("exception", new StackTrace(e).toString());
                    jsonGenerator.writeEndObject();
                    jsonGenerator.flush();
                }
            }
            catch(IOException ee)
            {
                log.error("Exception while sending exception to client", e);
            }
        }
        return null;
    }

    // subclass API

    /**
     * Creates new JSON factory.
     * <p>
     * Subclasses may override this method to set up custom Jackson configuration.
     * </p>
     * 
     * @return {@link JsonFactory} instance.
     */
    protected JsonFactory newJsonFactory()
    {
        return new JsonFactory();
    }

    /**
     * Set up HTTP response headers.
     * <p>
     * Subclasses that support JSONP may override this method to set response headers before
     * response body generation starts.
     * </p>
     * <p>
     * Default implementation does nothing.
     * </p>
     * <p>
     * If the implementation throws {@link ProcessingException}, the response sent to the client
     * will have the following form: <blockquote> { exception : "
     * {@code ProcessingException stacktrace}" } </blockquote> and the exception will be logged on
     * the server side.
     * </p>
     * 
     * @throws ProcessingException when there is an application level problem setting up the
     *         headers.
     */
    protected void buildResponseHeaders(HttpContext httpContext)
        throws ProcessingException
    {
    }

    /**
     * Build JSON tree to be sent to the client.
     * <p>
     * {@link #build(Template, String)} calls this method, and if it returns a non-null value,
     * writers it out to the client. Otherwise {@link #buildJsonStream()} is called.}
     * </p>
     * <p>
     * The default implementation returns {@code null}.
     * </p>
     * <p>
     * If the implementation throws {@link ProcessingException}, the response sent to the client
     * will have the following form: <blockquote> { exception : "
     * {@code ProcessingException stacktrace}" } </blockquote> and the exception will be logged on
     * the server side.
     * </p>
     * 
     * @return JSON tree to be sent to the client, or null if {@link #buildJsonStream()} should be
     *         called instead.
     * @throws ProcessingException when there is an application level problem generating the
     *         response.
     */
    protected JsonNode buildJsonTree()
        throws ProcessingException
    {
        return null;
    }

    /**
     * Build JSON response using streaming.
     * <p>
     * This method is expected to use {@link #jsonGenerator} object to write JSON response to the
     * client. It is called only if {@link #buildJsonTree()} returns {@code null}.
     * </p>
     * <p>
     * The default implementation writes out an empty object <code>{ }</code>.
     * </p>
     * <p>
     * If the implementation throws {@link ProcessingException} <em>before</em> starting to write
     * out the response to the client, the response will have the following form: <blockquote> {
     * exception : "{@code ProcessingException stacktrace}" } </blockquote> If
     * {@link ProcessingException} is thrown afterwards, partial response will be sent and the
     * exception will be logged only on the server side.
     * </p>
     * 
     * @throws ProcessingException when there is an application level problem generating the
     *         response.
     * @throws JsonGenerationException when there is a structural problem with JSON repose
     *         (non-matching end-object for example).
     * @throws IOException when there is a problem writing the response to the client.
     */
    protected void buildJsonStream()
        throws ProcessingException, JsonGenerationException, IOException
    {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeEndObject();
    }

    // XDM support

    /**
     * Returns callback parameter name, if JSONP support should be available.
     * <p>
     * You may override this method to enable <a href="http://en.wikipedia.org/wiki/JSONP">JSONP</a>
     * support.
     * </p>
     * <p>
     * If the request query string contains the specified parameter, response {@code Content-Type}
     * will be {@code text/javascript} instead of {@code application/json} and the response will be
     * padded with callback function invocation.
     * </p>
     * <p>
     * Default implementation returns {@code null}
     * </p>
     * 
     * @return callback parameter name, or {@code null} if JSONP support should be disabled.
     */
    protected String getCallbackParameterName()
    {
        return null;
    }

    // convenience methods

    /**
     * Returns {@link RequestParameters} from the request context.
     * 
     * @return RequestParameters object.
     */
    protected RequestParameters getRequestParameters()
    {
        return context.getAttribute(RequestParameters.class);
    }

    /**
     * Returns {@link HttpContext} from the request context.
     * 
     * @return HttpContext object.
     */
    protected HttpContext getHttpContext()
    {
        return context.getAttribute(HttpContext.class);
    }

    /**
     * Read the request data as JSON-mapped Java object.
     * <p>
     * If the request content type is not {@code application/json} this method will return
     * {@code null}.
     * </p>
     * 
     * @param clazz requested object class.
     * @return request data converted to Java object.
     * @throws JsonParseException if the request data is not well formed.
     * @throws JsonMappingException if the request data could not be mapped to requested object
     *         class.
     * @throws IOException if there was a problem reading the request data from client.
     */
    protected <T> T readRequestValue(Class<T> clazz)
        throws JsonParseException, JsonMappingException, IOException
    {
        if(jsonParser != null)
        {
            return objectMapper.readValue(jsonParser, clazz);
        }
        return null;
    }

    /**
     * Read the request data as JSON-mapped Java object.
     * <p>
     * If the request content type is not {@code application/json} this method will return
     * {@code null}.
     * </p>
     * 
     * @param typeRef generic type reference.
     * @return request data converted to Java object.
     * @throws JsonParseException if the request data is not well formed.
     * @throws JsonMappingException if the request data could not be mapped to requested object
     *         type.
     * @throws IOException if there was a problem reading the request data from client.
     */
    protected <T> T readRequestValue(TypeReference<T> typeRef)
        throws JsonParseException, JsonMappingException, IOException
    {
        if(jsonParser != null)
        {
            T value = (T)objectMapper.readValue(jsonParser, typeRef);
            return value;
        }
        return null;
    }
    
    /**
     * Write the response data as JSON-mapped Java object.
     * 
     * @param value response data.
     * 
     * @throws JsonGenerationException when there is a problem generating JSON response.
     * @throws JsonMappingException when there is a problem mapping Java object to JSON.
     * @throws IOException when there ie a problem writing response data to client.
     */
    protected void writeResponseValue(Object value)
        throws JsonGenerationException, JsonMappingException, IOException
    {
        objectMapper.writeValue(jsonGenerator, value);
    }
}
