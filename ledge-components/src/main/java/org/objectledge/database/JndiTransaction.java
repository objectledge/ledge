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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;

/**
 * Operates upon the UserTransaction object provided by the application server through JNDI.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: JndiTransaction.java,v 1.4 2005-10-03 07:28:24 rafal Exp $
 */
public class JndiTransaction extends Transaction
{
    /**
     * Constructs a JndiLogger instance.
     * 
     * @param tracing tracing depth.
     * @param defaultTimeout default transaction timeout in seconds.
     * @param context the threads processing context.
     * @param log the logger to use for error reporting.
     */
    public JndiTransaction(int tracing, int defaultTimeout, Context context, Logger log)
    {
        super(tracing, defaultTimeout, context, log);
    }
    
    /**
     * {@inheritDoc}
     */
    public UserTransaction getUserTransaction()
        throws SQLException
    {
        try
        {
            InitialContext ctx  = new InitialContext();
            return (UserTransaction)ctx.lookup("java:comp/UserTransaction");
        }
        catch(NamingException e)
        {
            throw (SQLException)new SQLException("failed to lookup UserTransaction object")
                .initCause(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public TransactionManager getTransactionManager()
        throws SQLException
    {
        throw new SQLException("TransactionManager is not accessible.");
    }
}
