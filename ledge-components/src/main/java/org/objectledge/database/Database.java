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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A convenience wrapper around database related components.
 * 
 * <p>Use this component to reduce the number of required constructor parameters in your database
 * dependant components, and make sure that they'll access the right data source, if you are using
 * the ThreadDataSource decorator.</p>
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Database.java,v 1.5 2004-02-10 10:13:23 fil Exp $
 */
public class Database
{
    /** the source of database connections. */
    private DataSource dataSource;
    
    /** the generator of per-table unique ids. */
    private IdGenerator idGenerator;
    
    /** the global transaction management helper object. */
    private Transaction transaction;
    
    /**
     * Creates a Database instance.
     * 
     * @param dataSource the source of database connections. 
     * @param idGenerator the generator of per-table unique ids.
     * @param transaction the global transaction management helper object.
     */
    public Database(DataSource dataSource, IdGenerator idGenerator, Transaction transaction)
    {
        this.dataSource = dataSource;
        this.idGenerator = idGenerator;
        this.transaction = transaction;
    }

    /**
     * Returns a database Connection produced by the DataSource.
     * 
     * @return a database Connection.
     * @throws SQLException if the conneciton could not be obtained.
     */ 
    public Connection getConnection()
        throws SQLException
    {
        return dataSource.getConnection();
    }
    
    /**
     * Returns a database Connection produced by the DataSource.
     * 
     * @param user the user to connect as.
     * @param password the user's password.
     * @return a database Connection.
     * @throws SQLException if the conneciton could not be obtained.
     */ 
    public Connection getConnection(String user, String password)
        throws SQLException
    {
        return dataSource.getConnection(user, password);
    }

    /**
     * Get the next row identifier for the table.
     * 
     * @param table the table name.
     * @return the identifier.
     * @throws SQLException if the id could not be generated.
     */
    public long getNextId(String table)
        throws SQLException
    {
        return idGenerator.getNextId(table);
    }
    
    /**
     * Begin the transaction, if there is none active.
     * 
     * @return <code>true</code> if the requestor become the controler.
     * @throws SQLException if the operation fails.
     */
    public boolean beginTransaction()
        throws SQLException
    {
        return transaction.begin();
    }
    
    /**
     * Commit the transaction, if the caller is the controller.
     * 
     * @param controller <code>true</code> if the caller is the controler.
     * @throws SQLException if the commit fails.
     */
    public void commitTransaction(boolean controller)
        throws SQLException
    {
        transaction.commit(controller);
    }
    
    /**
     * Rollback the transaction, if the caller is the controller.
     * 
     * @param controller <code>true</code> if the caller is the controler.
     * @throws SQLException if the rollback fails.
     */
    public void rollbackTransaction(boolean controller)
        throws SQLException
    {
        transaction.rollback(controller);
    }
}
