package org.objectledge.parameters.db;

import org.objectledge.parameters.Parameters;

/**
 * Manages the parameters stored in database.
 * 
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: DBParametersManager.java,v 1.7 2004-03-10 21:49:14 pablo Exp $
 */
public interface DBParametersManager
{
    /** the table name */
    public static final String TABLE_NAME = "parameters";
    
    /**
     * Creates an empty container stored in database.
     *
     * @return the container.
     * @throws DBParametersException thrown if creation failed.
     */
    public Parameters createContainer()
    	throws DBParametersException;
    /**
     * Retrieves parameters from database.
     *
     * @param id the parameters identifier.
     * @return the parameters.
     * @throws DBParametersException if parameters cannot be found.
     */
    public Parameters getParameters(long id)
        throws DBParametersException;
    
    /**
     * Deletes parameters from database.
     *
     * @param id the parameters identifier
     * @throws DBParametersException thrown if failed to delete.
     */
    public void deleteParameters(long id)
    	throws DBParametersException;
}