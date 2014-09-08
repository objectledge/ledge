/* Generated By:JJTree: Do not edit this line. ASTrule.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.objectledge.web.ratelimit.rules;

public class ASTrule
    extends SimpleNode
{
    private String action;

    public ASTrule(int id)
    {
        super(id);
    }

    public ASTrule(RateLimitRules p, int id)
    {
        super(p, id);
    }

    public void setAction(Token token)
    {
        this.action = token.image;
    }
    
    public String getAction()
    {
        return action;
    }

    /** Accept the visitor. **/
    public boolean jjtAccept(RateLimitRulesVisitor visitor, EvaluationContext data)
    {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=e5bbf06d7caa61035a3028644f4bf9e3 (do not edit this line) */
