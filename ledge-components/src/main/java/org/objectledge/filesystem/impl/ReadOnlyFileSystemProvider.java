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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;

/**
 * A base class for read only FileSystem backend implemetations. 
 * 
 *  @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: ReadOnlyFileSystemProvider.java,v 1.15 2004-03-19 13:52:11 pablo Exp $
 */
public abstract class ReadOnlyFileSystemProvider 
	implements FileSystemProvider
{
	// constants ////////////////////////////////////////////////////////////
	
    /** locations of the file listings, in order of precedence. */
	public static final String[] LISTING_LOCATION =  
	{
		"/WEB-INF/files.lst",
		"/META-INF/files.lst",
		"/files.lst"	
	};
    
    /** the character encoding used in file listings. */
    public static final String LISTING_ENCODING = "UTF-8";
	
	// instance variables ///////////////////////////////////////////////////

    /** Providers's name. */
    protected String name;

	/** The file listing. */	
	private Map listing = null;

    /** The directory tree. */
    private Map directoryTree = new HashMap();

	/** The file listing. */	
	private Map times = new HashMap();

	/** The file listing. */	
	private Map lengths = new HashMap();

	// initialization ///////////////////////////////////////////////////////
	
    /**
     * Creates an instance of the provider
     * 
     * @param name the name of the provider.
     */
	public ReadOnlyFileSystemProvider(String name)
	{
        this.name = name;
	}

    /**
     * Processes the liststings at the common locations.
     * 
     * <p>{@link #getInputStream(String)} method must be functional at the point of calling this
     * method.</p>
     * 
     * @throws IOException if the listings cannot be processed.
     */
    protected void processListings()
        throws IOException
    {
        String location = null;
        InputStream is = null;
        for(int i=0; i<LISTING_LOCATION.length; i++)
        {
            location = LISTING_LOCATION[i];
            is = getInputStream(location);
            if(is != null)
            {
                break;
            }
        }
        if(is != null)
        {
            processListing(location, is);
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
                listing = new HashMap();
            }
			ArrayList tempList = new ArrayList();
			StringBuffer tempBuffer = new StringBuffer();
			while(reader.ready())
			{
				String line = reader.readLine();
				String path = null;
				String length = null;
				String time = null;
				StringTokenizer lt = new StringTokenizer(line," ");
				if(lt.countTokens() > 3)
				{
					tempList.clear();
					tempList.ensureCapacity(lt.countTokens());
					while(lt.hasMoreTokens())
					{
						tempList.add(lt.nextToken());
					}
					tempBuffer.setLength(0);
					for(int i=0; i<tempList.size()-2; i++)
					{
						tempBuffer.append((String)tempList.get(i));
						tempBuffer.append(' ');
					}
					path = tempBuffer.substring(0, tempBuffer.length()-1);
					length = (String)tempList.get(tempList.size()-2);
					time = (String)tempList.get(tempList.size()-1);
				}
				else if(lt.countTokens() == 3)
				{
					path = lt.nextToken();
					length = lt.nextToken();
					time = lt.nextToken();
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
					lengthObj = new Long(length);
				}
				catch(NumberFormatException e)
				{
					throw new ComponentInitializationError("invalid length "+length+" for file "+
                        path+" in index "+location);
				}
				try
				{
					timeObj = new Long(time);
				}
				catch(NumberFormatException e)
				{
					throw new Error("invalid time "+time+" for file "+path+" in index "+location);
				}
				StringTokenizer st = new StringTokenizer(path,"/");
				String token = null;
                Map current = directoryTree;
				tokenLoop: while(st.hasMoreTokens())
				{
					token = st.nextToken();
					if(st.hasMoreTokens())
					{
						current = (Map)current.get(token);
						if(current == null)
						{
							throw new ComponentInitializationError("missing parent directory for "+
                                path+" in index "+location);
						}
					}
					else
					{
						if(current.containsKey(token))
						{
							// we seem to be merging indices
							continue tokenLoop;
						}
						Object item;
						if(path.endsWith("/"))
						{
							item = new HashMap();
							path = path.substring(0, path.length()-1);
						}
						else
						{
							item = new Object();
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
    public boolean exists(String path)
    {
		if(listing != null)
		{
			boolean contains = listing.containsKey(FileSystem.normalizedPath(path));
            if(contains)
            {
                return true;
            }
		}
		return getInputStream(path) != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile(String path)
    {
		if(listing != null)
		{
			return !(listing.get(FileSystem.normalizedPath(path)) instanceof Map);
		}
		else
		{
			return getInputStream(path) != null;
		}
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(String path)
    {
		if(listing != null)
		{
			return listing.get(FileSystem.normalizedPath(path)) instanceof Map;
		}
		else
		{
			return false;
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
    public String[] list(String dir) 
        throws IOException
    {
		if(listing != null)
		{
			Object obj = listing.get(FileSystem.normalizedPath(dir));
			if(obj instanceof Map)
			{
				Set names = ((Map)obj).keySet();
				String[] result = new String[names.size()];
				names.toArray(result);
				return result;
			}
			else
			{
				throw new IOException(dir+" is not a directory");
			}
		}
		else
		{
			throw new IOException(dir+" does not exist");
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
		if(times != null)
		{
			Long mod = (Long)times.get(FileSystem.normalizedPath(path));
            if(mod != null)
            {
                return mod.longValue();
            }
		}
        return -1L;
	}

    /**
     * {@inheritDoc}
     */
	public long length(String path)
	{
		if(lengths != null)
		{
			Long mod = (Long)lengths.get(FileSystem.normalizedPath(path));
            if(mod != null)
            {
                return mod.longValue();
            }
        }
		return -1L;
	}    
}
