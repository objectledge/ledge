package pl.caltha.internal.xml;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Localizer class for XMLService.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Localizer.java,v 1.1 2005-01-20 16:44:47 pablo Exp $
 */
public class Localizer
{
    private static HashMap bundles;
    
    public static void init()
    {
        addBundle("net.labeo.internal.xml.Messages");
    }
    
    /** Adds a new bundle to this localizer. */
    public static synchronized void addBundle(String baseName)
    {
        if(bundles == null)
        {
            bundles = new HashMap();
        }

        if(bundles.containsKey(baseName))
        {
            return;
        }
        
        try
        {
            ResourceBundle newBundle = ResourceBundle.getBundle(baseName);
            bundles.put(baseName, newBundle);
        }
        catch(MissingResourceException e)
        {
            // bum.....
        }
    }

    /** Localizes a message with a given property name and arguments. */
    public static String localize(String propertyName, Object[] args)
    {
        if(bundles == null)
        {
            init();
        }
        
        for(java.util.Iterator iter = bundles.values().iterator(); iter.hasNext();)
        {
            ResourceBundle bundle = (ResourceBundle)(iter.next());
            try
            {
                String format = bundle.getString(propertyName);
                return java.text.MessageFormat.format(format, args);
            }
            catch(MissingResourceException e)
            {
                // ignore
            }
        }
        throw new MissingResourceException("Cannot find a localization message",
                        propertyName.getClass().getName(), propertyName);
    }

    /** Localizes a message with a given property name. */
    public static String localize(String prop)
    {
        return localize(prop, null);
    }

    /** Localizes a message with a given property name and argument. */
    public static String localize(String prop, Object arg1)
    {
        return localize(prop,new Object[]{arg1});
    }

    /** Localizes a message with a given property name and arguments. */
    public static String localize(String prop, Object arg1, Object arg2)
    {
        return localize(prop,new Object[]{arg1,arg2});
    }
}
