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

package org.objectledge.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.objectledge.filesystem.FileSystem;
import org.picocontainer.PicoContainer;

/**
 * Allows running Ledge applications from the command line.
 * 
 * <p>
 * After the container is started and components are composed and configured,
 * a designated component is looked up and method 
 * <code>void main(String[])</code> is invoked on it. 
 * Note that it this mehtod should be non-static so it may take advantage of the actual
 * component instance composed by the container.
 * </p>
 * 
 * <h3>Recognized commandline options</h3>
 * <p>
 * <table class="bodyTable">
 * <tr class="b">
 * <th>syntax</th>
 * <th>required</th>
 * <th>default</th>
 * <th width="100%">description</th>
 * </tr>
 * <tr class="a">
 * <td>-h</td>
 * <td>no</td>
 * <td>n/a</td>
 * <td>Display usage information and exit</td>
 * </tr>
 * <tr class="b">
 * <td>-v</td>
 * <td>no</td>
 * <td>n/a</td>
 * <td>Display version information on startup</td>
 * </tr>
 * <tr class="a">
 * <td>-r &lt;root&gt;</td>
 * <td>no</td>
 * <td>current working directory</td>
 * <td>Root directory of Ledge FileSystem</td>
 * </tr>
 * <tr class="b">
 * <td>-c &lt;config&gt;</td>
 * <td>no</td>
 * <td>/config</td>
 * <td>Base directory of the system's configuration</td>
 * </tr>
 * <tr class="a">
 * <td>&lt;class-name&gt;</td>
 * <td>yes</td>
 * <td>none</td>
 * <td>Class name of the component to be invoked</td>
 * </tr>
 * </table>
 * 
 * <p>
 * Any command line arguments following the componen class name will be passed verbatim 
 * to the component's main method.
 * </p>
 * 
 * <h3>Dependencies</h3>
 * <ul>
 * <li><a href="http://jakarta.apache.org/commons/cli/">Jakarta Commons CLI</a></li>
 * </ul>
 * 
 * <p>Created on Dec 22, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Main.java,v 1.11 2005-07-07 08:30:02 zwierzem Exp $
 */
public class Main
{
    /** version string. */
    protected static final String VERSION = "0.1-dev";
    
    private static Options options = new Options();

    static 
    {
        options.addOption("r", "root", true, "set local file system root directory");
        options.addOption("c", "config", true, "set config directory FS path");
        options.addOption("v", "version", false, "print version information");
        options.addOption("h", "help", false, "print usage information and exit");
    }
    
    /**
     * A private constructor - this class should be used statically only.
     */
    private Main()
    {
        // static access only (commandline runnable class)
    }

    /**
     * Command line entry point.
     * 
     * @param args command line arguments.
     */
    public static void main(String[] args)
    {
        CommandLineParser parser = new PosixParser();
        try
        {
            CommandLine line = parser.parse(options, args);
            if(line.hasOption("v"))
            {
                printVersion();
            }
            if(line.hasOption("h"))
            {
                printUsage();
            }
            else
            {
                String root = line.getOptionValue("r", System.getProperty("user.dir"));
                String config = line.getOptionValue("c", "/config");
                int componentArgCount = line.getArgList().size()-1;
                if(componentArgCount < 0)
                {
                    System.err.println("component class name required");
                }
                else
                {
                    String componentClassName = (String)line.getArgList().get(0);
                    String[] componentArgs = new String[componentArgCount];
                    ((List<String>)line.getArgList()).subList(1, componentArgCount).toArray(componentArgs);
                    run(root, config, componentClassName, componentArgs);
                }
            }
        }
        catch(ParseException exp)
        {
            System.err.println("Command line parsing failed: " + exp.getMessage());
        }
    }
    
    /**
     * Prints version information.
     */
    protected static void printVersion()
    {
        System.out.println("ObjectLedge "+VERSION);
    }

    /**
     * Prints usage infomration and exits.
     */    
    protected static void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ledge", options);
    }

    /**
     * Runns LedgeContainer.
     * 
     * @param root the file system root.
     * @param config the configuration base directory.
     * @param componentClassName the component to invoke.
     * @param componentArgs the component arguments.
     */   
    public static void run(String root, String config, String componentClassName, 
        String[] componentArgs)
    {
        BasicConfigurator.configure();
        Logger log = Logger.getLogger(Main.class);
        PicoContainer container = null; 
        try
        {
            FileSystem fs = FileSystem.getStandardFileSystem(root);
            LedgeContainer ledgeContainer = new LedgeContainer(fs, config, 
                Main.class.getClassLoader());
            container = ledgeContainer.getContainer();
            addShutdownHook(ledgeContainer);
        }
        catch(Exception e)
        {
            log.error("Container composition failed", e);
        }
        Class<?> componentClass = null;
        Object component = null;
        if(container != null)
        {
            try
            {
                componentClass = Class.forName(componentClassName);
                component =  container.getComponentInstance(componentClass);
                if(component == null)
                {
                    log.error("Component "+componentClassName+" is missing from the assembly");
                }
            }
            catch(ClassNotFoundException e)
            {
                log.error("Component class "+componentClassName+" cannot be loaded", e);
            }
        }
        Method method = null;
        if(componentClass != null)
        {
            try
            {
                method = componentClass.getMethod("main", 
                    new Class[] { (new String[0]).getClass() });
            }
            catch(NoSuchMethodException e)
            {
                log.error("Component class "+componentClassName+
                    " does not declare main(String[]) method", e);            
            }
        }
        if(method != null)
        {
            try
            {
                method.invoke(component, new Object[] { componentArgs });
            }
            catch(InvocationTargetException e)
            {
                log.error("Invocation of "+componentClassName+".main(String[]) threw exception", 
                    e.getTargetException());
            }
            catch(Exception e)
            {
                log.error("Failed to invoke "+componentClassName+".main(String[])", e);
            }
        }
    }
    
    private static void addShutdownHook(final LedgeContainer ledgeContainer)
    {
        // add a shutdown hook that will tell the builder to kill it.
        Runnable shutdownHook = new Runnable() {
            public void run() {
                System.out.println("Shutting Down NanoContainer");
                try {
                    ledgeContainer.killContainer();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Exiting VM");
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
    }
}
