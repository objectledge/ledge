/*
 * Created on 2004-03-11
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pl.caltha.encodings;

import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author damian
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestHTMLEntityDecoder
{
    public static void main(String[] args)
    {
        HTMLEntityDecoder entParser = new HTMLEntityDecoder();
        
        try
        {
            String src1 = "<meta> &amp; &egrave; &#8222; &#x22AB; &dsds &#12 &#x12Ag &ap&amp; </meta>";
            String src2 = entParser.decode(src1);
            System.out.println(src1); 
            System.out.println(src2); 
            System.out.println(src2.equals("<meta> & \u00E8 \u201E \u22AB &dsds &#12 &#x12Ag &ap& </meta>"));

            src1 = "<meta> &amp; &egrave; &#8222; &#x22AB; &amp; </meta>";
            src2 = entParser.decodeXML(src1); 
            System.out.println(src1); 
            System.out.println(src2); 
            System.out.println(src2.equals("<meta> &amp; \u00E8 \u201E \u22AB &amp; </meta>"));
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating( false );
            factory.setNamespaceAware( true );
            SAXParser parser;
                parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();        
            SAXReader saxReader = new SAXReader(xmlReader);
            saxReader.read(new StringReader(src2));
        }
        catch (DocumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ParserConfigurationException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (SAXException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
