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

package org.objectledge.parameters.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.ScopedParameters;

/**
 * A persistent implementation of parameters container.
 *
 * @author <a href="mailto:pablo@caltha.com">Pawel Potempski</a>
 * @version $Id: DBParameters.java,v 1.11 2007-01-28 11:02:16 rafal Exp $
 */
public class DBParameters implements Parameters
{
	/** db access component */
	private Database database;
	
	/** container id */
	private long id;

	/** internal container */
	private DefaultParameters container;
    
    /** internal container */
    private DefaultParameters shadow;
    
	/** modified  */
	private HashSet<String> modified;
    
    /**
     * Create the container.
     * 
     * @param parameters the initial parameters.
     * @param id the database identifier.
     * @param database the db access component
     * @param logger the logger.
     */
    public DBParameters(Parameters parameters, long id, Database database, Logger logger)
    {
        modified = new HashSet<String>();
    	this.database = database;
    	if(parameters != null)
    	{
    	   	container = new DefaultParameters(parameters);
            shadow = new DefaultParameters(parameters);
    	}
    	else
    	{
			container = new DefaultParameters();
            shadow = new DefaultParameters();
    	}
    	this.id = id;
    }

	/**
	 * {@inheritDoc}
	 */
    public String get(String name)
    {
		return container.get(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public String get(String name, String defaultValue)
    {
		return container.get(name, defaultValue);
    }

	/**
	 * {@inheritDoc}
	 */
    public String[] getStrings(String name)
    {
		return container.getStrings(name);    }

	/**
	 * {@inheritDoc}
	 */
    public boolean getBoolean(String name)
    {
		return container.getBoolean(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean getBoolean(String name, boolean defaultValue)
    {
		return container.getBoolean(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
		return container.getBooleans(name);
    }


    /**
     * {@inheritDoc}
     */
    public Date getDate(String name)
    {
        return container.getDate(name);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String name, Date defaultValue)
    {
        return container.getDate(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public Date[] getDates(String name)
    {
        return container.getDates(name);
    }
    
    /**
	 * {@inheritDoc}
	 */
    public float getFloat(String name) throws NumberFormatException
    {
		return container.getFloat(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public float getFloat(String name, float defaultValue)
    {
		return container.getFloat(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name) throws NumberFormatException
    {
		return container.getFloats(name);
	}

	/**
	 * {@inheritDoc}
	 */
    public int getInt(String name) throws NumberFormatException
    {
		return container.getInt(name);
    	
    }

	/**
	 * {@inheritDoc}
	 */
    public int getInt(String name, int defaultValue)
    {
		return container.getInt(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name)
    {
		return container.getInts(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public long getLong(String name) throws NumberFormatException
    {
		return container.getLong(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public long getLong(String name, long defaultValue)
    {
		return container.getLong(name,defaultValue);
    }
    
	/**
	 * {@inheritDoc}
	 */
	public long[] getLongs(String name) throws NumberFormatException
	{
		return container.getLongs(name);
	}

	/**
	 * {@inheritDoc}
	 */
    public String[] getParameterNames()
    {
		return container.getParameterNames();
    }

	/**
	 * {@inheritDoc}
	 */
    public boolean isDefined(String name)
    {
    	return container.isDefined(name);
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove()
    {
        List<String> all = Arrays.asList(container.getParameterNames());
    	container.remove();
		modified.addAll(all);
		update();
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove(String name)
    {
		container.remove(name);
		modified.add(name);
    	update();    
    }

	/**
	 * {@inheritDoc}
	 */
    public void remove(String name, String value)
    {
		container.remove(name,value);
		modified.add(name);
		update();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, Date value)
    {
        container.remove(name,value);
        modified.add(name);
        update();       
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, float value)
    {
        container.remove(name,value);
        modified.add(name);
        update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, int value)
    {
		container.remove(name,value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String name, long value)
    {
		container.remove(name,value);
		modified.add(name);
		update();    	
    }
    
	/**
	 * Remove all parameters with a name contained in given set.
	 *
	 * @param keys the set of keys.
	 */
	public void remove(Set<String> keys)
	{
		container.remove(keys);
		modified.addAll(keys);
		update();    	
	}
    
	/**
	 * Remove all except those with a keys specified in the set.
	 *
	 * @param keys the set of names.
	 */
	public void removeExcept(Set<String> keys)
	{
		container.removeExcept(keys);
		List<String> all = new ArrayList<String>(Arrays.asList(container.getParameterNames()));
		all.removeAll(keys);
		modified.addAll(all);
		update();    	
	}

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, String value)
    {
		container.set(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, String[] values)
    {
		container.set(name, values);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date value)
    {
        container.set(name, value);
        modified.add(name);
        update();       
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, Date[] values)
    {
        container.set(name, values);
        modified.add(name);
        update();       
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, boolean value)
    {
		container.set(name, value);
        modified.add(name);
        update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, boolean[] values)
    {
		container.set(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, float value)
    {
		container.set(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, float[] values)
    {
		container.set(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, int value)
    {
		container.set(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, int[] values)
    {
		container.set(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void set(String name, long value)
    {
		container.set(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void set(String name, long[] values)
    {
		container.set(name, values);
		modified.add(name);
		update();    	
    }

	/**
     * {@inheritDoc}
     */
	public void set(Parameters parameters)
	{
		String[] names = container.getParameterNames();
		for(String name:names)
		{
			modified.add(name);
		}
		container.set(parameters);
		names = parameters.getParameterNames();
		for(String name:names)
		{
			modified.add(name);
		}
		update();
	}
	
	/**
	 * {@inheritDoc}
	 */
    public void add(String name, String value)
    {
		container.add(name, value);
		modified.add(name);
		update();    	
    }
    
	/**
	 * {@inheritDoc}
	 */
	public void add(String name, String[] values)
	{
		container.add(name, values);
		modified.add(name);
		update();    	
	}

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date value)
    {
        container.add(name, value);
        modified.add(name);
        update();       
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, Date[] values)
    {
        container.add(name, values);
        modified.add(name);
        update();       
    }

    /**
	 * {@inheritDoc}
	 */
    public void add(String name, boolean value)
    {
		container.add(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, boolean[] values)
    {
		container.add(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, float value)
    {
		container.add(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, float[] values)
    {
		container.add(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, int value)
    {
		container.add(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, int[] values)
    {
		container.add(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(String name, long value)
    {
		container.add(name, value);
		modified.add(name);
		update();    	
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, long[] values)
    {
		container.add(name, values);
		modified.add(name);
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public void add(Parameters parameters, boolean overwrite)
    {
		container.add(parameters, overwrite);
		modified.addAll(Arrays.asList(parameters.getParameterNames()));
		update();    	
    }

	/**
	 * {@inheritDoc}
	 */
    public String toString()
    {
		return container.toString();
    }

	/**
	 * {@inheritDoc}
	 */
    public Parameters getChild(String prefix)
    {
        return new ScopedParameters(this, prefix);
    }
    
	/**
	 * Called every time parameter is modified.
	 *
	 */
	private void update()
	{
		Connection conn = null;
		PreparedStatement deleteStmt = null;
		PreparedStatement insertStmt = null;
        boolean isInsert = false;
        boolean doDelete = false;
		try
		{
			conn = database.getConnection();
			Iterator<String> iterator = modified.iterator();
            deleteStmt = conn.prepareStatement(
				"DELETE FROM "+DBParametersManager.TABLE_NAME+" where parameters_id = ? AND name = ?");
            insertStmt = conn.prepareStatement(
				"INSERT INTO "+DBParametersManager.TABLE_NAME+" (parameters_id, name, value)" +
				" VALUES (?, ?, ?)");
			while(iterator.hasNext())
			{
				String name = iterator.next();
                if(!areValuesEqual(name))
                {
    				String[] values = container.getStrings(name);
    				deleteStmt.setLong(1, id);
    				deleteStmt.setString(2, name);
    				deleteStmt.addBatch();
                    doDelete = true;
    				for(int j = 0; j < values.length; j++)
    				{
                        isInsert = true;
                        insertStmt.setLong(1, id);
    					insertStmt.setString(2, name);
    					insertStmt.setString(3, values[j]);
    					insertStmt.addBatch();
    				}
                }
			}
            if(doDelete)
            {
                deleteStmt.executeBatch();
            }
            if(isInsert)
            {
    			insertStmt.executeBatch();
            }
            shadow = new DefaultParameters(container);
		}
        ///CLOVER:OFF
		catch(SQLException e)
		{
			throw new Error("Exception occurred during parameter update",e);
		}
        ///CLOVER:ON
		finally
		{
		    DatabaseUtils.close(insertStmt);
		    DatabaseUtils.close(deleteStmt);
			DatabaseUtils.close(conn);
		}
	}
    
    
    private boolean areValuesEqual(String name)
    {
        String[] current = container.getStrings(name);
        String[] previous = shadow.getStrings(name);
        if(current.length != previous.length)
        {
            return false;
        }
        if(current.length == 0)
        {
            return true;
        }
        if(current.length == 1)
        {
            return current[0].equals(previous[0]);
        }
        ArrayList<String> currentList = new ArrayList<String>();
        ArrayList<String> previousList = new ArrayList<String>();
        for(String temp: current)
        {
            currentList.add(temp);
        }
        for(String temp: previous)
        {
            previousList.add(temp);
        }
        Collections.sort(currentList);
        Collections.sort(previousList);
        for(int i = 0; i < currentList.size(); i++)
        {
            if(!currentList.get(i).equals(previousList.get(i)))
            {
                return false;
            }
        }
        return true;
    }
    

    /**
     * Get the parameters identifier in database.
     * 
     * @return the parameters identifier.
     */    
    public long getId()
    {
        return id;
    }
}
