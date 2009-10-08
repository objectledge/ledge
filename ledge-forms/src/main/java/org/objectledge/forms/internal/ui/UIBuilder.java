package org.objectledge.forms.internal.ui;

import java.util.Iterator;
import java.util.Stack;

import org.objectledge.forms.ConstructionException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Builds form-tool user interfaces upon SAX events.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UIBuilder.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class UIBuilder extends org.objectledge.forms.internal.util.AbstractBuilder
{
     /** UI object that is being built. */
    private UI buildUI;
    /** Node of a {@link #buildUI}. */
    private Node currentNode;
    /** Stack for Node tree processing. */
    private Stack nodeStack;

    public UIBuilder(String acceptedNamespace,
                     String definitionSchemaURI)
    {
        super(acceptedNamespace, definitionSchemaURI);
        nodeStack = new Stack();
    }

    public void startBuild(Object builtObject)
    throws ConstructionException
    {
        buildUI = (UI)builtObject;
        // null current node and buildUI so UIBuilder can be reused
        currentNode = null;
    }

    public void endBuild(Object builtObject)
    throws ConstructionException
    {
        // 1. attach UI tree to build UI
        buildUI.setUITree(currentNode);
        // 2. Set parents - this creates definitionUIPaths
        setParents(buildUI, null, currentNode);
        // 3. Clone RepeatSubTrees
        cloneRepeatSubTrees(buildUI, currentNode);
        // 4. Set parents - this creates uiPaths for cloned subtrees
        //    and also connects throws repeat sub trees, and
        //    indexes nodes in subtrees
        setParents(buildUI, null, currentNode);
        // 5. Init References
        initNodes(buildUI, currentNode);
    }

    private void setParents(UI ui, Node parent, Node node)
    throws ConstructionException
    {
        node.setParent(ui, parent);
        for(Iterator iter = node.getChildren().iterator(); iter.hasNext();)
        {
            Node child = (Node)(iter.next());
            setParents(ui, node, child);
        }
    }

    private void cloneRepeatSubTrees(UI ui, Node node)
    throws ConstructionException
    {
        for(Iterator iter = node.getChildren().iterator(); iter.hasNext();)
        {
            Node child = (Node)(iter.next());
            cloneRepeatSubTrees(ui, child);
        }
        // on coming back from element - this way sub repeats are copied first
        if(node instanceof NodeRepeat)
        {
            ((NodeRepeat)node).cloneRepeatSubTree(ui);
        }
    }

    private void initNodes(UI ui, Node node)
    throws ConstructionException
    {
        node.init(ui);
        for(Iterator iter = node.getChildren().iterator(); iter.hasNext();)
        {
            Node child = (Node)(iter.next());
            initNodes(ui, child);
        }
    }

    public void startElement(String elementName, Attributes atts)
    throws SAXException
    {
        // we should not push null, so we won't loose root element.
        if(currentNode != null)
        {
            nodeStack.push(currentNode);
        }

        Node parentNode = currentNode;

        // 1. build sub node from atts and elementName
        try
        {
            currentNode = buildNode(elementName, atts);
        }
        catch (ConstructionException e)
        {
            throw new SAXException("Problem constructing UI element.", (Exception)e);
        }
        // 2. Attach sub node to it's parent node
        try
        {
            if(parentNode != null)
            {
                parentNode.addChild(currentNode);
            }
        }
        catch (ConstructionException e)
        {
            throw new SAXParseException("An error occured while adding a child node '"+currentNode.getType()+"' to node '"+parentNode.getName()+"'", locator ,e);
        }
    }

    /**
     * Instantiaties Node.
     *
     * @return Node with containing elements data.
     * @param nodeType XML element's name -- node type.
     * @param nodeAtts SAX attributes.
     */
    protected Node buildNode(String nodeType, Attributes nodeAtts)
    throws ConstructionException
    {
        // 1. Get UI element class name
        String nodeClassName = (String)(UIConstants.classes.get(nodeType));
        if(nodeClassName == null)
        {
            throw new ConstructionException("Cannot find class name for element '"+nodeType+"'");
        }

        // 2. Get UI element class
        java.lang.Class nodeClass;
        try
        {
            nodeClass = Class.forName(nodeClassName);
        }
        catch (java.lang.ClassNotFoundException e)
        {
            throw new ConstructionException("Cannot find class '"+nodeClassName+"'", e);
        }

        // 3. Get a constructor
        java.lang.reflect.Constructor nodeConstructor;
        try
        {
            nodeConstructor = nodeClass.getDeclaredConstructor(new Class[]{String.class, Attributes.class});
        }
        catch (java.lang.NoSuchMethodException e)
        {
            throw new ConstructionException("Cannot find constructor for class '"+nodeClassName+"'", e);
        }

        // 4. Instantiate
        try
        {
            // WARN: Replacing nodeType with reference to constant string from UIConstants
            nodeType = (String)(UIConstants.elementNames.get(nodeType));

            Object node = nodeConstructor.newInstance(new Object[]{nodeType, nodeAtts});
            return (Node)node;
        }
        catch (java.lang.InstantiationException e)
        {
            throw new ConstructionException(
            "Cannot instantiate class '"+nodeClassName+"' with constructor '"+nodeConstructor+"'",e);
        }
        catch (java.lang.IllegalAccessException e)
        {
            throw new ConstructionException(
            "Cannot access constructor '"+nodeConstructor+"' in class '"+nodeClassName+"'", e);
        }
        catch (java.lang.reflect.InvocationTargetException e)
        {
            throw new ConstructionException(
            "Cannot instantiate class '"+nodeClassName+"' with constructor '"+nodeConstructor+"'", e);
        }
    }

    /**
     * End of a element call, moves up on a built UI tree.
     */
    public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException
    {
        // 1. Close current node
        if(currentNode instanceof TextNode)
        {
            ((TextNode)currentNode).setText(buffer.toString());
        }
        // 2. Get back to it's parent
        if(!nodeStack.empty()) // last one is root element so we should not loose it
        {
            currentNode = (Node)(nodeStack.pop());
        }
        //
        super.endElement(namespaceURI, localName, qName);
    }
}
