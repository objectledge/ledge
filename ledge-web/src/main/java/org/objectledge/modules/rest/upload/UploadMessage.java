package org.objectledge.modules.rest.upload;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.objectledge.upload.UploadBucket;
import org.objectledge.upload.UploadContainer;

public class UploadMessage
{
    private final List<FileInfo> files;

    public UploadMessage(UploadBucket bucket, URI bucketUri)
    {
        files = new ArrayList<>();
        for(UploadContainer container : bucket.getItems())
        {
            files.add(new FileInfo(container, bucketUri));
        }
    }

    public UploadMessage(List<FileInfo> files)
    {
        this.files = files;
    }

    public List<FileInfo> getFiles()
    {
        return files;
    }
}
