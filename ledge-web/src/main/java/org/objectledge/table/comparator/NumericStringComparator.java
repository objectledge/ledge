package org.objectledge.table.comparator;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Compares integer values of arbitrary precision encoded as decimal strings.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class NumericStringComparator
    implements Comparator<String>
{
    @Override
    public int compare(String o1, String o2)
    {
        BigInteger b1 = new BigInteger(o1);
        BigInteger b2 = new BigInteger(o2);
        return b1.compareTo(b2);
    }
}
