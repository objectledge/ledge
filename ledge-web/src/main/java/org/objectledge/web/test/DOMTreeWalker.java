package org.objectledge.web.test;

import java.util.ArrayList;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An utility class for traversig DOM trees.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Paweł Potempski</a>
 * @version $Id: DOMTreeWalker.java,v 1.4 2005-05-20 02:04:20 rafal Exp $
 */
public class DOMTreeWalker
{
    private Element root;

    private ArrayList<Object> elements;
    
    private int currentIndex;

    /**
     * Create a DOMTreeWalker instance.
     * 
     * @param root the traversal root.
     */
    public DOMTreeWalker(Element root)
    {
        this.root = root;
        this.elements = new ArrayList<Object>();
        this.currentIndex = 0;
        loadList();
    }

    /**
     * Resets the traversal back to origin.
     */
    public void reset()
    {
        currentIndex = 0;
    }

    /**
     * Loads all elements to the lookup buffer. 
     */
    public void loadList()
    {
        loadNode(root);
    }

    /**
     * Loads the specified element and it's descendants to the lookup buffer.
     * 
     * @param node the node to be loaded.
     */
    private void loadNode(Node node)
    {
        if(node instanceof CharacterData)        
        {
            String data = ((CharacterData)node).getData();
            elements.add(data);
        }
        if(node instanceof Element)
        {
            elements.add(node);
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
                loadNode(children.item(i));
            }
        }
    }

    /**
     * Looks up element of a specified type that occurs after specific text in documents's 
     * character data.
     * 
     * @param text the text preceeding the element.
     * @param tagName the element type to find.
     * @return the first matching element.
     */
    public Element findElementAfterText(String text, String tagName)
    {
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof String)
            {
                if(((String)ob).contains(text))
                {
                    break;
                }
            }
        }
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof Element)
            {
                if(tagName != null)
                {
                    String tag = ((Element)ob).getNodeName();
                    if(tagName.equals(tag.toLowerCase()))
                    {
                        return (Element)ob;
                    }
                }
                else
                {
                    return (Element)ob;
                }
            }
        }
        return null;
    }

    /**
     * Finds the element that contains the specific text in it's character data.
     * 
     * @param text the text to find.
     * @param tagName the type of element to find.
     * @return the matching element.
     */
    public Element findElementWithText(String text, String tagName)
    {
        Element matchedElement = null;
        Element currentElement = null;
        
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof Element)
            {
                if(tagName != null)
                {
                    String tag = ((Element)ob).getNodeName();
                    if(tagName.equals(tag.toLowerCase()))
                    {
                        currentElement = (Element)ob;
                    }
                }
                else
                {
                    currentElement = (Element)ob;
                }
            }
            if(ob instanceof String)
            {
                if(((String)ob).contains(text))
                {
                    matchedElement = currentElement;
                }
            }
        }
        return matchedElement;
    }

    /**
     * Returns next character data block in the lookup buffer.
     * 
     * @return next character data block in the lookup buffer.
     */
    public String getNextText()
    {
        return getNextText(0);
    }

    /**
     * Returns character data block in the lookup buffer in specific distance from current 
     * position.
     * 
     * @param skip number of character data blocks to skip.
     * @return the charcter data block.
     */
    public String getNextText(int skip)
    {
        currentIndex = currentIndex + skip + 1;
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof String)
            {
                return (String)ob;
            }
        }
        return null;
    }

    /**
     * Returns the next element in the lookup buffer.
     * 
     * @return the next element in the lookup buffer.
     */
    public Element getNextElement()
    {
        return getNextElement(0);
    }
    
    /**
     * Returns element in the lookup buffer in specific distance from current position.
     * 
     * @param skip number of elements to skip.
     * @return the element.
     */
    public Element getNextElement(int skip)
    {
        return getNextElement(skip, null);
    }
    
    /**
     * Returns element in the lookup buffer in specific distance from current position.
     * 
     * @param skip number of elements to skip.
     * @param tagName the expected tag name.
     * @return matched element, or null.
     */
    public Element getNextElement(int skip, String tagName)
    {
        currentIndex = currentIndex + skip + 1;
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof Element)
            {
                if(tagName != null)
                {
                    String tag = ((Element)ob).getNodeName();
                    if(tagName.equals(tag.toLowerCase()))
                    {
                        return (Element)ob;
                    }
                }
                else
                {
                    return (Element)ob;
                }
            }
        }
        return null;
    }

    /**
     * Go to the specific element in the lookup buffer.
     * 
     * @param element the element.
     */
    public void gotoElement(Element element)
    {
        for(currentIndex = 0; currentIndex < elements.size(); currentIndex++)
        {
            Object node = elements.get(currentIndex);
            if(node instanceof Element)
            {
                Element el = (Element)node;
                if(el.getNodeName().equals(element.getNodeName()))
                {
                    if(el.equals(element))
                    {
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Count elements of specific type in the lookup buffer.
     * 
     * @param tagName the element type.
     * @return the count of elements of specific type in the lookup buffer.
     */
    public int countTags(String tagName)
    {
        int i = 0;
        int counter = 0;
        for(; i < elements.size(); i++)
        {
            Object ob = elements.get(i);
            if(ob instanceof Element)
            {
                if(tagName != null)
                {
                    String tag = ((Element)ob).getNodeName();
                    if(tagName.equals(tag.toLowerCase()))
                    {
                        counter++;
                    }
                }
                else
                {
                    counter++;
                }
            }
        }
        return counter;
    }
    
    /**
     * Count child elements of a specified element of the specific types. 
     * 
     * @param node the node. 
     * @param tagName types of the child nodes.
     * @return the count of child elements of a specified element of the specific types.
     */
    public static int countElements(Node node, String tagName)
    {
        int counter = 0;
        NodeList list = node.getChildNodes();
        for(int i = 0; i< list.getLength(); i++)
        {
            Node child = list.item(i);
            if(child instanceof Element)
            {
                if(tagName != null)
                {
                    String tag = ((Element)child).getNodeName();
                    if(tagName.equals(tag.toLowerCase()))
                    {
                        counter++;
                    }
                }
                else
                {
                    counter++;
                }
            }
        }
        return counter;
    }
}
