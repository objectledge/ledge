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
package org.objectledge.database.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * A delegation pattern wrapper for java.sql.Connection.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegatingConnection.java,v 1.3 2005-10-07 14:50:00 rafal Exp $
 */
public class DelegatingConnection
    implements Connection
{
    /** The delegate Connection. */
    private Connection delegate;
    
    /**
     * Creates an instance of Connection wrapper.
     * 
     * @param delegate the deleage Connection.
     */
    public DelegatingConnection(Connection delegate)
    {
        this.delegate = delegate;
    }
    
    /**
     * Returns the delegate connection.
     * 
     * @return the delegate connection.
     */
    protected Connection getDelegate()
    {
        return delegate;
    }

    /**
     * Provides a Statement wrapper.
     * 
     * @param orig the original Statement object.
     * @return Statement wrapper.
     */
    protected Statement wrapStatement(Statement orig)
    {
        return orig;
    }
    
    /**
     * Provides a PreparedStatement wrapper.
     * 
     * @param orig the original PreparedStatement object.
     * @param sql statement body.
     * @return PreparedStatement wrapper.
     */
    protected PreparedStatement wrapPreparedStatement(PreparedStatement orig, String sql)
    {
        return orig;
    }
    
    /**
     * Provides a CallableStatement wrapper.
     * 
     * @param orig the original CallableStatement object.
     * @param sql statement body.
     * @return CallableStatement wrapper.
     */
    protected CallableStatement wrapCallableStatement(CallableStatement orig, String sql)
    {
        return orig;
    }
    
    // Connection interface /////////////////////////////////////////////////////////////////////
    ///CLOVER:OFF

    /** 
     * {@inheritDoc}
     */
    public void clearWarnings() throws SQLException
    {
        delegate.clearWarnings();       
    }

    /** 
     * {@inheritDoc}
     */
    public void close() throws SQLException
    {
        delegate.close();
    }

    /** 
     * {@inheritDoc}
     */
    public void commit() throws SQLException
    {
        delegate.commit();
    }

    /** 
     * {@inheritDoc}
     */
    public Statement createStatement() throws SQLException
    {
        return wrapStatement(delegate.createStatement());
    }

    /** 
     * {@inheritDoc}
     */
    public Statement createStatement(
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability)
        throws SQLException
    {
        return wrapStatement(delegate.createStatement(
            resultSetType,
            resultSetConcurrency,
            resultSetHoldability));
    }

    /** 
     * {@inheritDoc}
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return wrapStatement(delegate.createStatement(
            resultSetType,
            resultSetConcurrency));
    }

    /** 
     * {@inheritDoc}
     */
    public boolean getAutoCommit() throws SQLException
    {
        return delegate.getAutoCommit();
    }

    /** 
     * {@inheritDoc}
     */
    public String getCatalog() throws SQLException
    {
        return delegate.getCatalog();
    }

    /** 
     * {@inheritDoc}
     */
    public int getHoldability() throws SQLException
    {
        return delegate.getHoldability();
    }

    /** 
     * {@inheritDoc}
     */
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return delegate.getMetaData();
    }

    /** 
     * {@inheritDoc}
     */
    public int getTransactionIsolation() throws SQLException
    {
        return delegate.getTransactionIsolation();
    }

    /** 
     * {@inheritDoc}
     */
    public Map getTypeMap() throws SQLException
    {
        return delegate.getTypeMap();
    }

    /** 
     * {@inheritDoc}
     */
    public SQLWarning getWarnings() throws SQLException
    {
        return delegate.getWarnings();
    }

    /** 
     * {@inheritDoc}
     */
    public boolean isClosed() throws SQLException
    {
        return delegate.isClosed();
    }

    /** 
     * {@inheritDoc}
     */
    public boolean isReadOnly() throws SQLException
    {
        return delegate.isReadOnly();
    }

    /** 
     * {@inheritDoc}
     */
    public String nativeSQL(String sql) throws SQLException
    {
        return delegate.nativeSQL(sql);
    }

    /** 
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(
        String sql,
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability)
        throws SQLException
    {
        return wrapCallableStatement(delegate.prepareCall(
            sql,
            resultSetType,
            resultSetConcurrency,
            resultSetHoldability
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
        throws SQLException
    {
        return wrapCallableStatement(delegate.prepareCall(
            sql,
            resultSetType,
            resultSetConcurrency
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(String sql) throws SQLException
    {
        return wrapCallableStatement(delegate.prepareCall(sql), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(
        String sql,
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability)
        throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(
            sql,
            resultSetType,
            resultSetConcurrency,
            resultSetHoldability
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(
        String sql,
        int resultSetType,
        int resultSetConcurrency)
        throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(
            sql,
            resultSetType,
            resultSetConcurrency
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(
            sql, 
            autoGeneratedKeys
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(
            sql, 
            columnIndexes
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(
            sql, 
            columnNames
        ), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
        return wrapPreparedStatement(delegate.prepareStatement(sql), sql);
    }

    /** 
     * {@inheritDoc}
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
        delegate.releaseSavepoint(savepoint);
    }

    /** 
     * {@inheritDoc}
     */
    public void rollback() throws SQLException
    {
        delegate.rollback();
    }

    /** 
     * {@inheritDoc}
     */
    public void rollback(Savepoint savepoint) throws SQLException
    {
        delegate.rollback(savepoint);
    }

    /** 
     * {@inheritDoc}
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        delegate.setAutoCommit(autoCommit);
    }

    /** 
     * {@inheritDoc}
     */
    public void setCatalog(String catalog) throws SQLException
    {
        delegate.setCatalog(catalog);
    }

    /** 
     * {@inheritDoc}
     */
    public void setHoldability(int holdability) throws SQLException
    {
        delegate.setHoldability(holdability);
    }

    /** 
     * {@inheritDoc}
     */
    public void setReadOnly(boolean readOnly) throws SQLException
    {
        delegate.setReadOnly(readOnly);
    }

    /** 
     * {@inheritDoc}
     */
    public Savepoint setSavepoint() throws SQLException
    {
        return delegate.setSavepoint();
    }

    /** 
     * {@inheritDoc}
     */
    public Savepoint setSavepoint(String name) throws SQLException
    {
        return delegate.setSavepoint(name);
    }

    /** 
     * {@inheritDoc}
     */
    public void setTransactionIsolation(int level) throws SQLException
    {
        delegate.setTransactionIsolation(level);
    }

    /** 
     * {@inheritDoc}
     */
    public void setTypeMap(Map map) throws SQLException
    {
        delegate.setTypeMap(map);
    }
}
