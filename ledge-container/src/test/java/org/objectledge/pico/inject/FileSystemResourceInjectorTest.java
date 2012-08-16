package org.objectledge.pico.inject;

import java.util.Arrays;

import junit.framework.TestCase;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.pico.inject.FileSystemResourceInjector;

public class FileSystemResourceInjectorTest
    extends TestCase
{
    private String one;

    private byte[] two;

    public void testLoader()
        throws Exception
    {
        FileSystemResourceInjector injector = new FileSystemResourceInjector(
            FileSystem.getClasspathFileSystem(), ".sql", ".bin");
        injector.inject(this);
        assertEquals("SELECT 1 = 1\n", one);
        assertTrue(Arrays.equals(new byte[] { 32, 32, 32, 32, 32 }, two));
    }
}
