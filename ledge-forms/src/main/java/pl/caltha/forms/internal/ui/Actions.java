package pl.caltha.forms.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/** Action container.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Actions.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class Actions extends Node
{
    public Actions(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
    }

    private void addToActionsByEventType(Action child)
    {
        String event = child.getEventType();
        // TODO: do we have a default event
        if(event == null)
        {
            event = ActionEvent.ACTIVATE;
        }
        Util.insertMultipleIntoHash(event, child, actionsByEventType);
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Fields which are deep copied:</p>
     * <ul>
     *  <li>{@link #actionsByEventType} - some actions may have Instance references</li>
     * </ul>
     */
    protected Object clone()
    {
        // WARN: some actions have References so they have to be copied
        Actions next = (Actions)(super.clone());
        // Node.clone() copies the children collection, so we have to copy
        // only the references in actionsByEventType.
        next.actionsByEventType = new HashMap();
        for(int i = 0, s = next.children.size(); i < s; i++)
        {
            Action child = (Action)(next.children.get(i));
            next.addToActionsByEventType(child);
        }
        return next;
    }

    //------------------------------------------------------------------------
    // associations
    /** Map containing actions gropued by event type. */
    protected HashMap actionsByEventType = new HashMap();

    public void execute(UI ui, InstanceImpl instance, ActionEvent event)
    {
        ArrayList list = (ArrayList)(actionsByEventType.get(event.getType()));
        if(list != null)
        {
            for(int i = 0, s = list.size(); i < s; i++)
            {
                Action action = (Action)(list.get(i));
                action.execute(ui, instance, event);
            }
        }
    }

    //------------------------------------------------------------------------
    // overriden Node methods
   
    protected void addChild(Node child)
    throws ConstructionException
    {
        if(child instanceof Action)
        {
            // add to children - it will be used to generate uiPath
            super.addChild(child);

            // group actions by their event type
            addToActionsByEventType((Action)child);
        }
        else
        {
           throw new ConstructionException("Invalid child for Actions");
        }
    }

    protected void setParent(UI ui, Node parentNode)
    throws ConstructionException
    {
        // set parent for Actions node
        super.setParent(ui, parentNode);
        // set Parent for all the actions
        for(int i = 0, s = children.size(); i < s; i++)
        {
            // actions must have a parent node, because they
            // may have Instance References and such
            Action action = (Action)(children.get(i));
            action.setParent(ui, this);
        }
    }

    /** Used to init copied or built actions. */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        // init all of the actions
        for(int i = 0, s = children.size(); i < s; i++)
        {
            // actions must be initialized, because they
            // may have Instance References and such
            Action action = (Action)(children.get(i));
            action.init(ui);
        }
    }
}
