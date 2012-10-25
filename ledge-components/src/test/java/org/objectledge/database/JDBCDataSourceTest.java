package org.objectledge.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

public class JDBCDataSourceTest
    extends TestCase
{
    private static String PG_PATH = "/usr/share/java/postgresql-jdbc4.jar";

    public void testHSQL()
        throws SQLException
    {
        Properties properties = new Properties();
        properties.setProperty("datasource.className", "org.hsqldb.jdbc.JDBCDataSource");
        properties.setProperty("url", "jdbc:hsqldb:.");
        properties.setProperty("user", "sa");

        DataSource ds = new JDBCDataSource(properties);
        Connection conn = ds.getConnection();
        DatabaseMetaData cmd = conn.getMetaData();
        assertEquals("HSQL Database Engine", cmd.getDatabaseProductName());
    }

    public void testDerby()
        throws SQLException
    {
        Properties properties = new Properties();
        properties.setProperty("datasource.className",
            "org.apache.derby.jdbc.EmbeddedSimpleDataSource");
        properties.setProperty("databaseName", "target/derby/btm");
        properties.setProperty("createDatabase", "true");

        DataSource ds = new JDBCDataSource(properties);
        Connection conn = ds.getConnection();
        DatabaseMetaData cmd = conn.getMetaData();
        assertEquals("Apache Derby", cmd.getDatabaseProductName());
    }

    public void testPostgres()
        throws SQLException
    {
        if(new File(PG_PATH).exists())
        {
            Properties properties = new Properties();
            properties.setProperty("datasource.classpath", PG_PATH);
            properties.setProperty("datasource.className", "org.postgresql.ds.PGSimpleDataSource");
            properties.setProperty("databaseName", "template1");
            properties.setProperty("user", "postgres");

            DataSource ds = new JDBCDataSource(properties);
            try
            {
                Connection conn = ds.getConnection();
                DatabaseMetaData cmd = conn.getMetaData();
                assertEquals("PostgreSQL", cmd.getDatabaseProductName());
            }
            catch(SQLException e)
            {
                assertTrue(e.getMessage().contains("no password was provided")
                    || e.getMessage().contains("authentication failed"));
            }
        }
        else
        {
            System.err.println(PG_PATH + " not available - skipping test");
        }
    }
}
