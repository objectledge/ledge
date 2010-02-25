package org.objectledge.forms.internal.model;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.ui.Node;
import org.objectledge.forms.internal.ui.ReferenceNode;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/**
 * Form model element &amp; binding expression implementation.
 * Bind expressions are calculated relatively to connected ui node's
 * context node.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Bind.java,v 1.1 2005-01-19 06:55:35 pablo Exp $
 */
public class Bind
{
    /** XML ID of this bind element. */
    private String id;

    /** Compiled ref attribute of this bind element. */
    private InstanceReference instanceReference;
    /** Compiled readOnly attribute of this bind element. */
    private InstanceReference readOnlyReference;
    /** Compiled required attribute of this bind element. */
    private InstanceReference requiredReference;
    /** Compiled relevant attribute of this bind element. */
    private InstanceReference relevantReference;

    /** Bind object with default values. */
    private final static Bind defaultBind = new Bind();
    /** Default value of read only attribute. */
    private final static boolean DEFAULT_READ_ONLY = false;
    /** Default value of required attribute. */
    private final static boolean DEFAULT_REQUIRED = false;
    /** Default value of relevant attribute. */
    private final static boolean DEFAULT_RELEVANT = true;

    /** Constructor for a default bind element.
     * Default bind element is used for controls which do refer to bind
     * elements defined in main form definition file.
     * It has following values:
     * <ul>
     * <li><code>ref (instanceReference)</code> - <code>null</code></li>
     * <li><code>readOnly</code> - <code>false</code></li>
     * <li><code>required</code> - <code>false</code></li>
     * <li><code>relevant</code> - <code>true</code></li>
     * </ul>
     */
    private Bind()
    {
        instanceReference = null;
        readOnlyReference = null;
        requiredReference = null;
        relevantReference = null;
       /*this("",
        "",     // ?? empty "" or Root "/" ??
        "",     // or "0=1" WARNING: "false" in XPath evaluates to "true"
        "",     // or "0=1" WARNING: "false" in XPath evaluates to "true"
        ""      // or "1=1");
        */
    }

    /** Constructor used during form definition building.
     * @param atts Attributes of this bind element.
     * @throws ConstructionException Thrown on incorrect attribute values.
     */
    public Bind(Attributes atts)
    throws ConstructionException
    {
        this(
        Util.getSAXAttributeVal(atts, "id"),
        Util.getSAXAttributeVal(atts, "ref"),
        Util.getSAXAttributeVal(atts, "readOnly"),
        Util.getSAXAttributeVal(atts, "required"),
        Util.getSAXAttributeVal(atts, "relevant")
        );
    }

    /** Basic constructor.
     * @param id ID attribute
     * @param instanceRef ref attribute - this one is used as an instance
     * reference in controls which do not have their own
     * references and refer to this bind element.
     * @param readOnly readOnly attribute. If it has <code>true</code> value controls referring
     * to this bind element are displayed as read only.
     *
     * This is an XPath expression which should evaluate to
     * boolean value. It is not checked if this is the case. Please remember
     * that if this expression does not evaluate to boolean it will either
     * cause exceptions or will be evaluated to boolean in a way Java normally does.
     * @param required required attribute. If it has <code>true</code> value controls referring
     * to this bind element are treated as required ones - ie. they must
     * have non null values.
     *
     * This is an XPath expression which should evaluate to
     * boolean value. It is not checked if this is the case. Please remember
     * that if this expression does not evaluate to boolean it will either
     * cause exceptions or will be evaluated to boolean in a way Java normally does.
     * @param relevant relevant attribute. If it has <code>true</code> value controls referring
     * to this bind element are treated as visible and working ones, otherwise
     * they are not rendered.
     *
     * This is an XPath expression which should evaluate to
     * boolean value. It is not checked if this is the case. Please remember
     * that if this expression does not evaluate to boolean it will either
     * cause exceptions or will be evaluated to boolean in a way Java normally does.
     * @throws ConstructionException Thrown on invalid XPath expressions in attribute definitions.
     */
    private Bind(String id, String instanceRef, String readOnly, String required, String relevant)
    throws ConstructionException
    {
        if(id != null)
        {
            this.id = id;
        }
        else
        {
            throw new ConstructionException("Null id for bind element.");
        }

        this.instanceReference = makeInstanceReference("ref", instanceRef);
        this.readOnlyReference = makeInstanceReference("readOnly", readOnly);
        this.requiredReference = makeInstanceReference("required", required);
        this.relevantReference = makeInstanceReference("relevant", relevant);
    }

