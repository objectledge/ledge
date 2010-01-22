package org.objectledge.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which generates a file listing, as used by Ledge ReadOnlyFileSystem.
 * 
 * @goal listing
 * @phase prepare-package
 */
public class FileListingMojo
    extends AbstractMojo
{
    /**
     * Directory to scan.
     * 
     * @parameter expression="${dir}" default-value="${project.build.directory}/classes"
     * @required
     */
    private File dir;

    public void execute()
        throws MojoExecutionException
    {
        File listing = new File(dir, "/META-INF/files.lst");
        listing.getParentFile().mkdirs();
        if(!listing.getParentFile().exists())
        {
            throw new MojoExecutionException("failed to create directory "
                + listing.getParentFile().getAbsolutePath());
        }
        try
        {
            FileOutputStream os = new FileOutputStream(listing);
            OutputStreamWriter w = new OutputStreamWriter(os);
            w.write(listDirectory(dir));
            w.close();
        }
        catch(IOException e)
        {
            throw new MojoExecutionException("failed to write to file " + listing.getAbsolutePath());
        }
    }

    private String listDirectory(File baseDir)
    {
        List<ListingItem> items = new ArrayList<ListingItem>();
        listDirectory(baseDir, baseDir, items);
        Collections.sort(items);
        StringBuilder buff = new StringBuilder();
        for(ListingItem i : items)
        {
            buff.append(i);
            buff.append('\n');
        }
        return buff.toString();
    }
    
    private void listDirectory(File baseDir, File dir, List<ListingItem> items)
    {
        items.add(new DirectoryItem(baseDir, dir));
        for(File i : dir.listFiles())
        {
            if(i.isFile())
            {
                items.add(new FileItem(baseDir, i));
            }
            else
            {
                listDirectory(baseDir, i, items);
            }
        }
    }

    private class ListingItem
        implements Comparable<ListingItem>
    {
        private final String path;

        public ListingItem(File basePath, File path)
        {
            assert path.getPath().startsWith(basePath.getPath());
            this.path = path.getPath().substring(basePath.getPath().length());
        }

        public String toString()
        {
            return path;
        }

        public int compareTo(ListingItem o)
        {
            return path.compareTo(o.path);
        }
    }

    private class FileItem
        extends ListingItem
    {
        private final long lastModified;

        private final long length;

        public FileItem(File basePath, File path)
        {
            super(basePath, path);
            this.lastModified = path.lastModified();
            this.length = path.length();
        }

        public String toString()
        {
            return super.toString() + " " + length + " " + lastModified;
        }
    }

    private class DirectoryItem
        extends ListingItem
    {
        public DirectoryItem(File basePath, File path)
        {
            super(basePath, path);
        }
        
        public String toString()
        {
            return super.toString() + "/";
        }
    }
}
