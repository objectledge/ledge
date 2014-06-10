package org.objectledge.modules.rest.upload;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.objectledge.upload.UploadBucket;

public class UploadMessage
{
    private final List<ItemInfo> files;

    public UploadMessage(Collection<UploadBucket.Item> items, URI bucketUri)
    {
        files = new ArrayList<>();
        for(UploadBucket.Item item : items)
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
        Collections.sort(files, ItemInfo.BY_ID);
    }

    public List<ItemInfo> getFiles()
    {
        return files;
    }
}
