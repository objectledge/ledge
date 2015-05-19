package org.objectledge.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class CIDRBlockTest
    extends TestCase
{
    public void testIPv4Ctor()
        throws UnknownHostException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 24);
        assertEquals(24, block.getPrefixLength());
    }

    public void testIPv6Ctor()
        throws UnknownHostException
    {
        new CIDRBlock(IPAddressUtil.byAddress("100:200:FF::"), 48);
    }

    public void testIPv4InvalidLength()
        throws UnknownHostException
    {
        try
        {
            new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 48);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e)
        {
            // OK;
        }
    }

    public void testIPv6InvalidLength()
        throws UnknownHostException
    {
        try
        {
            new CIDRBlock(IPAddressUtil.byAddress("100:200:FF::"), 138);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e)
        {
            // OK;
        }
    }
    
    public void testMasks() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        byte[] hostMask = block.getHostMask();
        byte[] expectedHostMask = new byte[] {
          (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3F          
        };
        assertEquals(expectedHostMask, hostMask);
        byte[] networkMask = block.getNetworkMask();
        byte[] expectedNetworkMask = new byte[] {
          (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xC0            
        };
        assertEquals(expectedNetworkMask, networkMask);
    }
    
    public void testContains() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        InetAddress in = IPAddressUtil.byAddress("192.168.10.63");
        assertTrue(block.contains(in));
        InetAddress out = IPAddressUtil.byAddress("192.168.10.65");
        assertFalse(block.contains(out));
    }
    
    public void testMixed1() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("::192.168.10.0"), 96 + 26);
        assertEquals(16, block.getNetworkMask().length); // IPv6
        InetAddress in = IPAddressUtil.byAddress("192.168.10.63");
        assertTrue(block.contains(in));
        InetAddress out = IPAddressUtil.byAddress("192.168.10.65");
        assertFalse(block.contains(out));
    }
    
    public void testMixed2() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        assertEquals(4, block.getNetworkMask().length); // IPv4
        InetAddress in = IPAddressUtil.byAddress("::192.168.10.63");
        assertEquals(16, in.getAddress().length); // IPv6
        assertTrue(block.contains(in));
        InetAddress out = IPAddressUtil.byAddress("::192.168.10.65");
        assertFalse(block.contains(out));
    }
    
    public void testMixed3() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        assertEquals(4, block.getNetworkMask().length); // IPv4
        InetAddress in = IPAddressUtil.byAddress("::FFFF:192.168.10.63");
        assertEquals(4, in.getAddress().length); // implicit IPv4
        assertTrue(block.contains(in));
        InetAddress out = IPAddressUtil.byAddress("::FFFF:192.168.10.65");
        assertFalse(block.contains(out));
    }
    
    public void testMixed4() throws UnknownHostException, IllegalArgumentException
    {
        CIDRBlock block = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        assertEquals(4, block.getNetworkMask().length); // IPv4
        InetAddress nonCompat = IPAddressUtil.byAddress("100:200:FF::1");
        assertEquals(16, nonCompat.getAddress().length); // IPv6
        try
        {
            block.contains(nonCompat);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e)
        {
            // OK
        }
    }
    
    public void testCompareIPv4() throws UnknownHostException, IllegalArgumentException 
    {
        CIDRBlock block1 = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        CIDRBlock block2 = new CIDRBlock(IPAddressUtil.byAddress("192.168.11.0"), 26);
        assertEquals(0, block1.compareTo(block1));
        assertTrue(block1.compareTo(block2) < 0);
        assertTrue(block2.compareTo(block1) > 0);
    }

    public void testCompareIPv6() throws UnknownHostException, IllegalArgumentException 
    {
        CIDRBlock block1 = new CIDRBlock(IPAddressUtil.byAddress("100:200:FF::"), 64);
        CIDRBlock block2 = new CIDRBlock(IPAddressUtil.byAddress("100:201:FF::"), 64);
        assertEquals(0, block1.compareTo(block1));
        assertTrue(block1.compareTo(block2) < 0);
        assertTrue(block2.compareTo(block1) > 0);
    }
    
    public void testCompareMixed() throws UnknownHostException, IllegalArgumentException 
    {
        CIDRBlock block1 = new CIDRBlock(IPAddressUtil.byAddress("::FFFF:192.168.10.62"), 32);
        CIDRBlock block2 = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.63"), 32);
        assertEquals(0, block1.compareTo(block1));
        assertTrue(block1.compareTo(block2) < 0);
        assertTrue(block2.compareTo(block1) > 0);
    }
    
    public void testComparePrefixSizes() throws UnknownHostException, IllegalArgumentException 
    {
        CIDRBlock block1 = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 24);
        CIDRBlock block2 = new CIDRBlock(IPAddressUtil.byAddress("192.168.10.0"), 26);
        assertEquals(0, block1.compareTo(block1));
        assertTrue(block1.compareTo(block2) < 0);
        assertTrue(block2.compareTo(block1) > 0);
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
