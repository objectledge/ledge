// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import junit.framework.TestCase;

/**
 *
 * <p>Created on Jan 8, 2004</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FileSystemTest.java,v 1.4 2004-01-29 08:32:45 pablo Exp $
 */
public class FileSystemTest extends TestCase
{
    private FileSystem fs;

    /**
     * Constructor for FileSystemTest.
     * @param arg0
     */
    public FileSystemTest(String arg0)
        throws Exception
    {
        super(arg0);
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
             throw new Exception("system property ledge.root undefined. "+
                      "use -Dledge.root=.../ledge-components/src/test/resources");
        }
        fs = FileSystem.getStandardFileSystem(root);
    }

    public void testURL()
        throws Exception
    {
        
        URL url = fs.getResource("/file");
        InputStream is = url.openStream();
        is.read();  
    }
    
    public void testGetProtocol()
        throws Exception
    {
        assertEquals(fs.getProtocol(),"ledge");
    }
    
    public void testGetProviders()
        throws Exception
    {
        assertEquals(fs.getProviders().length,2);
    }
    
    public void testGetProviderString()
        throws Exception
    {
        assertEquals(fs.getProvider("local").getName(),"local");
        try
        {
            assertEquals(fs.getProvider("foo").getName(),"foo");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
    }
    
    public void testGetResource()
            throws Exception
    {
        assertNull(fs.getResource("foo/bar"));
    }


    public void testGetOutputStream()
            throws Exception
    {
        OutputStream os = fs.getOutputStream("file");
        assertNotNull(os);
        os.close();
        
        os = fs.getOutputStream("foo");
        assertNotNull(os);
        os.close();
        assertEquals(fs.exists("foo"),true);
        fs.delete("foo");
        assertEquals(fs.exists("foo"),false);
        os = fs.getOutputStream("file",true);
        assertNotNull(os);
        os.close();
        os = fs.getOutputStream("foo",true);
        assertNotNull(os);
        os.close();
        fs.delete("foo");
        assertEquals(fs.exists("foo"),false);
    }
            
    public void testGetRandomAccess()
       throws Exception
    {
        assertNotNull(fs.getRandomAccess("file","r"));
    }
    
    public void testIsFile()
        throws Exception
    {
        assertEquals(fs.isFile("file"),true);
        assertEquals(fs.isFile("config"),false);
    }
    
    
    public void testIsDirectory()
        throws Exception
    {
        assertEquals(fs.isDirectory("file"),false);
        assertEquals(fs.isDirectory("config"),true);
    }

    public void testCanRead()
        throws Exception
    {
        assertEquals(fs.canRead("file"),true);
        assertEquals(fs.canRead("foo"),false);
    }
    
    public void testCanWrite()
        throws Exception
    {
        assertEquals(fs.canWrite("file"),true);
        assertEquals(fs.canRead("foo"),false);
    }
    
    public void testLastModified()
        throws Exception
    {
        assertEquals(fs.lastModified("file") == -1L,false);
        assertEquals(fs.lastModified("foo"),-1L);
    }
    
    public void testLength()
        throws Exception
    {
        assertEquals(fs.length("file"),0);
        assertEquals(fs.length("foo"),-1L);
    }
        
    public void testList()
        throws Exception
    {
        assertEquals(fs.list("directory").length,5);
        try
        {
            assertEquals(fs.list("directory_that_does_not_exist").length,2);
            fail("should throw the exception");
        }
        catch(IOException e)
        {
          //ok!
        }
        try
        {
            assertEquals(fs.list("file").length,2);
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            //ok!
        }
        //assertEquals(fs.length("foo"),-1L);
    }
    
    public void testCreateNewFile()
        throws Exception
    {
        boolean result = fs.createNewFile("file");
        assertEquals(result,false);
        result = fs.createNewFile("foo");
        assertEquals(result,true);
        fs.delete("foo");
    }

    public void testMkdirs()
        throws IOException
    {
        assertEquals(fs.exists("foo"),false);
        fs.mkdirs("foo");
        assertEquals(fs.exists("foo"),true);
        assertEquals(fs.isDirectory("foo"),true);
        fs.delete("foo");
        try
        {
            fs.mkdirs("file");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
    }
    
    public void testDelete()
        throws IOException
    {
        try
        {
            fs.delete("foo");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
    }

    public void testRename()
        throws IOException
    {
        fs.mkdirs("foo");
        assertEquals(fs.exists("foo"),true);
        assertEquals(fs.exists("bar"),false);
        fs.rename("foo","bar");
        assertEquals(fs.exists("foo"),false);
        assertEquals(fs.exists("bar"),true);
        fs.delete("bar");
        try
        {
            fs.rename("bar","bar2");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
    }
    
    public void testCopyFile()
        throws IOException
    {
        assertEquals(fs.exists("foo"),false);
        fs.copyFile("file","foo");
        assertEquals(fs.exists("foo"),true);
        fs.delete("foo");
        try
        {
            fs.copyFile("foo","bar");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
        try
        {
            fs.copyFile("directory","bar");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }        
    }    
    
    public void testCopyDir()
        throws IOException
    {
        assertEquals(fs.exists("directory"),true);
        int size = fs.list("directory").length;
        fs.copyDir("directory","directory2");
        assertEquals(fs.exists("directory2"),true);
        assertEquals(fs.list("directory2").length,size);
        fs.deleteRecursive("directory2");
        assertEquals(fs.exists("directory2"),false);
        try
        {
            fs.copyDir("foo","bar");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
        try
        {
            fs.copyFile("directory","fail");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }        
    }    
    
    public void testReadStringOuputStream()
        throws Exception
    {
        fs.createNewFile("foo");
        assertEquals(fs.exists("foo"),true);
        fs.write("foo","bar","ISO-8859-2");
        assertEquals(fs.exists("foo"),true);
        assertEquals(fs.read("foo","ISO-8859-2"),"bar");
        assertEquals(fs.read("foo")[0],"bar".getBytes("ISO-8859-2")[0]);
        assertEquals(fs.read("foo")[1],"bar".getBytes("ISO-8859-2")[1]);
        assertEquals(fs.read("foo")[2],"bar".getBytes("ISO-8859-2")[2]);
        //Str
        //fs.read(String path, OutputStream out)    
        fs.delete("foo");
    }
    
    /*
    
    public byte[] read(String path) throws IOException
    public String read(String path, String encoding)
    public void write(String path, InputStream in) throws IOException
    public void write(String path, byte[] bytes) throws IOException
    public void write(String path, String string, String encoding)
        throws IOException
    public static String normalizedPath(String path)
    public static String basePath(String path)
    public static String directoryPath(String path)
    public static String relativePath(String path, String base)
        throws IllegalArgumentException
    public static FileSystem getStandardFileSystem(String root)
    */
}

