package pl.caltha.forms.internal.ui;

import java.util.HashMap;

/**
 *
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UIConstants.java,v 1.4 2006-04-28 10:02:23 pablo Exp $
 */
public class UIConstants
{
    //------------------------------------------------------------------------
    // special HTML form field names

    /** Key for instance.id parameter. */
    public static final String INSTANCE_ID_NAME = "formtool.instance.id";

    /** Key for name of a control which will dispatch an event. */
    public static final String DISPATCH_CONTROL_NAME = "formtool.dispatch.control";

    /** Getter for {@link #INSTANCE_ID_NAME} constant}. */
    public String getInstanceIdName()
    {
        return INSTANCE_ID_NAME;
    }
    /** Getter for {@link #DISPATCH_CONTROL_NAME} constant}. */
    public String getDispatchControlName()
    {
        return DISPATCH_CONTROL_NAME;
    }

    //------------------------------------------------------------------------
    // elements names
    public static final String ALERT = "alert";
    public static final String CAPTION = "caption";
    public static final String HELP = "help";
    public static final String HINT = "hint";

    public static final String ACTIONS = "actions";
    //public static final String ACTION = "action";
    public static final String NOOP = "noop";
    public static final String INSERT = "insert";
    public static final String DELETE = "delete";
    //public static final String SCROLL = "scroll";
    public static final String TOGGLE = "toggle";
    public static final String SETVALUE = "setValue";
    public static final String DISPATCH = "dispatch";

    public static final String FORM = "form";
    public static final String PAGE = "page";

    public static final String DEFCOMPONENT = "defComponent";
    public static final String COMPONENT = "component";

    public static final String REPEAT = "repeat";
    public static final String REPEATSUBTREE = "repeatSubTree";

    public static final String GROUP = "group";

    public static final String SWITCH = "switch";
    public static final String CASE = "case";

    public static final String BUTTON = "button";
    public static final String SUBMIT = "submit";

    public static final String HIDDEN = "hidden";

    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String SECRET = "secret";
    public static final String TEXTAREA = "textarea";
    public static final String HTMLAREA = "htmlarea";
	public static final String DATE = "date";

    public static final String RANGE = "range";

    public static final String UPLOAD = "upload";

    public static final String SELECTMANY = "selectMany";
    public static final String SELECTONE = "selectOne";
    public static final String DYNAMICSELECTONE = "dynamicSelectOne";
    public static final String CHOICES = "choices";
    public static final String ITEM = "item";
    public static final String ITEMREF = "itemref";

    static HashMap elementNames = new HashMap();
    static  HashMap classNames = new HashMap();
    static HashMap classes = new HashMap();
    static
    {
        String basePackage = "pl.caltha.forms.internal.ui.";

        classNames.put(ALERT,     "VisibleText");
        classNames.put(CAPTION,   "VisibleText");
        classNames.put(HELP,      "VisibleText");
        classNames.put(HINT,      "VisibleText");

        //classNames.put(ACTIONS,    "Actions");
        //classNames.put(ACTION,    "Action");
        classNames.put(NOOP,    "actions.NoOp");
        classNames.put(INSERT,    "actions.Insert");
        classNames.put(DELETE,    "actions.Delete");
        //classNames.put(SCROLL,    "Action");
        classNames.put(TOGGLE,    "actions.Toggle");
        classNames.put(SETVALUE,    "actions.SetValue");
        classNames.put(DISPATCH,    "actions.Dispatch");

        classNames.put(FORM,      "NodeForm");
        classNames.put(PAGE,      "NodeSelectable");

        //classNames.put(DEFCOMPONENT,  "ComponentDefinition");
        //classNames.put(COMPONENT,     "Component");

        classNames.put(REPEAT,          "NodeRepeat");
        classNames.put(REPEATSUBTREE,   "NodeRepeatSubTree");

        classNames.put(GROUP,     "NodeCaptionReference");

        classNames.put(SWITCH,    "NodeSwitch");
        classNames.put(CASE,      "NodeSelectable");

        classNames.put(BUTTON,    "NodeButton");
        classNames.put(SUBMIT,    "NodeButton");

        classNames.put(HIDDEN,    "NodeControl");

        classNames.put(INPUT,     "NodeControlFormatted");
        classNames.put(OUTPUT,    "NodeControlFormatted");
        classNames.put(SECRET,    "NodeControlFormatted");
        classNames.put(TEXTAREA,  "NodeControl");
        classNames.put(HTMLAREA,  "NodeControlHTML");
		classNames.put(DATE,  "NodeControlDate");

        classNames.put(RANGE,     "NodeControlRange");

        classNames.put(UPLOAD,    "NodeControlUpload");

        classNames.put(SELECTMANY,    "NodeControlSelect");
        classNames.put(SELECTONE,     "NodeControlSelect");
        classNames.put(DYNAMICSELECTONE,     "NodeControlSelect");
        classNames.put(CHOICES,       "NodeCaption");
        classNames.put(ITEM,          "NodeControlSelectItem");
        classNames.put(ITEMREF,       "NodeControlSelectItemRef");

        //-------------------------------------------------------
        for(java.util.Iterator iter = classNames.keySet().iterator(); iter.hasNext();)
        {
            Object key = iter.next();
            classes.put(key, basePackage+((String)classNames.get(key)));
            elementNames.put(key, key);
        }
    }

    private UIConstants()
    {
        // static factory pattern
    }

    public static UIConstants getInstance()
    {
        return uiConstants;
    }

    private static UIConstants uiConstants = new UIConstants();


    //public static final String (.)([^ ]*) = ".([^"]*)";
    //public String get$1$3() { return $1$2; }

    /*public String getAlert() { return ALERT; }
    public String getCaption() { return CAPTION; }
    public String getHelp() { return HELP; }
    public String getHint() { return HINT; }

    public String getAction() { return ACTION; }
    public String getInsert() { return INSERT; }
    public String getDelete() { return DELETE; }
    public String getScroll() { return SCROLL; }
    public String getToggle() { return TOGGLE; }
    */
    public String getForm() { return FORM; }
    public String getPage() { return PAGE; }

    public String getDefComponent() { return DEFCOMPONENT; }
    public String getComponent() { return COMPONENT; }

    public String getRepeat() { return REPEAT; }
    public String getRepeatSubTree() { return REPEATSUBTREE; }

    public String getGroup() { return GROUP; }

    public String getSwitch() { return SWITCH; }
    public String getCase() { return CASE; }

    public String getButton() { return BUTTON; }
    public String getSubmit() { return SUBMIT; }

    public String getHidden() { return HIDDEN; }

    public String getInput() { return INPUT; }
    public String getOutput() { return OUTPUT; }
    public String getSecret() { return SECRET; }
    public String getTextarea() { return TEXTAREA; }
    public String getHtmlarea() { return HTMLAREA; }
	public String getDate() { return DATE; }

    public String getRange() { return RANGE; }

    public String getUpload() { return UPLOAD; }

    public String getSelectMany() { return SELECTMANY; }
    public String getSelectOne() { return SELECTONE; }
    public String getChoices() { return CHOICES; }
    public String getItem() { return ITEM; }
    public String getItemref() { return ITEMREF; }
}

