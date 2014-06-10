package org.objectledge.upload;

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.tools.ContextToolFactory;

/**
 * A factory for the upload context tool.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class UploadToolFactory
    implements ContextToolFactory
{
    public static final String KEY = "uploadTool";

    private final FileUpload fileUpload;

    /**
     * Creates a new UploadToolFactory instance.
     * 
     * @param fileUpload FileUpload component.
     */
    public UploadToolFactory(FileUpload fileUpload)
    {
        this.fileUpload = fileUpload;
    }

    /**
     * Creates a new UploadTool instance.
     * 
     * @return UploadTool instance.
     */
    @Override
    public Object getTool()
        throws ProcessingException
    {
        return new UploadTool(fileUpload);
    }

    /**
     * Does nothing, tool has no internal state.
     */
    @Override
    public void recycleTool(Object tool)
        throws ProcessingException
    {
    }

    /**
     * Returns the context key for the tool, {@link #KEY}
     */
    @Override
    public String getKey()
    {
        return KEY;
    }
}