    /** Returns id of this bind element (expression).
     * @return XML ID of this bind element.
     */
    public String getId()
    {
        return id;
    }

    /** Object representing this bind element's ref attribute.
     * Bind element's ref attribute can be null.
     * @return this bind element's instance reference or null
     */
    public InstanceReference getInstanceReference()
    {
        return instanceReference;
    }

    /** Returns value of this bind element's read only expression for a given
     * instance.
     * @param instance Instance of form's user data.
     * @return value of this bind element's read only expression.
     */
    public boolean getReadOnly(InstanceImpl instance, ReferenceNode uiNode)
    {
        return evalProperty("readOnly", readOnlyReference, instance, uiNode, DEFAULT_READ_ONLY);
    }

    /** Returns value of this bind element's relevant expression for a given
     * instance.
     * @param instance Instance of form's user data.
     * @return value of this bind element's relevant expression.
     */
    public boolean getRelevant(InstanceImpl instance, ReferenceNode uiNode)
    {
        return evalProperty("relevant", relevantReference, instance, uiNode, DEFAULT_RELEVANT);
    }

    /** Returns value of this bind element's required expression for a given
     * instance.
     * @param instance Instance of form's user data.
     * @return value of this bind element's required expression.
     */
    public boolean getRequired(InstanceImpl instance, ReferenceNode uiNode)
    {
        return evalProperty("required", requiredReference, instance, uiNode, DEFAULT_REQUIRED);
    }

    /** Returns bind expression object with default values.
     * @return  default bind object
     */
    public static Bind getDefault()
    {
        return defaultBind;
    }

    /** This method evaluates XPath expressions and stores their results
     * in Instances bind property cache.
     * If XPath expression is <code>null</code> a given default value is
     * returned but not stored (it is not neccessary).
     */
    private boolean evalProperty(String refName, InstanceReference instRef, InstanceImpl instance,
                                 ReferenceNode uiNode, boolean defaultValue)
    {
        if(instRef != null)
        {
            if(instRef.isAbsolute())
            {
                Boolean propValue = (Boolean)(instance.getBindProperty(instRef));
                if(propValue == null)
                {
                    propValue = new Boolean( instRef.getStringValue(instance) );
                    instance.setBindProperty(instRef, propValue);
                }
                return propValue.booleanValue();
            }
            else
            {
                String key = refName+((Node)uiNode).getName();
                Boolean propValue = (Boolean)(instance.getBindProperty(key));
                if(propValue == null)
                {
                    Object context = uiNode.getRef().evaluate(instance);
                    propValue = new Boolean( instRef.getStringValue(context) );
                    instance.setBindProperty(key, propValue);
                }
                return propValue.booleanValue();
            }
        }
        else
        {
            return defaultValue;
        }
    }

    /** This utility method for constructor creates {@link InstanceReference}
     * objects from a given name and XPath expression.
     */
    private InstanceReference makeInstanceReference(String name, String xpath)
    throws ConstructionException
    {
        InstanceReference instRef = null;
        try
        {
            if(xpath != null)
            {
                instRef = new InstanceReference(name, xpath);
            }
        }
        catch(ConstructionException ce)
        {
            throw new ConstructionException("Bind element cannot not be constructed, wrong '"+name+"' attribute ", ce);
        }
        return instRef;
    }
}
