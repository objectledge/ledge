package org.objectledge.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectledge.filesystem.FileSystem;

/**
 * Upload bucket may contain multiple items that are being uploaded simultaneously.
 * <p>
 * A bucket is associated with a HTTP session. A new bucket is created each time when a page that
 * contains multi-upload widget is rendered, by using {@link UploadTool}.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class UploadBucket
{
    private final String id;

    private long lastAccessTime;

    private final Map<String, UploadContainer> items = new ConcurrentHashMap<>();

    private final AtomicInteger seq = new AtomicInteger();

    private final FileSystem fileSystem;

    private final String workArea;

    private final UploadBucketConfig config;

    /**
     * Creates a new upload bucket instance.
     * 
     * @param id bucket identifier.
     */
    public UploadBucket(FileSystem fileSystem, String workArea, String id, UploadBucketConfig config)
    {
        this.fileSystem = fileSystem;
        this.workArea = workArea + "/" + id;
        this.id = id;
        this.config = config;
        this.lastAccessTime = System.currentTimeMillis();
    }

    /**
     * Returns bucket identifier.
     * 
     * @return bucket identifier.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the UNIX time of last bucket operation.
     * 
     * @return
     */
    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    /**
     * Returns UploadContainers inside this bucket.
     * 
     * @return
     */
    public Collection<UploadContainer> getItems()
    {
        return items.values();
    }

    /**
     * Adds a file to the bucket.
     * 
     * @param fileName
     * @param contentType
     * @param is
     * @throws IOException
     */
    public UploadContainer addItem(String fileName, String contentType, InputStream is)
        throws IOException
    {
        String name = Integer.toString(seq.incrementAndGet());
        UploadContainer container = new DiskUploadContainer(fileSystem, workArea, name, fileName,
            contentType, is);
        items.put(name, container);
        lastAccessTime = System.currentTimeMillis();
        return container;
    }

    /**
     * Get a specific file.
     * 
     * @param itemName
     * @return
     */
    public UploadContainer getItem(String itemName)
    {
        return items.get(itemName);
    }

    /**
     * Add a data chunk to the specified file.
     * 
     * @param itemName
     * @param offset
     * @param length
     * @param is
     * @throws IOException
     */
    public void addDataChunk(String itemName, int offset, int length, InputStream is)
        throws IOException
    {
        UploadContainer container = items.get(itemName);
        if(container != null)
        {
            items.put(itemName, container.addChunk(offset, length, is));
        }
        else
        {
            throw new IllegalArgumentException(itemName + " container not found");
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
        {
            return false;
        }
        UploadBucket other = (UploadBucket)obj;
        if(!id.equals(other.id))
        {
            return false;
        }
        return true;
    }
}
