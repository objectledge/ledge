/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 3, 2003
 */
package org.objectledge.logging;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.xml.DOMConfigurator;
import org.objectledge.configuration.ConfigurationFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: LoggingConfigurator.java,v 1.1 2003-12-03 11:36:37 fil Exp $
 */
public class LoggingConfigurator
{

    /**
     * 
     */
    public LoggingConfigurator(ConfigurationFactory configurationFactory)
        throws ParserConfigurationException, IOException, SAXException
    {
        InputSource source = configurationFactory.getConfigurationSource(LoggingConfigurator.class);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document config = builder.parse(source);
        DOMConfigurator.configure(config.getDocumentElement());
    }
}
