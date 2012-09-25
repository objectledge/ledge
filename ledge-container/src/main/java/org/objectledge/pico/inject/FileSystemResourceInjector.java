package org.objectledge.pico.inject;

import java.io.IOException;
import java.lang.reflect.Field;

import org.objectledge.filesystem.FileSystem;

public class FileSystemResourceInjector
{
    private final FileSystem fileSystem;

    private final String[] suffices;

    public FileSystemResourceInjector(FileSystem fileSystem, String... suffices)
    {
        this.fileSystem = fileSystem;
        this.suffices = suffices;
    }

    public void inject(Object obj)
    {
        inject(fileSystem, obj, suffices);
    }

    /**
     * Inject object fields from the files present in the file system.
     * <p>
     * Supported field types are String (UTF-8 encoding is assumed) and byte[]
     * </p>
     * <p>
     * The files need to reside in a directory that correspond to the object's class package. The
     * name of the loaded file needs to conform to the following pattern:
     * <p>
     * <p>
     * <em>class name</em><strong>$</strong><em>field name</em><em>suffix</em>
     * </p>
     * <p>
     * Suffices are tried sequentially in the specified order. Empty suffix is always tried last.
     * </p>
     * <p>
     * Fields for which no matching resource is found are silently ignored.
     * </p>
     * 
     * @param fs the file system
     * @param obj object to inject to
     * @param suffices a list of file name suffices, in order of decreasing preference. Empty suffix
     *        is always implicitly included as the least preferred options.
     */
    public static void inject(FileSystem fs, Object obj, String... suffices)
    {
        try
        {
            Class<?> cl = obj.getClass();
            for(Field f : cl.getDeclaredFields())
            {
                final String dir = cl.getPackage().getName().replace('.', '/');
                final String clName = cl.getName().substring(cl.getName().lastIndexOf('.') + 1);
                final String resPrefix = clName + "$" + f.getName();
                String res = findResource(fs, dir, resPrefix, suffices);
                if(res != null)
                {
                    Object value = null;
                    if(f.getType().equals(String.class))
                    {
                        value = fs.read(dir + "/" + res, "UTF-8");
                    }
                    if(f.getType().isArray() && f.getType().getComponentType().equals(Byte.TYPE))
                    {
                        value = fs.read(dir + "/" + res);
                    }
                    if(value != null)
                    {
                        f.setAccessible(true);
                        f.set(obj, value);
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to load scripts", e);
        }
    }

    private static String findResource(FileSystem fs, String dir, String name, String... suffices)
        throws IOException
    {
        for(String f : fs.list(dir))
        {
            for(String suffix : suffices)
            {
                if(f.equals(name + suffix))
                {
                    return f;
                }
            }
            if(f.equals(name))
            {
                return f;
            }
        }
        return null;
    }
}
