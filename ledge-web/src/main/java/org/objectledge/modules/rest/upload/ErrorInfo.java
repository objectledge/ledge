package org.objectledge.modules.rest.upload;

import org.objectledge.upload.UploadError;

public class ErrorInfo
    implements ItemInfo
{
    private final String name;

    private final long size;

    private final UploadError error;

    public ErrorInfo(String fileName, long size, UploadError error)
    {
        this.name = fileName;
        this.size = size;
        this.error = error;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public long getSize()
    {
        return size;
    }

    public UploadError getError()
    {
        return error;
    }
}
