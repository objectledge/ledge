package org.objectledge.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.impl.ReadOnlyFileSystemProvider;

public class ClasspathFileSystemProvider
    extends ReadOnlyFileSystemProvider
{
    private final ClassLoader classLoader;

    public ClasspathFileSystemProvider(String name, ClassLoader classLoader)
    {
        super(name);
        this.classLoader = classLoader;

        try
        {
            long t = System.currentTimeMillis();
            ClassLoader cl = getClass().getClassLoader();
            while(cl != null)
            {
                if(cl instanceof URLClassLoader)
                {
                    analyzeClassPath(((URLClassLoader)cl).getURLs());
                }
                cl = cl.getParent();
            }
            t = System.currentTimeMillis() - t;
            // System.out.println("listing done in " + t + "ms");
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError("classpath analysis failed", e);
        }
    }

    private void analyzeClassPath(URL[] urls)
        throws IOException, URISyntaxException
    {
        for(URL url : urls)
        {
        	URI uri = url.toURI();
            if(uri.getScheme().equals("file") && uri.getPath().endsWith(".jar"))
            {
                // System.out.println("analyze jar "+url);
                analyzeJar(uri);
            }
            else if(uri.getScheme().equals("file") && uri.getPath().endsWith("/"))
            {
                // System.out.println("analyze directory "+url);
                File dir = new File(uri.getPath());
                analyzeDirectory(dir, dir);
            }
            else
            {
                // System.out.println("not parsing " + url);
            }
        }
    }

    private void analyzeJar(URI uri)
        throws IOException, URISyntaxException
    {
        File jarFile = new File(uri.getPath());
        if(!jarFile.exists())
        {
            // omit invalid classpath entries
            return;
        }
        JarFile jar = new JarFile(uri.getPath());
        Manifest manifest = jar.getManifest();
        if(manifest != null
            && "Java Runtime Environment".equals(manifest.getMainAttributes().getValue(
                "Implementation-Title")))
        {
            // don't analyze JRE jars
            return;
        }
        Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements())
        {
            JarEntry entry = entries.nextElement();
            if(entry.isDirectory())
            {
                addDirectoryEntry(entry.getName());
            }
            else
            {
                addFileEntry(entry.getName(), entry.getSize(), entry.getTime());
            }
        }
        // running surefire tests in a forked JVM (default mode) requires parsing Class-Path
        // manifest attribute of /tmp/surefireBooter<random>.jar
        if(manifest != null && manifest.getMainAttributes().containsKey(Attributes.Name.CLASS_PATH))
        {
            String classPathAttribute = manifest.getMainAttributes().getValue(
                Attributes.Name.CLASS_PATH);
            // System.out.println("classpath attribute " + classPathAttribute);
            String classPathElements[] = classPathAttribute.split("\\s+");
            List<URL> classPath = new ArrayList<URL>();
            for(String classPathElement : classPathElements)
            {
                if(classPathElement.startsWith("file:"))
                {
                    classPath.add(new URL(classPathElement));
                }
            }
            analyzeClassPath(classPath.toArray(new URL[classPath.size()]));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(String path)
    {
        return classLoader.getResourceAsStream(normalizedPath(path));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(String path)
    {
        return classLoader.getResource(normalizedPath(path));
    }

    //

    /**
     * {@inheritDoc}
     */
    private String normalizedPath(String path)
    {
        // strip leading slash
        return FileSystem.normalizedPath(path).substring(1);
    }
}
