package org.objectledge.parameters.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.utils.StringUtils;

/**
 * Manages the parameters stored in database.
 * 
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: DBParametersManager.java,v 1.1 2004-01-20 17:19:35 pablo Exp $
 */
public class DBParametersManager
{
	/** the table name */
	public static final String TABLE = "ledge_parameters";

	/** the logger */
	private Logger logger;
	
    /** The db access component */
    private Object dbc;
    
    /** the parameters cache */
    private Map localCache;
    
    /**
     * Component cons
     * 
     * @param logger the logger.
     * @param dbc the database access component.
     */
    public DBParametersManager(Logger logger, Object dbc)
    {
        this.logger = logger;
        this.dbc = dbc;
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
        	//TODO uncomment those when possible.
            //conn = dbc.getConnection();
            Statement statement = conn.createStatement();
            //id = dbc.getNextId(TABLE, conn);
            statement.execute("INSERT INTO "+TABLE+" values ("+id+",'','')");
        }
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to insert the empty parameters", e);
        }
        finally
        {
            close(conn);
        }
        DBParameters parameters = new DBParameters(logger, dbc, null, id);
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
            //TODO uncomment this when possible
            //conn = dbc.getConnection( );
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT * from " + TABLE +
             										  " where parameters_id = " + id);
            boolean exist = false;
            while (result.next())
            {
                exist = true;
                if (!result.getString("name").equals(""))
                {
                    parameters.add(unescape(result.getString("name")), 
                    			   unescape(result.getString("value")));
                }
            }
            if (!exist)
            {
                throw new DBParametersException("DBParameters with id = " + id + " does not exist");
            }
            parameters = new DBParameters(logger, dbc, parameters, id);
            localCache.put(key, parameters);
            return parameters;
        }
        catch (SQLException e)
        {
            throw new DBParametersException("Failed to retrieve object", e);
        }
        finally
        {
            close(conn);
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
        	//TODO uncomment it when possible
            //conn = dbc.getConnection();
            Statement statement = conn.createStatement();
            statement.execute("DELETE FROM "+TABLE+" where parameters_id = "+id);
            localCache.remove(key);
        }
        catch(SQLException e)
        {
            throw new DBParametersException("Failed to delete parameters", e);
        }
        finally
        {
            close(conn);
        }
    }
    
	/**
	 * Unescape unicode escapes.
	 */
	private String unescape(String string)
	{
		return StringUtils.expandUnicodeEscapes(string);
	}

    private void close(Connection conn)
    {
        try
        {
            if(conn != null)
            {
                conn.close();
            }
        }
        catch(SQLException e)
        {
            logger.error("failed to close connection", e);
        }
    }
}
