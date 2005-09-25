// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
package org.objectledge.database;

import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.logging.LoggingConfigurator;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;
import org.picocontainer.Startable;

/**
 * An implementation of the Transaction interface using JOTM.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: JotmTransaction.java,v 1.11 2005-09-25 12:03:49 rafal Exp $
 */
public class JotmTransaction
    extends Transaction
    implements Startable
{
    private TMService tmService;
    
    /**
     * Creates an instance of JOTM based transaction manager.
     * 
     * @param tracing tracing depth.
     * @param transactionTimeout transaction timeout in seconds.
     * @param context the threads processing context.
     * @param log the logger to use.
     * @param loggingConfigurator enforces instantiation order on Pico, may be null.
     * @throws NamingException if the manager could not be initialized.
     * @throws SystemException if transaction timeout could not be set.
     */
    public JotmTransaction(int tracing, int transactionTimeout, Context context, Logger log, 
        LoggingConfigurator loggingConfigurator)
        throws NamingException, SystemException
    {
        super(tracing, context, log);
        tmService = new Jotm(true, false);
        tmService.getTransactionManager().setTransactionTimeout(transactionTimeout);
    }
    
    /**
     * Creates an instance of JOTM based transaction manager.
     *
     * @param config component configuration.
     * @param context the threads processing context.
     * @param log the logger to use.
     * @param loggingConfigurator enforces instantiation order on Pico, may be null.
     * @throws NamingException if the manager could not be initialized.
     * @throws SystemException if transaction timeout could not be set.
     */    
    public JotmTransaction(Configuration config, Context context, Logger log, 
        LoggingConfigurator loggingConfigurator) 
        throws NamingException, SystemException
    {
        this(config.getChild("tracing").getValueAsInteger(0), 
            config.getChild("transactionTimeout").getValueAsInteger(0), context, log, 
            loggingConfigurator);
    }
    
    /**
     * {@inheritDoc}
     */
    public UserTransaction getUserTransaction()
    {
        return tmService.getUserTransaction();
    }

    /**
     * {@inheritDoc}
     */
    public TransactionManager getTransactionManager()
    {
        return tmService.getTransactionManager();
    }
    
    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // I wisht Startable interface was split
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        tmService.stop();
    }
}
