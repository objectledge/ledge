package org.objectledge.database;

import java.sql.SQLException;

public interface IdGenerator
{
    /**
     * Get the next row identifier for the table.
     * 
     * @param table the table name.
     * @return the identifier.
     * @throws SQLException if the id could not be generated.
     */
    long getNextId(String table)
        throws SQLException;
}
