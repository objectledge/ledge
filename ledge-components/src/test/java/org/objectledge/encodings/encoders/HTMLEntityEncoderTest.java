// 
//Copyright (c) 2003, 2004 Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

package org.objectledge.encodings.encoders;

import junit.framework.TestCase;

import org.objectledge.encodings.HTMLEntityEncoder;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLEntityEncoderTest.java,v 1.6 2004-08-24 14:15:46 rafal Exp $
 */
public class HTMLEntityEncoderTest extends TestCase
{
	private String srcPart1 = "\"&'<> ";

	private String srcPart2 =
		"\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB" +
		"\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8" +
		"\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5" +
		"\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2" +
		"\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF" +
		"\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC" +
		"\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9" +
		"\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF\u0152\u0153\u0160\u0161\u0178\u0192\u02C6" +
		"\u02DC\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039A\u039B\u039C" +
		"\u039D\u039E\u039F\u03A0\u03A1\u03A3\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9\u03B1" +
		"\u03B2\u03B3\u03B4\u03B5\u03B6\u03B7\u03B8\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE" +
		"\u03BF\u03C0\u03C1\u03C2\u03C3\u03C4\u03C5\u03C6\u03C7\u03C8\u03C9\u03D1\u03D2" +
		"\u03D6\u2002\u2003\u2009\u200C\u200D\u200E\u200F\u2013\u2014\u2018\u2019\u201A" +
		"\u201C\u201D\u201E\u2020\u2021\u2022\u2026\u2030\u2032\u2033\u2039\u203A\u203E" +
		"\u2044\u20AC\u2111\u2118\u211C\u2122\u2135\u2190\u2191\u2192\u2193\u2194\u21B5" +
		"\u21D0\u21D1\u21D2\u21D3\u21D4\u2200\u2202\u2203\u2205\u2207\u2208\u2209\u220B" +
		"\u220F\u2211\u2212\u2217\u221A\u221D\u221E\u2220\u2227\u2228\u2229\u222A\u222B" +
		"\u2234\u223C\u2245\u2248\u2260\u2261\u2264\u2265\u2282\u2283\u2284\u2286\u2287" +
		"\u2295\u2297\u22A5\u22C5\u2308\u2309\u230A\u230B\u2329\u232A\u25CA\u2660\u2663" +
		"\u2665\u2666\u0414\u0413";

