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

package org.objectledge.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.objectledge.database.ThreadDataSource.ThreadConnection;
import org.objectledge.database.impl.DelegatingPreparedStatement;

/**
 * PreparedStatement wrapper that monitors the number of DB reads/writes, and their duration.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: MonitoringPreparedStatement.java,v 1.1 2005-10-10 09:44:50 rafal Exp $
 */
public class MonitoringPreparedStatement
    extends DelegatingPreparedStatement
{
    private final ThreadConnection threadConn;

    /**
     * Creates a new MonitoringPreparedStatement instance.
     *
     * @param preparedStatement delegate prepared statement.
     * @param sql statement body.
     * @param threadConn the associated ThreadConnection.
     */
    public MonitoringPreparedStatement(PreparedStatement preparedStatement, String sql,
        ThreadConnection threadConn)
    {
        super(preparedStatement, sql);
        this.threadConn = threadConn;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute()
        throws SQLException
    {
        threadConn.startStatement(getBody());
        try
        {
            return super.execute();
        }
        finally
        {
            threadConn.finishStatement(getBody());
        }
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultSet executeQuery()
        throws SQLException
    {
        threadConn.startStatement(getBody());
        try
        {
            return super.executeQuery();
        }
        finally
        {
            threadConn.finishStatement(getBody());
        }
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public int executeUpdate()
        throws SQLException
    {
        threadConn.startStatement(getBody());
        try
        {
            return super.executeUpdate();
        }
        finally
        {
            threadConn.finishStatement(getBody());
        }
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] executeBatch()
        throws SQLException
    {
        threadConn.startStatement(getBatchBuffer());
        try
        {
            return super.executeBatch();
        }
        finally
        {
            threadConn.finishStatement(getBatchBuffer());
        }
    }    
}
