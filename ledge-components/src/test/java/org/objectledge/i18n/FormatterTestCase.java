package org.objectledge.i18n;

import java.util.Locale;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.ClasspathFileSystemProvider;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.utils.LedgeTestCase;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

/**
 *
 * @author <a href="maito:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @created 2005-08-15 <br>
 * $Id: FormatterTestCase.java,v 1.1 2006-03-07 17:35:07 zwierzem Exp $ <br>
 */
public abstract class FormatterTestCase extends LedgeTestCase
{
    protected Mock i18nMock = mock(I18n.class);
    protected I18n i18n = (I18n)i18nMock.proxy();
    
    protected Locale plLocale;
    protected Locale usLocale;

    public FormatterTestCase()
    {
        super();
        plLocale = new Locale("pl","PL");
        usLocale = new Locale("en","US");
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        FileSystemProvider lfs = new LocalFileSystemProvider("local", "src/test/resources");
        FileSystemProvider cfs = new ClasspathFileSystemProvider("classpath", 
            getClass().getClassLoader());
        FileSystem fs = new FileSystem(new FileSystemProvider[] { lfs, cfs }, 4096, 4096);
        XMLValidator xv = new XMLValidator(new XMLGrammarCache());
        ConfigurationFactory cf = new ConfigurationFactory(fs, xv, "config");
        Class formatterClass = getFormatterClass();
        Configuration config = cf.getConfig(formatterClass.getName(), formatterClass);
        i18nMock.expects(never()).method("getDefaultLocale").will(returnValue(plLocale));
        createFormatter(config, i18n);
    }

    protected abstract Class getFormatterClass();
    
    protected abstract void createFormatter(Configuration config, I18n i18n)
    throws ConfigurationException;
}
