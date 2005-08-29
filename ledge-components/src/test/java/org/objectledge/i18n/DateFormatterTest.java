package org.objectledge.i18n;

import java.util.Date;
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
 * $Id: DateFormatterTest.java,v 1.1 2005-08-29 20:26:50 rafal Exp $ <br>
 */
public class DateFormatterTest extends TestCase
{
    private Locale plLocale;
    private Locale usLocale;
    DateFormatter dateFormatter;
    Date date;

    public DateFormatterTest(String name)
    {
        super(name);
        plLocale = new Locale("pl","PL");
        usLocale = new Locale("en","US");
        date = new Date(99, 11, 31, 18, 45);
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
        Configuration config = cf.getConfig("org.objectledge.i18n.DateFormatter",
                DateFormatter.class);
        dateFormatter = new DateFormatter(config, logger);        
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.DateFormatter(Configuration, Logger)'
     */
    public void testDateFormatter()
    {
        assertNotNull(dateFormatter);
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.getDateFormat(String, Locale)'
     */
    public void testGetDateFormatStringLocale()
    {
        assertEquals("31.12.1999, 18:45", dateFormatter.getDateFormat("full", plLocale).format(date));
        assertEquals("6:45:00 PM, 31 December 1999", dateFormatter.getDateFormat("full", usLocale).format(date));
        assertEquals("18:45", dateFormatter.getDateFormat("shorttime", plLocale).format(date));
        assertEquals("6:45 PM", dateFormatter.getDateFormat("shorttime", usLocale).format(date));
        assertEquals("31.12.1999", dateFormatter.getDateFormat("shortdate", plLocale).format(date));
        assertEquals("12/31/1999", dateFormatter.getDateFormat("shortdate", usLocale).format(date));
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.getDateFormat(Locale)'
     */
    public void testGetDateFormatLocale()
    {
        assertEquals("31.12.1999, 18:45", dateFormatter.getDateFormat(plLocale).format(date));
        assertEquals("6:45:00 PM, 31 December 1999", dateFormatter.getDateFormat(usLocale).format(date));
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.getDefaultPattern(Locale)'
     */
    public void testGetDefaultPattern()
    {
        assertEquals("full", dateFormatter.getDefaultPattern(plLocale));
        assertEquals("full", dateFormatter.getDefaultPattern(usLocale));
    }

}
