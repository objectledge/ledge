package org.objectledge.web.ratelimit.impl;

import java.io.StringReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectledge.web.ratelimit.rules.ParseException;
import org.objectledge.web.ratelimit.rules.RateLimitRules;

public class RuleFactory
{
    private static final int MAX_PARSERS = 4;

    private final BlockingQueue<RateLimitRules> parserPool = new ArrayBlockingQueue<>(MAX_PARSERS);

    private final AtomicInteger curParsers = new AtomicInteger(0);

    private static final RuleFactory INSTANCE = new RuleFactory();

    private RuleFactory()
    {
    }

    public static RuleFactory getInstance()
    {
        return INSTANCE;
    }

    private RateLimitRules borrowParser()
        throws InterruptedException
    {
        RateLimitRules parser = parserPool.poll();
        while(parser == null)
        {
            int cur = curParsers.get();
            if(cur < MAX_PARSERS)
            {
                if(curParsers.compareAndSet(cur, cur + 1))
                {
                    parser = new RateLimitRules(new StringReader(""));
                }
            }
            else
            {
                parser = parserPool.take();
            }
        }
        return parser;
    }

    private void returnParser(RateLimitRules parser)
        throws InterruptedException
    {
        if(parser != null)
        {
            parserPool.add(parser);
        }
    }

    public Rule newRule(long id, String text)
        throws ParseException
    {
        try
        {
            RateLimitRules parser = null;
            try
            {
                parser = borrowParser();
                parser.ReInit(new StringReader(text));
                return new Rule(id, parser.rule());
            }
            finally
            {
                returnParser(parser);
            }
        }
        catch(InterruptedException e)
        {
            throw new IllegalStateException("parser pool operation was interrupted", e);
        }
    }
}
