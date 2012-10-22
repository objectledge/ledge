package org.objectledge.btm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;

import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.logging.LoggingConfigurator;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.SAXException;

public class BitronixTransactionManagerTest
    extends LedgeTestCase
{
    public void testConfigSchema()
        throws ParserConfigurationException, SAXException, IOException
    {
        XMLValidator xmlValidator = new XMLValidator(new XMLGrammarCache());
        URL relaxng = getFileSystem().getResource(XMLValidator.RELAXNG_SCHEMA);
        URL rng = getFileSystem().getResource("org/objectledge/btm/BitronixTransactionManager.rng");
        xmlValidator.validate(rng, relaxng);
    }

    public void testWithConfig()
        throws Exception
    {
        ConfigurationFactory cf = new ConfigurationFactory(getFileSystem(), new XMLValidator(
            new XMLGrammarCache()), "/btm");
        LoggingConfigurator lf = new LoggingConfigurator();
        initLog4J("INFO");
        BitronixTransactionManager btm = new BitronixTransactionManager(cf.getConfig("simple",
            BitronixTransactionManager.class));
        try
        {
            BitronixDataSource ds = new BitronixDataSource("hsql", btm);
            BitronixTransaction t = new BitronixTransaction(btm, new Context(), getLogger(), lf);
            t.begin();
            try (Connection c1 = ds.getConnection())
            {
                try (Statement s1 = c1.createStatement())
                {
                    s1.execute("CREATE TABLE test ( a integer )");
                    s1.execute("INSERT INTO test(a) values(1)");
                    t.commit(true);
                    t.begin();
                    try (Connection c2 = ds.getConnection())
                    {
                        assertTrue(c2 != c1);
                        try (Statement s2 = c2.createStatement())
                        {
                            s2.execute("UPDATE test SET a = 2");
                            try (ResultSet rs2 = s2.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs2.next());
                                assertEquals(2, rs2.getInt(1));
                            }
                            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs1.next());
                                assertEquals(1, rs1.getInt(1));
                            }
                            t.commit(true);
                            t.begin();
                            try (ResultSet rs1 = s1.executeQuery("SELECT a FROM test"))
                            {
                                assertTrue(rs1.next());
                                assertEquals(2, rs1.getInt(1));
                            }
                        }
                    }
                }
            }
            t.commit(true);
        }
        finally
        {
            btm.stop();
        }
    }
}
