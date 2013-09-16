package org.objectledge.jsonql.impl;

public class RelationPredicate
    extends SimpleNode
{
    private ASTvalue lhs;

    public RelationPredicate(int i)
    {
        super(i);
    }

    public RelationPredicate(JSONQL p, int i)
    {
        super(p, i);
    }

    public ASTvalue getLhs()
    {
        return lhs;
    }

    public void setLhs(ASTvalue lhs)
    {
        this.lhs = lhs;
    }

    public Object jjtAccept(JSONQLVisitor visitor, org.objectledge.jsonql.EvaluationContext data)
    {
        return visitor.visit(this, data);
    }

    @Override
    public String toString(String prefix)
    {
        return super.toString(prefix) + "\n" + stripNl(toString(prefix + " ", lhs));
    }

    private String toString(String prefix, SimpleNode node)
    {
        StringBuilder buff = new StringBuilder();
        buff.append(node.toString(prefix)).append('\n');
        if(node.children != null)
        {
            for(int i = 0; i < node.children.length; ++i)
            {
                SimpleNode n = (SimpleNode)node.children[i];
                if(n != null)
                {
                    buff.append(toString(prefix + " ", n));
                }
            }
        }
        return buff.toString();
    }

    private String stripNl(String s)
    {
        return s.endsWith("\n") ? s.substring(0, s.length() - 1) : s;
    }
}
