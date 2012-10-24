package org.objectledge.btm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.transaction.UserTransaction;
import javax.xml.parsers.ParserConfigurationException;

import org.jcontainer.dna.ConfigurationException;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.LocalUserTransaction;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.test.Coordination;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.SAXException;

public class BitronixTransactionManagerTest
    extends LedgeTestCase
{
    /**
     * As of HSQL 2.2.9, fails with ClassCastException inside HSQL XA implementation
     */
    private static boolean DERBY_WITH_HSQL = false;

    /**
     * As of HSQL 2.2.9 fails with assertion error. Update gets committed immediately despite
     * autoCommit = false
     */
    private static boolean HSQL_XA = false;

    /**
     * As of Derby 10.9.1.0 hangs (deadlock in derby)
     */
    private static boolean DERBY_XA = false;

    public void testConfigSchema()
        throws ParserConfigurationException, SAXException, IOException
    {
        XMLValidator xmlValidator = new XMLValidator(new XMLGrammarCache());
        URL relaxng = getFileSystem().getResource(XMLValidator.RELAXNG_SCHEMA);
        URL rng = getFileSystem().getResource("org/objectledge/btm/BitronixTransactionManager.rng");
        xmlValidator.validate(rng, relaxng);
    }

    protected BitronixTransactionManager startBtm()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException
    {
        ConfigurationFactory cf = new ConfigurationFactory(getFileSystem(), new XMLValidator(
            new XMLGrammarCache()), "/btm");
        initLog4J("INFO");
        return new BitronixTransactionManager(cf.getConfig("simple",
            BitronixTransactionManager.class));
    }

    public void testHsql()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            doTest(btm, "hsql", false);
        }
    }

    public void testHsqlXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        if(!HSQL_XA)
        {
            return;
        }
        try (BitronixTransactionManager btm = startBtm())
        {
            doTest(btm, "hsql", true);
        }
    }

    public void testDerby()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            doTest(btm, "derby", false);
        }
    }

    public void testDerbyXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        if(!DERBY_XA)
        {
            return;
        }
        try (BitronixTransactionManager btm = startBtm())
        {
            doTest(btm, "derby", true);
        }
    }

    public void testPg()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            final BitronixDataSource ds = new BitronixDataSource("pg", btm);
            ds.getConnection().close(); // ensure database is available
            doTest(btm, "pg", false);
        }
        catch(Exception e)
        {
            getLogger().warn("postgres database localhost/btm not available - skipping test");
            return;
        }
    }

    public void testPgXa()
        throws IOException, ParserConfigurationException, SAXException, ConfigurationException,
        SQLException
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            final BitronixDataSource ds = new BitronixDataSource("pg", btm);
            ds.getConnection().close(); // ensure database is available
            doTest(btm, "pg", true);
        }
        catch(Exception e)
        {
            getLogger().warn("postgres database localhost/btm not available - skipping test");
            return;
        }
    }

    protected void doTest(BitronixTransactionManager btm, String dsName, boolean xa)
        throws SQLException
    {
        BitronixDataSource ds = new BitronixDataSource(dsName, btm);
        BitronixTransaction t = new BitronixTransaction(btm, new Context(), getLogger(),
            new LoggingConfigurator());
        try (Connection conn = ds.getConnection())
        {
            doTest(conn, xa ? t.getUserTransaction() : new LocalUserTransaction(conn));
        }
    }

    protected void doTest(Connection c1, UserTransaction t)
        throws SQLException
    {
        try
        {
            t.begin();
            c1.setAutoCommit(false);
            try (Statement s1 = c1.createStatement())
            {
                if(DatabaseUtils.hasTable(c1, "test"))
                {
                    s1.execute("DROP TABLE test");
                }
                s1.execute("CREATE TABLE test ( a integer )");
                s1.execute("INSERT INTO test(a) values(1)");

                t.commit();
                t.begin();

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

                t.rollback();
                t.begin();

                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                {
                    assertTrue(rs1.next());
                    assertEquals(1, rs1.getInt(1));
                }
                t.commit();
            }
        }
        catch(Exception e)
        {
            throw new SQLException(e);
        }
    }

    /**
     * Attempt a global XA transaction with two XA resources, Derby and Hsql
     * 
     * @throws Exception
     */
    public void testDerbyWithHsql()
        throws Exception
    {
        if(!DERBY_WITH_HSQL)
        {
            return;
        }
        try (BitronixTransactionManager btm = startBtm())
        {
            BitronixDataSource ds1 = new BitronixDataSource("hsql", btm);
            BitronixDataSource ds2 = new BitronixDataSource("derby", btm);
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
                t.begin();

                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test");
                                ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
                {
                    assertTrue(rs1.next());
                    assertEquals(1, rs1.getInt(1));
                    assertTrue(rs2.next());
                    assertEquals(1, rs2.getInt(1));
                }

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
                t.begin();

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
            catch(Error | RuntimeException e)
            {
                t.rollback(true);
                throw e;
            }
        }
    }

    public void testPgMultiThreaded()
        throws Exception
    {
        try (BitronixTransactionManager btm = startBtm())
        {
            try
            {
                final BitronixTransaction t = new BitronixTransaction(btm, new Context(),
                    getLogger(), new LoggingConfigurator());
                final BitronixDataSource ds = new BitronixDataSource("pg", btm);
                ds.getConnection().close(); // ensure database is available

                Coordination.Participant p1 = new Coordination.Participant()
                    {
                        private Connection c1;

                        private Statement s1;

                        @Override
                        public void setup()
                            throws SQLException
                        {
                            t.begin();

                            c1 = ds.getConnection();
                            s1 = c1.createStatement();
                        }

                        @Override
                        public void step(int num)
                            throws Exception
                        {
                            switch(num)
                            {
                            case 1:
                                if(DatabaseUtils.hasTable(ds, "test"))
                                {
                                    s1.execute("DROP TABLE test");
                                }
                                s1.execute("CREATE TABLE test ( a integer )");
                                s1.execute("INSERT INTO test(a) values(1)");
                                t.commit(true);
                                t.begin();
                                break;
                            case 2:
                                break;
                            case 3:
                                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                                {
                                    assertTrue(rs1.next());
                                    assertEquals(1, rs1.getInt(1));
                                }
                                break;
                            case 4:
                                break;
                            case 5:
                                try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                                {
                                    assertTrue(rs1.next());
                                    assertEquals(2, rs1.getInt(1));
                                }
                                t.commit(true);
                                break;
                            }
                        }

                        @Override
                        public void cleanup()
                            throws SQLException
                        {
                            s1.close();
                            c1.close();
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

                        private Statement s2;

                        @Override
                        public void setup()
                            throws SQLException
                        {
                            t.begin();

                            c2 = ds.getConnection();
                            s2 = c2.createStatement();
                        }

                        @Override
                        public void step(int num)
                            throws Exception
                        {
                            switch(num)
                            {
                            case 1:
                                break;
                            case 2:
                                s2.execute("UPDATE test SET a = 2");
                                try (ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
                                {
                                    assertTrue(rs2.next());
                                    assertEquals(2, rs2.getInt(1));
                                }
                                break;
                            case 3:
                                break;
                            case 4:
                                t.commit(true);
                                break;
                            case 5:
                                break;
                            }
                        }

                        @Override
                        public void cleanup()
                            throws SQLException
                        {
                            s2.close();
                            c2.close();
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
                            System.out
                                .println("before " + participant.toString() + " step " + step);
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
            catch(Exception e)
            {
                getLogger().warn("postgres database localhost/btm not available - skipping test");
                return;
            }
        }
    }
}
