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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;

/**
 * A persistent implementation of parameters container.
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: DBParameters.java,v 1.4 2004-02-10 12:00:13 fil Exp $
 */
public class DBParameters implements Parameters
{
	/** the logger */
	private Logger logger;

	/** db access component */
	private Database database;
	
	/** container id */
	private long id;

	/** internal container */
	private DefaultParameters container;

	/** modified  */
	private HashSet modified;
    
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
    	this.logger = logger;
    	this.database = database;
    	if(parameters != null)
    	{
    	   	container = new DefaultParameters(parameters);
    	}
    	else
    	{
			container = new DefaultParameters();
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
    	container.remove();
		List all = Arrays.asList(container.getParameterNames());
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
	public void remove(Set keys)
	{
		container.remove(keys);
		modified.add(keys);
		update();    	
	}
    
	/**
	 * Remove all except those with a keys specified in the set.
	 *
	 * @param keys the set of names.
	 */
	public void removeExcept(Set keys)
	{
		container.removeExcept(keys);
		List all = Arrays.asList(container.getParameterNames());
		all.removeAll(keys);
		modified.add(all);
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
		modified.add(Arrays.asList(parameters.getParameterNames()));
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
		return container.getChild(prefix);
    }
    
	/**
	 * Called every time parameter is modified.
	 *
	 */
	private void update()
	{
		Connection conn = null;
		try
		{
			conn = database.getConnection();
			Iterator iterator = modified.iterator();
			PreparedStatement deleteStmt = conn.prepareStatement(
				"DELETE FROM "+DBParametersManager.TABLE_NAME+" where parameters_id = "+id+
				"AND name = ?");
			PreparedStatement insertStmt = conn.prepareStatement(
				"INSERT INTO "+DBParametersManager.TABLE_NAME+" (parameters_id, name, value)" +
				" VALUES ("+id+", ?, ?)");
			while(iterator.hasNext())
			{
				String name = (String)iterator.next();
				String[] values = container.getStrings(name);
				name = DatabaseUtils.escapeSqlString(name);
				deleteStmt.setString(1,name);
				deleteStmt.addBatch();
				for(int j = 0; j < values.length; j++)
				{
					insertStmt.setString(1,name);
					insertStmt.setString(1,DatabaseUtils.escapeSqlString(values[j]));
					insertStmt.addBatch();
				}
			}
			deleteStmt.executeBatch();
			insertStmt.executeBatch();
		}
		catch(SQLException e)
		{
			throw new Error("Exception occurred during parameter update",e);
		}
		finally
		{
			DatabaseUtils.close(conn);
		}
	}
}
