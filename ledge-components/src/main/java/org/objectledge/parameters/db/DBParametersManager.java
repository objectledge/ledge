package org.objectledge.parameters.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: DBParametersManager.java,v 1.5 2004-02-10 12:00:13 fil Exp $
 */
public class DBParametersManager
{
	/** the table name */
	public static final String TABLE_NAME = "ledge_parameters";

	/** the logger */
	private Logger logger;
	
    /** the database */
    private Database database;
    
    /** the parameters cache */
    private Map localCache;
    
    /**
     * Component cons
     * 
     * @param database the database access component.
     * @param logger the logger.
     */
    public DBParametersManager(Database database, Logger logger)
    {
        this.logger = logger;
        this.database = database;
        localCache = new HashMap();
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
        try
        {
        	conn = database.getConnection();
            Statement statement = conn.createStatement();
            id = database.getNextId(TABLE_NAME);
            statement.execute("INSERT INTO "+TABLE_NAME+" values ("+id+",'','')");
        }
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to insert the empty parameters", e);
        }
        finally
        {
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
            return (Parameters)localCache.get(key);
        }
        parameters = new DefaultParameters();
        Connection conn = null;
        try
        {
            conn = database.getConnection( );
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT * from " + TABLE_NAME +
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
        catch (SQLException e)
        {
            throw new DBParametersException("Failed to retrieve object", e);
        }
        finally
        {
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
        try
        {
        	conn = database.getConnection();
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM "+TABLE_NAME+" where parameters_id = "+id);
            localCache.remove(key);
        }
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to delete parameters", e);
        }
        finally
        {
            DatabaseUtils.close(conn);
        }
    }
}
