package org.objectledge.modules.rest.upload;

import java.net.URI;

import org.objectledge.upload.UploadContainer;

public class FileInfo
{
    private final UploadContainer container;

    private final URI bucketUri;

    public FileInfo(UploadContainer container, URI bucketUri)
    {
        this.container = container;
        this.bucketUri = bucketUri;
    }

    public String getName()
    {
        return container.getFileName();
    }

    public long getSize()
    {
        return container.getSize();
    }

    public URI getUrl()
    {
        return bucketUri.resolve(container.getName());
    }

    public URI getThumbnailUrl()
    {
        if(container.getMimeType() != null && container.getMimeType().startsWith("image/"))
        {
            return bucketUri.resolve(container.getName() + "/thumbnail");
        }
        else
        {
            return null;
        }
    }

    public URI getDeleteUrl()
    {
        return getUrl();
    }

    public String getDeleteType()
    {
        return "DELETE";
    }
}
