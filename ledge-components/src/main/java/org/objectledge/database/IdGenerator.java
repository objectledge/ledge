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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.picocontainer.lifecycle.Stoppable;

/**
 * A component that generates unique, monotonous ids for table rows in a relational database. 
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: IdGenerator.java,v 1.2 2004-02-04 12:17:49 fil Exp $
 */
public class IdGenerator
    implements Stoppable
{
    private DataSource dataSource;
    
    private Connection conn;

    private PreparedStatement fetchStmt;
    
    private PreparedStatement insertStmt;
    
    private PreparedStatement updateStmt;

    /**
     * Creates an IdGenerator component instance.
     * 
     * @param dataSource the DataSource to connect to. 
     */
    public IdGenerator(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    /**
     * Get the next identifier of the table.
     * 
     * @param table the table name.
     * @return the identifier.
     * @throws SQLException if the id could not be generated.
     */
    public synchronized long getNextId(String table)
        throws SQLException
    {
        if(conn == null)
        {
            start();
        }
        try
        {
            return getNextIdInternal(table);
        }
        catch(SQLException e)
        {
            // The db server might have disconnected. Attempt to reconnect once.
            stop();
            start();
            return getNextIdInternal(table);
        }
    }

    private void start()
        throws SQLException
    {
        conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        fetchStmt = conn.
            prepareStatement("SELECT next_id FROM id_table WHERE table = ?");
        insertStmt = conn.
            prepareStatement("INSERT INTO id_table(table,next_id) VALUES(?, 1)");
        updateStmt = conn.
            prepareStatement("UPDATE id_table SET next_id = next_id + 1 WHERE table = ?");
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        DatabaseUtils.close(insertStmt);
        DatabaseUtils.close(fetchStmt);
        DatabaseUtils.close(updateStmt);
        DatabaseUtils.close(conn);
    }
    
    private long getNextIdInternal(String table)
        throws SQLException
    {
        fetchStmt.setString(1, table);
        ResultSet rs = fetchStmt.executeQuery();
        long id;
        if(!rs.next())
        {
            insertStmt.setString(1, table);
            insertStmt.execute();
            id = 0;
        }
        else
        {
            updateStmt.setString(1, table);
            updateStmt.execute();
            id = rs.getLong(1);
        }
        conn.commit();
        DatabaseUtils.close(rs);
        return id;
    }
}
