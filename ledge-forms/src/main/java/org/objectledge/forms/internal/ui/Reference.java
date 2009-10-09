package org.objectledge.forms.internal.ui;

import org.objectledge.forms.ConstructionException;
import org.objectledge.forms.internal.model.Bind;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.model.InstanceReference;
import org.objectledge.forms.internal.util.Util;
import org.xml.sax.Attributes;


/** Implements a node with reference to bind element (model) and/or instance
 * part. Covers Bind All Attributes and  Bind First Attributes from XForms
 * specification.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Reference.java,v 1.2 2005-02-08 20:33:30 rafal Exp $
 */
public abstract class Reference
implements Cloneable
{
    /** In a derived class constructor must construct InstanceReference and
     * assign it to {@link #instanceReference}.
     */
    public Reference(Attributes atts, Node containerNode, String instRefName)
    throws ConstructionException
    {
        this.containerNode = containerNode;
        this.instRefName = instRefName;

        this.instRefExpr = Util.getSAXAttributeVal(atts, instRefName);

        // Store bindId - it will be used in connectBind after parent is set
        // and parentReferenceNode is found
        bindId = org.objectledge.forms.internal.util.Util.getSAXAttributeVal(atts, "bind");
    }

    /** Used when copying parts of UI tree for repeat nodes processing.
     * <p>Because of security reasons {@link #containerNode} for
     * a copied object is nulled - see {@link #setContainerNode(Node)}.
     */
    public Object clone()
    {
        Reference next = null;
        try
        {
            next = (Reference)(super.clone());
        }
        catch(CloneNotSupportedException e)
        {
            // this should never happen
        }
        next.containerNode = null;
        next.parentReferenceNode = null;
        return next;
    }

    /** Used after cloning a Reference object, sets a new containerNode,
     * but if it was set (ie. it's not <code>null</code>)
     * it's not changed - see {@link #clone()}.
     */
    public void setContainerNode(Node containerNode)
    {
        if(this.containerNode == null)
        {
            this.containerNode = containerNode;
        }
    }

    public void init(UI ui)
    throws ConstructionException
    {
        // find parentReferenceNode
        parentReferenceNode = getReferenceNodeParent();
        // connect bind element it also stores containerNode
        // in ui.nodesByBindId
        bind = connectBind(ui);

        // create or connect instanceReference
        if(instanceReference == null)
        {
            instanceReference = createInstanceReference(instRefName, instRefExpr);
        }
    }

    /** Returns a nearest parent reference node, which is used to
     * inherit bind elements and instance references.
     * @return Nearest parent reference node for this ReferenceNode or null.
     */
    private final ReferenceNode getReferenceNodeParent()
    {
        Node node = containerNode.getParent();
        while(node != null && !(node instanceof ReferenceNode))
        {
            node = node.getParent();
        }
        return (ReferenceNode)node;
    }

    /** This method returns a Bind element which is defined for this ReferenceNode.
     * first it tries to get a Bind element upon its IDREF - if <code>bind</code>
     * attribute is defined, if not it tries to get a bind element from
     * {@link #parentReferenceNode}. If there is no bind element for parent node
     * it gets a default one.
     * @param ui UI instance to connect to.
     * @throws ConstructionException Thrown on wrong bind IDREF.
     * @return Found Bind element.
     */
    private Bind connectBind(UI ui)
    throws ConstructionException
    {
        // bind - Bind
        Bind bindElt = null;

        // get bind from bind attribute
        if(bindId != null)
        {
            try
            {
                // Bind reference defined
                ui.addNodeByBindId(bindId, containerNode);
                bindElt = ui.getForm().getBind(bindId);
            }
            catch(ConstructionException e)
            {
                throw new ConstructionException("Bad bind element reference ('bind' attribute) for node '"+containerNode.getName()+"'", e);
            }
        }
        // inherit bind from parentReferenceNode
        else if(parentReferenceNode != null)
        {
            bindElt = parentReferenceNode.getRef().getBind();
        }
        else
        // no bind element - return default one.
        {
            bindElt = Bind.getDefault();
        }
        return bindElt;
    }

    /** Returns an instance reference for this node. First it tries to construct one
     * from <code>nodeset</code> or <code>ref</code> attribute. If no attribute
     * is defined, it checks if bind element has a defined InstanceReference.
     * If not it tries to get one from {@link #parentReferenceNode}.
     *
     * <p><b>WARNING!</b> It may return <code>null</code> - it mean's it fails
     * silently. Undefined instanceReference will be discovered on
     * getContextNode call.</p>
     *
     * <p><b>WARNING!</b> Beahviour of this method and whole class implementation
     * creates a danger of problems with badly written UI definition. Especially
     * with badly written or forgotten InstanceReferences. Examples of
     * suspicious definitions:</p>
     *
     * <ol>
     * <li>A control has a relative InstanceReference, as well none of it's
     * parents has one.</li>
     * <li>A few controls does not have an InstanceReference, this way they
     * all point to a root node in instance.</li>
     * </ol>
     * @param name Name of an attribute defining th InstanceReference. It is used to
     * give a more descriptive error messages.
     * @param instanceRef XPath expression deifining the InstanceReference. It is a value of
     * a defining attribute.
     * @throws ConstructionException Thrown when XPath expression is invalid.
     * @return Defined or found InstanceReference.
     */
    protected InstanceReference createInstanceReference(String name, String instanceRef)
    throws ConstructionException
    {
        // Bind All Attributes
        // Bind First Attributes
        // ref - InstanceReference
        // nodeset - InstanceReference for Grouping controls
        InstanceReference instRef = null;

        // 1. create InstanceReference from ref or nodeset attribute
        if(instanceRef != null)
        {
            try
            {
                // InstanceImpl reference defined
                //ui.addElementByInstanceRef(instanceRef, this);
                instRef = new InstanceReference(name, instanceRef);
            }
            catch(ConstructionException e)
            {
                throw new ConstructionException("Bad instance reference '"+name+"' for element '"+containerNode.getName()+"'", e);
            }
        }
        // 2. Inherit Bind expression reference.
        // WARN: bindEl is never null
        else if(bind.getInstanceReference() != null)
        {
            instRef = bind.getInstanceReference();
        }

        return instRef;
    }

    //-----------------------------------------------------------------------
    //attributes
    /** Contains value of <code>bind</code> attribute. It is an XML
     * IDREF pointing to a binding expression defined in form's
     * <code>binding</code> section.
     */
    protected String bindId;
    /** Contains name of <code>ref</code>, <code>nodeset</code> or custom
     * XPath reference attribute.
     */
    protected String instRefName;
    /** Contains value of <code>ref</code> or <code>nodeset</code> attribute.
     * It is an XPath expression pointing to a node or set of nodes in form
     * data instance.
     */
    protected String instRefExpr;

    //-----------------------------------------------------------------------
    // associations
    /** Reference to Bind object defined by value of <code>bind</code> attribute.
     */
    protected Bind bind;
    /** Contains value of <code>ref</code> or <code>nodeset</code> attribute.
     * It is an XPath expression defining a reference to interface node(s).
     */
    protected InstanceReference instanceReference;
    /** Nearest parent reference node {@link #getReferenceNodeParent()}, which is used to
     * inherit bind elements and instance references.
     */
    protected ReferenceNode parentReferenceNode;
    /** Node to which this reference is bound. */
    protected Node containerNode;

    //------------------------------------------------------------------------
    // Reference methods
    //
    public boolean hasContextNode(InstanceImpl instance)
    {
        Object contextNode = getContextNodeInternal(instance);
        return (contextNode != null);
    }

    public String getValue(org.dom4j.Node contextNode)
    {
        // WARN: Ugly hack here - dom4j throws NullPointerException when
        // there is no text inside contextNode or it returns "" or null.
        try
        {
            String text = contextNode.getText();
            if(text == "")
            {
                return null;
            }
            return text;
        }
        catch(NullPointerException e)
        {
            return null;
        }
    }

    public void setValue(org.dom4j.Node contextNode, String value)
    {
        contextNode.setText(value);
    }

    /** Returns a bind element for this ReferenceNode.
     * @return The Bind element.
     */
    public Bind getBind()
    {
        return bind;
    }

    /** Removes contexNode(s) cached for this ReferenceNode from Instance.
     * @param instance InstanceImpl in which and for which context nodes are cached.
     */
    public void clearContextNodeCache(InstanceImpl instance)
    {
        clearContextNodeCache(instance, containerNode);
    }

    private void clearContextNodeCache(InstanceImpl instance, Node uiNode)
    {
        clearContextNodeCache(instance, uiNode.children.iterator());

        // special case for nodes containing actions, which may have
        // ref objects too.
        if(uiNode instanceof NodeCaption)
        {
            clearContextNodeCache(instance, ((NodeCaption)uiNode).actions.children.iterator());
        }

        instance.clearContextNode(uiNode.uiPath);
    }

    private void clearContextNodeCache(InstanceImpl instance, java.util.Iterator<Node> iter)
    {
        while(iter.hasNext())
        {
            clearContextNodeCache(instance, iter.next());
        }
    }

    /** Returns a context node or list of context nodes for this ReferenceNode.
     * First tries to retrieve the node from Instance.contextNodeCache.
     * Then gets context node for this InstanceReference from
     * parent or takes a root node (for absolute InstanceReference).
     * After that evaluates InstanceReference and caches results in
     * Instance.contextNodeCache.
     * @param instance InstanceImpl in which context node(s) will be stored,
     * and from which context node(s) will be retrieved.
     * @return Context node or a list of context nodes.
     */
    protected Object getContextNodeInternal(InstanceImpl instance)
    {
        // 1. Check for context node in context node cache.
        Object localContextNode = instance.getContextNode(containerNode.uiPath);
        if(localContextNode != null)
        {
            return localContextNode;
        }
        // 2. Context node cache is empty - get parent context node
        org.dom4j.Node parentContextNode = getParentContextNode(instance);

        // 2.4. Evaluate instanceReference if there is one
        if(instanceReference != null)
        {
            localContextNode = evaluateInstanceReference(parentContextNode);
        }
        else
        {
            localContextNode = parentContextNode;
        }

        //error on null contextNode
        if(localContextNode == null)
        {
            throw new RuntimeException("'null' instance context node for Node "+containerNode.getName()+" check Your XPath references");
        }

        // 5. Store context node in UI context node cache.
        instance.setContextNode(containerNode.getName(), localContextNode);

        return localContextNode;
    }

    /** Returns a context node which is a context node for this ReferenceNode's
     * InstanceReference (context node for XPath expression).
     * @param instance InstanceImpl in which context node(s) will be stored,
     * and from which context node(s) will be retrieved.
     * @return Context node for XPath expression.
     */
    public org.dom4j.Node getParentContextNode(InstanceImpl instance)
    {
        // 1. get context node for this InstanceReference from parent
        org.dom4j.Node contextNode = null;
        // 2.1. Check instanceReference for being absolute.
        // Absolute => context node == root instance element
        if(instanceReference != null && instanceReference.isAbsolute())
        {
            contextNode = instance.getDocument().getRootElement();
        }
        else
        if(parentReferenceNode != null)
        // 2.2. Get Node from one of parent nodes.
        {
            contextNode = parentReferenceNode.getRef().getParentContextNodeForChild(instance, containerNode);
        }
        else
        // 2.3. Has no parent and a non absolute instance reference,
        // but it doesn't have to be => Root element is a context node.
        // FIXME: Maybe we should throw an exception!!!
        {
            contextNode = instance.getDocument().getRootElement();
        }

        return contextNode;
    }

    /** <b>WARNING</b> This method is created only to be overriden by
     * {@link ReferenceMultiple} nad {@link ReferenceMultipleRepeat}
     * to return a parent context node for one of it's calling children.
     *
     * @param instance InstanceImpl in which context node(s) will be stored,
     * and from which context node(s) will be retrieved.
     * @param child Child UI Node which called this method.
     * @return Parent context node for a calling child.
     */
    protected org.dom4j.Node getParentContextNodeForChild(InstanceImpl instance, Node child)
    {
        return (org.dom4j.Node)getContextNodeInternal(instance);
    }

    /** This abstract method is to be defined in ReferenceNode implementations.
     * Basically it will evaluate the InstanceReference (XPath expression) for
     * this ReferenceNode.
     * It will return {@link java.util.List} of context nodes in
     * MultipleReferenceVisibleNode, and a single context node {@link org.dom4j.Node}
     * in SingleReferenceVisibleNode.
     *
     * @param contextNode Context node for XPath expression to be evaluated.
     * @return Context node or a list of context nodes.
     */
    protected abstract Object evaluateInstanceReference(org.dom4j.Node contextNode);

    /** This method is for raw acces to Reference's evaluation result.
     */
    public Object evaluate(InstanceImpl instance)
    {
        return instanceReference.getValue(getParentContextNode(instance));
    }
}

