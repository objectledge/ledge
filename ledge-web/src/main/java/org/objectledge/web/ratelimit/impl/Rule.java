package org.objectledge.web.ratelimit.impl;

import org.objectledge.web.ratelimit.rules.ASTrule;

public class Rule
{
    private final long ruleId;
    
    private final ASTrule rule;

    public Rule(long ruleId, ASTrule rule)
    {
        this.rule = rule;
        this.ruleId = ruleId;
    }

    public ASTrule getRule()
    {
        return rule;
    }

    public long getRuleId()
    {
        return ruleId;
    }

    public String getAction()
    {
        return rule.getAction();
    }
}
