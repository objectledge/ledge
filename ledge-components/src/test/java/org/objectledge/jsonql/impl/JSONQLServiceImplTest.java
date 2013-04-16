package org.objectledge.jsonql.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.objectledge.jsonql.JSONQLParseException;
import org.objectledge.jsonql.JSONQLService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONQLServiceImplTest
    extends TestCase
{
    private static ObjectMapper mapper = new ObjectMapper();

    public void testEvaluator()
        throws IOException
    {
        InputStream is = getClass().getResourceAsStream("/jsonql/predicates.txt");
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
        JSONQLService eval = new JSONQLServiceImpl();
        String l;
        while((l = lnr.readLine()) != null)
        {
            runTest(lnr.getLineNumber(), l, eval);
        }
    }

    private void runTest(int lineNumber, String l, JSONQLService eval)
    {
        System.out.println(lineNumber + ": " + l);
        try
        {
            String[] k = l.split("\\|");
            assertEquals(3, k.length);
            JsonNode data = mapper.readTree(k[0]);
            EvaluationContextImpl context = new EvaluationContextImpl(data);

            boolean expectedValue = Boolean.parseBoolean(k[2].trim());
            assertEquals(expectedValue, eval.satisfies(k[1], context));
        }
        catch(RuntimeException | AssertionFailedError | IOException | JSONQLParseException e)
        {
            throw new AssertionError("test case " + lineNumber + " failed", e);
        }
    }
}
