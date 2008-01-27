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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Provides an abstration of files available in the local file system, in the ClassLoader, web 
 * application context, or through java.net.URL mechanism.
 *
 * @author <a href="rafal@caltha.pl">Rafal.Krzewski</a>
 * @version $Id: FileSystem.java,v 1.33.2.1 2008-01-27 21:02:46 rafal Exp $
 */
public class FileSystem
{
    private String protocol = "ledge";

    /** The providers. */
    private List<FileSystemProvider> providers = new ArrayList<FileSystemProvider>();

    /** The size of the buffer. */
    private int bufferSize;

    /** Maximum size of a file that is loaded into memory in one chunk by the. 
     * read() methods. */
    private int maxReadSize;

    /**
     * Protected no-arg constructor to allow mocking.
     */
    protected FileSystem()
    {
        // needed by jMock
    }
    
    /**
     * Creates a new instance of the File System.
     * 
     * @param providers the backend implementations of the service.
     * @param bufferSize the size of the buffers using for reading/writing files.
     * @param maxReadSize maximum size of file that is loaded into memory as a single chunk.
     */
    public FileSystem(FileSystemProvider[] providers, int bufferSize, int maxReadSize)
    {
        this.providers = Arrays.asList(providers);
        this.bufferSize = bufferSize;
        this.maxReadSize = maxReadSize;
    }

    // FileSystem interface ////////////////////////////////////////////////

    /**
     * Return the URL protocol name for this FileSystem.
     * 
     * @return the protocol prefix used by the service.
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * Return the installed providers.
     * 
     * <p>The provider objects are returned in the same order in which they are
     * accessed.</p>
     * 
     * @return the installed providers.
     */
    public FileSystemProvider[] getProviders()
    {
        FileSystemProvider[] result = new FileSystemProvider[providers.size()];
        providers.toArray(result);
        return result;
    }

    /**
     * Return the provider with the specified name.
     *
     * @param name the name of the provider
     * @return the provider.
     * @throws IllegalArgumentException if the requested provider is not installed.
     */
    public FileSystemProvider getProvider(String name) throws IllegalArgumentException
    {
        for (int i = 0; i < providers.size(); i++)
        {
            FileSystemProvider fp = (FileSystemProvider)providers.get(i);
            if (fp.getName().equals(name))
            {
                return fp;
            }
        }
        throw new IllegalArgumentException("provider " + name + " is not installed");
    }

