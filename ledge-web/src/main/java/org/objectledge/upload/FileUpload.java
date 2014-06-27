package org.objectledge.upload;

import java.util.Collection;

public interface FileUpload
{

    /**
     * Retrieve the upload container, this method should be called in the first place by action
     * valves before retrieving any multipart POST parameters from the request. This call will allow
     * identification of file upload size limit exceeding problems.
     * 
     * @param name the name of the item.
     * @return the upload container, or <code>null</code> if not available.
     * @throws UploadLimitExceededException thrown on upload limit exceeding
     */
    UploadContainer getContainer(String name)
        throws UploadLimitExceededException;

    /**
     * Get the upload size limit.
     * 
     * @return the upload limit.
     */
    int getUploadLimit();

    void limitExceeded();

    /**
     * Creates a new bucket and attaches it to the current session.
     * 
     * @param config upload bucket configuration
     * @param minSeq initial item sequence number (typically 1)
     */
    UploadBucket createBucket(UploadBucketConfig config, int minSeq);

    /**
     * @param bucket
     */
    void releaseBucket(UploadBucket bucket);

    /**
     * Returns active buckets owned by the current session.
     * 
     * @return
     */
    Collection<UploadBucket> getBuckets();

    /**
     * Returns upload bucket with the specified id.
     * 
     * @param id bucked id.
     */
    UploadBucket getBucket(String id);

}
