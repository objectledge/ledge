package org.objectledge.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class SequenceIdGenerator
    implements IdGenerator
{
    private final DataSource dataSource;

    private final String query;

    public SequenceIdGenerator(DataSource dataSource)
        throws SQLException
    {
        this.dataSource = dataSource;
        switch(DatabaseType.detect(dataSource))
        {
        case POSTGRES:
            query = "SELECT nextval('%s_seq')";
            break;
        case HSQL:
        case H2:
            query = "CALL NEXT VALUE FOR %s_seq";
            break;
        case DERBY:
            query = "VALUES NEXT VALUE FOR %s_seq";
            break;
        default:
            throw new IllegalArgumentException("usupported database type");
        }
    }

    @Override
    public long getNextId(String table)
        throws SQLException
    {
        try(Connection conn = dataSource.getConnection())
        {
            try(Statement stmt = conn.createStatement())
            {
                try(ResultSet rset = stmt.executeQuery(String.format(query, table)))
                {
                    if(rset.next())
                    {
                        return rset.getLong(1);
                    }
                    throw new SQLException("sequence not found");
                }
            }
        }
    }
}
