package org.objectledge.jsonql.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;

import junit.framework.TestCase;

public class JSONQLParserTest
    extends TestCase
{
    private static final boolean DEBUG = true;

    public void testWellFormed()
        throws Exception
    {
        runChecks("/jsonql/well_formed.txt", new CheckMethod()
            {
                public void check(int line, String expression, JSONQL parser)
                    throws ParseException
                {
                    try
                    {
                        ASTpredicate exp = parser.onlyPredicate();
                        if(DEBUG)
                        {
                            System.out.println(expression);
                            exp.dump(" ");
                        }
                    }
                    catch(ParseException e)
                    {
                        throw new AssertionError("should not throw exception at line " + line, e);
                    }
                }
            });
    }

    public void testMalformed()
        throws Exception
    {
        runChecks("/jsonql/malformed.txt", new CheckMethod()
            {
                public void check(int line, String expression, JSONQL parser)
                    throws ParseException
                {
                    try
                    {
                        parser.onlyPredicate();
                        fail("should throw exception at line " + line);
                    }
                    catch(ParseException e)
                    {
                        // OK
                    }
                }
            });
    }

    private abstract class CheckMethod
    {
        public void check(int line, String expression)
            throws ParseException
        {
            check(line, expression, new JSONQL(new StringReader(expression)));
        }

        public abstract void check(int line, String expression, JSONQL parser)
            throws ParseException;
    }

    private void runChecks(String file, CheckMethod checkMethod)
        throws IOException, ParseException
    {
        InputStream is = getClass().getResourceAsStream(file);
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        String l;
        while((l = lnr.readLine()) != null)
        {
            checkMethod.check(lnr.getLineNumber(), l);
        }
    }
}
