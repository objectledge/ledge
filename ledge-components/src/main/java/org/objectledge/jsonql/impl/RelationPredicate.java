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
}
