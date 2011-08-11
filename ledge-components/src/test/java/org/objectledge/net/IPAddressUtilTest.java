package org.objectledge.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    
    public void testInetAddress() throws UnknownHostException
    {
        String text = "1080:0:0:0:8:800:200C:417A";
        byte[] raw = IPAddressUtil.textToNumericFormatV6(text);
        InetAddress addr = InetAddress.getByAddress(raw);
        byte[] expected = new byte[] { 
                        (byte)0x10, (byte)0x80, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x08, (byte)0x08, (byte)0x00, 
                        (byte)0x20, (byte)0x0C, (byte)0x41, (byte)0x7A };
        assertEquals(expected, addr.getAddress());
        assertEquals(text, addr.getHostAddress().toUpperCase());
    }


    public void testByAddressIp4() throws UnknownHostException, IllegalArgumentException
    {
        InetAddress addr = IPAddressUtil.byAddress("192.168.1.1");
        byte[] expected = new byte[] {
          (byte)192, (byte)168, (byte)1, (byte)1              
        };
        assertEquals(expected, addr.getAddress());
    }
    
    public void testByAddressIp4Invalid() throws UnknownHostException, IllegalArgumentException
    {
        try
        {
            IPAddressUtil.byAddress("192.168.1.333");
            fail("should throw exception");
        }
        catch(Exception e)
        {
            // OK
        }
    }
    
    public void testByAddressIp6() throws UnknownHostException, IllegalArgumentException
    {
        String text = "1080:0:0:0:8:800:200C:417A";
        InetAddress addr = IPAddressUtil.byAddress(text);
        byte[] expected = new byte[] { 
                        (byte)0x10, (byte)0x80, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
                        (byte)0x00, (byte)0x08, (byte)0x08, (byte)0x00, 
                        (byte)0x20, (byte)0x0C, (byte)0x41, (byte)0x7A };
        assertEquals(expected, addr.getAddress());
    }
    
    public void testByAddressIp6Invalid() throws UnknownHostException, IllegalArgumentException
    {
        try
        {
            IPAddressUtil.byAddress(":::");
            fail("should throw exception");
        }
        catch(Exception e)
        {
            // OK
        }
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
