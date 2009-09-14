package pl.caltha.forms.internal.model;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.InvalidXPathException;
import org.dom4j.XPath;

import pl.caltha.forms.ConstructionException;

/**
 *
 * @author  damian
 */
public class InstanceReference
{
    String name;
    boolean absolute;
    String expression;
    XPath xpath;

    /** Creates a default instance of InstanceReference. */
    public InstanceReference()
    {
        this.name = "default";
        this.expression = "/";
        this.absolute = true;

        try
        {
            this.xpath = createXPath(name, expression);
        }
        catch(ConstructionException e)
        {
            throw new RuntimeException("Whoops! Cannot construct a default InstanceReference:"+e);
        }
    }

    /** Creates a new instance of InstanceReference */
    public InstanceReference(String name, String xPathExpression)
    throws ConstructionException
    {
        this.name = name;
        this.expression = xPathExpression;
        this.xpath = createXPath(name, expression);

        // check for absolute XPath
        this.absolute = (this.xpath.getText().charAt(0) == '/');
        // calculate absolute XPath for common functions
        // which are frequently used for bind expressions
        if(this.xpath.getText().equals("true()") ||
           this.xpath.getText().equals("false()"))
        {
            this.absolute = true;
        }
    }

    protected XPath createXPath(String name, String xPathExpression)
    throws ConstructionException
    {
        try
        {
            return DocumentHelper.createXPath(xPathExpression);
        }
        catch(InvalidXPathException e)
        {
            throw new ConstructionException("Invalid XPath in attribute '"+name+"'", e);
        }
    }

    public Object getValue(InstanceImpl instance)
    {
       return this.xpath.evaluate(instance.getDocument().getRootElement());
    }

    public Object getValue(Object context)
    {
       return this.xpath.evaluate(context);
    }

    public String getStringValue(InstanceImpl instance)
    {
       return this.xpath.valueOf(instance.getDocument().getRootElement());
    }

    public String getStringValue(Object context)
    {
       return this.xpath.valueOf(context);
    }

    public org.dom4j.Node getNode(InstanceImpl instance)
    {
       return this.xpath.selectSingleNode(instance.getDocument().getRootElement());
    }

    public org.dom4j.Node getNode(Object context)
    {
       return this.xpath.selectSingleNode(context);
    }

    public List getNodes(InstanceImpl instance)
    {
       return this.xpath.selectNodes(instance.getDocument().getRootElement());
    }

    public List getNodes(Object context)
    {
       return this.xpath.selectNodes(context);
    }

    public boolean isAbsolute()
    {
        return absolute;
    }
}
