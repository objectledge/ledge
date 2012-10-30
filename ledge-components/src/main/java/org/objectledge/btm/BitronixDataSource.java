package org.objectledge.btm;

import org.objectledge.database.impl.DelegatingDataSource;

public class BitronixDataSource
    extends DelegatingDataSource
{
    public BitronixDataSource(String uniqueName, BitronixTransactionManager b)
    {
        super(b.getDataSource(uniqueName));
    }
}