package org.objectledge.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Represents a Classless Inter-Domain Routing address block.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class CIDRBlock
{
    /** Network prefix */
    private final InetAddress prefix;

    private final byte[] prefixAddr;

    /** Network prefix length in bits. */
    private final int prefixLength;

    /** Network prefix mask. */
    private byte[] networkMask;

    /** Network prefix mask. */
    private byte[] hostMask;

    /**
     * Creates a new CIDRBlock object.
     * 
     * @param prefix the network prefix for the block.
     * @param prefixLength prefix length, must be positive and less or equal to address size, 32
     *        bits for IPv4 and 128 bits for IPv6.
     */
    public CIDRBlock(InetAddress prefix, int prefixLength)
    {
        this.prefix = prefix;
        this.prefixAddr = prefix.getAddress();
        this.prefixLength = prefixLength;

        byte[] networkAddr = prefix.getAddress();
        if(prefixLength < 0 || prefixLength > networkAddr.length * 8)
        {
            throw new IllegalArgumentException("invalid prefixLength " + prefixLength + " for IP"
                + (networkAddr.length == 4 ? "v4" : "v6") + " adress");
        }

        networkMask = new byte[networkAddr.length];
        for(int i = 0; i < prefixLength / 8; i++)
        {
            networkMask[i] = (byte)0xFF;
        }
        int bits = prefixLength % 8;
        if(bits > 0)
        {
            networkMask[prefixLength / 8] = (byte)~((byte)(0xFF >>> bits));
        }

        hostMask = new byte[networkMask.length];
        for(int i = 0; i < networkMask.length; i++)
        {
            hostMask[i] = (byte)~networkMask[i];
        }

        for(int i = 0; i < prefixAddr.length; i++)
        {
            if((prefixAddr[i] & hostMask[i]) != 0)
            {
                throw new IllegalArgumentException("illegal network address "
                    + prefix.getHostAddress() + " for prefix length " + prefixLength + ": "
                    + (networkAddr.length * 8 - prefixLength)
                    + " low order bits of the must be zero");
            }
        }
    }

    /**
     * Returns the network prefix for the block.
     * 
     * @return the network prefix for the block.
     */
    public InetAddress getPrefix()
    {
        return prefix;
    }

    /**
     * Returns network prefix length for the block, in bits.
     * 
     * @return network prefix length for the block, in bits.
     */
    public int getPrefixLength()
    {
        return prefixLength;
    }

    /**
     * Checks if the block contains the specified host address.
     * <p>
     * If block prefix is an IPv6 address and the host address is an IPv4 address and, host address
     * is expanded to a IPv4 compatible IPv6 address by prepending 96 zero bits.
     * </p>
     * <p>
     * If block prefix is an IPv4 address and the host address is an IPv6 address that is IPv4
     * compatible (96 high order bits are all zero), low order 32 bits are used for matching,
     * otherwise an exception is thrown.
     * </p>
     * 
     * @param addr address to check.
     * @return {@code true} if this CIDR block contains the specified host address.
     */
    public boolean contains(InetAddress addr)
    {
        byte[] hostAddr;
        if((prefix instanceof Inet4Address) && (addr instanceof Inet6Address))
        {
            if(((Inet6Address)addr).isIPv4CompatibleAddress())
            {
                hostAddr = new byte[4];
                System.arraycopy(addr.getAddress(), 12, hostAddr, 0, 4);
            }
            else
            {
                throw new IllegalArgumentException(addr.getHostAddress()
                    + " is not a IPv4 compatible address");
            }
        }
        else if((prefix instanceof Inet6Address && (addr instanceof Inet4Address)))
        {
            hostAddr = new byte[16];
            System.arraycopy(addr.getAddress(), 0, hostAddr, 12, 4);
        }
        else
        {
            hostAddr = addr.getAddress();
        }

        for(int i = 0; i < hostAddr.length; i++)
        {
            if(prefixAddr[i] != (hostAddr[i] & networkMask[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the network mask for the block.
     * 
     * @return network mask for the block.
     */
    public byte[] getNetworkMask()
    {
        byte[] copy = new byte[networkMask.length];
        System.arraycopy(networkMask, 0, copy, 0, networkMask.length);
        return copy;
    }

    /**
     * Returns the host mask for the block.
     * 
     * @return network host for the block.
     */
    public byte[] getHostMask()
    {
        byte[] copy = new byte[hostMask.length];
        System.arraycopy(hostMask, 0, copy, 0, hostMask.length);
        return copy;
    }

    @Override
    public int hashCode()
    {
        return prefix.hashCode() + prefixLength * 127;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof CIDRBlock))
        {
            return false;
        }
        CIDRBlock other = (CIDRBlock)obj;
        return prefix.equals(other.prefix) && prefixLength == other.prefixLength;
    }

    @Override
    public String toString()
    {
        return prefix.getHostAddress() + "/" + prefixLength;
    }

    public int compareTo(CIDRBlock that)
    {
        byte[] addr1Bytes;
        byte[] addr2Bytes;
        if(this.prefix.getAddress().length != that.prefix.getAddress().length)
        {
            addr1Bytes = upgrade(this.prefix.getAddress());
            addr2Bytes = upgrade(that.prefix.getAddress());
        }
        else
        {
            addr1Bytes = this.prefix.getAddress();
            addr2Bytes = that.prefix.getAddress();
        }
        int cmp = compare(addr1Bytes, addr2Bytes);
        if(cmp != 0)
        {
            return cmp;
        }
        else
        {
            return this.getPrefixLength() - that.getPrefixLength();
        }
    }

    private byte[] upgrade(byte[] in)
    {
        if(in.length == 16)
        {
            return in;
        }
        byte out[] = new byte[16];
        Arrays.fill(out, 0, 10, (byte)0x00);
        Arrays.fill(out, 10, 12, (byte)0xFF);
        System.arraycopy(in, 0, out, 12, 4);
        return out;
    }

    private int compare(byte[] a, byte[] b)
    {
        for(int i = 0; i < a.length; i++)
        {
            if(a[i] < b[i])
            {
                return -1;
            }
            else if(a[i] > b[i])
            {
                return 1;
            }
        }
        return 0;
    }
}
