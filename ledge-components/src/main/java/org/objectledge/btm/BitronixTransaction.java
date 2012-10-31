package org.objectledge.btm;

import java.sql.SQLException;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.logging.LoggingConfigurator;

public class BitronixTransaction
    extends org.objectledge.database.Transaction
{
    private final BitronixTransactionManager btm;

    public BitronixTransaction(BitronixTransactionManager btm, Context context, Logger logger,
        LoggingConfigurator loggingConfigurator)
    {
        super(btm.getTansactionConfig(), context, logger, loggingConfigurator);
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
