package pl.caltha.forms.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.FormImpl;
import pl.caltha.forms.internal.model.Bind;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UI.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class UI
{
    /** Keeps URI for UI definition.. */
    protected String definitionURI;
    /** Represents UI structure */
    private Node uiTree;
    /** Contains UI nodes with Id's. */
    private HashMap nodesById = new HashMap();
    /** Contains UI nodes keyed by their location in UI tree.
     * This is used to name HTML controls and to put incoming values
     * into Instance.
     */
    private HashMap nodesByUIPath = new HashMap();
    /** Contains UI nodes with model references. */
    private HashMap nodesByBindId = new HashMap();
    // /** Contains UI's components definitions. */
    // private HashMap componentDefinitions = new HashMap();

    /** Reference to contatining Form object. */
    private FormImpl form;

    /** Creates new UI */
    public UI(FormImpl parentForm, String definitionURI)
    {
        this.definitionURI = definitionURI;
        form = parentForm;
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder
    // phase 1 Initialization
    //
    void setUITree(Node root)
    {
        uiTree = root;
    }

    void addNodeById(Node node)
    throws ConstructionException
    {
        // check if there are already nodes with this id
        List nodes = (List)(nodesById.get(node.id));
        // if not - add node and return
        if(nodes == null)
        {
            Util.insertMultipleIntoHash(node.id, node, nodesById);
            return;
        }

        // if yes - check definitionUiPath
        Node firstNode = (Node)(nodes.get(0));

        if(!firstNode.definitionUiPath.equals(node.definitionUiPath))
        {
            throw new ConstructionException("Duplicate UI element id='"+node.id+"' for element (uiPath='"+node.uiPath
            +"' definitionUiPath='"+node.definitionUiPath+"')");
        }

        // definitionUiPath is ok - add node to nodesById
        Util.insertMultipleIntoHash(node.id, node, nodesById);
    }

    /**
     * Adds elements to a HashMap keyed by their location in UI tree.
     */
    void addNodeByUIPath(Node node)
    throws ConstructionException
    {
        if(nodesByUIPath.containsKey(node.uiPath))
        {
            // check for different objects under the same key)
            if(node != nodesByUIPath.get(node.uiPath))
            {
                throw new ConstructionException("Duplicate UI element uiPath for element (uiPath='"+node.uiPath
                +"' definitionUiPath='"+node.definitionUiPath+"')");
            }
        }
        else
        {
            nodesByUIPath.put(node.uiPath, node);
        }
    }

    void addNodeByBindId(String bindId, Node node)
    {
        // there cen be many nodes connected to one Bind
        Util.insertMultipleIntoHash(bindId, node, nodesByBindId);
    }

    /*
    boolean addComponentDefinition(String id, ComponentDefinition node)
    throws DuplicateComponentDefinitionException
    {
        if( componentDefinitions.containsKey(id) )
        {
            throw new DuplicateComponentDefinitionException("Id: \""+id+"\"");
        }
        return (componentDefinitions.put(id, node) == null);
    }*/

    //------------------------------------------------------------------------
    // Internal formtool UI API methods
    // phase 2 Processing (User Interaction)

    //--------------------------------
    // access methods for fields

    /** Returns a parent form for this UI. */
    public FormImpl getForm()
    {
        return form;
    }

    /** Returns a root node of UI structure. */
    public Node getUIRoot()
    {
        return uiTree;
    }

    /** Returns node(s) with a given ID. */
    public List getNodesById(String id)
    {
        return (List)(nodesById.get(id));
    }

    /** Returns node with a given UI path (similar to canonical XPath expression). */
    public Node getNodeByUIPath(String uiPath)
    {
        return (Node)(nodesByUIPath.get(uiPath));
    }

    /** Returns node(s) connected to a bind ({@link pl.caltha.forms.internal.model.Bind})
     * expression with a given ID. */
    public List getNodesByBindId(String bindId)
    {
        return (List)(nodesByBindId.get(bindId));
    }

    //--------------------------------
    // user interaction

    /** Sets values inputed by a user on a given instance. Values are taken
     * from current request's {@link net.labeo.webcore.RunData}.
     * This method is recursive, but traverses only the user visible part
     * of the tree - this avoids clearing unvisible select fields.
     */
    public void setValues(InstanceImpl instance, ParameterContainer parameters)
    {
        setValues(instance, parameters, ((NodeForm)uiTree).getPage(instance));
    }

    private void setValues(InstanceImpl instance, ParameterContainer parameters, Node node)
    {
        // check if a node can have a value and set it
        if(node instanceof NodeControl)
        {
            String[] values = parameters.getStrings(node.uiPath);
            String valueFlat = null;

            // WARN: this call is very important - it makes the instance dirty.
            StringBuffer value = instance.getValueBuffer();

            // Because there can be many values for one control name -
            // for instance for select fields (select and checkboxes)
            // - those values from values parameter
            // have to be concatenated in an XMLSchema list datatype manner
            // (values separated by spaces).
            if(node instanceof NodeControlSelect)
            {
                //clear the buffer
                value.setLength(0);
                int valLenMinOne = values.length-1; // this is for avoiding
                                                    // a space at the end
                // protect from empty value -
                // for instance unselected Select controls
                if(valLenMinOne > -1)
                {
                    for(int j = 0; j < valLenMinOne; j++)
                    {
                        value.append(values[j]);
                        value.append(' ');
                    }
                    value.append(values[valLenMinOne]);
                }
                //get value from buffer
                valueFlat = value.toString();
            }
            else if(values.length > 0)
            {
                valueFlat = values[0];
            }

            // protect from empty values
            if(valueFlat != null)
            {
                ((NodeControl)node).setValue(instance, valueFlat);
            }
        }

        // recurse down to the tree to look for ReferenceNode children
        List children = null;
        if(node instanceof NodeRepeat)
        {
            children = ((NodeRepeat)node).getChildren(instance);
        }
        else if(node instanceof NodeSwitch)
        {
            children = new ArrayList(1);
            children.add(((NodeSwitch)node).getCase(instance));
        }
        else
        {
            children = node.getChildren();
        }

        for(int i = 0, s = children.size(); i < s; i++)
        {
            setValues(instance, parameters, (Node)(children.get(i)));
        }
    }

    /** Dispatches events upon incoming parameters. Parameters are taken
     * from current request's {@link net.labeo.webcore.RunData}.
     */
    public void dispatchEvents(InstanceImpl instance, ParameterContainer parameters)
    {
        String[] strings = parameters.getStrings(UIConstants.DISPATCH_CONTROL_NAME);
        if(strings.length > 0)
        {
            String nodeName = strings[0];
            Node node = getNodeByUIPath(nodeName);
            if(node != null && node instanceof ActionNode)
            {
                // dispatch an activate event of a button control
                ((ActionNode)node).dispatchEvent(this, instance, new ActionEvent(ActionEvent.ACTIVATE, node));
            }
            else
            {
                instance.setSubmitted();
            }
        }
    }

    //--------------------------------
    // validation

    /** This method checks if a given instance has all values stated
     * as required in bind expressions (they are evaluated in this method).
     * Returns <code>true</code> if all required values are present in instance.
     */
    public boolean hasRequired(InstanceImpl instance)
    {
        return nodeHasRequired(instance, uiTree);
    }

    private final boolean nodeHasRequired(InstanceImpl inst, Node node)
    {
        // check if a node can have a bind element reference
        if(node instanceof ReferenceNode)
        {
            ReferenceNode refNode = (ReferenceNode)node;
            Bind bind = refNode.getRef().getBind();

            // fast check for default bind element
            if(bind != Bind.getDefault())
            {
                // if this node is not relevant - we don't have to check it for
                // required values
                if(bind.getRelevant(inst, refNode))
                {
                    // it is relevant - chek if it is required
                    if(bind.getRequired(inst, refNode))
                    {
                        // it is required

                        // first check if this node has its context Node
                        if(!refNode.getRef().hasContextNode(inst))
                        {
                            return false;
                        }

                        // then check if there is a required value
                        if(node instanceof Control)
                        {
                            if(!((Control)node).hasValue(inst))
                            {
                                // No required value found - no need to check it further
                                return false;
                            }
                        }
                    }
                }
            }
        }

        // recurse down to the tree to look for ReferenceNode children
        List children = null;
        if(node instanceof NodeRepeat)
        {
            children = ((NodeRepeat)node).getChildren(inst);
        }
        else
        {
            children = node.getChildren();
        }

        for(int i = 0, s = children.size(); i < s; i++)
        {
            if(!nodeHasRequired(inst, (Node)(children.get(i))))
            {
                // No required value found - no need to check it further
                return false;
            }
        }

        // required values were found or SetValue action node was found
        // - everything is ok
        return true;
    }
}
