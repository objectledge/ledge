package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import org.objectledge.web.ratelimit.rules.ASTconjunction;
import org.objectledge.web.ratelimit.rules.ASTdisjunction;
import org.objectledge.web.ratelimit.rules.ASTnegation;
import org.objectledge.web.ratelimit.rules.ASTpredicateHeader;
import org.objectledge.web.ratelimit.rules.ASTpredicateHeaderMatch;
import org.objectledge.web.ratelimit.rules.ASTpredicateHits;
import org.objectledge.web.ratelimit.rules.ASTpredicateHost;
import org.objectledge.web.ratelimit.rules.ASTpredicateHostMatch;
import org.objectledge.web.ratelimit.rules.ASTpredicateIp;
import org.objectledge.web.ratelimit.rules.ASTpredicateIpMatch;
import org.objectledge.web.ratelimit.rules.ASTrule;
import org.objectledge.web.ratelimit.rules.EvaluationContext;
import org.objectledge.web.ratelimit.rules.RateLimitRulesVisitor;
import org.objectledge.web.ratelimit.rules.SimpleNode;

public class RuleEvaluator
{
    private final HitTable hitTable;

    private final String defaultAction;

    public RuleEvaluator(HitTable hitTable, String defaultAction)
    {
        this.hitTable = hitTable;
        this.defaultAction = defaultAction;
    }

    public String action(RequestInfo request, List<Rule> rules)
    {
        hitTable.hit(request);
        for(Rule rule : rules)
        {
            if(VISITOR.visit(rule.getRule(), new RuleEvaluationContext(rule, request)))
            {
                return rule.getAction();
            }
        }
        return defaultAction;
    }

    public String action(RequestInfo request, Rule... rules)
    {
        return action(request, Arrays.asList(rules));
    }

    private class RuleEvaluationContext
        implements EvaluationContext
    {

        private RequestInfo request;

        private final int hits;

        public RuleEvaluationContext(Rule rule, RequestInfo request)
        {
            this.request = request;
            hits = hitTable.getHits(request);
        }

        @Override
        public InetAddress getAddress()
        {
            return request.getAddress();
        }

        @Override
        public String getHost()
        {
            return request.getHost();
        }

        @Override
        public String getHeader(String headerName)
        {
            return request.getHeaders().get(headerName);
        }

        @Override
        public int getHits()
        {
            return hits;
        }
    }

    private static final RateLimitRulesVisitor VISITOR = new RateLimitRulesVisitor()
        {
            @Override
            public boolean visit(SimpleNode node, EvaluationContext data)
            {
                throw new IllegalStateException("shouldn't have been called");
            }

            @Override
            public boolean visit(ASTpredicateIp node, EvaluationContext data)
            {
                return node.getAddress().equals(data.getAddress());
            }

            @Override
            public boolean visit(ASTpredicateIpMatch node, EvaluationContext data)
            {
                return node.getAddressBlock().contains(data.getAddress());
            }

            @Override
            public boolean visit(ASTpredicateHost node, EvaluationContext data)
            {
                return node.getName().equals(data.getHost());
            }

            @Override
            public boolean visit(ASTpredicateHostMatch node, EvaluationContext data)
            {
                return node.getPattern().matcher(data.getHost()).matches();
            }

            @Override
            public boolean visit(ASTpredicateHits node, EvaluationContext data)
            {
                return node.getValue() < data.getHits();
            }

            @Override
            public boolean visit(ASTpredicateHeader node, EvaluationContext data)
            {
                return node.getValue().equals(data.getHeader(node.getHeader()));
            }

            @Override
            public boolean visit(ASTpredicateHeaderMatch node, EvaluationContext data)
            {
                return node.getPattern().matcher(data.getHeader(node.getHeader())).matches();
            }

            @Override
            public boolean visit(ASTnegation node, EvaluationContext data)
            {
                return !node.childrenAccept(this, data)[0];
            }

            @Override
            public boolean visit(ASTconjunction node, EvaluationContext data)
            {
                for(boolean c : node.childrenAccept(this, data))
                {
                    if(!c)
                    {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean visit(ASTdisjunction node, EvaluationContext data)
            {
                for(boolean c : node.childrenAccept(this, data))
                {
                    if(c)
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean visit(ASTrule node, EvaluationContext data)
            {
                return node.childrenAccept(this, data)[0];
            }
        };
}
