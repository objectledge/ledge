package org.objectledge.modules.rest.upload;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.objectledge.upload.UploadBucket;

public class UploadMessage
{
    private final List<ItemInfo> files;

    public UploadMessage(UploadBucket bucket, URI bucketUri)
    {
        files = new ArrayList<>();
        for(UploadBucket.Item item : bucket.getItems())
        {
            if(item instanceof UploadBucket.ContainerItem)
            {
                files
                    .add(new FileInfo(((UploadBucket.ContainerItem)item).getContainer(), bucketUri));
            }
            if(item instanceof UploadBucket.RejectedItem)
            {
                files.add(new ErrorInfo(item.getName(), item.getFileName(), item.getSize(),
                    ((UploadBucket.RejectedItem)item).getError()));
            }
        }
    }

    public List<ItemInfo> getFiles()
    {
        return files;
    }
}
