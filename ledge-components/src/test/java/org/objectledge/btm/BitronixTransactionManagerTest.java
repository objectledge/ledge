package org.objectledge.btm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.test.Coordination;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.SAXException;

public class BitronixTransactionManagerTest
    extends LedgeTestCase
{
    // hsqldb 2.2.9 has a bug causing a commit whenever XAResource is delisted from a transaction.
    // Because of this rollback attempt actually performs a commit!

    private static boolean HSQL_XA = false;

    private static boolean DERBY_XA = true;

    private static boolean PG_XA = true;

    public void testConfigSchema()
        throws ParserConfigurationException, SAXException, IOException
    {
        XMLValidator xmlValidator = new XMLValidator(new XMLGrammarCache());
        URL relaxng = getFileSystem().getResource(XMLValidator.RELAXNG_SCHEMA);
        URL rng = getFileSystem().getResource("org/objectledge/btm/BitronixTransactionManager.rng");
        xmlValidator.validate(rng, relaxng);
    }

    private BitronixTransactionManager startBtm()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException
    {
        ConfigurationFactory cf = new ConfigurationFactory(getFileSystem(), new XMLValidator(
            new XMLGrammarCache()), "/btm");
        initLog4J("INFO");
        return new BitronixTransactionManager(cf.getConfig("simple",
            BitronixTransactionManager.class));
    }

    public void testHsqlLocal()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            testLocal(btm, "hsql");
        }
    }

    public void testDerbyLocal()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            testLocal(btm, "derby");
        }
    }

    public void testPgLocal()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            try
            {
                final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                ds.getConnection().close(); // ensure database is available
            }
            catch(Exception e)
            {
                getLogger().warn("postgres database localhost/btm not available - skipping test");
                return;
            }

            testLocal(btm, "pg");
        }
    }

    /**
     * Perform local transactions, driven from Connection interface.
     */
    private void testLocal(BitronixTransactionManager btm, String dsName)
        throws SQLException
    {
        BitronixDataSource ds1 = new BitronixDataSource(dsName, btm);
        try (Connection c1 = ds1.getConnection())
        {
            try (Statement s1 = c1.createStatement())
            {
                if(DatabaseUtils.hasTable(c1, "test"))
                {
                    s1.execute("DROP TABLE test");
                }
                s1.execute("CREATE TABLE test ( a integer )");
                s1.execute("INSERT INTO test(a) values(1)");

                c1.commit();

                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                {
                    assertTrue(rs1.next());
                    assertEquals(1, rs1.getInt(1));
                }

                s1.execute("UPDATE test SET a = 2");
                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                {
                    assertTrue(rs1.next());
                    assertEquals(2, rs1.getInt(1));
                }

                c1.rollback();

                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                {
                    assertTrue(rs1.next());
                    assertEquals(1, rs1.getInt(1));
                }
            }
        }
        catch(Exception e)
        {
            throw new SQLException(e);
        }
    }

    public void testHsqlXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        if(HSQL_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                testXa(btm, "hsql");
            }
        }
    }

    public void testDerbyXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        if(DERBY_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                testXa(btm, "derby");
            }
        }
    }

    public void testPgXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        if(PG_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                try
                {
                    final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                    ds.getConnection().close(); // ensure database is available
                }
                catch(Exception e)
                {
                    getLogger().warn(
                        "postgres database localhost/btm not available - skipping test");
                    return;
                }

                testXa(btm, "pg");
            }
        }
    }

    /**
     * Perform global transactions with single XA resource.
     */
    private void testXa(BitronixTransactionManager btm, String dsName)
        throws SQLException
    {
        BitronixDataSource ds1 = new BitronixDataSource(dsName, btm);
        BitronixTransaction t = new BitronixTransaction(btm, new Context(), getLogger(),
            new LoggingConfigurator());
        try
        {
            t.begin();
            try (Connection c1 = ds1.getConnection())
            {
                try (Statement s1 = c1.createStatement())
                {
                    if(DatabaseUtils.hasTable(c1, "test"))
                    {
                        s1.execute("DROP TABLE test");
                    }
                    s1.execute("CREATE TABLE test ( a integer )");
                    s1.execute("INSERT INTO test(a) values(1)");
                }
                t.commit(true);
            }

            t.begin();
            try (Connection c1 = ds1.getConnection())
            {
                try (Statement s1 = c1.createStatement())
                {
                    try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                    {
                        assertTrue(rs1.next());
                        assertEquals(1, rs1.getInt(1));
                    }
                }
                t.commit(true);
            }

            t.begin();
            try (Connection c1 = ds1.getConnection())
            {
                try (Statement s1 = c1.createStatement())
                {
                    s1.execute("UPDATE test SET a = 2");
                    try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                    {
                        assertTrue(rs1.next());
                        assertEquals(2, rs1.getInt(1));
                    }
                    t.rollback(true);
                }
            }

            t.begin();
            try (Connection c1 = ds1.getConnection())
            {
                try (Statement s1 = c1.createStatement())
                {
                    try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                    {
                        assertTrue(rs1.next());
                        assertEquals(1, rs1.getInt(1));
                    }
                }
                t.commit(true);
            }
        }
        catch(Exception e)
        {
            throw new SQLException(e);
        }
    }

    public void testDerbyHsql2Xa()
        throws Exception
    {
        if(DERBY_XA && HSQL_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                test2xa(btm, "derby", "hsql");
            }
        }
    }

    public void testDerbyPg2Xa()
        throws Exception
    {
        if(DERBY_XA && PG_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                try
                {
                    final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                    ds.getConnection().close(); // ensure database is available
                }
                catch(Exception e)
                {
                    getLogger().warn(
                        "postgres database localhost/btm not available - skipping test");
                    return;
                }

                test2xa(btm, "derby", "pg");
            }
        }
    }

    public void testHsqlPg2Xa()
        throws Exception
    {
        if(HSQL_XA && PG_XA)
        {
            try (BitronixTransactionManager btm = startBtm())
            {
                try
                {
                    final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                    ds.getConnection().close(); // ensure database is available
                }
                catch(Exception e)
                {
                    getLogger().warn(
                        "postgres database localhost/btm not available - skipping test");
                    return;
                }

                test2xa(btm, "derby", "pg");
            }
        }
    }

    /**
     * Perform global XA transactions with two XA resources, which requires 2 phase commit
     */
    private void test2xa(BitronixTransactionManager btm, String pool1, String pool2)
        throws Exception
    {
        BitronixDataSource ds1 = new BitronixDataSource(pool1, btm);
        BitronixDataSource ds2 = new BitronixDataSource(pool2, btm);
        BitronixTransaction t = new BitronixTransaction(btm, new Context(), getLogger(),
            new LoggingConfigurator());

        t.begin();
        try (Connection c1 = ds1.getConnection();
             Statement s1 = c1.createStatement();
             Connection c2 = ds2.getConnection();
             Statement s2 = c2.createStatement())
        {
            if(DatabaseUtils.hasTable(ds1, "test"))
            {
                s1.execute("DROP TABLE test");
            }
            s1.execute("CREATE TABLE test ( a integer )");
            s1.execute("INSERT INTO test(a) values(1)");

            if(DatabaseUtils.hasTable(ds2, "test"))
            {
                s2.execute("DROP TABLE test");
            }
            s2.execute("CREATE TABLE test ( a integer )");
            s2.execute("INSERT INTO test(a) values(1)");
            t.commit(true);
        }

        t.begin();
        try (Connection c1 = ds1.getConnection();
             Statement s1 = c1.createStatement();
             Connection c2 = ds2.getConnection();
             Statement s2 = c2.createStatement())
        {
            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test");
                 ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
            {
                assertTrue(rs1.next());
                assertEquals(1, rs1.getInt(1));
                assertTrue(rs2.next());
                assertEquals(1, rs2.getInt(1));
            }
            t.commit(true);
        }

        t.begin();
        try (Connection c1 = ds1.getConnection();
             Statement s1 = c1.createStatement();
             Connection c2 = ds2.getConnection();
             Statement s2 = c2.createStatement())
        {
            s1.execute("UPDATE test SET a = 2");
            s2.execute("UPDATE test SET a = 2");
            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test");
                 ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
            {
                assertTrue(rs1.next());
                assertEquals(2, rs1.getInt(1));
                assertTrue(rs2.next());
                assertEquals(2, rs2.getInt(1));
            }

            t.rollback(true);
        }

        t.begin();
        try (Connection c1 = ds1.getConnection();
             Statement s1 = c1.createStatement();
             Connection c2 = ds2.getConnection();
             Statement s2 = c2.createStatement())
        {
            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test");
                 ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
            {
                assertTrue(rs1.next());
                assertEquals(1, rs1.getInt(1));
                assertTrue(rs2.next());
                assertEquals(1, rs2.getInt(1));
            }
            t.commit(true);
        }
    }

    public void testHsqlParallel()
        throws Exception
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            testParallel(btm, "hsql");
        }
    }

    public void testDerbyParallel()
        throws Exception
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            testParallel(btm, "derby");
        }
    }

    public void testPgParallel()
        throws Exception
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            try
            {
                final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                ds.getConnection().close(); // ensure database is available
            }
            catch(Exception e)
            {
                getLogger().warn("postgres database localhost/btm not available - skipping test");
                return;
            }

            testParallel(btm, "pg");
        }
    }

    /**
     * Interleave transactions from two parallel threads.
     */
    private void testParallel(BitronixTransactionManager btm, String poolName)
        throws Exception
    {
        final BitronixDataSource ds = new BitronixDataSource("pg", btm);
        final BitronixTransaction t = new BitronixTransaction(btm, new Context(), getLogger(),
            new LoggingConfigurator());

        Coordination.Participant p1 = new Coordination.Participant()
            {
                @Override
                public void step(int num)
                    throws Exception
                {
                    switch(num)
                    {
                    case 1:
                        t.begin();
                        try (Connection c1 = ds.getConnection();
                             Statement s1 = c1.createStatement())
                        {
                            if(DatabaseUtils.hasTable(ds, "test"))
                            {
                                s1.execute("DROP TABLE test");
                            }
                            s1.execute("CREATE TABLE test ( a integer )");
                            s1.execute("INSERT INTO test(a) values(1)");
                            t.commit(true);
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        t.begin();
                        try (Connection c1 = ds.getConnection();
                             Statement s1 = c1.createStatement())
                        {
                            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs1.next());
                                assertEquals(1, rs1.getInt(1));
                            }
                            t.commit(true);
                        }
                        break;
                    case 4:
                        break;
                    case 5:
                        t.begin();
                        try (Connection c1 = ds.getConnection();
                             Statement s1 = c1.createStatement())
                        {
                            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs1.next());
                                assertEquals(2, rs1.getInt(1));
                            }
                            t.commit(true);
                        }
                        break;
                    }
                }

                @Override
                public String toString()
                {
                    return "p1";
                }
            };

        Coordination.Participant p2 = new Coordination.Participant()
            {
                private Connection c2;

                @Override
                public void step(int num)
                    throws Exception
                {
                    switch(num)
                    {
                    case 1:
                        break;
                    case 2:
                        t.begin();
                        c2 = ds.getConnection();
                        try (Statement s2 = c2.createStatement())
                        {
                            s2.execute("UPDATE test SET a = 2");
                            try (ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs2.next());
                                assertEquals(2, rs2.getInt(1));
                            }
                        }
                        break;
                    case 3:
                        break;
                    case 4:
                        t.commit(true);
                        c2.close();
                        break;
                    case 5:
                        break;
                    }
                }

                @Override
                public String toString()
                {
                    return "p2";
                }
            };

        Coordination.Monitor monitor = new Coordination.Monitor()
            {
                @Override
                public void before(Coordination.Participant participant, int step)
                {
                    System.out.println("before " + participant.toString() + " step " + step);
                }
            };

        Coordination coord = new Coordination(5, 5, TimeUnit.SECONDS, monitor, p1, p2);
        Collection<Exception> exceptions = coord.run();
        for(Exception e : exceptions)
        {
            e.printStackTrace();
        }
        assertTrue(exceptions.isEmpty());
    }
}
