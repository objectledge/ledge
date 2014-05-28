package org.objectledge.upload;

import java.io.IOException;
import java.io.InputStream;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.RandomAccessFile;

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
        throws IOException
    {
        this.fileSystem = fileSystem;
        this.location = workArea + "/" + name;
        this.name = name;
        this.filename = fileName;
        this.mimeType = mimeType;
        fileSystem.mkdirs(workArea);
        fileSystem.write(location, inputStream);
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

    @Override
    public UploadContainer addChunk(int offset, int length, InputStream is)
        throws IOException
    {
        try(RandomAccessFile ra = fileSystem.getRandomAccess(location, "rw"))
        {
            ra.seek(offset);
            byte buff[] = new byte[65536];
            int cnt = 0;
            int total = 0;
            do
            {
                cnt = is.read(buff, 0, buff.length);
                if(cnt > 0)
                {
                    ra.write(buff, 0, cnt);
                    total += cnt;
                }
            }
            while(cnt > 0 && total < length);
            return this;
        }
    }

    @Override
    public void dispose()
        throws IOException
    {
        fileSystem.delete(location);
    }
}
