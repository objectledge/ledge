package pl.caltha.internal.xml;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** EntityResolver for Labeo applications.
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CalthaEntityResolver.java,v 1.2 2005-01-21 14:00:05 pablo Exp $
 */
public class CalthaEntityResolver 
    implements EntityResolver
{
    private FileSystem fileSystem;
    private Logger log;
    //CatalogResolver catalogResolver;
    
    /** Creates a new instance of LabeoEntityResolver */
    public CalthaEntityResolver(XMLServiceImpl xmlService, FileSystem fileSystem, Logger logger)
    {
        this.fileSystem = fileSystem;
        this.log = logger;
        /*
        // use Sun's "XML Entity and URI Resolvers" by Norman Walsh
        // to resolve external entities.
        // http://www.sun.com/xml/developers/resolver/
        catalogResolver = new CatalogResolver(true);
        catalogResolver.getCatalog().parseCatalog(catalogURL);
         */
    }
    
    /** This is an implementation of EntityResolver's only method.
     *
     * <p><i>WARNING!</i>
     * <br />
     * In this version it resolves only SYSTEMIDs. PUBLICIDs are not
     * used.</p>
     *
     * <p><b>Supported URI schemes:</b>
     * <ul>
     * <li><code>labeo:</code> - scheme used to access resources via
     * Labeo <code>FileService</code></li>
     * <li><code>classpath:</code> - scheme used to access resources via
     * <code>ClassLoader</code>'s <code>getResourceAsStream(String)</code></li>
     * <li>Schemes supported by java.net.URL (except from <code>file:</code> scheme)
     * - schemes used to retrieve network resources available for instance through HTTP.</li>
     * </ul>
     * </p>
     *
     * <p><b>Unsupported URI schemes:</b>
     * <br />
     * Because of application portability issues, URIs with <code>file:</code>
     * scheme are not supported, and are considered harmful (SecurityException
     * is thrown). Labeo aplications should use <code>labeo:</code> scheme instead.
     * </p>
     *
     */
    public InputSource resolveEntity(String publicId, String systemId)
    throws SAXException, IOException
    {
        log.debug("Resolving entity PUBLIC '"+publicId+"' SYSTEM '"+systemId+"'");

        if(systemId == null)
        {
            throw new RuntimeException("In this version cannot use PUBLICID to resolve entities.");
        }
        
        // build an URI
        URI uri;
        try
        {
            uri = new URI(systemId);
        }
        catch(Exception e)
        {
            throw new SAXException("SYSTEMID '"+systemId+"' is not a valid URI", e);
        }
        
        InputSource is;
        String scheme = uri.getScheme();
        // try it is as labeo:// resource
        // TODO: Make it network and distributed labeo aware
        if(scheme.equals("labeo"))
        {
            //TODO should be get path...
            String path = uri.getSchemeSpecificPart();
            is = new InputSource(fileSystem.getInputStream(path));
        }
        else if(scheme.equals("file"))
        {
            throw new SecurityException("Direct file system access not allowed, use FileService");
        }
        else if(scheme.equals("classpath"))
        {
            //TODO should be get path...
            String path = uri.getSchemeSpecificPart();
            is = new InputSource(getClass().getClassLoader().getResourceAsStream(path));
        }
        else
        // try it as a URL
        {
            URL url;
            try
            {
                url = new URL(systemId);
                is = new InputSource(url.openStream());
            }
            catch(java.net.MalformedURLException e)
            {
                // not a valid url
                throw new SAXException("SYSTEMID '"+systemId+"' is not a valid URL", e);
            }
            catch(IOException e)
            {
                // cannot reach a resource
                throw new SAXException("Cannot access a resource for SYSTEMID '"+systemId+"'", e);
            }
        }
        
        String newURI = uri.toString(); 
        log.debug("Entity URI '"+newURI+"' and InputStream '"+is.getByteStream()+"'");
        // This is important
        is.setSystemId(newURI);
        return is;
    }
    //private String expandSystemId(String systemId, String currentSystemId)
    //private static String fixURI(String str)
}
