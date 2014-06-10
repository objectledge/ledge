package org.objectledge.modules.rest.upload;

import org.objectledge.upload.UploadError;

public class ErrorInfo
    implements ItemInfo
{
    private final String name;

    private final String fileName;

    private final long size;

    private final UploadError error;

    public ErrorInfo(String name, String fileName, long size, UploadError error)
    {
        this.name = name;
        this.fileName = fileName;
        this.size = size;
        this.error = error;
    }

    @Override
    public int getId()
    {
        return Integer.parseInt(name);
    }

    @Override
    public String getName()
    {
        return fileName;
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
