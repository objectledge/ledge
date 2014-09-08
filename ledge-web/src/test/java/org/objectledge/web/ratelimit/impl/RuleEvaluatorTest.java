package org.objectledge.web.ratelimit.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.objectledge.net.IPAddressUtil;
import org.objectledge.web.ratelimit.rules.ParseException;

public class RuleEvaluatorTest
    extends TestCase
{
    private HitTable hitsTable = new HitTable();

    private RuleEvaluator eval = new RuleEvaluator(hitsTable, "accept");

    private RuleFactory factory = RuleFactory.getInstance();

    private RequestInfo request(String addr, String host, String... headersTab)
        throws UnknownHostException
    {
        InetAddress address = IPAddressUtil.byAddress(addr);
        Map<String, String> headers = new HashMap<>();
        for(int i = 0; i < headersTab.length / 2; i++)
        {
            headers.put(headersTab[2 * i], headersTab[2 * i + 1]);
        }
        return new RequestInfo(address, host, headers);
    }

    public void testEqIP()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "IP = 192.168.0.1 => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost");
        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));
        RequestInfo q2 = request("192.168.0.51", "otherhost");
        assertEquals("accept", eval.action(q2, r));
        assertEquals(1, hitsTable.getHits(q2));
    }

    public void testMatchIP()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "IP ~ 192.168.0.0/24 => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost");
        RequestInfo q2 = request("192.168.1.1", "otherhost");

        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r));
        assertEquals(1, hitsTable.getHits(q2));
    }

    public void testEqHost()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "Host = \"somehost\" => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost");
        RequestInfo q2 = request("192.168.1.1", "otherhost");

        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r));
        assertEquals(1, hitsTable.getHits(q2));
    }

    public void testMatchHost()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "Host ~ /^some.*/ => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost");
        RequestInfo q2 = request("192.168.1.1", "otherhost");

        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r));
        assertEquals(1, hitsTable.getHits(q2));
    }

    public void testEqHeader()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "Referrer = \"somedomain\" => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost", "Referrer", "somedomain");
        RequestInfo q2 = request("192.168.0.1", "somehost", "Referrer", "otherdomain");

        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r));
        assertEquals(2, hitsTable.getHits(q2)); // matched for the same host
    }

    public void testMatchHeader()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "User-Agent ~ /.*Internet Explorer.*/ => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost", "User-Agent", "Internet Explorer 8.0");
        assertEquals("reject", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));
        RequestInfo q2 = request("192.168.0.1", "somehost", "User-Agent", "Google Chrome 32");
        assertEquals("accept", eval.action(q2, r));
        assertEquals(2, hitsTable.getHits(q2)); // matched for the same host
    }

    public void testEqIPAndHits()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1, "IP = 192.168.0.1 && Hits > 1 => reject");
        RequestInfo q1 = request("192.168.0.1", "somehost");
        RequestInfo q2 = request("192.168.0.1", "somehost");

        assertEquals("accept", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("reject", eval.action(q2, r));
        assertEquals(2, hitsTable.getHits(q2));
    }

    public void testHostMatchOrHeaderMatchAndHits()
        throws ParseException, UnknownHostException
    {
        Rule r = factory
            .newRule(1,
                "(User-Agent ~ /.*Internet Explorer.*/ || Host ~ /.*\\.adsl\\.tpnet\\.pl/ ) && Hits > 2 => reject");
        RequestInfo q1 = request("192.168.0.1", "a7b1.adsl.tpnet.pl", "User-Agent",
            "Internet Explorer 8");
        RequestInfo q2 = request("192.168.0.1", "a7b1.adsl.tpnet.pl", "User-Agent",
            "Google Chrome 32");

        assertEquals("accept", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r));
        assertEquals(2, hitsTable.getHits(q2));

        assertEquals("reject", eval.action(q1, r));
        assertEquals(3, hitsTable.getHits(q1));

        assertEquals("reject", eval.action(q2, r));
        assertEquals(4, hitsTable.getHits(q2));
    }

    public void testHostMatchAndNotHeaderMatch()
        throws ParseException, UnknownHostException
    {
        Rule r = factory.newRule(1,
            "Host ~ /.*\\.adsl\\.tpnet\\.pl/ && !User-Agent ~ /.*Internet Explorer.*/  => reject");
        RequestInfo q1 = request("192.168.0.1", "a7b1.adsl.tpnet.pl", "User-Agent",
            "Internet Explorer 8");
        RequestInfo q2 = request("192.168.0.1", "a7b1.adsl.tpnet.pl", "User-Agent",
            "Google Chrome 32");

        assertEquals("accept", eval.action(q1, r));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("reject", eval.action(q2, r));
        assertEquals(2, hitsTable.getHits(q2));
    }

    public void testMultiRule()
        throws ParseException, UnknownHostException
    {
        Rule r1 = factory.newRule(1, "User-Agent ~ /.*Internet Explorer.*/ => reject");
        Rule r2 = factory.newRule(1, "Host ~ /.*\\.adsl\\.tpnet\\.pl/ => reject");
        RequestInfo q1 = request("192.168.0.1", "a7b1.adsl.tpnet.pl", "User-Agent",
            "Google Chrome 32");
        RequestInfo q2 = request("192.168.1.1", "zyzyzz.netia.pl", "User-Agent", "Google Chrome 32");

        assertEquals("reject", eval.action(q1, r1, r2));
        assertEquals(1, hitsTable.getHits(q1));

        assertEquals("accept", eval.action(q2, r1, r2));
        assertEquals(1, hitsTable.getHits(q1));
    }
}
