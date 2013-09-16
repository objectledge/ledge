package org.objectledge.jsonql.impl;

import java.util.HashSet;
import java.util.Set;

import org.objectledge.jsonql.EvaluationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

public class EvaluationContextImpl implements EvaluationContext
{
    private final Set<String> errors = new HashSet<>();

    private final JsonNode node;

    private final EvaluationContextImpl outer;

    private final String path;

    EvaluationContextImpl(JsonNode node)
    {
        this.node = node;
        this.outer = this;
        this.path = "";
    }

    private EvaluationContextImpl(EvaluationContextImpl outer, String path, JsonNode node)
    {
        this.node = node;
        this.outer = outer;
        this.path = outer.path + path;
    }

    /* (non-Javadoc)
     * @see net.cyklotron.bazy.organizations.autocat.parser.EvaluationContextContract#getNode()
     */
    @Override
    public JsonNode getNode()
    {
        return node;
    }

    /* (non-Javadoc)
     * @see net.cyklotron.bazy.organizations.autocat.parser.EvaluationContextContract#getField(java.lang.String)
     */
    @Override
    public EvaluationContextImpl getField(String field)
    {
        JsonNode n = MissingNode.getInstance();
        if(node.isObject())
        {
            if(node.has(field))
            {
                n = node.get(field);
            }
            else
            {
                addError(" does not have field " + field);
            }
        }
        else
        {
            addError(" is not an object");
        }
        JsonNode variable = n;
        return new EvaluationContextImpl(this, (outer == this ? "" : ".") + field, variable);
    }

    /* (non-Javadoc)
     * @see net.cyklotron.bazy.organizations.autocat.parser.EvaluationContextContract#getElement(int)
     */
    @Override
    public EvaluationContextImpl getElement(int index)
    {
        JsonNode n = MissingNode.getInstance();
        if(node.isArray())
        {
            if(index < node.size())
            {
                n = node.get(index);
            }
            else
            {
                addError(" does not have element " + index);
            }
        }
        else
        {
            addError(" is not an array");
        }
        JsonNode variable = n;
        return new EvaluationContextImpl(this, "[" + index + "]", variable);
    }

    /* (non-Javadoc)
     * @see net.cyklotron.bazy.organizations.autocat.parser.EvaluationContextContract#getValue()
     */
    @Override
    public String getValue()
    {
        if(node.isMissingNode())
        {
            addError(" is undefined");
        }
        if(node.isContainerNode())
        {
            addError(" is a container node");
        }
        if(node.isNull())
        {
            addError(" is null");
        }
        else if(node.isValueNode())
        {
            return node.asText();
        }
        return null;
    }

    void notContainer()
    {
        addError(" is not a container node");
    }

    void notFound(String predicate)
    {
        addError(" does not contain child node satisfying " + predicate);
    }

    public void addError(String msg)
    {
        outer.errors.add((path.length() == 0 ? "root node" : path) + msg);
    }

    @Override
    public Set<String> getErrors()
    {
        if(!(outer == this))
        {
            throw new IllegalStateException("not a top level PropertyAccessor");
        }
        final HashSet<String> result = new HashSet<>(errors);
        errors.clear();
        return result;
    }
}
