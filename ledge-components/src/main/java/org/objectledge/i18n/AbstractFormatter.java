package org.objectledge.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.utils.StringUtils;

/**
 * The abstract formater component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractFormatter.java,v 1.1 2006-03-07 17:35:06 zwierzem Exp $
 */
public abstract class AbstractFormatter
{
    /** The i18n is used as default locale provider. */
    protected I18n i18n;
    /** The locale map. */
    protected Map<Locale,Map<String,String>> localeMap;
    /** The default locale patterns. */
    protected Map<Locale,String> defaultPatterns;

    /**
     * Component constructor.
     *
     * @param config the configuration.
     * @param logger the logger.
     * @param i18n used as default locale provider.
     * @throws ConfigurationException if the component configuration is malformed.
     */
    public AbstractFormatter(Configuration config, I18n i18n)
        throws ConfigurationException
    {
        this.i18n = i18n;
        localeMap = new HashMap<Locale,Map<String,String>>();
        defaultPatterns = new HashMap<Locale,String>();
        Configuration[] locales = config.getChildren("locale");
        for (int i = 0; i < locales.length; i++)
        {
            String name = locales[i].getAttribute("name");
            String defaultPattern = locales[i].getAttribute("defaultPattern");
            Configuration[] patterns = locales[i].getChildren("pattern");
            Locale locale = StringUtils.getLocale(name);
            Map<String,String> map = new HashMap<String,String>();
            localeMap.put(locale, map);
            defaultPatterns.put(locale, defaultPattern);
            for (int j = 0; j < patterns.length; j++)
            {
                String patternName = patterns[j].getAttribute("name");
                String patternValue = patterns[j].getAttribute("value", null);
                if (patternValue == null)
                {
                    patternValue = patterns[j].getValue();
                }
                map.put(patternName, patternValue);
            }
        }
    }
    
    /**
     * Get the default pattern for locale. 
     * 
     * @param locale the locale.
     * @return the default pattern name.
     */
    public String getDefaultPattern(Locale locale)
    {
        String pattern = defaultPatterns.get(locale);
        if(pattern == null)
        {
            pattern = defaultPatterns.get(i18n.getDefaultLocale());
        }
        return pattern;
    }

    /**
     * Retrieves the pattern value by it's name and locale.
     * If pattern is not defined than pattern for default locale is retrieved.
     * If pattern is not defined for default locale than <code>null</code> is returned.
     * 
     * @param patternName the requested pattern name.
     * @param locale the locale.
     * @return value of the pattern.
     */
    protected String getPatternValue(String patternName, Locale locale)
    {
        Map<String,String> patterns = localeMap.get(locale);
        if(patterns == null)
        {
            patterns = localeMap.get(i18n.getDefaultLocale());
        }
        if(patterns == null)
        {
            return null;
        }
        return patterns.get(patternName);
    }

}
