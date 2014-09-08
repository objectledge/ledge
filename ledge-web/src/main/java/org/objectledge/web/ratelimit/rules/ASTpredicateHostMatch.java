/* Generated By:JJTree: Do not edit this line. ASTpredicateHostMatch.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.objectledge.web.ratelimit.rules;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ASTpredicateHostMatch
    extends SimpleNode
{
    private Pattern pattern;

    public ASTpredicateHostMatch(int id)
    {
        super(id);
    }

    public ASTpredicateHostMatch(RateLimitRules p, int id)
    {
        super(p, id);
    }

    public void setPattern(Token t)
        throws ParseException
    {
        try
        {
            pattern = Pattern.compile(t.image.substring(1, t.image.length() - 1));
        }
        catch(PatternSyntaxException e)
        {
            throw new ParseException("Invalid pattern at line " + t.beginLine + " column "
                + t.beginColumn + ": " + e.getMessage());
        }
    }
    
    public Pattern getPattern()
    {
        return pattern;
    }

    /** Accept the visitor. **/
    public boolean jjtAccept(RateLimitRulesVisitor visitor, EvaluationContext data)
    {
        return visitor.visit(this, data);
    }
}
/* JavaCC - OriginalChecksum=142a15268db32bcaefb224661c7d7c3c (do not edit this line) */
