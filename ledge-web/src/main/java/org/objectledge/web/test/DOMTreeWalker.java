package org.objectledge.web.test;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMTreeWalker
{
    private Element root;

    private Element lastVisited;
    
    private boolean textFound;
    
    public DOMTreeWalker(Element root)
    {
        this.root = root;
        this.lastVisited = root;
        this.textFound = false;
    }

    public void reset()
    {
        lastVisited = root;
        this.textFound = false;
    }
    
    public Element findElementAfterText(String text, String tagName)
    {
        return findElementAfterText(lastVisited, text, tagName);
    }
    
    private Element findElementAfterText(Node node, String text, String tagName)
    {
        if(node instanceof CharacterData && !textFound)
        {
            String data = ((CharacterData)node).getData();
            if(data != null && data.indexOf(text) > -1)
            {
                textFound = true;
            }
        }
        if(node instanceof Element)
        {
            lastVisited = (Element)node;
            String tag = node.getNodeName();
            if(textFound && tagName.equals(tag.toLowerCase()))
            {
                return (Element)node;
            }
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
                Element el = findElementAfterText(children.item(i), text, tagName);
                if(el != null)
                {
                    return el;
                }
            }
        }
        return null;
    }
    
    public int countTags(String tagName)
    {   
        return countTags(lastVisited, tagName);
    }
    
    public int countTags(Node node, String tagName)
    {
        int counter = 0;
        if(node instanceof Element)
        {
            lastVisited = (Element)node;
            String tag = node.getNodeName();
            if(tagName.equals(tag.toLowerCase()))
            {
                counter++;
            }
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
                counter = counter + countTags(children.item(i), tagName);
            }
        }
        return counter;
    }
}
