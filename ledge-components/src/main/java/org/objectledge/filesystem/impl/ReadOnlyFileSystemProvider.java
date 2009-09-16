//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.filesystem.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;

/**
 * A base class for read only FileSystem backend implemetations. 
 * 
 *  @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: ReadOnlyFileSystemProvider.java,v 1.3 2009-09-07 21:00:50 mgolebsk Exp $
 */
public abstract class ReadOnlyFileSystemProvider 
	implements FileSystemProvider
{
	// constants ////////////////////////////////////////////////////////////
	
    /** locations of the file listings, in order of precedence. */
	public static final String[] LISTING_LOCATIONS =  
	{
		"/WEB-INF/files.lst",
		"/META-INF/files.lst",
		"/files.lst"	
	};
    
    /** the character encoding used in file listings. */
    public static final String LISTING_ENCODING = "UTF-8";
	
    /** the regexp pattern of file entry in files.lst list.*/
    private static final Pattern FILE_PATTERN = Pattern.compile("^(.*) ([0-9]+) ([0-9]+)$");
    
    /** Marker object stored in the listing to represent files. */
    private static final FileItem FILE = new FileItem(); 
    
	// instance variables ///////////////////////////////////////////////////

    /** Providers's name. */
    protected String name;

	/** The file listing. */	
	private DirectoryItem listing = null;

    /** The directory tree. */
    private DirectoryItem directoryTree = new DirectoryItem();

	/** The file listing. */	
	private Map<String,Long> times = new HashMap<String,Long>();

	/** The file listing. */	
	private Map<String,Long> lengths = new HashMap<String,Long>();

	// initialization ///////////////////////////////////////////////////////
	
    /**
     * Creates an instance of the provider.
     * 
     * @param name the name of the provider.
     */
	public ReadOnlyFileSystemProvider(String name, Collection<URL> listings)
	{
        this.name = name;
        for(URL listing : listings)
        {
            try
            {
                InputStream is = listing.openStream();
                processListing(listing.toString(), is);
            }
            catch(IOException e)
            {
                throw new ComponentInitializationError("failed to load listing " + listing, e);
            }
        }
	}

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly()
    {
        return true;
    }

    /**
     * Processes a listing.
     *  
     * @param location the location of the listing
     * @param is the stream used for reading the listing.
     * @throws IOException if the index file cannot be processed.
     */
	protected void processListing(String location, InputStream is)
        throws IOException
	{
        LineNumberReader reader = null;
		try
		{
            reader = new LineNumberReader(
               new InputStreamReader(is, LISTING_ENCODING));
            if(listing == null)
            {
                listing = new DirectoryItem();
                listing.put("/", directoryTree);
            }
			while(reader.ready())
			{
				final String line = reader.readLine();
                
				String path;
				final String length;
				final String time;
                
                final Matcher matcher = FILE_PATTERN.matcher(line);
                if(matcher.matches())
                {
                    path = matcher.group(1);
                    length = matcher.group(2);
                    time = matcher.group(3);
                }
                else
                {
                    path = line;
                    length = "-1";
                    time = "-1";
                }
				Long lengthObj;
				Long timeObj;
				try
				{
					lengthObj = Long.parseLong(length);
				}
				catch(NumberFormatException e)
				{
					throw new ComponentInitializationError("invalid length "+length+" for file "+
                        path+" in index "+location);
				}
				try
				{
					timeObj = Long.parseLong(time);
				}
				catch(NumberFormatException e)
				{
					throw new Error("invalid time "+time+" for file "+path+" in index "+location);
				}
				StringTokenizer st = new StringTokenizer(path,"/");
				String token = null;
                DirectoryItem current = directoryTree;
				tokenLoop: while(st.hasMoreTokens())
				{
					token = st.nextToken();
					if(st.hasMoreTokens())
					{
						current = (DirectoryItem)current.get(token);
						if(current == null)
						{
							throw new ComponentInitializationError("missing parent directory for "+
                                path+" in index "+location);
						}
					}
					else
					{
						if(current.contains(token))
						{
							// we seem to be merging indices
							continue tokenLoop;
						}
						ListingItem item;
						if(path.endsWith("/"))
						{
							item = new DirectoryItem();
							path = path.substring(0, path.length()-1);
						}
						else
						{
							item = FILE;
						}
						current.put(token, item);
						listing.put(path, item);
						times.put(path, timeObj);
						lengths.put(path, lengthObj);
					}
				}
			}
		}
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }
	}

	// FileProvider interface ////////////////////////////////////////
	
    /**
     * {@inheritDoc}
     */
    public boolean checkPathChars(String path)
    {
        return path != null && path.length() > 0;
    }
    
    /**
     * Translate the path to the form suitable for underlying resource provider.
     * 
     * @param path a path.
     * @return normalized path.
     */
    protected abstract String normalizedPath(String path);

    /**
     * {@inheritDoc}
     */
    public boolean exists(String path)
    {
		if(listing != null)
		{
			return listing.contains(FileSystem.normalizedPath(path));
		}
		return getInputStream(path) != null;
    }

    private boolean checkItemType(String path, boolean directory)
    {
        URL url;
        try
        {
            url = getResource(path);
            if(url != null)
            {
                if(url.getProtocol().equals("file"))
                {
                    try
                    {
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        // URLConnection does not provide a way to determine if the target object
                        // is a file or directory in the API, so we need to pry the lid open...
                        Field f = conn.getClass().getDeclaredField("isDirectory");
                        f.setAccessible(true);
                        return directory == f.getBoolean(conn);
                    }
                    catch(Exception e)
                    {
                        Logger log = Logger.getLogger(this.getClass());
                        log.error("faield to check item type for "+path, e);
                        return false;
                    }
                }
                if(url.getProtocol().equals("jar"))
                {
                    // jar protocol supports only files - if item exists it must be a file
                    return !directory;
                }
                return false;
            }
            else
            {
                return false;
            }
        }
        catch(MalformedURLException e1)
        {
            throw new RuntimeException("malformed path", e1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile(String path)
    {
		if(listing != null)
		{
		    path = FileSystem.normalizedPath(path);
			return listing.contains(path) && !(listing.get(path) instanceof DirectoryItem);
		}
		else
		{
	        return checkItemType(path, false);
		}
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(String path)
    {
		if(listing != null)
		{
		    path = FileSystem.normalizedPath(path);
			return listing.contains(path) && listing.get(path) instanceof DirectoryItem;
		}
		else
		{
		    return checkItemType(path, true);
		}
    }

    /**
     * {@inheritDoc}
     */
    public boolean canRead(String path)
    {
		return exists(path);
    }

    /**
     * {@inheritDoc}
     */
    public boolean canWrite(String path)
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> list(String path) 
        throws IOException
    {
		if(listing != null)
		{
			ListingItem item = listing.get(FileSystem.normalizedPath(path));
			if(item instanceof DirectoryItem)
			{
				Set<String> names = ((DirectoryItem)item).list();
				return names;
			}
			else
			{
				throw new IOException(path+" is not a directory");
			}
		}
		else
		{
	        if(isDirectory(path))
	        {
	            HashSet<String> res;
	            try
	            {
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                int c;
	                InputStream is = getInputStream(path);
	                while((c = is.read()) > 0)
	                {
	                    baos.write(c);
	                }
	                is.close();
	                String dir = baos.toString("UTF-8");
	                String[] items = dir.split("\n");
	                res = new HashSet<String>(items.length);
	                for(String item : items)
	                {
	                    res.add(item.trim());
	                }
	                return res;
	            }
	            catch(IOException e)
	            {
	                Logger log = Logger.getLogger(this.getClass());
	                log.error("faield to list directory "+path, e);
	                // fall trough to return statement below 
	            }
	        }
	        return new HashSet<String>(0);
		}
    }

    /**
     * {@inheritDoc}
     */
    public boolean createNewFile(String path)
        throws IOException
    {
        throw new IOException("not supported");
    }

    /**
     * {@inheritDoc}
     */
    public void mkdirs(String path) 
    	throws IOException
    {
    	throw new IOException("not supported");
    }

    /**
     * {@inheritDoc}
     */
    public void delete(String path) 
    	throws IOException
    {
		throw new IOException("not supported");
    }

    /**
     * {@inheritDoc}
     */
    public void rename(String from, String to) throws IOException
    {
		throw new IOException("not supported");
    }

    /**
     * {@inheritDoc}
     */
	public abstract InputStream getInputStream(String path); 

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream(String path, boolean append)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RandomAccessFile getRandomAccess(String path, String mode)
    {
		return null;
    }

    /**
     * {@inheritDoc}
     */
	public long lastModified(String path)
	{
		if(listing != null)
		{
			Long mod = times.get(FileSystem.normalizedPath(path));
            if(mod != null)
            {
                return mod.longValue();
            }
            else
            {
                return -1L;
            }
		}
		else
		{
	        try
            {
                URL url = getResource(path);
                if(url != null)
                {
                    URLConnection conn;
                    try
                    {
                        conn = url.openConnection();
                        conn.connect();
                        return conn.getLastModified();
                    }
                    catch(IOException e)
                    {
                        return -1L;
                    }
                }
                else
                {
                    return -1L;
                }
            }
            catch(MalformedURLException e)
            {
                throw new RuntimeException("malformed path", e);
            }
		}
	}

    /**
     * {@inheritDoc}
     */
	public long length(String path)
	{
		if(listing != null)
		{
			Long mod = lengths.get(FileSystem.normalizedPath(path));
            if(mod != null)
            {
                return mod.longValue();
            }
            else
            {
                return -1L;
            }
        }
		else
		{
	        try
            {
                URL url = getResource(path);
                if(url != null)
                {
                    URLConnection conn;
                    try
                    {
                        conn = url.openConnection();
                        conn.connect();
                        return conn.getContentLength();
                    }
                    catch(IOException e)
                    {
                        return -1;
                    }
                }
                else
                {
                    return -1;
                }
            }
            catch(MalformedURLException e)
            {
                throw new RuntimeException("malformed path", e);
            }
		}
	}  
	
	private static abstract class ListingItem 
	{
	    
	}
	
    private static class DirectoryItem
        extends ListingItem
    {
        private final Map<String, ListingItem> items = new HashMap<String, ListingItem>();
        
        public void put(String name, ListingItem item)
        {
            items.put(name, item);
        }

        public Set<String> list()
        {            
            return items.keySet();
        }

        public ListingItem get(String name)
        {
            return items.get(name);
        }

        public boolean contains(String name)
        {
            return items.containsKey(name);
        }
    }
    
    private static class FileItem
        extends ListingItem
    {
        
    }
}
