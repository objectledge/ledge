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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nanocontainer.Log4JNanoContainerMonitor;
import org.nanocontainer.NanoContainerMonitor;
import org.objectledge.filesystem.FileSystem;
import org.realityforge.cli.CLArgsParser;
import org.realityforge.cli.CLOption;
import org.realityforge.cli.CLOptionDescriptor;
import org.realityforge.cli.CLUtil;

/**
 * An entry point to ObjectLedge in command line environment.
 *
 * <p>Created on Dec 22, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Main.java,v 1.3 2004-01-14 11:44:48 fil Exp $
 */
public class Main
{
    /** version string. */
    protected static final String VERSION = "0.1-dev";
    
    /** root command line option identifier. */
    protected static final int ROOT_OPTION = 'r';
    
    /** config command line option identifier. */
    protected static final int CONFIG_OPTION = 'c';
    
    /** version command line option identifier. */
    protected static final int VERSION_OPTION = 'v';
    
    /** help command line option identifier. */
    protected static final int HELP_OPTION = 'h';
    
    /** recognized command line options. */
    protected static CLOptionDescriptor[] options =
    { 
        new CLOptionDescriptor("root", CLOptionDescriptor.ARGUMENT_REQUIRED, 
            ROOT_OPTION, "set local file system root directory"),
        new CLOptionDescriptor("config", CLOptionDescriptor.ARGUMENT_REQUIRED,
            CONFIG_OPTION, "set config directory FS path"),
        new CLOptionDescriptor("version", CLOptionDescriptor.ARGUMENT_DISALLOWED,
            VERSION_OPTION, "print version information"),
        new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED,
            VERSION_OPTION, "print usage information and exit")
    };

    /**
     * A private constructor - this class should be used statically only.
     */
    private Main()
    {
    }

    /**
     * Command line entry point
     * 
     * @param args command line arguments;
     */
    public static void main(String[] args)
    {
        CLArgsParser parser = new CLArgsParser(args, options);

        if( null != parser.getErrorString() ) 
        {
            System.err.println( "Error: " + parser.getErrorString() );
            return;
        }
        
        String root = System.getProperty("user.dir");
        String config = "/config";
        String componentClassName = null;
        List clOptions = parser.getArguments();
        String[] componentArgs = new String[0];
        boolean usage = false;
        int i=0;
        optionLoop: for(; i<clOptions.size(); i++)
        {
            CLOption option = (CLOption)clOptions.get(i);
            switch(option.getId())
            {
                case ROOT_OPTION:
                    root = option.getArgument();
                    break;
                case CONFIG_OPTION:
                    config = option.getArgument();
                    break;
                case VERSION_OPTION:
                    printVersion();
                    break;
                case HELP_OPTION:                    usage = true;
                    break;
                case CLOption.TEXT_ARGUMENT:
                    componentClassName = option.getArgument();
                    break optionLoop;
                default:
                    throw new IllegalStateException("illegal state of the option parser");
            }
        }
        if(usage)
        {
            printUsage();
        }
        else
        {
            if(componentClassName == null)
            {
                printUsage();
            }
            else
            {
                if(i < clOptions.size()-1)
                {
                    componentArgs = new String[clOptions.size()-i-1];
                    for(int j=0; j<componentArgs.length; j++)
                    {
                        CLOption option = (CLOption)clOptions.get(i+1+j);
                        if(option.getId() != CLOption.TEXT_ARGUMENT)
                        {
                            throw new IllegalStateException("Ooops, conflicting option "+
                                option.getId());
                        }
                        componentArgs[j] = option.getArgument();
                    }
                }
                run(root, config, componentClassName, componentArgs);
            }
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
        String nl = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("Usage: java "+Main.class.getName()+
            " [options] componentClass [componentOptions]").append(nl).append(nl);
        msg.append("Options: ").append(nl);
        msg.append(CLUtil.describeOptions(options).toString());
        System.out.println(msg.toString());
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
        LedgeContainer container = null; 
        NanoContainerMonitor monitor = new Log4JNanoContainerMonitor();
        try
        {
            FileSystem fs = FileSystem.getStandardFileSystem(root);
            container = new LedgeContainer(fs, config, monitor);
        }
        catch(Exception e)
        {
            log.error("Container composition failed", e);
        }
        Class componentClass = null;
        Object component = null;
        if(container != null)
        {
            try
            {
                componentClass = Class.forName(componentClassName);
                component =  container.getRootContainer().getComponentInstance(componentClass);
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
}
