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
 * @version $Id: FileSystemTest.java,v 1.12 2008-01-29 20:09:43 rafal Exp $
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
        fs.createNewFile("file_1");
        fs.mkdirs("directory_1");
        // ensure those are gone even if previous test fails
        if(fs.exists("new_file_1"))
        {
            fs.delete("new_file_1");
        }
        if(fs.exists("new_file_1"))
        {
            fs.delete("new_file_1");
        }
        if(fs.exists("new_directory_1"))
        {
            fs.deleteRecursive("new_directory_1");
        }
        if(fs.exists("new_directory_2"))
        {
            fs.deleteRecursive("new_directory_2");
        }
    }

    public void tearDown()
        throws Exception
    {
        fs.delete("file_1");
        fs.deleteRecursive("directory_1");
        if(fs.exists("new_file_1"))
        {
            fs.delete("new_file_1");
        }
        if(fs.exists("new_file_1"))
        {
            fs.delete("new_file_1");
        }
        if(fs.exists("new_directory_1"))
        {
            fs.deleteRecursive("new_directory_1");
        }
        if(fs.exists("new_directory_2"))
        {
            fs.deleteRecursive("new_directory_2");
        }
    }

    public void testURL()
        throws Exception
    {
        
        URL url = fs.getResource("/file_1");
        InputStream is = url.openStream();
        is.read();  
        is.close();
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
            assertEquals(fs.getProvider("nonexistent_provider").getName(),"nonexistent_provider");
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
        assertNull(fs.getResource("nonexistent_file"));
    }


    public void testGetOutputStream()
            throws Exception
    {
        OutputStream os = fs.getOutputStream("new_file_1");
        assertNotNull(os);
        os.close();
        
        os = fs.getOutputStream("new_file_2");
        assertNotNull(os);
        os.close();

        assertEquals(fs.exists("new_file_2"),true);
        fs.delete("new_file_2");
        assertEquals(fs.exists("new_file_2"),false);
        os = fs.getOutputStream("new_file_1",true);
        assertNotNull(os);
        os.close();
        
        os = fs.getOutputStream("new_file_2",true);
        assertNotNull(os);
        os.close();
        
        fs.delete("new_file_2");
        assertEquals(fs.exists("new_file_2"),false);
    }
            
    public void testGetRandomAccess()
       throws Exception
    {
        RandomAccessFile file = fs.getRandomAccess("file_1","rw");
        assertNotNull(file);
        file.write("abcd".getBytes(), 0, 4);
        byte[] buff = new byte[4];
        
        
        long ptr = file.getFilePointer();
        assertTrue(4L == ptr);
        file.seek(0);
        ptr = file.getFilePointer();
        assertTrue(0L == ptr);
        assertTrue(4L == file.length());
        file.read();
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
        assertEquals(fs.isFile("file_1"),true);
        assertEquals(fs.isFile("directory_1"),false);
    }
    
    
    public void testIsDirectory()
        throws Exception
    {
        assertEquals(fs.isDirectory("file_1"),false);
        assertEquals(fs.isDirectory("directory_1"),true);
    }

    public void testCanRead()
        throws Exception
    {
        assertEquals(fs.canRead("file_1"),true);
        assertEquals(fs.canRead("nonexistent_file"),false);
    }
    
    public void testCanWrite()
        throws Exception
    {
        assertEquals(fs.canWrite("file_1"),true);
        assertEquals(fs.canRead("nonexistent_file"),false);
    }
    
    public void testLastModified()
        throws Exception
    {
        assertEquals(fs.lastModified("file_1") == -1L,false);
        assertEquals(fs.lastModified("nonexistent_file"),-1L);
    }
    
    public void testLength()
        throws Exception
    {
        assertEquals(fs.length("file_1"),0);
        assertEquals(fs.length("nonexistent_file"),-1L);
    }
        
    public void testList()
        throws Exception
    {
        assertEquals(fs.list("directory").length,5);
        try
        {
            fs.list("nonexistent_directory");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
          //ok!
        }
        try
        {
            fs.list("file_1");
            fail("should throw the exception");
        }
        catch(IOException e)
        {
            //ok!
        }
    }
    
    public void testCreateNewFile()
        throws Exception
    {
        boolean result = fs.createNewFile("file_1");
        assertEquals(result,false);
        result = fs.createNewFile("new_file_1");
        assertEquals(result,true);
        fs.delete("new_file_1");
    }

    public void testMkdirs()
        throws IOException
    {
        assertEquals(fs.exists("new_directory_1"),false);
        try
        {
            fs.mkdirs("new_directory_1");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("new_directory_1"),true);
        assertEquals(fs.isDirectory("new_directory_1"),true);
        fs.delete("new_directory_1");
        try
        {
            fs.mkdirs("file_1");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
    }
    
    public void testDelete()
    {
        try
        {
            fs.delete("nonexistent_file");
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
            fs.mkdirs("new_directory_1");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("new_directory_1"),true);
        assertEquals(fs.exists("new_directory_2"),false);
        try
        {
            fs.rename("new_directory_1","new_directory_2");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("new_directory_1"),false);
        assertEquals(fs.exists("new_directory_2"),true);
        fs.delete("new_directory_2");
        try
        {
            fs.rename("nonexistent_file", "nonexistent_file_2");
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
        assertEquals(fs.exists("new_file_1"),false);
        try
        {
            fs.copyFile("file_1","new_file_1");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("new_file_1"),true);
        fs.delete("new_file_1");
        try
        {
            fs.copyFile("new_file_1","new_file_2");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
        try
        {
            fs.copyFile("directory_1", "new_directory_1");
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
            fs.copyDir("directory","new_directory_1");
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            fail("simple ascii characters should be supported: "+e.getMessage());
        }
        assertEquals(fs.exists("new_directory_1"),true);
        assertEquals(fs.list("new_directory_1").length,size);
        fs.deleteRecursive("new_directory_1");
        assertEquals(fs.exists("new_directory_1"),false);
        try
        {
            fs.copyDir("nonexistent_directory","new_directory_1");
            fail("should throw the exception");
        }
        catch(Exception e)
        {
            //ok!
        }
        try
        {
            fs.copyFile("directory","new_directory_1");
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
        fs.createNewFile("new_file_1");
        assertEquals(fs.exists("new_file_1"),true);
        fs.write("new_file_1","bar","ISO-8859-2");
        assertEquals(fs.exists("new_file_1"),true);
        assertEquals(fs.read("new_file_1","ISO-8859-2"),"bar");
        assertEquals(fs.read("new_file_1")[0],"bar".getBytes("ISO-8859-2")[0]);
        assertEquals(fs.read("new_file_1")[1],"bar".getBytes("ISO-8859-2")[1]);
        assertEquals(fs.read("new_file_1")[2],"bar".getBytes("ISO-8859-2")[2]);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
        fs.read("new_file_1", baos);
        assertEquals(baos.toString("ISO-8859-2"),"bar");
        
        fs.createNewFile("new_file_1");
        fs.write("new_file_1","bar".getBytes("ISO-8859-2"));
        assertEquals(fs.read("new_file_1","ISO-8859-2"),"bar");
            
        fs.createNewFile("new_file_1");
        fs.write("new_file_1",new ByteArrayInputStream("bar".getBytes("ISO-8859-2")));
        assertEquals(fs.read("new_file_1","ISO-8859-2"),"bar");

        fs.delete("new_file_1");
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

        assertTrue(fs.checkPathChars("/only/ascii/characters"));
        assertFalse(fs.checkPathChars(""));
        assertFalse(fs.checkPathChars(null));
    }
}