	private String outPart2Iso2 =
		"&iexcl;&cent;&pound;\u00A4&yen;&brvbar;\u00A7\u00A8&copy;&ordf;" +
		"&laquo;&not;\u00AD&reg;&macr;\u00B0&plusmn;&sup2;&sup3;\u00B4&micro;&para;&middot;" +
		"\u00B8&sup1;&ordm;&raquo;&frac14;&frac12;&frac34;&iquest;&Agrave;\u00C1\u00C2" +
		"&Atilde;\u00C4&Aring;&AElig;\u00C7&Egrave;\u00C9&Ecirc;\u00CB&Igrave;\u00CD\u00CE" +
		"&Iuml;&ETH;&Ntilde;&Ograve;\u00D3\u00D4&Otilde;\u00D6\u00D7&Oslash;&Ugrave;\u00DA" +
		"&Ucirc;\u00DC\u00DD&THORN;\u00DF&agrave;\u00E1\u00E2&atilde;\u00E4&aring;&aelig;" +
		"\u00E7&egrave;\u00E9&ecirc;\u00EB&igrave;\u00ED\u00EE&iuml;&eth;&ntilde;&ograve;" +
		"\u00F3\u00F4&otilde;\u00F6\u00F7&oslash;&ugrave;\u00FA&ucirc;\u00FC\u00FD&thorn;" +
		"&yuml;&OElig;&oelig;\u0160\u0161&Yuml;&fnof;&circ;&tilde;&Alpha;&Beta;&Gamma;" +
		"&Delta;&Epsilon;&Zeta;&Eta;&Theta;&Iota;&Kappa;&Lambda;&Mu;&Nu;&Xi;&Omicron;&Pi;" +
		"&Rho;&Sigma;&Tau;&Upsilon;&Phi;&Chi;&Psi;&Omega;&alpha;&beta;&gamma;&delta;&epsilon;" +
		"&zeta;&eta;&theta;&iota;&kappa;&lambda;&mu;&nu;&xi;&omicron;&pi;&rho;&sigmaf;&sigma;" +
		"&tau;&upsilon;&phi;&chi;&psi;&omega;&thetasym;&upsih;&piv;&ensp;&emsp;&thinsp;&zwnj;" +
		"&zwj;&lrm;&rlm;&ndash;&mdash;&lsquo;&rsquo;&sbquo;&ldquo;&rdquo;&bdquo;&dagger;" +
		"&Dagger;&bull;&hellip;&permil;&prime;&Prime;&lsaquo;&rsaquo;&oline;&frasl;&euro;" +
		"&image;&weierp;&real;&trade;&alefsym;&larr;&uarr;&rarr;&darr;&harr;&crarr;&lArr;" +
		"&uArr;&rArr;&dArr;&hArr;&forall;&part;&exist;&empty;&nabla;&isin;&notin;&ni;&prod;" +
		"&sum;&minus;&lowast;&radic;&prop;&infin;&ang;&and;&or;&cap;&cup;&int;&there4;&sim;" +
		"&cong;&asymp;&ne;&equiv;&le;&ge;&sub;&sup;&nsub;&sube;&supe;&oplus;&otimes;&perp;" +
		"&sdot;&lceil;&rceil;&lfloor;&rfloor;&lang;&rang;&loz;&spades;&clubs;&hearts;&diams;" +		"&#1044;&#1043;";

    /**
     * Constructor for HTMLEntityEncoderTest.
     * @param arg0
     */
    public HTMLEntityEncoderTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Test for String encodeAttribute(String, String, boolean)
     * &amp; String encodeAttribute(String, String)
     */
    public void testEncodeAttribute()
    {
		HTMLEntityEncoder encoder = new HTMLEntityEncoder();

		String src = srcPart1+srcPart2;
		String outDblQuote1 = "&quot;&amp;'&lt;&gt; ";
		String outDblQuote = outDblQuote1+outPart2Iso2;
		String outQuote = "\"&amp;&apos;&lt;&gt; "+outPart2Iso2;
		
		String realDblQuote = encoder.encodeAttribute(src, "ISO-8859-2", true);
		assertEquals(realDblQuote, outDblQuote);

		String realQuote = encoder.encodeAttribute(src, "ISO-8859-2", false);
		assertEquals(realQuote, outQuote);

		realDblQuote = encoder.encodeAttribute(src, "ISO-8859-2");
		assertEquals(realDblQuote, outDblQuote);
		
		realDblQuote = encoder.encodeAttribute(null, "ISO-8859-2");
		assertNull(realDblQuote);

		realDblQuote = encoder.encodeAttribute("", "ISO-8859-2");
		assertNull(realDblQuote);

		realDblQuote = encoder.encodeAttribute(src, null);
		assertEquals(realDblQuote, outDblQuote1+srcPart2);
    }

    public void testEncodeHTML()
    {
		HTMLEntityEncoder encoder = new HTMLEntityEncoder();

		String src = srcPart1+srcPart2;
		String out = "\"&'<> "+outPart2Iso2;

		String realOut = encoder.encodeHTML(src, "ISO-8859-2");
		assertEquals(realOut, out);

		realOut = encoder.encodeHTML(null, "ISO-8859-2");
		assertNull(realOut);

		realOut = encoder.encodeHTML("", "ISO-8859-2");
		assertEquals("", realOut);

		realOut = encoder.encodeHTML(src, null);
		assertEquals(realOut, src);
    }
}
