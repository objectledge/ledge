// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.selector;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: RuleTest.java,v 1.2 2004-06-01 11:13:57 zwierzem Exp $
 */
public class RuleTest extends TestCase
{

    /**
     * Constructor for RuleTest.
     * @param arg0
     */
    public RuleTest(String arg0)
    {
        super(arg0);
    }

    public void testRules()
        throws Exception
    {
        String root = System.getProperty("ledge.root");
        if(root == null)
        {
            throw new Exception("system property ledge.root undefined. "+
            "use -Dledge.root=.../ledge-container/src/test/resources");
        }
        FileSystem fs = FileSystem.getStandardFileSystem(root+"/selector");
        URL configUrl = fs.getResource("RuleTest.xml");
        URL schemaUrl = fs.getResource("org/objectledge/selector/RuleTest.rng");
        if(configUrl == null || schemaUrl == null)
        {
            throw new Exception("fs config invalid, or files missing");
        }
        XMLValidator validator = new XMLValidator(new XMLGrammarCache());
        try
        {
            validator.validate(configUrl, schemaUrl);
        }
        catch(SAXParseException e)
        {
            throw new Exception("parser error "+e.getMessage()+" in "+e.getSystemId()+" at line "+
                e.getLineNumber(), e);
        }
        InputSource source = new InputSource(configUrl.toString());
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        Configuration config = handler.getConfiguration();
        
        Configuration[] tests = config.getChildren("test");
        Rule[] rules = new Rule[tests.length];    
        String[] expected = new String[tests.length];
        for(int i=0; i<tests.length; i++)
        {
            rules[i] = new Rule(tests[i].getChild("rule"));
            expected[i] = tests[i].getAttribute("result");
        }
        
        Map values = new HashMap();
        values.put("string", "string");
        values.put("string2", "string");
        values.put("one", new Integer(1));
        Variables vars = new MapVariables(values);
        
        for(int i=0; i<tests.length; i++)
        {
            Rule rule = rules[i];
            try
            {
                boolean result = rule.evaluate(vars);
                if(expected[i].equals("true"))
                {
                    assertTrue("rule #"+i, result);
                }
                else if(expected[i].equals("false"))
                {
                    assertFalse("rule #"+i, result);
                }
                else if(expected[i].equals("exception"))
                {
                    fail("rule #"+i+": exeption expected");
                }
            }
            catch(Exception e)
            {
                if(!expected[i].equals("exception"))
                {   
                    throw e;
                }
            }
        }
    }
}
