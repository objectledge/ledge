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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * A delegation pattern wrapper for a DataSource interface.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DelegatingDataSource.java,v 1.5 2008-06-04 22:55:44 rafal Exp $
 */
public abstract class DelegatingDataSource implements DataSource
{
    /** The underlying data source. */
    private DataSource dataSource;

    /**
     * Creates a wrapper instance.
     * 
     * @param dataSource the dataSource to deleage to.
     */    
    protected DelegatingDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    
    /**
     * Returns the underlying DataSource.
     * 
     * @return the underlying DataSource.
     */
    protected DataSource getDelegate()
    {
        return dataSource;
    }
    
    /**
     * {@inheritDoc}
     */
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    ///CLOVER:OFF
    /**
     * {@inheritDoc}
     */
    public int getLoginTimeout() throws SQLException
    {
        return dataSource.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     */
    public void setLoginTimeout(int seconds) throws SQLException
    {
        dataSource.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    public PrintWriter getLogWriter() throws SQLException
    {
        return dataSource.getLogWriter();
    }

    /**
     * {@inheritDoc}
     */
    public void setLogWriter(PrintWriter out) throws SQLException
    {
        dataSource.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    public Connection getConnection(String username, String password) throws SQLException
    {
        return dataSource.getConnection(username, password);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWrapperFor(Class<? > iface)
        throws SQLException
    {
        if(iface.equals(DataSource.class))
        {
            return true;
        }
        else
        {
            return dataSource.isWrapperFor(iface);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T>T unwrap(Class<T> iface)
        throws SQLException
    {
        if(iface.equals(DataSource.class))
        {
            return iface.cast(this);
        }
        else
        {
            return dataSource.unwrap(iface);
        }        
    }
}
