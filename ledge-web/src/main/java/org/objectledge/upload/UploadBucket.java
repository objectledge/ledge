package org.objectledge.upload;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectledge.filesystem.FileSystem;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

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

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    private final AtomicInteger seq;

    private final FileSystem fileSystem;

    private final String workArea;

    private final UploadBucketConfig config;

    /**
     * Creates a new upload bucket instance.
     * 
     * @param fileSystem Ledge file system
     * @param workArea work area path in Ledge file system.
     * @param id bucket identifier.
     * @param config bucket configuration
     * @param minSeq initial item sequence number (typically 1)
     */
    public UploadBucket(FileSystem fileSystem, String workArea, String id,
        UploadBucketConfig config, int minSeq)
    {
        this.fileSystem = fileSystem;
        this.workArea = workArea + "/" + id;
        this.id = id;
        this.config = config;
        this.lastAccessTime = System.currentTimeMillis();
        this.seq = new AtomicInteger(minSeq);
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
     * Returns items inside this bucket.
     * 
     * @return
     */
    public Collection<Item> getItems()
    {
        return items.values();
    }

    /**
     * Returns UploadContainers inside this bucket.
     * 
     * @return
     */
    public Collection<UploadContainer> getContainers()
    {
        return transform(filter(items.values(), Item.IS_CONTAINER), Item.TO_CONTAINER);
    }

    /**
     * Get a specific file.
     * 
     * @param itemName
     * @return
     */
    public Item getItem(String itemName)
    {
        return items.get(itemName);
    }

    private Optional<UploadError> checkItem(String fileName, int size)
    {
        if(config.getMaxCount() > 0
            && filter(items.values(), Item.IS_CONTAINER).size() > config.getMaxCount())
        {
            return Optional.of(UploadError.ITEM_COUNT_EXCEEDED);
        }
        if(config.getMaxSize() > 0 && size > config.getMaxSize() * 1024)
        {
            return Optional.of(UploadError.ITEM_SIZE_EXCEEDED);
        }
        if(!config.isAllowed(fileName))
        {
            return Optional.of(UploadError.FORMAT_NOT_ALLOWED);
        }
        return Optional.<UploadError> absent();
    }

    /**
     * Adds a file to the bucket.
     * 
     * @param fileName
     * @param contentType
     * @param is
     * @throws IOException
     */
    public Item addItem(String fileName, int size, String contentType, InputStream is)
        throws IOException
    {
        String name = Integer.toString(seq.incrementAndGet());
        Optional<UploadError> error = checkItem(fileName, size);
        Item item;
        if(!error.isPresent())
        {
            UploadContainer container = new DiskUploadContainer(fileSystem, workArea, name,
                fileName, contentType, is);
            item = new ContainerItem(container);
        }
        else
        {
            item = new RejectedItem(name, fileName, size, error.get());
        }
        items.put(name, item);
        lastAccessTime = System.currentTimeMillis();
        return item;
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
        Item item = items.get(itemName);
        if(item instanceof ContainerItem)
        {
            UploadContainer container = ((ContainerItem)item).getContainer();
            items.put(itemName, new ContainerItem(container.addChunk(offset, length, is)));
        }
        else
        {
            throw new IllegalArgumentException(itemName + " UploadContainer not found or not valid");
        }
    }

    public byte[] getThumbnail(String itemName)
        throws IOException
    {
        Item item = items.get(itemName);
        if(item instanceof ContainerItem)
        {
            UploadContainer container = ((ContainerItem)item).getContainer();
            return container.getThumbnail(config.getThumbnailSize());
        }
        else
        {
            throw new IllegalArgumentException(itemName + " UploadContainer not found or not valid");
        }
    }

    /**
     * Removes the specified item
     * 
     * @param itemName
     * @throws IOException
     */
    public Item removeItem(String itemName)
        throws IOException
    {
        Item item = items.get(itemName);
        if(item instanceof ContainerItem)
        {
            final UploadContainer container = ((ContainerItem)item).getContainer();
            container.dispose();
            final DeletedItem marker = new DeletedItem(container);
            items.put(itemName, marker);
            return marker;
        }
        else
        {
            throw new IllegalArgumentException(itemName + " UploadContainer not found or not valid");
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

    public static abstract class Item
    {
        private final String name;

        private final String fileName;

        private final long size;

        public Item(String name, String fileName, long size)
        {
            this.name = name;
            this.fileName = fileName;
            this.size = size;
        }

        public String getName()
        {
            return name;
        }

        public String getFileName()
        {
            return fileName;
        }

        public long getSize()
        {
            return size;
        }

        public static final Predicate<Item> IS_CONTAINER = new Predicate<Item>()
            {
                @Override
                public boolean apply(Item input)
                {
                    return input instanceof ContainerItem;
                }
            };

        public static final Function<Item, UploadContainer> TO_CONTAINER = new Function<Item, UploadContainer>()
            {
                @Override
                public UploadContainer apply(Item input)
                {
                    return ((ContainerItem)input).getContainer();
                }
            };
    }

    public static class ContainerItem
        extends Item
    {
        private final UploadContainer container;

        public ContainerItem(UploadContainer container)
        {
            super(container.getName(), container.getFileName(), container.getSize());
            this.container = container;
        }

        public UploadContainer getContainer()
        {
            return container;
        }
    }

    public static class DeletedItem
        extends Item
    {
        public DeletedItem(UploadContainer container)
        {
            super(container.getName(), container.getFileName(), container.getSize());
        }
    }

    public static class RejectedItem
        extends Item
    {
        private final UploadError error;

        public RejectedItem(String name, String fileName, long size, UploadError error)
        {
            super(name, fileName, size);
            this.error = error;
        }

        public UploadError getError()
        {
            return error;
        }
    }
}
