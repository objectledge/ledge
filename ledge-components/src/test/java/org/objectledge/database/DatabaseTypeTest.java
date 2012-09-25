package org.objectledge.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import junit.framework.TestCase;

public class DatabaseTypeTest
    extends TestCase
{

    public void testHsql()
        throws SQLException
    {
        DataSource dataSource = new HsqldbDataSource("jdbc:hsqldb:.", "sa", "");
        final Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        DatabaseType system = DatabaseType.detect(connection);
        assertEquals(DatabaseType.HSQL, system);
        assertEquals("hsql", system.getSuffix());
    }
}
