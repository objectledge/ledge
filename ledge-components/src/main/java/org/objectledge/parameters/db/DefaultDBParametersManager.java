package org.objectledge.parameters.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;

/**
 * Manages the parameters stored in database.
 * 
 * @author <a href="mailto:pablo@caltha.com">Pawel Potempski</a>
 * @version $Id: DefaultDBParametersManager.java,v 1.5 2005-12-20 09:09:32 pablo Exp $
 */
public class DefaultDBParametersManager implements DBParametersManager
{
	/** the logger */
	private Logger logger;
	
    /** the database */
    private Database database;
    
    /** the parameters cache */
    private Map<Long, Parameters> localCache;
    
    /**
     * Component cons.
     * 
     * @param database the database access component.
     * @param logger the logger.
     */
    public DefaultDBParametersManager(Database database, Logger logger)
    {
        this.logger = logger;
        this.database = database;
        localCache = Collections.synchronizedMap(new HashMap<Long, Parameters>());
    }
    
    /**
     * Creates an empty container stored in database.
     *
     * @return the container.
     * @throws DBParametersException thrown if creation failed.
     */
    public Parameters createContainer()
    	throws DBParametersException
    {
    	// TODO consider the synchronization.
        long id = -1;
        Connection conn = null;
        Statement statement = null;
        try
        {
        	conn = database.getConnection();
            statement = conn.createStatement();
            id = database.getNextId(TABLE_NAME);
            statement.execute("INSERT INTO "+TABLE_NAME+" values ("+id+",'','')");
        }
        ///CLOVER:OFF
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to insert the empty parameters", e);
        }
        ///CLOVER:ON
        finally
        {
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
        DBParameters parameters = new DBParameters(null, id, database, logger);
		Long key = new Long(id);
		localCache.put(key, parameters);
		return parameters;
    }

    /**
     * Retrieves parameters from database.
     *
     * @param id the parameters identifier.
     * @return the parameters.
     * @throws DBParametersException if parameters cannot be found.
     */
    public Parameters getParameters(long id) throws DBParametersException
    {
        Parameters parameters;
        Long key = new Long(id);
        if (localCache.containsKey(key))
        {
            return localCache.get(key);
        }
        parameters = new DefaultParameters();
        Connection conn = null;
        Statement statement = null;
        ResultSet result = null;
        try
        {
            conn = database.getConnection( );
            statement = conn.createStatement();
            result = statement.executeQuery("SELECT * from " + TABLE_NAME +
             										  " where parameters_id = " + id);
            boolean exist = false;
            while (result.next())
            {
                exist = true;
                if (!result.getString("name").equals(""))
                {
                    parameters.add(DatabaseUtils.unescapeSqlString(result.getString("name")), 
                                   DatabaseUtils.unescapeSqlString(result.getString("value")));
                }
            }
            if (!exist)
            {
                throw new DBParametersException("DBParameters with id = " + id + " does not exist");
            }
            parameters = new DBParameters(parameters, id, database, logger);
            localCache.put(key, parameters);
            return parameters;
        }
        ///CLOVER:OFF
        catch (SQLException e)
        {
            throw new DBParametersException("Failed to retrieve object", e);
        }
        ///CLOVER:ON
        finally
        {
            DatabaseUtils.close(result);
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }

    /**
     * Deletes parameters from database.
     *
     * @param id the parameters identifier
     * @throws DBParametersException thrown if failed to delete.
     */
    public void deleteParameters(long id)
    	throws DBParametersException
    {
        Long key = new Long(id);
        Connection conn = null;
        Statement statement = null;
        try
        {
        	conn = database.getConnection();
            statement = conn.createStatement();
            statement.execute("DELETE FROM "+TABLE_NAME+" where parameters_id = "+id);
            localCache.remove(key);
        }
        ///CLOVER:OFF
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to delete parameters", e);
        }
        ///CLOVER:ON
        finally
        {
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void preloadContainers()
        throws DBParametersException
    {
        Connection conn = null;
        Statement statement = null;
        ResultSet result = null;
        try
        {
            conn = database.getConnection();
            statement = conn.createStatement();
            result = statement.executeQuery("SELECT parameters_id, name, value FROM "+
                TABLE_NAME+" ORDER BY parameters_id");
            if(result.next())
            {
                long lastId;
                Parameters temp = new DefaultParameters(); 
                do
                {
                    do
                    {
                        lastId = result.getLong(1);
                        if(result.getString(2).length() > 0)
                        {
                            temp.add(DatabaseUtils.unescapeSqlString(result.getString(2)),
                                DatabaseUtils.unescapeSqlString(result.getString(3)));
                        }
                    }
                    while(result.next() && result.getLong(1) == lastId);
                    Parameters pc = new DBParameters(temp, lastId, database, logger);
                    temp.remove();
                    localCache.put(new Long(lastId), pc);
                }
                while(!result.isAfterLast());
            }
        }
        catch(SQLException e)
        {
            throw new DBParametersException("failed to preload parameters", e);
        }
        finally
        {
            DatabaseUtils.close(result);
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }    
}
