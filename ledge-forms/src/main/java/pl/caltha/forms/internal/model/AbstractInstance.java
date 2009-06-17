package pl.caltha.forms.internal.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dom4j.Document;

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
        com.sosnoski.xmls.XmlsOutput sout = new com.sosnoski.xmls.Dom4jOutput();
        sout.writeDocument(document, out);
        return out.toByteArray();
    }

    public static Document documentFromByteArray(byte[] bytes)
    throws IOException
    {
        com.sosnoski.xmls.XmlsInput sin = new com.sosnoski.xmls.Dom4jInput();
        ByteArrayInputStream dis = new ByteArrayInputStream(bytes);
        Document document = (Document)(sin.readDocument(dis));
        //("Exception reading serialized form at byte " + (bytes.length-dis.available()-sin.getBytesRemaining()-1));
        return document;
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
