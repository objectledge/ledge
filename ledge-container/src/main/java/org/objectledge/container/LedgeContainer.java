// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pico.xml.LedgeXMLContainerBuilder;
import org.objectledge.xml.XMLValidator;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.xml.sax.SAXParseException;

/**
 * A customized NanoContainer that uses {@link FileSystem} to load the composition file.
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LedgeContainer.java,v 1.10 2004-02-20 08:40:34 fil Exp $
 */
public class LedgeContainer
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    /** Location of the container composition file. */
    public static final String COMPOSITION_FILE = "/container.xml";

    /** Default xml front end implementation. */
    public static final String FRONT_END_CLASS = 
        "org.objectledge.pico.xml.LedgeXMLContainerBuilder";
    
    /** Config base path key. */
    public static final String CONFIG_BASE_KEY = "org.objectledge.ConfigBase";
    
    /** The embedded container. */
    protected ObjectReference containerRef = new SimpleReference();

    /** The embedded container builder. */
    protected ContainerBuilder containerBuilder;

    // initialization //////////////////////////////////////////////////////////////////////////

    /**
     * Creates an instance of the LedgeContainer
     * 
     * @param fs the FileSystem
     * @param configBase configuration directory.
     * @param classLoader the classload to load classes with.
     * @throws IOException if the composition file could not be read.
     * @throws ClassNotFoundException if the container builder class could not be instantiated.
     * @throws PicoCompositionException if the composition file is invalid, or could not be 
     *         verified due to system failure.
     */    
    public LedgeContainer(FileSystem fs, String configBase, ClassLoader classLoader)
        throws IOException, ClassNotFoundException, PicoCompositionException
    {
        NanoContainer nano = new NanoContainer(getCompositionFile(fs, configBase), FRONT_END_CLASS,
            classLoader);
        containerBuilder = nano.getContainerBuilder();
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(getBootContainer(fs, configBase, classLoader));
        containerBuilder.buildContainer(containerRef, parentRef, null);
    }

    /**
     * Returns the embedded container.
     * 
     * @return the embedded container.
     */
    public MutablePicoContainer getContainer()
    {
        return (MutablePicoContainer)containerRef.get();
    }
    
    /**
     * Kills the embedded container. 
     */
    public void killContainer()
    {
        containerBuilder.killContainer(containerRef);
    }
    
    /**
     * Verifies and returns the composition file.
     * 
     * @param fs the FileSystem to load compositoin file from.
     * @param configBase configuration directory.
     * @return the composition file.
     * @throws IOException if the file could not be read.
     * @throws PicoCompositionException if the composition file is invalid, or could not be 
     *         verified due to system failure.
     */
    protected static Reader getCompositionFile(FileSystem fs, String configBase)
        throws IOException, PicoCompositionException
    {
        URL compositionUrl = fs.getResource(configBase + COMPOSITION_FILE);
        try
        {
            XMLValidator validator = new XMLValidator();
            validator.validate(compositionUrl, 
                fs.getResource(LedgeXMLContainerBuilder.SCHEMA_PATH));
        }
        catch(SAXParseException e)
        {
            throw new PicoCompositionException("parse error "+e.getMessage()+" in "+
                e.getSystemId()+" at line "+e.getLineNumber(), e);
        }
        catch(Exception e)
        {
            throw new PicoCompositionException("composition file "+compositionUrl+
                "is missing or invalid", e);
        }
        
        return new InputStreamReader(fs.getInputStream(configBase + COMPOSITION_FILE));        
    }

    /**
     * Returns the boot component container.
     * 
     * @param fs the FileSystem
     * @param configBase configuration directory.
     * @param classLoader the classload to load classes with.
     * @return the boot component container.
     */    
    protected static PicoContainer getBootContainer(FileSystem fs, String configBase, 
        ClassLoader classLoader)
    {
        MutablePicoContainer bootContainer = new DefaultPicoContainer();
        bootContainer.registerComponentInstance(FileSystem.class, fs);
        bootContainer.registerComponentInstance(ClassLoader.class, classLoader);
        bootContainer.registerComponentInstance(CONFIG_BASE_KEY, configBase);
        return bootContainer;
    }
}
