/* Generated By:JJTree: Do not edit this line. ASTnegation.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.objectledge.web.ratelimit.rules;

public
class ASTnegation extends SimpleNode {
  public ASTnegation(int id) {
    super(id);
  }

  public ASTnegation(RateLimitRules p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public boolean jjtAccept(RateLimitRulesVisitor visitor, EvaluationContext data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1ea9a90becb84682f91dda1f87d1c1e3 (do not edit this line) */