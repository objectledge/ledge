package org.objectledge.net;

/**
 * An utility class for IP address manipulation.
 * <p>
 * String to byte IP address conversion routines are missing from JDK as of version 6. This class is
 * modeled after internal SUN JDK class {@code com.sun.net.IPAddressUtil}.
 * </p>
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class IPAddressUtil
{
    public static byte[] textToNumericFormatV4(String text)
    {
        if(text.length() == 0)
        {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try
        {
            long l;
            int i;
            switch(elements.length)
            {
            case 1:
                l = Long.parseLong(elements[0]);
                if((l < 0L) || (l > 4294967295L))
                    return null;
                bytes[0] = (byte)(int)(l >> 24 & 0xFF);
                bytes[1] = (byte)(int)((l & 0xFFFFFF) >> 16 & 0xFF);
                bytes[2] = (byte)(int)((l & 0xFFFF) >> 8 & 0xFF);
                bytes[3] = (byte)(int)(l & 0xFF);
                break;
            case 2:
                l = Integer.parseInt(elements[0]);
                if((l < 0L) || (l > 255L))
                    return null;
                bytes[0] = (byte)(int)(l & 0xFF);
                l = Integer.parseInt(elements[1]);
                if((l < 0L) || (l > 16777215L))
                    return null;
                bytes[1] = (byte)(int)(l >> 16 & 0xFF);
                bytes[2] = (byte)(int)((l & 0xFFFF) >> 8 & 0xFF);
                bytes[3] = (byte)(int)(l & 0xFF);
                break;
            case 3:
                for(i = 0; i < 2; ++i)
                {
                    l = Integer.parseInt(elements[i]);
                    if((l < 0L) || (l > 255L))
                        return null;
                    bytes[i] = (byte)(int)(l & 0xFF);
                }
                l = Integer.parseInt(elements[2]);
                if((l < 0L) || (l > 65535L))
                    return null;
                bytes[2] = (byte)(int)(l >> 8 & 0xFF);
                bytes[3] = (byte)(int)(l & 0xFF);
                break;
            case 4:
                for(i = 0; i < 4; ++i)
                {
                    l = Integer.parseInt(elements[i]);
                    if((l < 0L) || (l > 255L))
                        return null;
                    bytes[i] = (byte)(int)(l & 0xFF);
                }
                break;
            default:
                return null;
            }
        }
        catch(NumberFormatException e)
        {
            return null;
        }
        return bytes;
    }

    public static byte[] textToNumericFormatV6(String text)
    {
        byte[] bytes = new byte[16];

        String[] s6t = text.split(":", -1);
        if(s6t.length < 3 || s6t.length > 8)
        {
            return null;
        }
        int skipSeg = 0;
        short[] s6i;
        try
        {
            if(s6t[s6t.length - 1].contains("."))
            {
                skipSeg = 1;
                String[] s4t = s6t[s6t.length - 1].split("\\.", -1);
                if(s4t.length != 4)
                {
                    return null;
                }
                for(int i = 0; i < 4; i++)
                {
                    int b = Integer.parseInt(s4t[i]);
                    if(b < 0 || b > 255)
                    {
                        return null;
                    }
                    bytes[12 + i] = (byte)b;
                }
            }
            s6i = new short[s6t.length];
            for(int i = 0; i < s6t.length - skipSeg; i++)
            {
                if(s6t[i].length() > 0)
                {
                    if(s6t[i].length() <= 4)
                    {
                        s6i[i] = (short)Integer.parseInt(s6t[i], 16);
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }
        catch(NumberFormatException e)
        {
            return null;
        }
        int startLen = 0;
        for(int i = 0; i < s6t.length - skipSeg; i++)
        {
            if(s6t[i].length() > 0)
            {
                startLen++;
            }
            else
            {
                break;
            }
        }
        int endLen = 0;
        for(int i = s6t.length - skipSeg - 1; i >= 0; i--)
        {
            if(s6t[i].length() > 0)
            {
                endLen++;
            }
            else
            {
                break;
            }
        }
        int numGaps = 0;
        for(int i = 0; i < s6t.length - skipSeg; i++)
        {
            if(s6t[i].length() == 0)
            {
                numGaps++;
            }
        }
        if(numGaps > 3 || (numGaps == 3 && s6t.length > 3))
        {
            return null;
        }
        if(numGaps != 0 && numGaps + startLen + endLen + skipSeg != s6t.length)
        {
            return null;
        }
        for(int i = 0; i < startLen; i++)
        {
            bytes[i * 2] = (byte)(s6i[i] >> 8);
            bytes[i * 2 + 1] = (byte)(s6i[i] & 0xFF);
        }
        if(numGaps != 0)
        {
            for(int i = s6t.length - skipSeg - endLen; i < s6t.length - skipSeg; i++)
            {
                bytes[(i + 7 - s6t.length - skipSeg + 1) * 2] = (byte)(s6i[i] >> 8);
                bytes[(i + 7 - s6t.length - skipSeg + 1) * 2 + 1] = (byte)(s6i[i] & 0xFF);
            }
        }
        return bytes;
    }

    public static boolean isIPv4LiteralAddress(String text)
    {
        return (textToNumericFormatV4(text) != null);
    }

    public static boolean isIPv6LiteralAddress(String text)
    {
        return (textToNumericFormatV6(text) != null);
    }

    public static byte[] convertFromIPv4MappedAddress(byte[] ip6bytes)
    {
        if(isIPv4MappedAddress(ip6bytes))
        {
            byte[] ip4bytes = new byte[4];
            System.arraycopy(ip6bytes, 12, ip4bytes, 0, 4);
            return ip4bytes;
        }
        return null;
    }

    private static boolean isIPv4MappedAddress(byte[] ip6bytes)
    {
        if(ip6bytes.length < 16)
        {
            return false;
        }

        return ((ip6bytes[0] == 0) && (ip6bytes[1] == 0) && (ip6bytes[2] == 0)
            && (ip6bytes[3] == 0) && (ip6bytes[4] == 0) && (ip6bytes[5] == 0) && (ip6bytes[6] == 0)
            && (ip6bytes[7] == 0) && (ip6bytes[8] == 0) && (ip6bytes[9] == 0)
            && (ip6bytes[10] == -1) && (ip6bytes[11] == -1));
    }
}
