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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import junit.framework.TestCase;

/**
 *
 * <p>Created on Jan 8, 2004</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: FileSystemTest.java,v 1.8 2004-09-24 11:25:37 zwierzem Exp $
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
        fs = FileSystem.getStandardFileSystem("src/test/resources");
    }

    public void setUp()
        throws Exception
    {
        fs.createNewFile("filex");
    }

    public void tearDown()
        throws Exception
    {
        fs.delete("filex");
    }

    public void testURL()
        throws Exception
    {
        
        URL url = fs.getResource("/filex");
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
        OutputStream os = fs.getOutputStream("filex");
        assertNotNull(os);
        os.close();
        
        os = fs.getOutputStream("foo");
        assertNotNull(os);
        os.close();
        assertEquals(fs.exists("foo"),true);
        fs.delete("foo");
        assertEquals(fs.exists("foo"),false);
        os = fs.getOutputStream("filex",true);
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
        RandomAccessFile file = fs.getRandomAccess("filex","rw");
        assertNotNull(file);
        file.write("abcd".getBytes(), 0, 4);
        byte[] buff = new byte[4];
        
        
        long ptr = file.getFilePointer();
        assertTrue(4L == ptr);
        file.seek(0);
        ptr = file.getFilePointer();
        assertTrue(0L == ptr);
        assertTrue(4L == file.length());
        int r = file.read();
        ptr = file.getFilePointer();
        assertTrue(1L == ptr);
        int rb = file.read(buff);
        ptr = file.getFilePointer();
        assertTrue(4L == ptr);
        assertEquals(3, rb);
        file.seek(0);
        int roff = file.read(buff, 0, 4);
        assertEquals(4, roff);
        file.seek(0);
        file.skipBytes(2);
        ptr = file.getFilePointer();
        assertTrue(2L == ptr);
        file.write("efg".getBytes());
        file.write('h');
        assertTrue(6L == file.length());
        file.setLength(4);
        assertTrue(4L == file.length());
        file.close();
    }
    
    public void testIsFile()
        throws Exception
    {
        assertEquals(fs.isFile("filex"),true);
        assertEquals(fs.isFile("config"),false);
    }
    
    
    public void testIsDirectory()
        throws Exception
    {
        assertEquals(fs.isDirectory("filex"),false);
        assertEquals(fs.isDirectory("config"),true);
    }

    public void testCanRead()
        throws Exception
    {
        assertEquals(fs.canRead("filex"),true);
        assertEquals(fs.canRead("foo"),false);
    }
    
    public void testCanWrite()
        throws Exception
    {
        assertEquals(fs.canWrite("filex"),true);
        assertEquals(fs.canRead("foo"),false);
    }
    
    public void testLastModified()
        throws Exception
    {
        assertEquals(fs.lastModified("filex") == -1L,false);
        assertEquals(fs.lastModified("foo"),-1L);
    }
    
    public void testLength()
        throws Exception
    {
        assertEquals(fs.length("filex"),0);
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
            assertEquals(fs.list("filex").length,2);
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
        boolean result = fs.createNewFile("filex");
        assertEquals(result,false);
        result = fs.createNewFile("foo");
        assertEquals(result,true);
        fs.delete("foo");
    }

    public void testMkdirs()
        throws IOException
    {
        assertEquals(fs.exists("foo"),false);
        try
        {
            fs.mkdirs("foo");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("foo"),true);
        assertEquals(fs.isDirectory("foo"),true);
        fs.delete("foo");
        try
        {
            fs.mkdirs("filex");
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
        try
        {
            fs.mkdirs("foo");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("foo"),true);
        assertEquals(fs.exists("bar"),false);
        try
        {
            fs.rename("foo","bar");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
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
        try
        {
            fs.copyFile("filex","foo");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
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
        try
        {
            fs.copyDir("directory","directory2");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
        fs.read("foo", baos);
        assertEquals(baos.toString("ISO-8859-2"),"bar");
        
        fs.createNewFile("foo");
        fs.write("foo","bar".getBytes("ISO-8859-2"));
        assertEquals(fs.read("foo","ISO-8859-2"),"bar");
            
        fs.createNewFile("foo");
        fs.write("foo",new ByteArrayInputStream("bar".getBytes("ISO-8859-2")));
        assertEquals(fs.read("foo","ISO-8859-2"),"bar");

        fs.delete("foo");
    }
    
    public void testPaths()
        throws Exception
    {
        assertEquals(FileSystem.normalizedPath("foo/bar"),"/foo/bar");
        assertEquals(FileSystem.normalizedPath("foo//bar"),"/foo/bar");
        assertEquals(FileSystem.normalizedPath("foo/../bar"),"/bar");
        assertEquals(FileSystem.normalizedPath("foo/./bar"),"/foo/bar");
        assertEquals(FileSystem.normalizedPath("/"),"/");
        
        assertEquals(FileSystem.basePath("/foo/bar"),"bar");
        assertEquals(FileSystem.basePath("foo"),"foo");
        assertEquals(FileSystem.basePath("/"),"");
        
        assertEquals(FileSystem.directoryPath("/"),"");
        assertEquals(FileSystem.directoryPath("foo"),"");
        assertEquals(FileSystem.directoryPath("foo/bar"),"/foo");
    
        assertEquals(FileSystem.relativePath("/foo/bar","/foo"),"/bar");
        try
        {
            assertEquals(FileSystem.relativePath("/foo","/foo/bar"),"/bar");
            fail("should throw the exception");
        }
        catch(IllegalArgumentException e)
        {
            //ok!
        }
        
    }
    
    
}

