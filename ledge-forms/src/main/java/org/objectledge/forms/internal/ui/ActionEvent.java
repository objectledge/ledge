package org.objectledge.forms.internal.ui;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: ActionEvent.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class ActionEvent
{
    /* Event types. */
    // targeted at all controls (in FormTool - only buttons ans submits)
    public static String ACTIVATE = "activate";
    // targeted at all controls
    public static String VALUE_CHANGED = "valueChanged";

    // targeted at repeat elements
    public static String SCROLL_FIRST = "scrollFirst";
    public static String SCROLL_LAST = "scrollLast";

    // targeted at instance (?)
    public static String INSERT = "insert";
    public static String DELETE = "delete";

    //targeted at model
    public static String REVALIDATE = "revalidate";
    public static String RECALCULATE = "recalculate";

    private String type;
    private Node target;

    public ActionEvent(String type, Node target)
    {
        this.type = type;
        this.target = target;
    }

    public String getType()
    {
        return type;
    }

    public Node getTarget()
    {
        return target;
    }
}

