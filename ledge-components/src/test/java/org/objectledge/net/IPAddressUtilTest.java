package org.objectledge.net;

import junit.framework.TestCase;

public class IPAddressUtilTest
    extends TestCase
{
    public void testIp4()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV4("127.0.0.1");
        assertNotNull(raw);
        assertEquals(new byte[] { 127, 0, 0, 1 }, raw);
    }

    public void testIp4seg3()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV4("192.168.65535");
        assertNotNull(raw);
        assertEquals(new byte[] { (byte)192, (byte)168, (byte)255, (byte)255 }, raw);
    }

    public void testIp4seg2()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV4("172.16777215");
        assertNotNull(raw);
        assertEquals(new byte[] { (byte)172, (byte)255, (byte)255, (byte)255 }, raw);
    }

    public void testIp4seg1()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV4("4294967295");
        assertNotNull(raw);
        assertEquals(new byte[] { (byte)255, (byte)255, (byte)255, (byte)255 }, raw);
    }

    public void testIp6Full()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("1080:0:0:0:8:800:200C:417A");
        assertNotNull(raw);
        byte[] expected = new byte[] { 
                        (byte)0x10, (byte)0x80, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x08, (byte)0x08, (byte)0x00, 
                        (byte)0x20, (byte)0x0C, (byte)0x41, (byte)0x7A };
        assertEquals(expected, raw);
    }

    public void testIp6Contracted()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("1080::8:800:200C:417A");
        assertNotNull(raw);
        byte[] expected = new byte[] { 
                        (byte)0x10, (byte)0x80, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x08, (byte)0x08, (byte)0x00, 
                        (byte)0x20, (byte)0x0C, (byte)0x41, (byte)0x7A };
        assertEquals(expected, raw);
        raw = IPAddressUtil.textToNumericFormatV6("1080:0::8:800:200C:417A");
        assertEquals(expected, raw);
        raw = IPAddressUtil.textToNumericFormatV6("1080::0:8:800:200C:417A");
        assertEquals(expected, raw);
    }

    public void testIp6Localhost()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("::1");
        assertNotNull(raw);
        byte[] expected = new byte[] { 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01 };
        assertEquals(expected, raw);
    }

    public void testIp6Unspecified()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("::");
        byte[] expected = new byte[] { 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00 };
        assertEquals(expected, raw);
    }

    public void testIp6MppedIp4()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("::FFFF:129.144.52.38");
        assertNotNull(raw);
        byte[] expected = new byte[] { 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, 
                        (byte)129,  (byte)144,  (byte)52,   (byte)38 };
        assertEquals(expected, raw);
    }

    public void testIp6NonMappedIp4()
    {
        byte[] raw = IPAddressUtil.textToNumericFormatV6("::129.144.52.38");
        assertNotNull(raw);
        byte[] expected = new byte[] { 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)129,  (byte)144,  (byte)52,   (byte)38 };
        assertEquals(expected, raw);
    }

    public void testIp6Invalid()
    {
        assertNull(IPAddressUtil.textToNumericFormatV6(":"));
        assertNull(IPAddressUtil.textToNumericFormatV6("1:2:3:4:5:6:7:8:9"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::x"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::FFFFF"));
        assertNull(IPAddressUtil.textToNumericFormatV6(":::"));
        assertNull(IPAddressUtil.textToNumericFormatV6(":::1"));
        assertNull(IPAddressUtil.textToNumericFormatV6("1:::"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::::1"));
        assertNull(IPAddressUtil.textToNumericFormatV6("1::1::1"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::1.2"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::1.2.3.4.5"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::1.2.3.x"));
        assertNull(IPAddressUtil.textToNumericFormatV6("::1.2.3.555"));
    }

    private static void assertEquals(byte[] expected, byte[] actual)
    {
        assertEquals("length", expected.length, actual.length);
        for(int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], actual[i]);
        }
    }
}
