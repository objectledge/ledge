package org.objectledge.forms.internal.ui.actions;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.ui.Reference;
import org.objectledge.forms.internal.ui.ReferenceNode;
import org.objectledge.forms.internal.ui.UI;
import org.xml.sax.Attributes;


/** Action that has an instance Reference.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseReferenceAction.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public abstract class BaseReferenceAction extends org.objectledge.forms.internal.ui.Action
implements ReferenceNode
{
    public BaseReferenceAction(String type, Attributes atts) throws ConstructionException
    {
        super(type, atts);
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are deep copied:</p>
     * <ul>
     *  <li>{@link #ref} - ref is also set a new container Node, see
     *  {@link Reference#clone()}</li>
     * </ul>
     */
    protected Object clone()
    {
        BaseReferenceAction next = (BaseReferenceAction)(super.clone());
        // References must be copied
        next.ref = (Reference)(ref.clone());
        // set a new container node
        next.ref.setContainerNode(next);
        return next;
    }

    //------------------------------------------------------------------------
    //attributes
    protected Reference ref;

    //------------------------------------------------------------------------
    // ReferenceNode methods

    public Reference getRef()
    {
        return ref;
    }

    //------------------------------------------------------------------------
    // methods used during building

    // Node methods

    /** Initialisation for action embeded reference. */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        ref.init(ui);
    }
}
