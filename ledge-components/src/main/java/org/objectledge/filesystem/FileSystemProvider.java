//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, 
// are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, 
//	 this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright notice, 
//	 this list of conditions and the following disclaimer in the documentation 
//	 and/or other materials provided with the distribution.
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//	 nor the names of its contributors may be used to endorse or promote products 
//	 derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
// POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Specifies the contract between FileSystem abstarction and its concrete delegates.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FileSystemProvider.java,v 1.6 2004-09-27 13:30:56 zwierzem Exp $
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
     * Returns <code>true</code> if the given path contains acceptable characters.
     * 
     * @param path the path.
     * @return <code>true</code>  if the given path contains acceptable characters.
     */
    public boolean checkPathChars(String path);

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
     * @throws IOException if <code>dir</code> does not exist
     *         or is not a directory.
     */
    public String[] list(String dir)
        throws IOException;

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
     * @throws UnsupportedCharactersInFilePathException if the given path contains characters
     *  incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public boolean createNewFile(String path)
        throws IOException, UnsupportedCharactersInFilePathException;

    /**
     * Creates a directory, and all necceray parent directories.
     * 
     * @param path the path.
     * @throws UnsupportedCharactersInFilePathException if the given path contains characters
     *  incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public void mkdirs(String path)
        throws IOException, UnsupportedCharactersInFilePathException;
    
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
     * @throws UnsupportedCharactersInFilePathException if the given destination path contains
     *  characters incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public void rename(String from, String to)
        throws IOException, UnsupportedCharactersInFilePathException;

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
     * Returns an URL to the resource.
     * 
     * @param path the abstract pathname.
     * @return an URL to the resource, or null if not available.
     * @throws MalformedURLException if the path contains invalid characters.
     */
    public URL getResource(String path) throws MalformedURLException;

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
