// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * A delegation pattern wrapper for java.sql.Statement.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: DelegatingStatement.java,v 1.2 2005-10-10 08:48:18 rafal Exp $
 */
public class DelegatingStatement
    implements Statement
{
    private final Statement statement;
    
    private final StringBuilder batchBuffer = new StringBuilder();
    
    /**
     * Creates a new DelegatingStatement instance.
     *
     * @param statement the delegate statement.
     */
    public DelegatingStatement(final Statement statement)
    {
        this.statement = statement;
    }
    
    /**
     * Returns the contents of the batch buffer.
     * 
     * @return the contents of the batch buffer.
     */
    protected String getBatchBuffer()
    {
        return batchBuffer.toString();
    }
    
    /**
     * Adds a statement to the batch buffer.
     * 
     * @param statement the statement body.
     */
    protected void addToBatchBuffer(String statement)
    {
        batchBuffer.append(statement);
        batchBuffer.append("\n");
    }    
    
    // .. Statement .............................................................................

    /**
     * {@inheritDoc}
     */
    public void addBatch(String sql)
        throws SQLException
    {
        addToBatchBuffer(sql);
        statement.addBatch(sql);
    }

    /**
     * {@inheritDoc}
     */
    public void clearBatch()
        throws SQLException
    {
        batchBuffer.setLength(0);
        statement.clearBatch();
    }

    /**
     * {@inheritDoc}
     */
    public int[] executeBatch()
        throws SQLException
    {
        batchBuffer.setLength(0);
        return statement.executeBatch();
    }
    
    /**
     * {@inheritDoc}
     */
    public ResultSet executeQuery(String sql)
        throws SQLException
    {
        return statement.executeQuery(sql);
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(String sql)
        throws SQLException
    {
        return statement.executeUpdate(sql);
    }

    /**
     * {@inheritDoc}
     */
    public void close()
        throws SQLException
    {
        statement.close();
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxFieldSize()
        throws SQLException
    {
        return statement.getMaxFieldSize();
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxFieldSize(int max)
        throws SQLException
    {
        statement.setMaxFieldSize(max);
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxRows()
        throws SQLException
    {
        return statement.getMaxRows();
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxRows(int max)
        throws SQLException
    {
        statement.setMaxRows(max);
    }

    /**
     * {@inheritDoc}
     */
    public void setEscapeProcessing(boolean enable)
        throws SQLException
    {
        statement.setEscapeProcessing(enable);
    }

    /**
     * {@inheritDoc}
     */
    public int getQueryTimeout()
        throws SQLException
    {
        return statement.getQueryTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setQueryTimeout(int seconds)
        throws SQLException
    {
        statement.setQueryTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    public void cancel()
        throws SQLException
    {
        statement.cancel();
    }

    /**
     * {@inheritDoc}
     */
    public SQLWarning getWarnings()
        throws SQLException
    {
        return statement.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    public void clearWarnings()
        throws SQLException
    {
        statement.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    public void setCursorName(String name)
        throws SQLException
    {
        statement.setCursorName(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(String sql)
        throws SQLException
    {
        return statement.execute(sql);
    }

    /**
     * {@inheritDoc}
     */
    public ResultSet getResultSet()
        throws SQLException
    {
        return statement.getResultSet();
    }

    /**
     * {@inheritDoc}
     */
    public int getUpdateCount()
        throws SQLException
    {
        return statement.getUpdateCount();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getMoreResults()
        throws SQLException
    {
        return statement.getMoreResults();
    }

    /**
     * {@inheritDoc}
     */
    public void setFetchDirection(int direction)
        throws SQLException
    {
        statement.setFetchDirection(direction);
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchDirection()
        throws SQLException
    {
        return statement.getFetchDirection();
    }

    /**
     * {@inheritDoc}
     */
    public void setFetchSize(int rows)
        throws SQLException
    {
        statement.setFetchSize(rows);
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchSize()
        throws SQLException
    {
        return statement.getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getResultSetConcurrency()
        throws SQLException
    {
        return statement.getResultSetConcurrency();
    }

    /**
     * {@inheritDoc}
     */
    public int getResultSetType()
        throws SQLException
    {
        return statement.getResultSetType();
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection()
        throws SQLException
    {
        return statement.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    public boolean getMoreResults(int current)
        throws SQLException
    {
        return statement.getMoreResults();
    }

    /**
     * {@inheritDoc}
     */
    public ResultSet getGeneratedKeys()
        throws SQLException
    {
        return statement.getGeneratedKeys();
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(String sql, int autoGeneratedKeys)
        throws SQLException
    {
        return statement.executeUpdate(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(String sql, int[] columnIndexes)
        throws SQLException
    {
        return statement.executeUpdate(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate(String sql, String[] columnNames)
        throws SQLException
    {
        return statement.executeUpdate(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(String sql, int autoGeneratedKeys)
        throws SQLException
    {
        return statement.execute(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(String sql, int[] columnIndexes)
        throws SQLException
    {
        return statement.execute(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute(String sql, String[] columnNames)
        throws SQLException
    {
        return statement.execute(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    public int getResultSetHoldability()
        throws SQLException
    {
        return statement.getResultSetHoldability();
    }
}
