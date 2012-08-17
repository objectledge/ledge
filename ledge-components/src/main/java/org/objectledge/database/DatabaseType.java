package org.objectledge.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.regex.Pattern;

import javax.sql.DataSource;

/**
 * Represents relational database type.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public enum DatabaseType
{
    // @formatter:off
    
    HSQL("HSQL Database Engine", "hsql"), 
    POSTGRES("PostgreSQL", "pg"), 
    MYSQL("MySQL", "mysql"), 
    H2("H2", "h2"),
    UNKNOWN(".*", null); // must be last 
    
    // @formatter:on

    private Pattern productNamePattern;

    private String suffix;

    private DatabaseType(String prouctName, String suffix)
    {
        this.productNamePattern = Pattern.compile(prouctName);
        this.suffix = suffix;
    }

    /**
     * Returns a short textual identifier of the database that can be used to differentiate SQL
     * script files.
     * 
     * @return database identifier, or {@code null} for the {@link DatabaseType#UNKNOWN} type.
     */
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * Attempt to detect connected database type.
     * 
     * @param conn a {@code java.sql.Connection}
     * @return database type, possibly {@link DatabaseType#UNKNOWN}
     * @throws SQLException when database metadata cannot be retrieved from the connection.
     */
    public static DatabaseType detect(Connection conn)
        throws SQLException
    {
        DatabaseMetaData md = conn.getMetaData();
        for(DatabaseType type : EnumSet.allOf(DatabaseType.class))
        {
            if(type.productNamePattern.matcher(md.getDatabaseProductName()).matches())
            {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Attempt to detect connected database type.
     * 
     * @param dataSource a SQL {@code javax.sql.DataSource}
     * @return database type, possibly {@link DatabaseType#UNKNOWN}
     * @throws SQLException when database metadata cannot be retrieved from the data source.
     */
    public static DatabaseType detect(DataSource dataSource)
        throws SQLException
    {
        Connection conn = dataSource.getConnection();
        try
        {
            return detect(conn);
        }
        finally
        {
            conn.close();
        }
    }
}
