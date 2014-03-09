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
     * Creates a new bucket for the current HTTP sesion and returns it's id.
     * 
     * @return new bucket id.
     */
    public String newBucket()
    {
        return fileUpload.createBucket().getId();
    }
}
