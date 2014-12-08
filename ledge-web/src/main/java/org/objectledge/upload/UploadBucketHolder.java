package org.objectledge.upload;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Container for upload buckets owned by a HTTP session.
 * 
 * @author rafal.krzewski@caltha.pl
 */
class UploadBucketHolder
    implements HttpSessionBindingListener
{
    private final Set<UploadBucket> buckets = new CopyOnWriteArraySet<>();

    private final FileUpload fileUpload;

    UploadBucketHolder(FileUpload fileUpload)
    {
        this.fileUpload = fileUpload;
    }

    public void addBucket(UploadBucket bucket)
    {
        buckets.add(bucket);
    }

    public void removeBucket(UploadBucket bucket)
    {
        buckets.remove(bucket);
    }

    public Set<UploadBucket> getBuckets()
    {
        return new HashSet<>(buckets);
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event)
    {
    }

    /**
     * Releases all buckets when the session expires.
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event)
    {
        for(UploadBucket bucket : buckets)
        {
            fileUpload.releaseBucket(bucket, "at session expiration");
        }
    }
}
