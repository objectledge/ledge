// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.objectledge.ComponentInitializationError;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Provides information about components deployed in the system and their configuration. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ConfigurationInspector.java,v 1.4 2005-05-09 05:33:59 rafal Exp $
 */
public class ConfigurationInspector
{
    private final ConfigurationFactory configurationFactory;

    private final SAXParserFactory parserFactory;

    /**
     * Creates new ConfigurationViewer instance.
     * 
     * @param configurationFactoryArg the configuration factory.
     */
    public ConfigurationInspector(ConfigurationFactory configurationFactoryArg)
    {
        this.configurationFactory = configurationFactoryArg;
        try
        {
            parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);
        }
        catch(Exception e)
        {
            throw new ComponentInitializationError(e);
        }
    }

    /**
     * Returns the configuration desctiptors for all components present in the system.
     * 
     * @return the configuration desctiptors for all components present in the system.
     */
    public List<ComponentConfiguration> getComponentConfigurations()
    {
        List<ComponentConfiguration> list = new ArrayList<ComponentConfiguration>();
        InputSource composition = configurationFactory.getCompositionSource();
        try
        {
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(new CompositionHandler(list));
            reader.parse(composition);
        }
        catch(Exception e)
        {
            throw new RuntimeException("composition parsing failed", e);
        }
        
        return list;
    }    
    
    List<String> getComponentConfig(String key)
    {
        if(!configurationFactory.hasConfig(key))
        {
            return Collections.EMPTY_LIST;
        }
        List<String> entries = new ArrayList<String>();
        InputSource config = configurationFactory.getRawConfigurationSource(key);
        try
        {
            XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(new ConfigurationHandler(entries));
            reader.parse(config);
        }
        catch(Exception e)
        {
            throw new RuntimeException("composition parsing failed", e);
        }
        
        return entries;
    }
    
    /**
     * Describes a component's configuration.
     */
    public class ComponentConfiguration
    {
        /** Key of the component. */
        private final String componentKey;
        
        /** Component implementation class. */
        private final String componentClass;
        
        /** Component's constructor parameters. */
        private final List<String> parameters;
        
        /** Component's configuration entries. */
        private final List<String> config;
        
        /**
         * Creates new ComponentConfiguration instance.
         * 
         * @param componentKeyArg component's key.
         * @param componentClassArg component's configuration class.
         * @param parametersArg component's constructor parameter list.
         * @param configArg component's configuration entries.
         */
        public ComponentConfiguration(final String componentKeyArg, final String componentClassArg,
            final List<String> parametersArg, final List<String> configArg)
        {
            componentKey = componentKeyArg;
            componentClass = componentClassArg;
            parameters = parametersArg;
            config = configArg;
        }

        /**
         * Returns the componentClass.
         *
         * @return the componentClass.
         */
        public String getComponentClass()
        {
            return componentClass;
        }

        /**
         * Returns the componentKey.
         *
         * @return the componentKey.
         */
        public String getComponentKey()
        {
            return componentKey;
        }

        /**
         * Returns the config.
         *
         * @return the config.
         */
        public List<String> getConfig()
        {
            return config;
        }

        /**
         * Returns the parameters.
         *
         * @return the parameters.
         */
        public List<String> getParameters()
        {
            return parameters;
        }
    }
    
    /**
     * A SAX2 DocumentHandler for parsing the composition file.
     */
    private class CompositionHandler 
        extends DefaultHandler
    {
        private final List<ComponentConfiguration> list;

        public CompositionHandler(List<ComponentConfiguration> listArg)
        {
            this.list = listArg;
        }
        
        /**
         * {@inheritDoc}
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
        {
            if(localName.equals("component"))
            {
                String key = null;
                boolean classKey = false;
                if(attributes.getValue("key") != null)
                {
                    key = attributes.getValue("key");
                }
                if(key == null && attributes.getValue("class-key") != null)
                {
                    key = attributes.getValue("class-key");
                    classKey = true;
                }
                if(key == null && attributes.getValue("anon") != null)
                {
                    key = "anon";
                }
                if(key == null)
                {
                    key = attributes.getValue("class");
                    classKey = true;
                }
                String impl = attributes.getValue("class");
                
                List<String> params = Collections.EMPTY_LIST;
                List<String> config = getComponentConfig(key);
                list.add(new ComponentConfiguration((classKey ? "class " : "") + key, impl, params, 
                    config));
             }
        }
    }

    /**
     * 
     * 
     *
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: ConfigurationInspector.java,v 1.4 2005-05-09 05:33:59 rafal Exp $
     */
    private class ConfigurationHandler 
        extends DefaultHandler
    {
        private final List<String> entries;
        
        private final StringBuilder buff = new StringBuilder();
        
        private final StringBuilder charContent = new StringBuilder();
        
        private final LinkedList<String> elementStack = new LinkedList<String>();
        
        public ConfigurationHandler(List<String> entriesArg)
        {
            entries = entriesArg;
        }
        
        /**
         * {@inheritDoc}
         */
        public void characters(char[] ch, int start, int length)
            throws SAXException
        {
            String s = new String(ch, start, length);
            if(s.trim().length() > 0)
            {
                charContent.append(s);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
        {
            elementStack.add(localName);
            if(attributes.getLength() > 0)
            {
                String elementPath = getElementPath();
                for(int i = 0; i < attributes.getLength(); i++)
                {
                    buff.append(elementPath);
                    buff.append('.');
                    buff.append(attributes.getQName(i));
                    buff.append(" = ");
                    buff.append(attributes.getValue(i));
                    commitEntry();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void endElement(String uri, String localName, String qName)
            throws SAXException
        {
            if(charContent.length() > 0)
            {
                buff.append(getElementPath());
                buff.append(" = ");
                buff.append(charContent.toString().trim());
                commitEntry();
                charContent.setLength(0);
            }
            elementStack.removeLast();
        }
        
        private String getElementPath()
        {
            StringBuilder pathBuff = new StringBuilder();
            for(Iterator<String> element = elementStack.iterator(); element.hasNext();)
            {
                pathBuff.append(element.next());
                if(element.hasNext())
                {
                    pathBuff.append('/');
                }
            }
            return pathBuff.toString();
        }
        
        private void commitEntry()
        {
            entries.add(buff.toString());
            buff.setLength(0);
        }
    }
}
