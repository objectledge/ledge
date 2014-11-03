package org.objectledge.filesystem.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;

public abstract class ReadOnlyFileSystemProvider
    implements FileSystemProvider
{
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	// fields //

    private final String name;

    private final Map<String, ListingEntry> entries = new HashMap<String, ListingEntry>();

    private final Map<String, Set<String>> listings = new HashMap<String, Set<String>>();

    public ReadOnlyFileSystemProvider(String name)
    {
        this.name = name;
    }

    // methods for building the listing //

    protected void addFileEntry(String path, long length, long lastModified)
    {
        addEntry(path, new FileEntry(length, lastModified));
    }

    protected void addDirectoryEntry(String path)
    {
        addEntry(path, DirectoryEntry.INSTANCE);
    }

    protected void analyzeDirectory(File base, File item)
    {
        String relativePath = item.getPath().substring(base.getPath().length()).replace(
            FILE_SEPARATOR, "/");
        if(item.isFile())
        {
            addFileEntry(relativePath, item.length(), item.lastModified());
        }
        else if(item.isDirectory())
        {
            File[] files = item.listFiles();
            if(files != null)
            {                
                addDirectoryEntry(relativePath);
                for(File child : files)
                {
                    analyzeDirectory(base, child);
                }
            }
        }
    }

    // internal listing handling //

    private void addEntry(String path, ListingEntry entry)
    {
        String normalizedPath = FileSystem.normalizedPath(path);
        entries.put(normalizedPath, entry);
        // System.out.println(normalizedPath + " " + (entry.isDirectory ? "directory" : "file"));
        String[] pathElements = normalizedPath.split("/");
        for(int i = 0; i < pathElements.length - 1; i++)
        {
            addListingEntry(subPath(pathElements, i), pathElements[i + 1]);
        }
    }

    private void addListingEntry(String parent, String child)
    {
        Set<String> contents = listings.get(parent);
        if(contents == null)
        {
            contents = new HashSet<String>();
            listings.put(parent, contents);
        }
        contents.add(child);
    }

    private String subPath(String[] pathElements, int numElements)
    {
        if(numElements == 0)
        {
            return "/";
        }
        else
        {
            StringBuilder buff = new StringBuilder();
            for(int i = 1; i <= numElements; i++)
            {
                buff.append("/").append(pathElements[i]);
            }
            return buff.toString();
        }
    }

    // FileSystemProvider - implemented methods //

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public boolean canRead(String path)
    {
        return exists(path);
    }

    @Override
    public boolean canWrite(String path)
    {
        return false;
    }

    @Override
    public boolean checkPathChars(String path)
    {
        return path != null && path.length() > 0;
    }

    @Override
    public boolean exists(String path)
    {
        return entries.containsKey(FileSystem.normalizedPath(path));
    }

    @Override
    public boolean isDirectory(String path)
    {
        ListingEntry entry = entries.get(FileSystem.normalizedPath(path));
        return entry != null && entry.isDirectory();
    }

    @Override
    public boolean isFile(String path)
    {
        ListingEntry entry = entries.get(FileSystem.normalizedPath(path));
        return entry != null && !entry.isDirectory();
    }

    @Override
    public long lastModified(String path)
    {
        ListingEntry entry = entries.get(FileSystem.normalizedPath(path));
        return entry != null ? entry.getLastModified() : -1L;
    }

    @Override
    public long length(String path)
    {
        ListingEntry entry = entries.get(FileSystem.normalizedPath(path));
        return entry != null ? entry.getLength() : -1L;
    }

    @Override
    public Set<String> list(String path)
        throws IOException
    {
        String normalizedPath = FileSystem.normalizedPath(path);
        ListingEntry entry = entries.get(normalizedPath);
        if(entry != null)
        {
            if(entry.isDirectory())
            {
                Set<String> listing = listings.get(normalizedPath);
                if(listing == null)
                {
                    listing = Collections.emptySet();
                }
                return listing;
            }
            throw new IOException(path + " is not a directory");
        }
        throw new IOException(path + " does not exist");
    }

    // FileSystemProvider - not implemented methods //

    @Override
    public OutputStream getOutputStream(String path, boolean append)
    {
        return null;
    }

    @Override
    public RandomAccessFile getRandomAccess(String path, String mode)
    {
        return null;
    }

    @Override
    public boolean createNewFile(String path)
        throws IOException
    {
        throw new IOException("not supported");
    }

    @Override
    public void mkdirs(String path)
        throws IOException
    {
        throw new IOException("not supported");
    }

    @Override
    public void rename(String from, String to)
        throws IOException
    {
        throw new IOException("not supported");
    }

    @Override
    public void delete(String path)
        throws IOException
    {
        throw new IOException("not supported");
    }

    private static class ListingEntry
    {
        private final boolean isDirectory;

        private final long length;

        private final long lastModified;

        protected ListingEntry(boolean isDirectory, long length, long lastModified)
        {
            this.isDirectory = isDirectory;
            this.length = length;
            this.lastModified = lastModified;
        }

        public long getLength()
        {
            return length;
        }

        public long getLastModified()
        {
            return lastModified;
        }

        public boolean isDirectory()
        {
            return isDirectory;
        }
        
        public String toString()
        {
            StringBuilder buff = new StringBuilder();
            buff.append("file (");
            if(length >= 0)
            {
                buff.append(length).append(", ").append(new Date(lastModified)).append(")");
            }
            else
            {
                buff.append("undef, undef)");
            }
            return buff.toString();
        }        
    }

    private static class FileEntry
        extends ListingEntry
    {
        public FileEntry(long length, long lastModified)
        {
            super(false, length, lastModified);
        }
    }

    private static class DirectoryEntry
        extends ListingEntry
    {
        public static final DirectoryEntry INSTANCE = new DirectoryEntry();
        
        private DirectoryEntry()
        {
            super(true, -1L, -1L);
        }
        
        public String toString()
        {
            return "directory";
        }
    }
}
