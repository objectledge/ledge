package org.objectledge.web.json;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract base class for views speaking JSON using org.codehasus.jackson package.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public abstract class AbstractJsonView
    extends AbstractBuilder
{
    private static final String OPTIONS_METHOD = "OPTIONS";
    
    private static final String ORIGIN_HEADER = "Origin";

    private static final String ACCESS_CONTROL_REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
    
    private static final String ACCESS_CONTROL_REQUEST_HEADERS_HEADER = "Access-Control-Request-Headers";
    
    private static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";

    private static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";

    /** Cached instance of JsonFactory returned by newJsonFactory. */
    private JsonFactory factory;

    /** JSON Object reader that will be used for parsing request data. */
    private ObjectMapper objectMapper;

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
        this.factory = newJsonFactory();
        this.objectMapper = new ObjectMapper(factory);
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
        try
        {
            buildResponseHeaders(httpContext);
            if(httpContext.getRequest().getMethod().equals(OPTIONS_METHOD))
            {
                httpContext.getResponse().setContentLength(0);
                httpContext.setDirectResponse(true);
            }
            else
            {
                try
                {
                    final String callbackParameterName = getCallbackParameterName();
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
                    final PrintWriter printWriter = httpContext.getPrintWriter();
                    if(callback != null)
                    {
                        printWriter.append(callback).append("(");
                    }
                    final JsonGenerator jsonGenerator = factory.createJsonGenerator(printWriter)
                        .setCodec(objectMapper);
                    JsonNode tree = buildJsonTree();
                    if(tree == null)
                    {
                        buildJsonStream(jsonGenerator);
                    }
                    else
                    {
                        jsonGenerator.writeTree(tree);
                    }
                    jsonGenerator.flush();
                    if(callback != null)
                    {
                        printWriter.append(");");
                        printWriter.flush();
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
        }
        catch(ProcessingException e)
        {
            log.error("Exception during JSON view processing", e);
            try
            {
                if(!httpContext.getResponse().isCommitted())
                {
                    httpContext.setContentType("application/json");
                    final JsonGenerator jsonGenerator = factory
                        .createJsonGenerator(getHttpContext().getPrintWriter());
                    jsonGenerator.setCodec(objectMapper);
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
        HttpServletRequest request = httpContext.getRequest();
        HttpServletResponse response = httpContext.getResponse();
        String origin = request.getHeader(ORIGIN_HEADER);
        if(origin != null)
        {
            String reqMethod = request.getHeader(ACCESS_CONTROL_REQUEST_METHOD_HEADER);
            if(request.getMethod().equals(OPTIONS_METHOD) && reqMethod != null)
            {
                // a pre-flight request
                if(reqMethod != null)
                {
                    if(isCORSMethodAllowed(reqMethod))
                    {
                        response.addHeader(ACCESS_CONTROL_ALLOW_METHODS_HEADER, reqMethod);
                    }
                    else
                    {
                        log.error("rejected CORS request because of invalid method " + reqMethod);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                }

                final String reqHeadersList = request
                    .getHeader(ACCESS_CONTROL_REQUEST_HEADERS_HEADER);
                if(reqHeadersList != null)
                {
                    boolean reqHeadersValid = true;
                    String[] reqHeaders = reqHeadersList.trim().split(",");
                    for(String reqHeder : reqHeaders)
                    {
                        if(!isCORSHeaderAllowed(reqHeder.trim()))
                        {
                            log.error("rejected CORS request because of invalid header "
                                + reqHeder.trim());
                            reqHeadersValid = false;
                            break;
                        }
                    }
                    if(reqHeadersValid)
                    {
                        response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, reqHeadersList);
                    }
                    else
                    {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                }
            }
            String host = request.getProtocol() + "://" + request.getServerName();
            if(isCORSOriginAllowed(host, origin))
            {
                response.addHeader("Access-Control-Allow-Origin", origin);
            }
        }
    }

    /**
     * Build JSON tree to be sent to the client.
     * <p>
     * {@link #build(Template, String)} calls this method, and if it returns a non-null value,
     * writers it out to the client. Otherwise {@link #buildJsonStream(JsonGenerator)} is called.}
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
     * @return JSON tree to be sent to the client, or null if
     *         {@link #buildJsonStream(JsonGenerator)} should be called instead.
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
     * @param jsonGenerator JsonGenereator used for writing
     * @throws ProcessingException when there is an application level problem generating the
     *         response.
     * @throws JsonGenerationException when there is a structural problem with JSON repose
     *         (non-matching end-object for example).
     * @throws IOException when there is a problem writing the response to the client.
     */
    protected void buildJsonStream(JsonGenerator jsonGenerator)
        throws ProcessingException, JsonGenerationException, IOException
    {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeEndObject();
    }

    /**
     * Write the response data as JSON-mapped Java object.
     * 
     * @param jsonGenerator JsonGenereator used for writing
     * @param value response data.
     * @throws JsonGenerationException when there is a problem generating JSON response.
     * @throws JsonMappingException when there is a problem mapping Java object to JSON.
     * @throws IOException when there ie a problem writing response data to client.
     */
    protected void writeResponseValue(JsonGenerator jsonGenerator, Object value)
        throws JsonGenerationException, JsonMappingException, IOException, ProcessingException
    {
        objectMapper.writeValue(jsonGenerator, value);
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

    /**
     * Invoked on CORS pre-flight requests to determine if the view allows a specific HTTP method.
     * 
     * @param method HTTP method name.
     */
    protected boolean isCORSMethodAllowed(String method)
    {
        return true;
    }

    /**
     * Invoked on CORS pre-flight request to determine if the view allows specific HTTP headers.
     * 
     * @param header HTTP header name.
     */
    protected boolean isCORSHeaderAllowed(String header)
    {
        return true;
    }

    /**
     * Invoked on CORS requests to determine if the view allows specific request origin. Default
     * implementation allows only host == origin, effectively disabling CORS.
     * 
     * @param host protocol and host part of current request's URI
     * @param origin protocol and host part of request origin URI
     * @see org.objectledge.web.cors.CrossOriginRequestValidator
     */
    protected boolean isCORSOriginAllowed(String host, String origin)
    {
        return host.equals(origin);
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
     * Creates a JsonParser object. When request content type is not {@code application/json},
     * jsonParser fields is {@code null}. The object may only be used by
     * {@link #buildJsonStream(JsonGenerator)} and {@link #buildJsonTree()} method implementations.
     */
    protected JsonParser getJsonParser()
        throws IOException
    {
        String requestContentType = getHttpContext().getRequest().getHeader("Content-Type");
        if(requestContentType != null && requestContentType.equals("application/json"))
        {
            return factory.createJsonParser(getHttpContext().getRequest().getInputStream());
        }
        else
        {
            return null;
        }
    }

    /**
     * Read the request data as JSON-mapped Java object.
     * <p>
     * If the request content type is not {@code application/json} this method will return
     * {@code null}.
     * </p>
     * 
     * @param jsonParser JsonParser
     * @param clazz requested object class.
     * @return request data converted to Java object.
     * @throws JsonParseException if the request data is not well formed.
     * @throws JsonMappingException if the request data could not be mapped to requested object
     *         class.
     * @throws IOException if there was a problem reading the request data from client.
     */
    protected <T> T readRequestValue(JsonParser jsonParser, Class<T> clazz)
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
    protected <T> T readRequestValue(JsonParser jsonParser, TypeReference<T> typeRef)
        throws JsonParseException, JsonMappingException, IOException
    {
        if(jsonParser != null)
        {
            T value = (T)objectMapper.readValue(jsonParser, typeRef);
            return value;
        }
        return null;
    }
}
