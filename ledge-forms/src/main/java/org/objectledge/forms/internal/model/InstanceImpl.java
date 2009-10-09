package org.objectledge.forms.internal.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Node;
import org.objectledge.forms.Instance;
import org.objectledge.forms.internal.FormImpl;
import org.objectledge.forms.internal.xml.validation.DOM4JValidationErrorCollector;
import org.objectledge.forms.internal.xml.validation.DOM4JValidator;


/**
 * Container for user form data.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: InstanceImpl.java,v 1.5 2008-07-24 17:07:12 rafal Exp $
 */
public class InstanceImpl extends AbstractInstance
    implements Instance, java.io.Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    //-----------------------------------------------------------------------
    // Parent form definition connection
    /** Reference to contatining Form object. */
    protected FormImpl form;

    /** ID of a form for which this instance was created. */
    protected String formId;

    //------------------------------------------------------------------------
    // Runtime instance state
    /** Instance Id produced from Form's name and screen name. */
    protected String id;

    /** Dirty flag. */
    private boolean isDirty = false;

    /** Submit requested flag, this one is set if a user clicked a submit button. */
    private boolean submitRequested = false;

    /** This map contains state values for dynamic UI elements. */
    private HashMap<Node, HashMap<Object, Object>> uiStateMap = new HashMap<Node, HashMap<Object, Object>>();

    /** Error collector used for validation. */
    private DOM4JValidationErrorCollector errorCollector = new DOM4JValidationErrorCollector();

    /** This field is <code>true</code> if this instance has all required values. */
    private boolean hasRequired = false;

    //------------------------------------------------------------------------
    // Runtime cache
    /** This map contains context nodes for UI elements - it decreases
     * number of XPath evaluations. */
    protected HashMap<Object, Object> contextNodeCache = new HashMap<Object, Object>();

    /** This map contains results for bind expression evaluation - it decreases
     * number of XPath evaluations. */
    protected HashMap<Object, Object> bindPropertyCache = new HashMap<Object, Object>();

    /** Value cache buffer is used for instance in Select controls. */
    private StringBuilder valueCacheBuffer = new StringBuilder(1024);

    /** Creates Instance from a given DOM4J Document and UI.
     */
    public InstanceImpl(FormImpl form, String schemaURI, Document instanceDocument)
    {
        super(schemaURI, instanceDocument);
        this.form = form;
        this.formId = form.getId();
    }

    //------------------------------------------------------------------------
    // Instance methods

    //-------------------------------------
    // HTML form indentification methods

    /** Getter for session wide instance id.
     * @return Value of instance id. */
    public String getId()
    {
        return id;
    }

    //-------------------------------------

    /** Returns <code>true</code> if instance has been changed by the user. */
    public boolean isDirty()
    {
        return isDirty;
    }

    /** Sets a dirty flag on instance object - for use when changing instance's
     * internal data - like dom4j document. */
    public void setDirty(boolean dirtyFlag)
    {
        isDirty = dirtyFlag;
    }

    /** Returns <code>true</code> if instance is valid and the user requested
     * a submit operation on this instance. */
    public boolean isSubmitted()
    {
        return submitRequested && isValid();
    }

    /** Return <code>true</code> if instance has valid values and all
     * of required values are inputed. */
    public boolean isValid()
    {
        return hasRequired && (errorCollector.getErrorsByNode().size() == 0);
    }

    /** Return <code>true</code> if instance has all required values. */
    public boolean hasRequired()
    {
        return hasRequired;
    }

	//------------------------------------------------------------------------
    // Methods used during Instance processing
    public FormImpl getForm()
    {
        return form;
    }

    /** Sets <code>submitted</code> flag. */
    public void setSubmitted()
    {
        this.submitRequested = true;
    }

    public boolean validate(DOM4JValidator validator)
    throws Exception
    {
        return validator.validate(instanceDocument, schemaURI, errorCollector);
    }

    /** Cleans a bind property cache. It is used when setting new values in
     * an Instance. */
    public void preprocessInit()
    {
        errorCollector.init();
        submitRequested = false;
        bindPropertyCache.clear();
    }

    public void setHasRequired(boolean hasRequired)
    {
        this.hasRequired = hasRequired;
    }

    public boolean hasError(Node contextNode)
    {
        return errorCollector.getErrorsByNode().containsKey(contextNode);
    }

    /**
     * Set error flag for a Node explicitly.
     * <p>
     * This is necessary that nodes that have validation process different that XML schema (most
     * notably NodeControlHTML) can bring their validation problem to the attention of form instance
     * and further the controller components. At this point it's implemented as a hack - exposed
     * internal state of errorCollector object is modified directly.
     * </p>
     */
    public void setError(Node contextNode, Object error)
    {
        errorCollector.getErrorsByNode().put(contextNode, error);
    }

    //------------------------------------------------------------------------
    // State Values access
    /** Sets a UI state value for this instance. */
    public void setStateValue(Node contextNode, Object key, Object value)
    {
        HashMap<Object, Object> stateMap = uiStateMap.get(contextNode);
        if(stateMap == null)
        {
            stateMap = new HashMap<Object, Object>();
        }
        stateMap.put(key, value);
        uiStateMap.put(contextNode, stateMap);
    }

    /** Gets a UI state value for this instance. */
    public Object getStateValue(Node contextNode, Object valueKey)
    {
        HashMap<Object, Object> stateMap = uiStateMap.get(contextNode);
        if(stateMap != null)
        {
            return stateMap.get(valueKey);
        }
        return null;
    }

    //------------------------------------------------------------------------
    // Context Nodes cache access
    public void setContextNode(Object key, Object contextNode)
    {
        contextNodeCache.put(key, contextNode);
    }

    public void clearContextNode(Object key)
    {
        if(contextNodeCache.containsKey(key))
        {
            contextNodeCache.put(key, null);
        }
    }

    public Object getContextNode(Object key)
    {
        return contextNodeCache.get(key);
    }

    //------------------------------------------------------------------------
    // Bind property cache methods
    /** Sets a value of a Bind property. */
    public void setBindProperty(Object key, Object propValue)
    {
        bindPropertyCache.put(key, propValue);
    }

    /** Gets a cached value of a Bind property. */
    public Object getBindProperty(Object key)
    {
        return bindPropertyCache.get(key);
    }

    //------------------------------------------------------------------------
    // Value cache buffer access
    /** This method is used while setting the value for this instance, it marks
     * the instance dirty. */
    public StringBuilder getValueBuffer()
    {
        this.isDirty = true;

        return valueCacheBuffer;
    }

    //------------------------------------------------------------------------
    // Serialization methods
    private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
    {
        // 1. serialize formId
        out.writeObject(formId);
        // 2. serialize UI state
        HashMap<String, HashMap<Object, Object>> xPathKeyedUIStateMap = new HashMap<String, HashMap<Object, Object>>();
        for(Iterator<Node> iter = uiStateMap.keySet().iterator(); iter.hasNext();)
        {
            Node contextNode = iter.next();
            xPathKeyedUIStateMap.put(contextNode.getUniquePath(), uiStateMap.get(contextNode));
        }
        out.writeObject(xPathKeyedUIStateMap);
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
    {
        // 1. deserialize formId
        this.formId = (String)(in.readObject());
        // 2. deserialize UI state
        HashMap<String, HashMap<Object, Object>> xPathKeyedUIStateMap = (HashMap<String, HashMap<Object, Object>>)(in.readObject());
        for(Iterator<String> iter = xPathKeyedUIStateMap.keySet().iterator(); iter.hasNext();)
        {
            String uniqueXPath = iter.next();
            Node contextNode = instanceDocument.selectSingleNode(uniqueXPath);
            uiStateMap.put(contextNode, xPathKeyedUIStateMap.get(uniqueXPath));
        }
    }
}
