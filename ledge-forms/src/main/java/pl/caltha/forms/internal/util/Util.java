package pl.caltha.forms.internal.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;

/** Utility class for form-tool.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Util.java,v 1.3 2005-02-08 20:33:12 rafal Exp $
 */
public class Util
{
    //------------------------------------------------------------------------
    // Utility methods

    public static void insertMultipleIntoHash(Object key, Object value, HashMap hashMap)
    {
        if(key == null)
        {
            return;
        }

        ArrayList list = (ArrayList)(hashMap.get(key));
        if(list == null)
        {
            list = new ArrayList(2);
        }
        // we dont have to insert the same object again
        if(!list.contains(value))
        {
            list.add(value);
        }
        hashMap.put(key, list);
    }

    //------------------------------------------------------------------------
    // construction utility methods

    public static boolean createBooleanAttribute(Attributes atts, String name, boolean defVal)
    {
        if(Util.getSAXAttributeVal(atts, name) != null)
        {
            return createBooleanAttribute(atts, name);
        }
        else
        {
            return defVal;
        }
    }

    public static boolean createBooleanAttribute(Attributes atts, String name)
    {
        return Boolean.valueOf(Util.getSAXAttributeVal(atts, name)).booleanValue();
    }

    public static double createDoubleAttribute(Attributes atts, String name, double defVal)
    throws ConstructionException
    {
        if(Util.getSAXAttributeVal(atts, name) != null)
        {
            return createDoubleAttribute(atts, name);
        }
        else
        {
            return defVal;
        }
    }

    public static double createDoubleAttribute(Attributes atts, String name)
    throws ConstructionException
    {
        try
        {
            return Double.parseDouble(Util.getSAXAttributeVal(atts, name));
        }
        catch (NumberFormatException nfe)
        {
            throw new ConstructionException("Invalid number attribute '"+name+"'", nfe);
        }
    }

    public static int createIntAttribute(Attributes atts, String name, int defVal)
    throws ConstructionException
    {
        if(Util.getSAXAttributeVal(atts, name) != null)
        {
            return createIntAttribute(atts, name);
        }
        else
        {
            return defVal;
        }
    }

    public static int createIntAttribute(Attributes atts, String name)
    throws ConstructionException
    {
        try
        {
            return Integer.parseInt(Util.getSAXAttributeVal(atts, name));
        }
        catch (NumberFormatException nfe)
        {
            throw new ConstructionException("Invalid number attribute '"+name+"'", nfe);
        }
    }

    /** Retrieves a value from SAX Attributes object.
     * @param atts Attributes from element parsing, provided by SAX parser.
     * @param name Name of the attribute to be extracted.
     * @return Value of the attribute or null if attribute was not found.
     */
    public static String getSAXAttributeVal(Attributes atts, String name)
    {
        return getSAXAttributeVal(atts, null, name);
    }

    /** Retrieves a value from SAX Attributes object.
     * @return Value of the attribute or null if attribute was not found.
     * @param nameSpace XML namespace URI to which this attribute belongs.
     * @param atts Attributes from element parsing, provided by SAX parser.
     * @param name Name of the attribute to be extracted.
     */
    public static String getSAXAttributeVal(Attributes atts, String nameSpace, String name)
    {
        int i = -1;
        if(nameSpace == null)
        {
            i = atts.getIndex(name);
        }
        else
        {
            i = atts.getIndex(nameSpace, name);
        }
        if (i >= 0)
        {
            return atts.getValue(i);
        }
        return null;
    }

    /** Returns expanded string version of an URI.
     * First checks if a <code>resourceURI</code> if its already expanded,
     * if not treats it as an URI relative to a <code>baseURI</code>.
     * @param baseURI a base URI
     * @param resourceURI an URI of a resoruce.
     * @return Expanded URI or an empty string if resoruceURI was empty.
     */
    public static String expandURI(String baseURI, String resourceURI)
    {
        // check for bad parameters resourceURI
        if (resourceURI == null || resourceURI.length() == 0)
        {
            return "";
        }

        // if resourceURI already expanded, return
        try
        {
            new URI(resourceURI);
            return resourceURI;
        }
        catch (Exception e) {
            // continue on...
        }

        // normalize resourceURI -- do we need it?
        resourceURI = fixURI(resourceURI);

        // normalize base and expand resourceURI
        URI base = null;
        URI uri = null;
        try
        {
            base = new URI(baseURI);
            // adapt to URI changes...
            //TODO see if its ok! 
            //uri = new URI(base, resourceURI);
            uri = base.resolve(resourceURI);
        }
        catch (Exception e)
        {
            // let it go through
        }

        if (uri == null)
        {
            return resourceURI;
        }

        return uri.toString();
    }

    private static String fixURI(String str)
    {
        // handle platform dependent strings
        str = str.replace(java.io.File.separatorChar, '/');

        // Windows fix
        if (str.length() >= 2)
        {
            char ch1 = str.charAt(1);
            if (ch1 == ':')
            {
                char ch0 = Character.toUpperCase(str.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z')
                {
                    str = "/" + str;
                }
            }
        }
        // done
        return str;
    }

    public static String getWorkingPath(String parentPath, String resourcePath)
    {
        File resourceFile = new File(resourcePath);
        if(resourceFile.exists())
        {
            return resourceFile.getAbsolutePath();
        }

        File parentFile = new File(parentPath);
        if(!parentFile.isDirectory())
        {
            if(parentFile.getParentFile() != null)
            {
                parentFile = parentFile.getParentFile();
            }
        }

        resourceFile = new File(parentFile.getPath(), resourcePath);
        if(resourceFile.exists())
        {
            return resourceFile.getAbsolutePath();
        }

        return null;
    }

    public static URL getWorkingURL(URL parentURL, String resourceURL)
    {
        try
        {
            return new URL(resourceURL);
        }
        catch(MalformedURLException e)
        {
            // this is not a absolute url
        }

        try
        {
            return new URL(parentURL, resourceURL);
        }
        catch(MalformedURLException e)
        {
            // this is not a part of an URL
        }
        return null;
    }
}
