//
// Copyright (c) 2005 Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.encodings;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.ext.DefaultHandler2;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLEntityDecoderTest.java,v 1.1 2005-03-22 04:43:57 zwierzem Exp $
 */
public class HTMLEntityDecoderTest extends TestCase
{
    private HTMLEntityDecoder entParser = new HTMLEntityDecoder();

    /**
     * Constructor for TestHTMLEntityDecoder.
     * @param arg0
     */
    public HTMLEntityDecoderTest(String arg0)
    {
        super(arg0);
    }

    public void testDecode()
    {
        String src1 = "<meta> &amp; &egrave; &#8222; &#x22AB; &dsds &#12 &#x12Ag &ap&amp; </meta>";
        String src2 = entParser.decode(src1);
        assertEquals("<meta> & \u00E8 \u201E \u22AB &dsds &#12 &#x12Ag &ap& </meta>", src2);
    }
    
    public void testDecodeXML()
    throws Exception
    {
        String src1 = "<meta> &amp; &egrave; &#8222; &#x22AB; &amp; </meta>";
        String src2 = entParser.decodeXML(src1); 
        assertEquals("<meta> &amp; \u00E8 \u201E \u22AB &amp; </meta>", src2);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating( false );
        factory.setNamespaceAware( true );
        SAXParser parser = factory.newSAXParser();
        DefaultHandler2 dhandler = new DefaultHandler2();
        InputSource is = new InputSource(new StringReader(src2));
        parser.parse(is, dhandler);
    }
}
