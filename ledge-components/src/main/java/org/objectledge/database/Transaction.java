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

import java.sql.SQLException;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Logger;

/**
 * Helps dealing with transactions in the application code.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Transaction.java,v 1.2 2004-02-03 14:38:08 fil Exp $
 */
public abstract class Transaction
{
    /** the logger. */
    protected Logger log;

    /**
     * Constructs a Transaction component.
     * 
     * @param log the logger to use for error reporting.
     */
    protected Transaction(Logger log)
    {
        this.log = log;
    }
    
    /**
     * Returns the UserTransaction object.
     * 
     * @return the UserTransaction object.
     * @throws SQLException if the UserTransaction object is not accessible.
     */
    public abstract UserTransaction getUserTransaction()
        throws SQLException;
    
    /**
     * Begin the transaction, if there is none active.
     * 
     * @return <code>true</code> if the requestor become the controler.
     * @throws SQLException if the operation fails.
     */
    public boolean begin()
        throws SQLException
    {
        boolean controler;
        try 
        {
            int status = getUserTransaction().getStatus();
            controler = (status == Status.STATUS_NO_TRANSACTION ||
                         status == Status.STATUS_COMMITTED ||
                         status == Status.STATUS_ROLLEDBACK);
        }
        catch(Exception e)
        {
            log.error("failed to check transaction status", e);
            throw (SQLException)new SQLException("failed to check transaction status").initCause(e);
        }
        if(controler)
        {
            try
            {
                getUserTransaction().begin();
            }
            catch(Exception e)
            {
                log.error("failed to begin transaction", e);
                throw (SQLException)new SQLException("failed to begin transaction").initCause(e);
            }
        }  
        return controler;
    }
    
    /**
     * Commit the transaction.
     * 
     * @param controler the controler.
     * @throws SQLException if the commit fails.
     */
    public void commit(boolean controler)
        throws SQLException
    {
        if(controler)
        {
            try
            {
                getUserTransaction().commit();
            }
            catch(Exception e)
            {
                log.error("commit failed", e);
                throw (SQLException)new SQLException("commit failed").initCause(e);
            }
        }
    }
    
    /**
     * Rollback the transaction.
     * 
     * @param controler the controler.
     * @throws SQLException if the rollback fails.
     */
    public void rollback(boolean controler)
        throws SQLException
    {
        if(controler)
        {
            try
            {
                getUserTransaction().rollback();
            }
            catch(Exception e)
            {
                log.error("rollback failed", e);
                throw (SQLException)new SQLException("rollback failed"+e).initCause(e);
            }
        }
        else
        {
            try
            {
                getUserTransaction().setRollbackOnly();
            }
            catch(Exception e)
            {
                log.error("setRollbackOnly failed", e);
                throw (SQLException)new SQLException("setRollbackOnly failed").initCause(e);
            }
        }
    }
}
