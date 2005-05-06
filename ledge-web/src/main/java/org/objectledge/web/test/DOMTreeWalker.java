package org.objectledge.web.test;

import java.util.ArrayList;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMTreeWalker
{
    private Element root;

    private ArrayList<Object> elements;
    
    private int currentIndex;
    
    public DOMTreeWalker(Element root)
    {
        this.root = root;
        this.elements = new ArrayList<Object>();
        this.currentIndex = 0;
        loadList();
    }

    public void reset()
    {
        currentIndex = 0;
    }
    
    public void loadList()
    {
        loadNode(root);
    }
    
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
    
    public String getNextText()
    {
        return getNextText(0);
    }
    
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
    
    public Element getNextElement()
    {
        return getNextElement(0);
    }
    
    public Element getNextElement(int skip)
    {
        currentIndex = currentIndex + skip + 1;
        for(; currentIndex < elements.size(); currentIndex++)
        {
            Object ob = elements.get(currentIndex);
            if(ob instanceof Element)
            {
                return (Element)ob;
            }
        }
        return null;
    }
  
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
}
