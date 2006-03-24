// 
// Copyright (c) 2003-2006, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.external;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;
import org.objectledge.utils.StringUtils;

/**
 * A helper object for manangin a directory containing executable scripts in the local filesystem.
 * <p>
 * The helper has two functions:
 * <ul>
 * <li>Ensuring that the scripts in the local filesystem have apropriate privileges. This is useful
 * after the scripts get extracted from war/jar files using the
 * {@link org.objectledge.filesystem.ContentExtractor}</li>
 * <li>Providing absolute paths to the script files in the server filesystem. This is necessary to
 * execute the scritpts.</li>
 * </ul>
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: ScriptDirectory.java,v 1.1 2006-03-24 14:27:37 rafal Exp $
 */
public class ScriptDirectory
{
    public static final String DEFAULT_PROVIDER = "local";

    public static final String DEFAULT_DIRECTORY = null;

    public static final String DEFAULT_PATH_PATTERN = ".*\\.(sh|pl|py)$";

    public static final String DEFAULT_COMMAND = "chmod a+x";

    private static String fs = System.getProperty("file.separator");

    private final LocalFileSystemProvider provider;

    private final String directory;

    private final File baseDir;

    /**
     * Creates a new ScriptDirectory instance.
     * 
     * @param fileSystem the FileSystem component.
     * @param providerName the name of the provider to use ("local" in normal circumstances).
     * @param directory the directory to scan for scripts in order to ensure executability (null to
     *        disable).
     * @param pathPattern pattern to match file name against in order to decide if they should be
     *        made executable.
     * @param command the command string to execute to make a script executable - path is appended
     *        as additional argument argument.
     */
    public ScriptDirectory(FileSystem fileSystem, String providerName, String directory,
        String pathPattern, String command)
    {
        this.directory = directory;
        this.provider = (LocalFileSystemProvider)fileSystem.getProvider(providerName);
        this.baseDir = null;
        ensureExecutable(pathPattern, command);
    }

    /**
     * Creates a new ScriptDirectory instance.
     * 
     * @param baseDir the script directory in the host filesystem.
     * @param pathPattern pattern to match file name against in order to decide if they should be
     *        made executable.
     * @param command the command string to execute to make a script executable - path is appended
     *        as additional argument argument.
     */
    public ScriptDirectory(File baseDir, String pathPattern, String command)
    {
        this.directory = null;
        this.provider = null;
        this.baseDir = baseDir;
        ensureExecutable(pathPattern, command);
    }

    /**
     * Creates a new ScriptDirectory instance.
     * 
     * @param fileSystem the FileSystem component.
     * @param configuration component configuration.
     * @throws ConfigurationException
     */
    public ScriptDirectory(FileSystem fileSystem, Configuration configuration)
        throws ConfigurationException
    {
        if(configuration.getChild("ledge-fs", false) != null)
        {
            String providerName = configuration.getChild("ledge-fs").getChild("provider").getValue(
                DEFAULT_PROVIDER);
            this.provider = (LocalFileSystemProvider)fileSystem.getProvider(providerName);
            this.directory = configuration.getChild("ledge-fs").getChild("directory").getValue();
            this.baseDir = null;
        }
        else
        {
            this.provider = null;
            this.directory = null;
            this.baseDir = new File(configuration.getChild("host-fs").getChild("directory")
                .getValue());
        }
        String pathPattern = configuration.getChild("ensure-executable").getChild("pattern")
            .getValue(DEFAULT_PATH_PATTERN);
        String command = configuration.getChild("ensure-executable").getChild("command").getValue(
            DEFAULT_COMMAND);
        ensureExecutable(pathPattern, command);
    }

    /**
     * Returns the canonical script path in the server's filesystem.
     * 
     * @param script the path inside Ledge virtual filesystem.
     * @return the canonical script path in the server's filesystem.
     * @throws IOException if the path cannot be determined.
     */
    public String getPath(String script)
        throws IOException
    {
        if(provider != null)
        {
            return provider.getFile(directory + "/" + script).getCanonicalPath();
        }
        else
        {
            return new File(baseDir, script.replace("/", fs)).getCanonicalPath();
        }
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

    private void ensureExecutable(String pathPattern, String command)
    {
        if(pathPattern != null && pathPattern.length() > 0)
        {
            try
            {
                if(provider != null)
                {
                    ensureExecutable(provider, directory, Pattern.compile(pathPattern), command
                        .split(" "));
                }
                else
                {
                    ensureExecutable(baseDir, Pattern.compile(pathPattern), command.split(" "));
                }
            }
            catch(Exception e)
            {
                throw new ComponentInitializationError(
                    "failed to ensure script execution permissions", e);
            }
        }
    }

    private static void ensureExecutable(LocalFileSystemProvider provider, String directory,
        Pattern namePattern, String[] commandTokens)
        throws IOException, InterruptedException
    {
        Set<String> items = provider.list(directory);
        for(String item : items)
        {
            String path = directory + "/" + item;
            if(provider.isDirectory(path))
            {
                ensureExecutable(provider, path, namePattern, commandTokens);
            }
            else if(namePattern.matcher(path).matches())
            {
                Runtime.getRuntime().exec(
                    StringUtils.push(commandTokens, provider.getFile(path).getCanonicalPath()))
                    .waitFor();
            }
        }
    }

    private static void ensureExecutable(File dir, Pattern namePattern, String[] commandTokens)
        throws InterruptedException, IOException
    {
        File[] items = dir.listFiles();
        for(File item : items)
        {
            if(item.isDirectory())
            {
                ensureExecutable(dir, namePattern, commandTokens);
            }
            else if(namePattern.matcher(item.getPath()).matches())
            {
                Runtime.getRuntime().exec(StringUtils.push(commandTokens, item.getCanonicalPath()))
                    .waitFor();
            }
        }
    }
}
