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

package org.objectledge.logging;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.xml.DOMConfigurator;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.filesystem.FileSystem;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A component that initializes Log4J using a configuration file.
 * 
 * <p>
 * This component was introduced so that log system configufration were preformed in a way uniform 
 * with other parts of the system. The configuration file conforms to the Log4J
 * <code>DOMConfigurator</code> schema. 
 * This package provides specialized Log4J objects, aware of ObjectLedge features that can be 
 * used in the configuration.
 * </p>
 * 
 * <p>
 * At this point the configurator retrieves a configuration <code>InputSource</code> from
 * the configuration factory (it will get validated agains apropriate schema), parses it and 
 * passes on to Log4j <code>DomConfigurator</code>. In the future we may want to iterate over 
 * the configuration ourselves and instantiate the needed objects (appenders etc. The Pico Way).
 * This will allow a number of neat things, including Appenders wrinting to Ledge
 * {@link org.objectledge.filesystem.FileSystem}.
 * Until then paths in the conifguration files need to be either absolute in the machine's FS,
 * or relative to the container's working directory.
 * </p>
 * 
 * <h3>Functionality anticipated in the future</h3>
 * <ul>
 * <li>runtime modification of logging configuration, most notably logger verbosities</li>
 * <li>management of loggers through textual interaction, and possibly JMX</li>
 * <li>customized log4j file appenders working over Ledge
 *  {@link org.objectledge.filesystem.FileSystem}</li>
 * </ul>
 * 
 * <h3>Related documentation</h3>
 * <ul>
 * <li><a href="http://logging.apache.org/log4j">Log4J documentation</a></li>
 * </ul>
 * 
 * <h3>Dependencies</h3>
 * <ul>
 * <li><a href="http://logging.apache.org/log4j">Log4J</a></li>
 * </ul>
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LoggingConfigurator.java,v 1.8 2005-10-10 14:06:16 rafal Exp $
 */
public class LoggingConfigurator
{
    /**
     * Creates a new LoggingConfigurator instance.
     * 
     * <p>
     * Instance created using this constructor does not alter configuration of Log4j in any way.
     * </p>
     */
    public LoggingConfigurator()
    {        
    }
    
    /**
     * Creates a new LoggingConfigurator.
     * 
     * @param configurationFactory the configuration factory.
     * @param fileSystem the file system component.
     * @throws ParserConfigurationException if the JAXP system is not configured properly.
     * @throws IOException if the configuration cannot be loaded.
     * @throws SAXException if the configuration cannot be parsed.
     */
    public LoggingConfigurator(ConfigurationFactory configurationFactory, FileSystem fileSystem)
        throws ParserConfigurationException, IOException, SAXException
    {
        InputSource source = configurationFactory.
        	getConfigurationSource(LoggingConfigurator.class, LoggingConfigurator.class);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document config = builder.parse(source);
        DOMConfigurator configurator = new LedgeDOMConfigurator(fileSystem);

        Hierarchy hierarchy = new LedgeLoggerHierarchy(new RootCategory((Level)Level.DEBUG));
        configurator.doConfigure(config.getDocumentElement(), hierarchy);
        // We use ClassLoader local, but accessible object as the guard. This allows reinitializing 
        // Log4J from within the same sandbox.
        LogManager.setRepositorySelector(new DefaultRepositorySelector(hierarchy), 
            LogManager.class);
    }
    
    
    /**
     * Creates a logger instance.
     * 
     * @param name of the logger.
     * @return a logger instance.
     */
    public Logger createLogger(String name)
    {
        return new Log4JLogger(LogManager.getLogger(name));
    }    
}
