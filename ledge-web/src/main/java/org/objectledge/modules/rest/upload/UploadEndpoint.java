package org.objectledge.modules.rest.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadBucket;
import org.objectledge.upload.UploadContainer;
import org.objectledge.utils.StackTrace;

import com.google.common.base.Optional;

@Path("upload/{bucketId}")
public class UploadEndpoint
{
    private final FileUpload fileUpload;

    @Inject
    public UploadEndpoint(FileUpload fileUpload)
    {
        this.fileUpload = fileUpload;
    }

    @POST
    public Response upload(@PathParam("bucketId") String bucketId,
        @HeaderParam(HttpHeaders.ACCEPT) String accept, @Context UriInfo uriInfo,
        FormDataMultiPart multiPart)
    {
        try
        {
            UploadBucket bucket = fileUpload.getBucket(bucketId);
            if(bucket != null)
            {
                saveParts(multiPart, bucket);
                return buildResponse(accept, uriInfo, bucket, Optional.<UploadContainer> absent());
            }
            else
            {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        catch(IOException e)
        {
            return Response.serverError().entity(new StackTrace(e).toString()).build();
        }
    }

    private void saveParts(FormDataMultiPart multiPart, UploadBucket bucket)
        throws IOException
    {
        for(List<FormDataBodyPart> field : multiPart.getFields().values())
        {
            for(FormDataBodyPart part : field)
            {
                if(!part.isSimple())
                {
                    FormDataContentDisposition disposition = part.getFormDataContentDisposition();
                    InputStream is = part.getValueAs(InputStream.class);
                    String contentLength = part.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
                    // when Content-Length for the part is set, use length of the full request
                    if(contentLength == null)
                    {
                        contentLength = multiPart.getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
                    }
                    int size = contentLength != null ? Integer.parseInt(contentLength) : 0;
                    bucket.addItem(disposition.getFileName(), size, part.getMediaType().toString(),
                        is);
                }
            }
        }
    }

    private Response buildResponse(String accept, UriInfo uriInfo, UploadBucket bucket,
        Optional<UploadContainer> created)
    {
        UploadMessage msg = new UploadMessage(bucket, uriInfo.getRequestUri());
        final ResponseBuilder respBuilder = created.isPresent() ? Response.status(Status.CREATED)
            : Response.ok();
        if(created.isPresent())
        {
            final String path = bucket.getId() + "/" + created.get().getName();
            respBuilder.header(HttpHeaders.LOCATION, uriInfo.getAbsolutePath().resolve(path));
        }
        respBuilder.entity(msg);
        respBuilder.header(HttpHeaders.VARY, HttpHeaders.ACCEPT);
        if(accept != null && accept.contains(MediaType.APPLICATION_JSON))
        {
            respBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
        else
        {
            respBuilder.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        }
        final Response resp = respBuilder.build();
        return resp;
    }

    private static final Pattern CONTENT_DISPOSITION_RE = Pattern
        .compile("attachment; filename=\"([^\"]+)\"");

    @POST
    public Response upload(@PathParam("bucketId") String bucketId,
        @HeaderParam(HttpHeaders.CONTENT_DISPOSITION) String contentDisposition,
        @HeaderParam("Content-Range") String contentRange,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) int contentLength,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.ACCEPT) String accept, @Context UriInfo uriInfo, InputStream is)
    {
        try
        {
            UploadBucket bucket = fileUpload.getBucket(bucketId);
            if(bucket != null && contentDisposition != null)
            {
                Matcher dispMatch = CONTENT_DISPOSITION_RE.matcher(contentDisposition);
                if(dispMatch.matches())
                {
                    int size = contentLength;
                    if(contentRange != null)
                    {
                        Matcher rangeMatch = CONTENT_RANGE_RE.matcher(contentRange);
                        if(rangeMatch.matches())
                        {
                            size = Integer.parseInt(rangeMatch.group(3));
                        }
                    }

                    UploadBucket.Item item = bucket.addItem(dispMatch.group(1), size, contentType,
                        is);
                    Optional<UploadContainer> container = item instanceof UploadBucket.ContainerItem ? Optional
                        .of(((UploadBucket.ContainerItem)item).getContainer()) : Optional
                        .<UploadContainer> absent();
                    return buildResponse(accept, uriInfo, bucket, container);
                }
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
            return Response.serverError().entity(new StackTrace(e).toString()).build();
        }
    }

    private static final Pattern CONTENT_RANGE_RE = Pattern.compile("bytes (\\d+)-(\\d+)/(\\d+)");

    @Path("{itemId}")
    @PUT
    public Response uploadChunk(@PathParam("bucketId") String bucketId,
        @PathParam("itemId") String itemId, @HeaderParam("Content-Range") String contentRange,
        @HeaderParam(HttpHeaders.ACCEPT) String accept, @Context UriInfo uriInfo, InputStream is)
    {
        try
        {
            UploadBucket bucket = fileUpload.getBucket(bucketId);
            if(bucket != null && bucket.getItem(itemId) instanceof UploadBucket.ContainerItem
                && contentRange != null)
            {
                Matcher m = CONTENT_RANGE_RE.matcher(contentRange);
                if(m.matches())
                {
                    int start = Integer.parseInt(m.group(1));
                    int end = Integer.parseInt(m.group(2));
                    bucket.addDataChunk(itemId, start, end - start + 1, is);

                    return buildResponse(accept, uriInfo, bucket,
                        Optional.<UploadContainer> absent());
                }
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch(IOException e)
        {
            return Response.serverError().entity(new StackTrace(e).toString()).build();
        }
    }
}
