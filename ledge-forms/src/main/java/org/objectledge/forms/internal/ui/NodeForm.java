package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.model.SubmitInfo;
import org.xml.sax.Attributes;


/**
 * Represents ...
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeForm.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeForm extends NodeSelectableContainer
{
    public NodeForm(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        ref = new ReferenceSingleForm(atts, this);
    }

    /** UI containing this form node. */
    private UI ui;
    /** Connceted submit info. */
    private SubmitInfo submitInfo;

    //------------------------------------------------------------------------
    // Form methods
    //
    /** Returns actual page, depending on Instance. */
    public NodeSelectable getPage(InstanceImpl instance)
    {
        return getCurrentChild(instance);
    }

    //------------------------------------------------------------------------
    // XForms properties
    public String getMethod()
    {
        return submitInfo.getMethod();
    }

    public String getEnctype()
    {
        return submitInfo.getEncType();
    }

    public String getAcceptCharset()
    {
        return submitInfo.getEncoding();
    }

    public String getAcceptMIME()
    {
        return submitInfo.getMediaType();
    }

    //------------------------------------------------------------------------
    // HTML properties

    public String getOnSubmit()
    {
        // TODO: not implemented
        return null;
    }

    public String getOnReset()
    {
        // TODO: not implemented
        return null;
    }
    //------------------------------------------------------------------------
    // methods used during building
    //

    /** Inits a Node.
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        this.ui = ui;
        submitInfo = ui.getForm().getSubmitInfo();
    }
}

