package org.objectledge.upload;

import java.io.IOException;
import java.io.InputStream;

import org.objectledge.filesystem.FileSystem;

/**
 * File system-based uploaded resource container.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class DiskUploadContainer
    implements UploadContainer
{
    /** resource name */
    private String name;

    /** original file name */
    private String filename;

    /** mime type */
    private String mimeType;

    private final String location;

    private final FileSystem fileSystem;

    public DiskUploadContainer(FileSystem fileSystem, String workArea, String name,
        String fileName, String mimeType, InputStream inputStream)
    {
        this.fileSystem = fileSystem;
        this.location = workArea + "/" + name;
        this.name = name;
        this.filename = fileName;
        this.mimeType = mimeType;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getFileName()
    {
        return filename;
    }

    @Override
    public long getSize()
    {
        return fileSystem.length(location);
    }

    @Override
    public byte[] getBytes()
        throws IOException
    {
        return fileSystem.read(location);
    }

    @Override
    public String getString()
        throws IOException
    {
        return fileSystem.read(location, "UTF-8");
    }

    @Override
    public String getString(String encoding)
        throws IOException
    {
        return fileSystem.read(location, encoding);
    }

    @Override
    public InputStream getInputStream()
    {
        return fileSystem.getInputStream(location);
    }

    @Override
    public String getMimeType()
    {
        return mimeType;
    }
}
