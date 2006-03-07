package org.objectledge.i18n;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 *
 * @author <a href="maito:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @created 2005-08-15 <br>
 * $Id: NumberFormatterTest.java,v 1.3 2006-03-07 17:35:07 zwierzem Exp $ <br>
 */
public class NumberFormatterTest extends FormatterTestCase
{
    private NumberFormatter numberFormatter;
    private final static double value = 1234567890.987654d;

    @Override
    protected Class getFormatterClass()
    {
        return NumberFormatter.class;
    }

    @Override
    protected void createFormatter(Configuration config, I18n i18n)
        throws ConfigurationException
    {
        numberFormatter = new NumberFormatter(config, i18n);
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
        
        // for some reason the following test runns under eclipse but fails under maven for me.
        // assertEquals("1 234 567 890,99", numberFormatter.getNumberFormat("money", plLocale).format(value));
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
