package org.objectledge.i18n;

import java.util.Calendar;
import java.util.Date;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 *
 * @author <a href="maito:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @created 2005-08-15 <br>
 * $Id: DateFormatterTest.java,v 1.2 2006-03-07 17:35:07 zwierzem Exp $ <br>
 */
public class DateFormatterTest extends FormatterTestCase
{
    private DateFormatter dateFormatter;
    private Date date;

    public DateFormatterTest()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(1999, 11, 31, 18, 45, 0);
        date = cal.getTime();
    }

    @Override
    protected Class<?> getFormatterClass()
    {
        return DateFormatter.class;
    }

    @Override
    protected void createFormatter(Configuration config, I18n i18n)
        throws ConfigurationException
    {
        dateFormatter = new DateFormatter(config, i18n);        
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.DateFormatter(Configuration, I18n)'
     */
    public final void testDateFormatter()
    {
        assertNotNull(dateFormatter);
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.getDateFormat(String, Locale)'
     */
    public final void testGetDateFormatStringLocale()
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
    public final void testGetDateFormatLocale()
    {
        assertEquals("31.12.1999, 18:45", dateFormatter.getDateFormat(plLocale).format(date));
        assertEquals("6:45:00 PM, 31 December 1999", dateFormatter.getDateFormat(usLocale).format(date));
    }

    /*
     * Test method for 'org.objectledge.i18n.DateFormatter.getDefaultPattern(Locale)'
     */
    public final void testGetDefaultPattern()
    {
        assertEquals("full", dateFormatter.getDefaultPattern(plLocale));
        assertEquals("full", dateFormatter.getDefaultPattern(usLocale));
    }
}
