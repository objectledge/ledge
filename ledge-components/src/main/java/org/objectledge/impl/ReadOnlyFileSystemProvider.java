package org.objectledge.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.RandomAccessFile;

/**
 * A base class for read only FileService backend implemetations. 
 * 
 *  @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 *  @version $Id: ReadOnlyFileSystemProvider.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public abstract class ReadOnlyFileSystemProvider 
	implements FileSystemProvider
{
	// constants ////////////////////////////////////////////////////////////
	
	public static final String[] listingLocation = new String[] 
	{
		"/WEB-INF/files.lst",
		"/META-INF/files.lst",
		"/files.lst"	
	};
	
	// instance variables ///////////////////////////////////////////////////

    /** Providers's name. */
    protected String name;

	/** The file listing. */	
	private Map listing = new HashMap();

    /** The directory tree. */
    private Map directoryTree = new HashMap();

	/** The file listing. */	
	private Map times = new HashMap();

	/** The file listing. */	
	private Map lengths = new HashMap();

	// initialization ///////////////////////////////////////////////////////
	
	public ReadOnlyFileSystemProvider(String name, String listingEncoding)
	{
        this.name = name;
		String location = null;
		InputStream is = null;
		for(int i=0; i<listingLocation.length; i++)
		{
			location = listingLocation[i];
			is = getInputStream(location);
			if(is != null)
			{
				break;
			}
		}
		if(is != null)
		{
			processListing(location, listingEncoding, is);
		}
	}

    public String getName()
    {
        return name;
    }

    public boolean isReadOnly()
    {
        return true;
    }

    // TODO throw a meaningful exception type
	protected void processListing(String location, String encoding, InputStream is)
	{
		try
		{
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(is, encoding));
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
					throw new Error("invalid length "+length+" for file "+path+" in index "+location);
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
				String name = null;
                Map current = directoryTree;
				tokenLoop: while(st.hasMoreTokens())
				{
					name = st.nextToken();
					if(st.hasMoreTokens())
					{
						current = (Map)current.get(name);
						if(current == null)
						{
							throw new Error("missing parent directory for "+path+" in index "+location);
						}
					}
					else
					{
						if(current.containsKey(name))
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
						current.put(name, item);
						listing.put(path, item);
						times.put(path, timeObj);
						lengths.put(path, lengthObj);
					}
				}
			}
		}
		catch(UnsupportedEncodingException e)
		{
			throw new Error("listingEncoding "+encoding+" is not supported", e);
		}
		catch(IOException e)
		{
			throw new Error("failed to load index "+location, e);
		}
	}

	// FileProvider interface ////////////////////////////////////////
	
    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#exists(java.lang.String)
     */
    public boolean exists(String path)
    {
		if(listing != null)
		{
			return listing.containsKey(FileSystem.normalizedPath(path));
		}
		else
		{
			return getInputStream(path) != null;
		}
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#isFile(java.lang.String)
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

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#isDirectory(java.lang.String)
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

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#canRead(java.lang.String)
     */
    public boolean canRead(String path)
    {
		return exists(path);
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#canWrite(java.lang.String)
     */
    public boolean canWrite(String path)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#list(java.lang.String)
     */
    public String[] list(String dir) throws IllegalArgumentException
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
				return null;
			}
		}
		else
		{
			return null;
		}
    }

    public boolean createNewFile(String path)
        throws IOException
    {
        throw new IOException("not supported");
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#mkdirs(java.lang.String)
     */
    public void mkdirs(String path) 
    	throws IOException
    {
    	throw new IOException("not supported");
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#delete(java.lang.String)
     */
    public void delete(String path) 
    	throws IOException
    {
		throw new IOException("not supported");
    }

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#rename(java.lang.String, java.lang.String)
     */
    public void rename(String from, String to) throws IOException
    {
		throw new IOException("not supported");
    }

	/* (non-Javadoc)
	 * @see net.labeo.services.file.spi.FileProvider#getInputStream(java.lang.String)
	 */
	public abstract InputStream getInputStream(String path); 

    /* (non-Javadoc)
     * @see net.labeo.services.file.spi.FileProvider#getOutputStream(java.lang.String, boolean)
     */
    public OutputStream getOutputStream(String path, boolean append)
    {
        return null;
    }

	/* (non-Javadoc)
	 * @see net.labeo.services.file.spi.FileProvider#getRandomAccess(java.lang.String, java.lang.String)
     */
    public RandomAccessFile getRandomAccess(String path, String mode)
    {
		return null;
    }

    /* (non-Javadoc)
	 * @see net.labeo.services.file.spi.FileProvider#lastModified(java.lang.String)
	 */
	public long lastModified(String path)
	{
		if(times != null)
		{
			Long mod = (Long)times.get(FileSystem.normalizedPath(path));
			return mod.longValue();
		}
		else
		{
			return -1L;
		}
	}

	/* (non-Javadoc)
	 * @see net.labeo.services.file.spi.FileProvider#length(java.lang.String)
	 */
	public long length(String path)
	{
		if(lengths != null)
		{
			Long mod = (Long)lengths.get(FileSystem.normalizedPath(path));
			return mod.longValue();
		}
		else
		{
			return -1L;
		}
	}    
    
    // implementation ///////////////////////////////////////////////////////
}
