package org.objectledge.btm;

import java.sql.SQLException;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;

public class BitronixTransaction
    extends org.objectledge.database.Transaction
{
    private final BitronixTransactionManager btm;

    public BitronixTransaction(BitronixTransactionManager btm, Context context, Logger logger)
    {
        super(btm.getTracingConfiguration().getDepth(), btm.getTracingConfiguration()
            .getDefaultTimeout(), context, logger);
        this.btm = btm;
    }

    @Override
    public UserTransaction getUserTransaction()
        throws SQLException
    {
        return btm.getUserTransaction();
    }

    @Override
    public TransactionManager getTransactionManager()
        throws SQLException
    {
        return btm.getTransactionManager();
    }
}
