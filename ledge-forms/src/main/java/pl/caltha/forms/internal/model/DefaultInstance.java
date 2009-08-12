package pl.caltha.forms.internal.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.dom4j.Document;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.FormsException;
import pl.caltha.forms.internal.FormImpl;

/**
 * Contains default DOM4J instance document, produces Instance documents.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DefaultInstance.java,v 1.1 2005-01-19 06:55:35 pablo Exp $
 */
public class DefaultInstance extends InstanceImpl
{
    public DefaultInstance(FormImpl form, String schemaURI, Document instanceDocument)
    throws ConstructionException
    {
        super(form, schemaURI, instanceDocument);
    }

    /** Creates a new instance object. */
    public InstanceImpl createInstance(String instanceId)
    {
        InstanceImpl instance = new InstanceImpl(form, schemaURI, (Document)(instanceDocument).clone());

        instance.id = instanceId;

        return instance;
    }

    /** Creates an instance object from it's serialized state.
     *  It also connects a form to deserialized instance object.
     * @throws Exception thrown in following cases:
     * <ul>
     *    <li>on deserialization problems</li>
     *    <li>on <code>instance.formId == null</code> - it means that this
     *      field was not serialized (sic)</li>
     *    <li>and on <code>instance.formId</code> different than one
     *    from <code>DefaultInstance</code> object - it means that this instance should not be
     *    connected to this <code>Form</code> because it was created for a different
     *    form definition.</li>
     * </ul>
     */

    public InstanceImpl createInstance(String instanceId, byte[] savedState)
    throws Exception
    {
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(savedState);
        java.io.ObjectInputStream oin = new java.io.ObjectInputStream(in);
        InstanceImpl instance = (InstanceImpl)(oin.readObject());

        instance.id = instanceId;

        instance.form = this.form;
        if(instance.formId == null)
        {
            throw new FormsException("An instance is not connected to a Form definition.");
        }
        else if(!instance.formId.equals(this.form.getId()))
        {
            throw new FormsException("Cannot connect Instance to a different Form definition.");
        }

        return instance;
    }

    /**
     * Serializes an instance object
     * 
     * @param instance the Instance to be serialized
     * @return serialized instance state
     */
    public byte[] serializeInstance(InstanceImpl instance)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(instance);
        out.close();
        return baos.toByteArray();
    }
}
