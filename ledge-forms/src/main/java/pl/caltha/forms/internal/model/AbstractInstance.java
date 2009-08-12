package pl.caltha.forms.internal.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXWriter;
import org.xbis.SAXToXBISAdapter;
import org.xbis.XBISToSAXAdapter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Container for DOM instance documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: AbstractInstance.java,v 1.1 2005-01-19 06:55:35 pablo Exp $
 */
public abstract class AbstractInstance
implements java.io.Serializable
{
    protected String schemaURI;
    protected Document instanceDocument;

    public AbstractInstance(String schemaURI, Document instanceDocument)
    {
        this.schemaURI = schemaURI;
        this.instanceDocument = instanceDocument;
    }

    //------------------------------------------------------------------------
    // pl.caltha.forms.Instance methods
    
    public String getSchemaURI()
    {
        return schemaURI;
    }

    public Document getDocument()
    {
        return instanceDocument;
    }

    //------------------------------------------------------------------------
    public static byte[] documentAsByteArray(Document document)
        throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        SAXToXBISAdapter sax2xbis = new SAXToXBISAdapter();
        sax2xbis.setStream(out);
        SAXWriter dom4j2sax = new SAXWriter(sax2xbis, sax2xbis, sax2xbis);
        try
        {
            dom4j2sax.write(document);
            return out.toByteArray();
        }
        catch(SAXException e)
        {
            throw new IOException("failed to deserialize xbis encoded document", e);
        }
    }

    public static Document documentFromByteArray(byte[] bytes)
        throws IOException
    {
        InputSource inputSource = new InputSource(new ByteArrayInputStream(bytes));
        XBISToSAXAdapter xbis2sax = new XBISToSAXAdapter();
        SAXContentHandler sax2dom4j = new SAXContentHandler();
        xbis2sax.setContentHandler(sax2dom4j);
        xbis2sax.setDTDHandler(sax2dom4j);
        xbis2sax.setEntityResolver(sax2dom4j);
        try
        {
            xbis2sax.parse(inputSource);
            return sax2dom4j.getDocument();
        }
        catch(SAXException e)
        {
            throw new IOException("failed to deserialize xbis encoded document", e);
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
    {
        // 1. serialize schemaURI
        out.writeObject(schemaURI);
        // 2. serialize document
        out.writeObject(documentAsByteArray(instanceDocument));
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
    {
        // 1. deserialize schemaURI
        schemaURI = (String)(in.readObject());
        // 2. deserialize document
        byte[] documentBytes = (byte[])(in.readObject());
        instanceDocument = documentFromByteArray(documentBytes);
    }
}
