/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 18, 2003
 */
package org.objectledge.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Specifies the contract between FileSystem abstarction and its concrete delegates.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FileSystemProvider.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public interface FileSystemProvider
{
    /**
     * Returns the name of the provider.
     * 
     * @return name of the provider.
     */
    public String getName();

    /**
     * Returns <code>true</code> if the provider is readonly.
     * 
     * @return <code>true</code> if the provider is readonly.
     */
    public boolean isReadOnly();

    /**
     * Checks if the given file or directory exists.
     * 
     * @param path the path.
     * @return <code>true</code> if the given file or directory exists.
     */ 
    public boolean exists(String path);

    /**
     * Checks if the given pathname points to an ordinary file.
     * 
     * @param path the path
     * @return <code>true</code> if the given pathname points to a directory.
     */
    public boolean isFile(String path);

    /**
     * Checks if the given pathname points to a directory.
     * 
     * @param path the path
     * @return <code>true</code> if the given pathname points to a directory.
     */
    public boolean isDirectory(String path);

    /**
     * Checks if the application is allowed to read given file
     * or directory. 
     * 
     * @param path the path.
     * @return <code>true</code> if the application is allowed to read given 
     *         file or directory.
     */
    public boolean canRead(String path);

    /**
     * Checks if the application is allowed to write given file
     * or directory. 
     * 
     * @param path the path.
     * @return <code>true</code> if the application is allowed to write given 
     *         file or directory.
     */
    public boolean canWrite(String path);

    /**
     * Lists the files and directories inside a directory.
     * 
     * @param dir the directory.
     * @return a list of names.
     * @throws IllegalArgumentException if <code>dir</code> does not exist
     *         or is not a directory.
     */
    public String[] list(String dir)
        throws IllegalArgumentException;

    /**
     * Atomically creates a new, empty file named by this abstract pathname if 
     * and only if a file with this name does not yet exist. 
     * 
     * <p>The check for the existence of the file and the creation of the file 
     * if it does not exist are a single operation that is atomic with respect 
     * to all other filesystem activities that might affect the file.</p>
     * 
     * @param path the pathname of the file to create.
     * @return <code>true</code> if the named file does not exist and was 
     *          successfully created; <code>false</code> if the named file 
     *          already exists. 
     * @throws IOException if the operation fails.
     */
    public boolean createNewFile(String path)
        throws IOException;

    /**
     * Creates a directory, and all necceray parent directories.
     * 
     * @param path the path.
     * @throws IOException if the operation fails.
     */
    public void mkdirs(String path)
        throws IOException;
    
    /**
     * Deletes a file or directory.
     * 
     * <p>Directories must be empty when being deleted.</p>
     * 
     * @param path the path.
     * @throws IOException if the operation fails.
     */
    public void delete(String path)
        throws IOException;

    /**
     * Atomically renames a file or directory.
     * 
     * @param from source path.
     * @param to destination path.
     * @throws IOException if the operation fails.
     */
    public void rename(String from, String to)
        throws IOException;

    /**
     * Opens an input stream for reading a file.
     * 
     * @param path the path.
     * @return the InputStream or null if not available.
     */
    public InputStream getInputStream(String path);

    /**
     * Opens an output stream for writing to file.
     * 
     * @param path the path.
     * @param append <code>true</code> to append, <code>false</code> to truncate.
     * @return the InputStream or null if not available.
     */
    public OutputStream getOutputStream(String path, boolean append);

    /**
     * Returns a <code>RandomAccess</code> interface implementation for accessing the file at
     * arbitrary positions.
     *
     * @param path the abstract pathname.
     * @param mode the string which defines the opening mode for this random access file,
     *              the form of this string is equal to the <code>mode</code> parameter in
     *              <code>java.io.RandomAccessFile</code> constructor.
     * @return an RandomAccess interface implementation, or <code>null</code>
     *         if the operation is not supported.
     */
    public RandomAccessFile getRandomAccess(String path, String mode);

    /**
     * Returns the time of the last modificaion of the specified file.
     *
     * <p>The time of the modification is returned as the number of
     * milliseconds sice the epoch (Jan 1 1970), or -1L if the feature is not
     * supported.</p>
     *
     * @param path the path.
     * @return the time of last modification of the specified file.
     */
    public long lastModified(String path);

    /**
     * Returns the size of the specified file.
     *
     * @param path the path.
     * @return the size of the file in bytes, of -1 if not supported.    
     */
    public long length(String path);
}
