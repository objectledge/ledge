package org.objectledge.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.TestCase;

public class DatabaseTypeTest
    extends TestCase
{

    public void testHsql()
        throws SQLException
    {
        Properties properties = new Properties();
        properties.setProperty("url", "jdbc:hsqldb:.");
        properties.setProperty("user", "sa");
        DataSource dataSource = new JDBCDataSource("", "org.hsqldb.jdbc.JDBCDataSource", properties);
        Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        DatabaseType system = DatabaseType.detect(connection);
        assertEquals(DatabaseType.HSQL, system);
        assertEquals("hsql", system.getSuffix());
    }
}
