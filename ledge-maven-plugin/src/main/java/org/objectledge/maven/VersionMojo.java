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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which generates a version file.
 * 
 * @goal version
 * @phase prepare-package
 */
public class VersionMojo
    extends AbstractMojo
{
    /**
     * Build output directory.
     * 
     * @parameter expression="${dir}" default-value="${project.build.directory}/classes"
     * @required
     */
    private File dir;

    /**
     * Current project.
     * 
     * @parameter expression="${project}
     */
    private MavenProject project;

    /**
     * Optional build label, should be set from command line.
     * 
     * @parameter expression="${buildLabel}"
     * @default-value=""
     */
    private String buildLabel;

    public void execute()
        throws MojoExecutionException
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        File path = new File(dir, "/META-INF/versions/" + project.getGroupId() + "/"
            + project.getArtifactId());
        path.getParentFile().mkdirs();
        if(!path.getParentFile().exists())
        {
            throw new MojoExecutionException("failed to create directory "
                + path.getParentFile().getAbsolutePath());
        }
        try
        {
            FileOutputStream os = new FileOutputStream(path);
            OutputStreamWriter w = new OutputStreamWriter(os);
            w.append(project.getGroupId());
            w.append("/");
            w.append(project.getArtifactId());
            w.append("/");
            w.append(project.getVersion());
            w.append("/");
            w.append(buildLabel != null ? buildLabel : "");
            w.append("/");
            w.append(f.format(new Date()));
            w.append("/");
            w.append(System.getProperty("user.name"));
            w.close();
        }
        catch(IOException e)
        {
            throw new MojoExecutionException("failed to write to file " + path.getAbsolutePath());
        }
    }
}
