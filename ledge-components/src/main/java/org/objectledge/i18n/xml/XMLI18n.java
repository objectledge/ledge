// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.i18n.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.impl.I18nBase;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.validate.IncorrectSchemaException;

/**
 * I18n Component XML implementation.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class XMLI18n extends I18nBase
{
    /** localization file schema path */
    private static final String LOCALIZATION_SCHEMA = "org/objectledge/i18n/xml/localization.rng";
    
	/** the file system */
	private FileSystem fileSystem;
    
    /** xml validator */
    private XMLValidator xmlValidator;
	
	/** the locale files directory */
	private String localeDir;
	
	/** the sax handler */
	private SAXEventHandler handler;
	
	/** the sax parser */
	private SAXParser parser;
	
	/** locale file pattern */
	private Pattern localeFilePattern;
	
	/**
	 * Component constructor.
	 * 
	 * @param config the configuration.
	 * @param logger the logger.
	 * @param fileSystem the file system.
	 * @param localeDir the locale files directory.
	 * @throws ParserConfigurationException if happen.
	 * @throws SAXException if happen.
	 */	
	public XMLI18n(Configuration config, Logger logger, FileSystem fileSystem, XMLValidator xmlValidator, String localeDir)
		throws ParserConfigurationException, SAXException 
	{
		super(config, logger);
		System.out.println("XMLI18n init called");
		this.fileSystem = fileSystem;
		this.localeDir = localeDir;
        this.xmlValidator = xmlValidator;
		localeFilePattern = Pattern.
			compile("[a-zA-Z0-9]*(\\.[a-zA-Z0-9]+)*_[a-z]{2}_[A-Z]{2}\\.xml");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		handler = new SAXEventHandler();
		reload();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void reload()
	{
		Map newLocaleMap = new HashMap();
		try
		{
			String[] files = fileSystem.list(localeDir);
			for(int i=0; i<files.length; i++)
			{
				if(files[i].equals("CVS"))
				{
					continue;
				}
				Matcher m = localeFilePattern.matcher(files[i]);
				if(!m.matches())
				{
					throw new ComponentInitializationError("file '"+files[i]+
								"' doesn't match locale file pattern");
				}
				//tokenize file...
				int split = files[i].indexOf('_');
				String prefix = files[i].substring(0,split);
				String rest = files[i].substring(split+1, files[i].length());
				split = rest.indexOf('.');
				String locale = rest.substring(0, split);
				Map map = (Map)newLocaleMap.get(locale);
				if(map == null)
				{
					map = new HashMap();
					newLocaleMap.put(locale, map);
				}
				loadFile(files[i], map, prefix);
			}
		}
		catch(Exception e)	
		{
			throw new ComponentInitializationError("Exception during locale loading",e);
		}
		localeMap = newLocaleMap;
	}

	private void loadFile(String file, Map map, String prefix)
		throws IOException, SAXException, IncorrectSchemaException
	{
        xmlValidator.validate(localeDir+file, LOCALIZATION_SCHEMA);
		try
		{
			InputStream is = fileSystem.getInputStream(localeDir+file);
			handler.init(map, prefix);
			parser.parse(is, handler);
		}
		catch(SAXParseException e)
		{
			throw new ComponentInitializationError("error parsing "+file+
				 " on line "+((SAXParseException)e).getLineNumber(), e);
		}
	}
	
	/**
	 * SAX Parser event handler class to load locale properties.
	 *  
	 */
	protected class SAXEventHandler
		   extends DefaultHandler
	{
	    /** the document locator */
	    private Locator locator;

		/** prefix stack */
		private LinkedList prefix = new LinkedList();

		/** current item */
		private String item;

		/** string buffer */
		private StringBuffer sb = new StringBuffer();
		
		/** map to load the mappings */
		private Map map;
		
		/** base prefix */
		private String basePrefix;
		
		/**
		 * Initializes the hadler before parsing a new file.
		 *
		 * @param map the locale map.
		 * @param basePrefix the base prefix.
		 */
		public void init(Map map, String basePrefix)
		{
			this.basePrefix = basePrefix;
		    this.map = map;
			item = null;
		   	prefix.clear();
	    }

		/**
		 * Receive an object for locating the origin of SAX document events.
		 *
		 * @param locator An object that can return the location of any SAX
		 * document event.
		 */
		public void setDocumentLocator(Locator locator)
		{
		    this.locator = locator;
		}

		/**
 		  * {@inheritDoc}
		  */
		public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
        {
		    String name;
		    if("prefix".equals(localName))
			{
			    if(item != null)
			    {
			        throw new SAXParseException("<prefix> cannot be nested inside <item>", 
												 locator);
				}
				name = attributes.getValue("name");
				if(name == null)
				{
				    throw new SAXParseException("name attribute required", locator);
				}
				prefix.addLast(name);
			}
			else if("item".equals(localName))
			{
			    if(item != null)
				{
				    throw new SAXParseException("<item> can not be nested", locator);
				}
				name = attributes.getValue("name");
				if(name == null)
				{
				    throw new SAXParseException("name attribute required", locator);
				}
				item = name;
			}
	    }

		/**
		 * {@inheritDoc}
		 */
		public void endElement(String uri, String localName, String qName)
		{
			if("prefix".equals(localName))
			{
			    prefix.removeLast();
			}
			else if("item".equals(localName))
			{
			    item = null;
			}
		}
    
		/**
		 * {@inheritDoc}
		 */
		public void characters(char[] ch, int start, int length)
			throws SAXParseException
        {
		   	String value = new String(ch,start,length);
			value = value.trim();
			if(value.length()>0)
			{
			    if(item==null)
			    {
					throw new SAXParseException("strings need to be nested in <item>", locator);
				}
                sb.setLength(0);
                if(basePrefix != null && basePrefix.length()>0)
                {
                	sb.append(basePrefix);
                	if(!basePrefix.endsWith("."))
                	{
                		sb.append('.');
                	}
                }
                for(int i=0; i<prefix.size(); i++)
                {
				    sb.append(prefix.get(i)).append('.');
				}
				sb.append(item);
				String name = sb.toString();
 				String previousValue = (String)map.get(name);
                if(map.containsKey(name))
				{
                    throw new SAXParseException(name+" already defined ", locator);
				}
				else
				{
				    map.put(name, value);
				}
			}
		}
    }
}
