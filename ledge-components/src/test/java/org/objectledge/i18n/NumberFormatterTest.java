package org.objectledge.i18n;

import java.util.Locale;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.mail.MailSystem;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

import junit.framework.TestCase;

/**
 *
 * @author <a href="maito:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @created 2005-08-15 <br>
 * $Id: NumberFormatterTest.java,v 1.1 2005-08-29 20:28:14 rafal Exp $ <br>
 */
public class NumberFormatterTest extends TestCase
{
    private NumberFormatter numberFormatter;
    private Locale plLocale;
    private Locale usLocale;
    private final static double value = 1234567890.987654d;
    

    public NumberFormatterTest(String name)
    {
        super(name);
        plLocale = new Locale("pl","PL");
        usLocale = new Locale("en","US");
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(MailSystem.class));
        
        FileSystemProvider lfs = new LocalFileSystemProvider("local", "src/test/resources");
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        XMLValidator xv = new XMLValidator(new XMLGrammarCache());
        ConfigurationFactory cf = new ConfigurationFactory(fs, xv, "config");
        Configuration config = cf.getConfig("org.objectledge.i18n.NumberFormatter",
                NumberFormatter.class);
        numberFormatter = new NumberFormatter(config, logger);
    }


    /*
     * Test method for 'org.objectledge.i18n.NumberFormatter.NumberFormatter(Configuration, Logger)'
     */
    public final void testNumberFormatter()
    {
        assertNotNull(numberFormatter);
    }

    /*
     * Test method for 'org.objectledge.i18n.NumberFormatter.getNumberFormat(String, Locale)'
     */
    public final void testGetNumberFormatStringLocale()
    {
        
        assertEquals("1 234 567 890,99", numberFormatter.getNumberFormat("money", plLocale).format(value));
        assertEquals("1234567891,0", numberFormatter.getNumberFormat("precision1", plLocale).format(value));
        assertEquals("1234567890,99", numberFormatter.getNumberFormat("precision2", plLocale).format(value));
        assertEquals("1234567890,9876540000000000000000000000000", numberFormatter.getNumberFormat("full", plLocale).format(value));

        assertEquals("1,234,567,890.99", numberFormatter.getNumberFormat("money", usLocale).format(value));
        assertEquals("1234567891.0", numberFormatter.getNumberFormat("precision1", usLocale).format(value));
        assertEquals("1234567890.99", numberFormatter.getNumberFormat("precision2", usLocale).format(value));
        assertEquals("1234567890.9876540000000000", numberFormatter.getNumberFormat("full", usLocale).format(value));

    }

    /*
     * Test method for 'org.objectledge.i18n.NumberFormatter.getNumberFormat(Locale)'
     */
    public final void testGetNumberFormatLocale()
    {
        assertEquals("1234567890,9876540000000000000000000000000", numberFormatter.getNumberFormat(plLocale).format(value));
        assertEquals("1234567890.9876540000000000", numberFormatter.getNumberFormat(usLocale).format(value));

    }

    /*
     * Test method for 'org.objectledge.i18n.NumberFormatter.getDefaultPattern(Locale)'
     */
    public final void testGetDefaultPattern()
    {
        assertEquals("full", numberFormatter.getDefaultPattern(plLocale));
        assertEquals("full", numberFormatter.getDefaultPattern(usLocale));
    }

}
