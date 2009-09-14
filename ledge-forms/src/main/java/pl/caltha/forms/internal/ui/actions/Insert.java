package pl.caltha.forms.internal.ui.actions;

import java.util.List;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.ActionEvent;
import pl.caltha.forms.internal.ui.ReferenceMultipleRepeat;
import pl.caltha.forms.internal.ui.ReferenceSingle;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.util.Util;

/** Action that adds repeat instance trees.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Insert.java,v 1.1 2005-01-19 06:55:32 pablo Exp $
 */
public class Insert extends BaseInsertDeleteAction
{
    public Insert(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // this field is required so it won't be null
        before = "before".equals(Util.getSAXAttributeVal(atts, "position"));
    }

    private boolean before;

    //------------------------------------------------------------------------
    // Action methods

    /** This method performs the action, ie. adds a subtree to instance
     * document.
     */
    public void execute(UI ui, InstanceImpl instance, ActionEvent evt)
    {
        // get repeat's context nodes
        org.dom4j.Element parentContextNode = (org.dom4j.Element)(((ReferenceMultipleRepeat)(repeat.getRef())).getParentContextNode(instance));
        List contextNodes = ((ReferenceMultipleRepeat)(repeat.getRef())).getContextNodes(instance);
        // evaluate the index on which an insertion will be performed
        Object at = ((ReferenceSingle)ref).evaluate(instance);

        if(at instanceof Number)
        {
            // clip insertion index
            int index = clipIndex((Number)at, contextNodes);
            // real index points to a real location in parentContextNode content collection
            int realIndex = 0;

            // content list for parentContextNode
            // WARN: We only insert Elements
            List elements = ((org.dom4j.Element)parentContextNode).elements();

            // set real index for a not empty node collection
            if(contextNodes.size() > 0)
            {
                org.dom4j.Node contextNode = (org.dom4j.Node)(contextNodes.get(index));
                realIndex = elements.indexOf(contextNode);
            }

            // perform action
            List defaultNodes = ((ReferenceMultipleRepeat)(repeat.getRef())).getContextNodes(ui.getForm().getDefaultInstance());
            org.dom4j.Node insertNode = (org.dom4j.Node)(defaultNodes.get(defaultNodes.size()-1));
            insertNode = (org.dom4j.Node)(insertNode.clone());
            insertNode.detach();

            if(!before)
            {
                realIndex++;
            }

            // WARN: workaround for bad backedList implementation in dom4j
            // this is spupposed to work in dom4j 1.4
            if(elements.size() > 0)
            {
                elements.add(realIndex, insertNode);
            }
            else
            {
                elements.add(insertNode);
            }
            // set instance dirty
            instance.setDirty(true);

            // flush repeat states
            repeat.flushChildrenState(instance);
        }
    }
}
