package org.objectledge.forms.internal.ui.actions;

import java.util.List;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.ui.ActionEvent;
import org.objectledge.forms.internal.ui.ReferenceMultipleRepeat;
import org.objectledge.forms.internal.ui.ReferenceSingle;
import org.objectledge.forms.internal.ui.UI;
import org.xml.sax.Attributes;


/** Action that removes a repeat instance trees.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Delete.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public class Delete extends BaseInsertDeleteAction
{
    public Delete(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    //------------------------------------------------------------------------
    // Action methods

    /** This method performs the action, ie. removes a subtree from instance
     * document.
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        List contextNodes = ((ReferenceMultipleRepeat)(repeat.getRef())).getContextNodes(instance);
        Object result = ((ReferenceSingle)ref).evaluate(instance);

        if(result instanceof Number)
        {
            // clip index
            int index = clipIndex((Number)result, contextNodes);

            org.dom4j.Node contextNode = (org.dom4j.Node)(contextNodes.get(index));
            // remove node & set instacne dirty
            contextNode.detach();
            instance.setDirty(true);
            // flush repeat states
            repeat.flushChildrenState(instance);
        }
    }
}
