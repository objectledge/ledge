package org.objectledge.upload;

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

    /**
     * Creates a new upload bucket instance.
     * 
     * @param id bucket identifier.
     */
    public UploadBucket(String id)
    {
        this.id = id;
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