    /**
     * Returns an URL poiting to a file.
     * 
     * @param path the abstract pathname
     * @return an URL of <code>null</code> if the file is not found.
     * @throws MalformedURLException if the pathname is not valid.
     */
    public URL getResource(String path)
        throws MalformedURLException
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            URL url = fp.getResource(path);
            if (url != null)
            {
                return url;
            }
        }
        return null;
    }

    /**
     * Returns an InputStream for reading the file.
     *
     * @param path the abstract pathname.
     * @return an InputStream, or <code>null</code> if the file is not found.
     */
    public InputStream getInputStream(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            InputStream is = fp.getInputStream(path);
            if (is != null)
            {
                return is;
            }
        }
        return null;
    }
    
    /**
     * Returns a Reader for reading the file.
     * 
     * @param path the abstract pathname.
     * @param encoding character encoding to use.
     * @return a Reader, or <code>null</code> if the file is not found.
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    public Reader getReader(String path, String encoding)
        throws UnsupportedEncodingException
    {
        InputStream is = getInputStream(path);
        if(is != null)
        {
            return new InputStreamReader(is, encoding);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns an OutputStream for writing the file, or appending to it.
     *
     * @param path the abstract pathname.
     * @param append <code>false</code> to truncate the file,
     *        <code>true</code> to append.
     * @return an OutputStream, or <code>null</code> if the operation is not
     *         supported, or the file could not be opened for writing.
     */
    public OutputStream getOutputStream(String path, boolean append)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            OutputStream os = fp.getOutputStream(path, append);
            if (os != null)
            {
                return os;
            }
        }
        return null;
    }
    
    /**
     * Returns an OutputStream for writing the file.
     *
     * @param path the abstract pathname.
     * @return an OutputStream, or <code>null</code> if the operation is not
     *         supported, or the file could not be opened for writing.
     */
    public OutputStream getOutputStream(String path)
    {
        return getOutputStream(path, false);
    }

    /**
     * Returns a Writer for writing the file, or appendig to it.
     *
     * @param path the abstract pathname.
     * @param encoding the character encoding to use.
     * @param append <code>false</code> to truncate the file,
     *        <code>true</code> to append.  
     * @return a Writer, or <code>null</code> if the operation is not
     *         supported, or the file could not be opened for writing.
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    public Writer getWriter(String path, String encoding, boolean append)
        throws UnsupportedEncodingException
    {
        OutputStream os = getOutputStream(path, append);
        if(os != null)
        {
            return new OutputStreamWriter(os, encoding);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns a Writer for writing the file, or appendig to it.
     *
     * @param path the abstract pathname.
     * @param encoding the character encoding to use.
     * @return a Writer, or <code>null</code> if the operation is not
     *         supported, or the file could not be opened for writing.
     * @throws UnsupportedEncodingException if the requested encoding is not supported.
     */
    public Writer getWriter(String path, String encoding)
        throws UnsupportedEncodingException
    {
        return getWriter(path, encoding, false);
    }
    
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
    public RandomAccessFile getRandomAccess(String path, String mode)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            RandomAccessFile ra = fp.getRandomAccess(path, mode);
            if (ra != null)
            {
                return ra;
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> if the file specified by the abstract
     * pathname exists.
     *
     * @param path the abstract pathname
     * @return <code>true</code> if the file specified by the abstract
     *         pathname exists.
     */
    public boolean exists(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the abstract pathname points to an
     * ordinary file.
     *
     * @param path the abstract pathname
     * @return <code>true</code> if the file specified by the abstract
     *         pathname exists.
     */
    public boolean isFile(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.isFile(path);
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the abstract pathname points to a
     * directory.
     *
     * @param path the abstract pathname
     * @return <code>true</code> if the file specified by the abstract
     *         pathname exists.
     */
    public boolean isDirectory(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.isDirectory(path);
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the file specified by the abstract
     * pathname can be read.
     *
     * @param path the abstract pathname
     * @return <code>true</code> if the file specified by the abstract
     *         pathname can be read.
     */
    public boolean canRead(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.canRead(path);
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if the file specified by the abstract
     * pathname can be written.
     *
     * @param path the abstract pathname
     * @return <code>true</code> if the file specified by the abstract
     *         pathname can be written.
     */
    public boolean canWrite(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.canWrite(path);
            }
        }
        return false;
    }

    /**
     * Returns the time of the last modificaion of the specified file.
     *
     * <p>The time of the modification is returned as the number of
     * milliseconds sice the epoch (Jan 1 1970), or -1L if the feature is not
     * supported.</p>
     *
     * @param path the abstract pathname.
     * @return the time of last modification of the specified file.
     */
    public long lastModified(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.lastModified(path);
            }
        }
        return -1L;
    }

    /**
     * Returns the size of the specified file.
     *
     * <p>If the operation is not supported -1L is returned.</p>
     * 
     * @param path an abstract pathname.
     * @return the lenght of the file
     */
    public long length(String path)
    {
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(path))
            {
                return fp.length(path);
            }
        }
        return -1L;
    }

    /**
     * Lists the contents of a directory.
     * 
     * @param path the directory to list.
     * @return array of names of the contents of a directory.
     * @throws IOException if the pathname does not point to a directory.
     */
    public String[] list(String path)
        throws IOException
    {
        // acquire Log4j logger directly - FileSystem needs to work before LoggerFactory compoennt
        // is initialized.
        Logger log = Logger.getLogger(this.getClass());
        if(!exists(path))
        {
            throw new IOException(path + " does not exist");
        }
        Set<String> results = new HashSet<String>();
        for (FileSystemProvider fp : providers)
        {
            if(fp.exists(path))
            {
                if(fp.isDirectory(path))
                {
                    if(fp.canRead(path))
                    {
                        results.addAll(fp.list(path));
                    }
                    else
                    {
                        // complain, but continue.
                        log.error(fp.getName() + " provider has " + path
                            + " but it's not readable.");
                    }
                }
                else
                {
                    log
                        .error(fp.getName() + " provider has " + path
                            + " but it's not a directory.");
                    // CYKLO-474
                    // trying to collect some information about the context in which the bug occurs
                    StringBuffer sb = new StringBuffer();
                    sb.append("CYKLO-474 assessment for ").append(path).append("\n");
                    for (FileSystemProvider p : providers)
                    {
                        sb.append(p.getName()).append(": ");
                        sb.append("exists ").append(p.exists(path)).append(", ");
                        sb.append("isDirectory ").append(p.isDirectory(path)).append(", ");
                        sb.append("isFile ").append(p.isFile(path)).append(", ");
                        sb.append("canRead ").append(p.canRead(path)).append("\n");
                    }
                    log.error(sb.toString().trim());
                }
            }
        }
        return results.toArray(new String[0]);
    }

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
     * @throws UnsupportedCharactersInFilePathException if the given path contains
     *  characters incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public boolean createNewFile(String path)
        throws IOException, UnsupportedCharactersInFilePathException
    {
        String dir = directoryPath(path);
        if (!exists(dir))
        {
            throw new IOException(dir + " does not exist");
        }
        if (!isDirectory(dir))
        {
            throw new IOException(dir + " is not a directory");
        }
        if (!canWrite(dir))
        {
            throw new IOException(dir + ": access denied");
        }
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(dir))
            {
                return fp.createNewFile(path);
            }
        }
        throw new IOException("internal error");
    }

    /**
     * Creates a directory and all neccessary parent directories.
     * 
     * @param path the directory name.
     * @throws UnsupportedCharactersInFilePathException if the given path contains
     *  characters incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public void mkdirs(String path)
        throws IOException, UnsupportedCharactersInFilePathException
    {
        path = normalizedPath(path);
        StringTokenizer st = new StringTokenizer(path, "/");
        StringBuilder sb = new StringBuilder();
        String parent = "/";
        while (st.hasMoreTokens())
        {
            sb.append('/').append(st.nextToken());
            String dir = sb.toString();
            if (exists(dir))
            {
                if (!isDirectory(dir))
                {
                    throw new IOException(dir + " exists and is not a directory");
                }
            }
            inner : for (Iterator i = providers.iterator(); i.hasNext();)
            {
                FileSystemProvider fp = (FileSystemProvider)i.next();
                if (fp.canWrite(parent) && !fp.exists(dir))
                {
                    fp.mkdirs(dir);
                    break inner;
                }
            }
            parent = dir;
        }
    }

    /**
     * Deletes a file or directory.
     * 
     * <p>A directory must be empty at the time it is deleted.</p>
     * 
     * @param path the path of the file or directory
     * @throws IOException if the operation fails.
     */        
    public void delete(String path) throws IOException
    {
        if (!exists(path))
        {
            throw new IOException(path + " does not exist");
        }
        String dir = directoryPath(path);
        if (!canWrite(dir))
        {
            throw new IOException(dir + " : access denied");
        }
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(dir))
            {
                fp.delete(path);
                return;
            }
        }
    }

	/**
	 * Deletes recursive a directory.
	 * 
	 * @param path the path of the directory.
	 * @throws IOException if the operation fails.
	 */        
	public void deleteRecursive(String path) throws IOException
	{
		if (!exists(path))
		{
			throw new IOException(path + " does not exist");
		}
		String[] files = list(path);
		for(int i = 0; i < files.length;i++)
		{
			if(isDirectory(path+"/"+files[i]))
			{
				deleteRecursive(path + "/" + files[i]);
            }
			else
            {
			    delete(path+"/"+files[i]);
			}
		}
		String dir = directoryPath(path);
		if (!canWrite(dir))
		{
			throw new IOException(dir + " : access denied");
		}
		for (Iterator i = providers.iterator(); i.hasNext();)
		{
			FileSystemProvider fp = (FileSystemProvider)i.next();
			if (fp.exists(dir))
			{
				fp.delete(path);
				return;
			}
		}
	}

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
        throws IOException, UnsupportedCharactersInFilePathException    
    {
        if (!exists(from))
        {
            throw new IOException("source file " + from + " does not exist");
        }
        String dir = directoryPath(to);
        if (!exists(dir))
        {
            throw new IOException("destination directory " + dir + " does not exist");
        }
        if (!canWrite(dir))
        {
            throw new IOException(dir + " : access denied");
        }
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (fp.exists(dir))
            {
                fp.rename(from, to);
                return;
            }
        }
    }

    /**
     * Copies a file from one location to another.
     * 
     * @param from the source path.
     * @param to the destination path.
     * @throws UnsupportedCharactersInFilePathException if the given destination path contains
     *  characters incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public void copyFile(String from, String to) 
        throws IOException, UnsupportedCharactersInFilePathException    
    {
        InputStream in = getInputStream(from);
        if (in == null)
        {
            throw new IOException("failed to open source file " + from);
        }
        String dir = directoryPath(to);
        mkdirs(dir);
        OutputStream out = getOutputStream(to);
        if (out == null)
        {
            throw new IOException("failed to open destination file " + to);
        }
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        do
        {
            count = in.read(buffer, 0, bufferSize);
            if (count > 0)
            {
                out.write(buffer, 0, count);
            }
        }
        while (count > 0);
        out.close();
        in.close();
    }

    /**
     * Copies the contents of a directory to another location.
     * 
     * @param src source directory.
     * @param dst destination directory.
     * @throws UnsupportedCharactersInFilePathException if the given destination path contains
     *  characters incompatible with underlying filesystem.
     * @throws IOException if the operation fails.
     */
    public void copyDir(String src, String dst)
        throws IOException, UnsupportedCharactersInFilePathException    
    {
        if (!exists(src))
        {
            throw new IOException("source directory " + src + " does not exist");
        }
        if (!canRead(src))
        {
            throw new IOException("source directory " + src + " is not readable");
        }
        if (!isDirectory(src))
        {
            throw new IOException(src + " is not a directory");
        }

        mkdirs(dst);
        String[] srcFiles = list(src);
        for (int i = 0; i < srcFiles.length; i++)
        {
            String name = srcFiles[i];
            if (isDirectory(src + "/" + name))
            {
                copyDir(src + "/" + name, dst + "/" + name);
            }
            else
            {
                copyFile(src + "/" + name, dst + "/" + name);
            }
        }
    }

    /**
     * Read the contents of a file and write them into an OutputStream.
     * 
     * @param path the pathname of the file.
     * @param out the stream to write file contents to.
     * @throws IOException if the operation fails.
     */
    public void read(String path, OutputStream out) throws IOException
    {
        InputStream ins = getInputStream(path);
        if (ins == null)
        {
            throw new IOException(path + " does not exist");
        }
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        do
        {
            count = ins.read(buffer, 0, bufferSize);
            if (count > 0)
            {
                out.write(buffer, 0, count);
            }
        }
        while (count > 0);
        out.flush();
        ins.close();
    }

    /**
     * Read the contents of a file into a byte array.
     *  
     * @param path the pathnamame of the file.
     * @return the contents of the file.
     * @throws IOException if the operation fails.
     */ 
    public byte[] read(String path) throws IOException
    {
        InputStream in = getInputStream(path);
        if (in == null)
        {
            throw new IOException(path + " does not exist");
        }
        long length = length(path);
        if (length < 0)
        {
            length = bufferSize;
        }
        if (length > maxReadSize)
        {
            throw new IOException(path + " is too large (" + length + "b)");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)length);
        read(path, out);
        return out.toByteArray();
    }

    /**
     * Read the contents of a file into a String.
     *  
     * @param path the pathnamame of the file.
     * @param encoding the character encoding to use for decoding bytes into 
     *        Unicode characters.
     * @return the contents of the file.
     * @throws IOException if the file cannot be read, or the specified encoding 
     *         is not supported
     */ 
    public String read(String path, String encoding)
        throws IOException
    {
        if(!exists(path))
        {
            throw new IOException(path + " does not exist");
        }
        long length = length(path);
        if (length < 0)
        {
            length = bufferSize;
        }
        if (length > maxReadSize)
        {
            throw new IOException(path + " is too large (" + length + "b)");
        }
        Reader in = null;
        try
        {
            InputStream ins = getInputStream(path);
            in = new InputStreamReader(ins, encoding);
            StringWriter out = new StringWriter((int)length);
            char[] buffer = new char[bufferSize];
            int count = 0;
            do
            {
                count = in.read(buffer, 0, bufferSize);
                if (count > 0)
                {
                    out.write(buffer, 0, count);
                }
            }
            while (count > 0);
            return out.toString();
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }

    /**
     * Write the data read from an InputStream into a file.
     * 
     * @param path the pathname of the file
     * @param in the steram to read data from.
     * @throws IOException if the opreation fails.
     */
    public void write(String path, InputStream in) throws IOException
    {
        OutputStream out = getOutputStream(path);
        if (out == null)
        {
            throw new IOException("failed to open output file " + path);
        }
        try
        {
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            do
            {
                count = in.read(buffer, 0, bufferSize);
                if (count > 0)
                {
                    out.write(buffer, 0, count);
                }
            }
            while (count > 0);
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Write contents of a byte array into a file.
     * 
     * @param path the pathname of the file.
     * @param bytes the bytes to be written
     * @throws IOException if the operation fails. 
     */
    public void write(String path, byte[] bytes) throws IOException
    {
        OutputStream out = getOutputStream(path);
        if (out == null)
        {
            throw new IOException("failed to open output file " + path);
        }
        try
        {
            out.write(bytes);
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Write contents of a byte array into a file.
     * 
     * @param path the pathname of the file.
     * @param string the String to be written
     * @param encoding the character encoding to use for encoding Unicode 
     *        characters into bytes.
     * @throws IOException if the file cannot be written to, or the specified encoding is not 
     *         supported.
     */
    public void write(String path, String string, String encoding)
        throws IOException
    {
        OutputStream outs = getOutputStream(path);
        if (outs == null)
        {
            throw new IOException("failed to open output file " + path);
        }
        Writer out = new OutputStreamWriter(outs, encoding);
        try
        {
            out.write(string);
        }
        finally
        {
            out.close();
        }
    }
    
    // pathnames ////////////////////////////////////////////////////////////
    
    /**
     * Normalizes a pathname.
     *
     * <p>This method removes redundant / characters, removes . and .. path elements,
     * taking care that the paths dont reach outside filesystem root, removes trailing /
     * from directories and adding leading / as neccessary.</p>
     * 
     * @param path the path.
     * @return normalized path.
     * @throws IllegalArgumentException if the path reaches outside the filesystem root.
     */
    public static String normalizedPath(String path)
        throws IllegalArgumentException
    {
        if(path.length()==0 || path.equals("/"))
        {
            return "/";
        }
        StringTokenizer st = new StringTokenizer(path, "/");
        ArrayList<String> temp = new ArrayList<String>(st.countTokens());
        while(st.hasMoreTokens())
        {
            String t = st.nextToken();
            if(t.equals("."))
            {
                continue;
            }
            else if(t.equals(".."))
            {
                if(temp.isEmpty())
                {
                    throw new IllegalArgumentException("path outside filesystem root: "+path);  
                }
                else
                {
                    temp.remove(temp.size()-1);
                }
            }
            else
            {
                temp.add(t);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<temp.size(); i++)
        {
            sb.append('/').append((String)temp.get(i));
        }
        return sb.toString();
    }
    
    /**
     * Returns the base name of a file.
     * 
     * <p>This method returns the contents of the pathname after the last '/' 
     * character. </p>
     *
     * @param path the pathname of the file.
     * @return the basename of the file.     
     */
    public static String basePath(String path)
    {
        int pos = path.lastIndexOf('/');
        if(pos < 0)
        {
            return path;
        }
        else
        {
            return path.substring(pos+1);
        }
    }
    
    /**
     * Returns hte directory name of a file.
     * 
     * <p>This method returns the normalized path before the last '/' character
     * in the path.</p>
     * 
     * @param path the pathname of the file.
     * @return the directory name of the file.   
     */
    public static String directoryPath(String path)
    {
        path = normalizedPath(path);
        return path.substring(0, path.lastIndexOf('/'));        
    }
    
    /**
     * Returns the relative pathname of a file with respect to given
     * base directory.
     *
     * @param path the pathname of a file.
     * @param base the base pathname.
     * @return the relative pathname.
     * @throws IllegalArgumentException if the file is contained
     *         outside of base.
     */
    public static String relativePath(String path, String base)
        throws IllegalArgumentException
    {
        base = normalizedPath(base);
        path = normalizedPath(path);
        if(!path.startsWith(base))
        {
            throw new IllegalArgumentException(path+" is not contained in "+base);
        }
        return path.substring(base.length());
    } 
    
    /**
     * Returns <code>true</code> if the given path contains acceptable characters.
     *
     * @param path the path
     * @return <code>true</code> if the given path contains acceptable characters.
     */
    public boolean checkPathChars(String path)
    {
        if(path == null || path.length() == 0)
        {
            return false;
        }
        for (Iterator i = providers.iterator(); i.hasNext();)
        {
            FileSystemProvider fp = (FileSystemProvider)i.next();
            if (!fp.checkPathChars(path))
            {
                return false;
            }
        }
        return true;
    }
    
    // standard filesystems /////////////////////////////////////////////////
    
    /**
     * Creates a standard file system.
     * 
     * <p>The file system is composed of a local file system, with the specified root, augmented
     * with a classpath file system.</p>
     * 
     * @param root the local filesystem root.
     * @return a standard file system.
     */
    public static FileSystem getStandardFileSystem(String root)
    {
        FileSystemProvider lfs = new org.objectledge.filesystem.
            LocalFileSystemProvider("local", root);
        FileSystemProvider cfs = new org.objectledge.filesystem.
            ClasspathFileSystemProvider("classpath", 
            FileSystem.class.getClassLoader());
        return new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 65536);
    }
    
    /**
     * Creates a classpath file system.
     * 
     * @return a classpath file system.
     */
    public static FileSystem getClasspathFileSystem()
    {
        FileSystemProvider cfs = new org.objectledge.filesystem.
            ClasspathFileSystemProvider("classpath", 
            FileSystem.class.getClassLoader());
        return new FileSystem(new FileSystemProvider[] { cfs }, 4096, 65536);
    }
}
