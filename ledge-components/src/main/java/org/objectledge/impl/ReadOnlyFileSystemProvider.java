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
 *  @version $Id: ReadOnlyFileSystemProvider.java,v 1.3 2003-11-25 08:18:31 fil Exp $
 */
public abstract class ReadOnlyFileSystemProvider 
	implements FileSystemProvider
{
	// constants ////////////////////////////////////////////////////////////
	
    /** locations of the file listings, in order of precedence. */
	public static final String[] LISTING_LOCATION = new String[] 
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
	
    /**
     * Creates an instance of the provider
     * 
     * @param name the name of the provider.
     * @param listingEncoding the character encoding used in the file listng.
     */
	public ReadOnlyFileSystemProvider(String name, String listingEncoding)
	{
        this.name = name;
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
			processListing(location, listingEncoding, is);
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
     * @param encoding the character encoding used in the listing.
     * @param is the stream used for reading the listing.
     */
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
					throw new Error("invalid length "+length+" for file "+path+" in index "+
                        location);
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
							throw new Error("missing parent directory for "+path+" in index "+
                                location);
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
	
    /**
     * {@inheritDoc}
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
			return mod.longValue();
		}
		else
		{
			return -1L;
		}
	}

    /**
     * {@inheritDoc}
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
}
