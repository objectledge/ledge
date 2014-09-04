package org.objectledge.web.ratelimit.rules;

import java.io.StringReader;

import org.objectledge.test.LedgeTestCase;

public class RuleParserTest
    extends LedgeTestCase
{
    private RateLimitRules parser = new RateLimitRules(new StringReader(""));

    public void testValidRules()
        throws Exception
    {
        String allRules = getFileSystem().read("ratelimit/valid.txt", "UTF-8");
        int num = 1;
        for(String rule : allRules.split("\n"))
        {
            parser.ReInit(new StringReader(rule));
            try
            {
                parser.rule();
            }
            catch(ParseException e)
            {
                throw new Exception("Failed to parse valid rule #" + num, e);
            }
            num++;
        }
    }

    public void testInvalidRules()
        throws Exception
    {
        String allRules = getFileSystem().read("ratelimit/invalid.txt", "UTF-8");
        int num = 1;
        for(String rule : allRules.split("\n"))
        {
            parser.ReInit(new StringReader(rule));
            try
            {
                parser.rule();
                throw new Exception("Failed to detect errror in invalid rule #" + num);
            }
            catch(ParseException e)
            {
                // OK
            }
            num++;
        }
    }
}
