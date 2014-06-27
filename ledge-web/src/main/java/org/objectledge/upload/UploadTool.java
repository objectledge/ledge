package org.objectledge.upload;

/**
 * A context tool that allows requesting upload bucket creation from view templates.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class UploadTool
{
    private final FileUpload fileUpload;

    /**
     * Creates a new UploadTool instance.
     * 
     * @param fileUpload FileUpload component.
     */
    public UploadTool(FileUpload fileUpload)
    {
        this.fileUpload = fileUpload;
    }

    /**
     * Creates a new bucket for the current HTTP session and returns it's id.
     * 
     * @return new bucket id.
     */
    public String newBucket()
    {
        return fileUpload.createBucket(UploadBucketConfig.DEFAULT, 1).getId();
    }

    /**
     * Creates a new bucket for the current HTTP session and returns it's id.
     * 
     * @return new bucket id.
     * @param minSeq initial item sequence number (typically 1)
     */
    public String newBucket(int minSeq)
    {
        return fileUpload.createBucket(UploadBucketConfig.DEFAULT, minSeq).getId();
    }

    /**
     * Creates a new bucket for the current HTTP session and returns it's id.
     * 
     * @param maxCount Maximum number of files that may be uploaded into the bucket, or -1 for no
     *        restriction.
     * @param maxSize Maximum size of a file that may be uploaded into the bucket in kilobytes, or
     *        -1 for no restriction.
     * @param allowedFormats space separated list of file name extensions, or empty string for no
     *        restriction.
     * @param thumbnailSize maximum size of server-side generated image thumbnails. It might be
     *        width or height dependent on image orientation.
     * @param minSeq initial item sequence number (typically 1)
     * @return new bucket id.
     */
    public String newBucket(int maxCount, int maxSize, String allowedFormats, int thumbnailSize,
        int minSeq)
    {
        return fileUpload.createBucket(
            new UploadBucketConfig(maxCount, maxSize, allowedFormats, thumbnailSize), minSeq)
            .getId();
    }
}
