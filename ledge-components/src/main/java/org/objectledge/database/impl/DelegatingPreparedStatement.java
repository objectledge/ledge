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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * A delegation pattern wrapper for java.sql.PreparedStatement.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: DelegatingPreparedStatement.java,v 1.1 2005-10-07 14:50:00 rafal Exp $
 */
@SuppressWarnings("deprecation")
public class DelegatingPreparedStatement
    extends DelegatingStatement
    implements PreparedStatement
{
    private final PreparedStatement preparedStatement;
    
    /**
     * Creates a new DelegatingPreparedStatement instance.
     *
     * @param preparedStatement the delegate prepared statement.
     */
    public DelegatingPreparedStatement(final PreparedStatement preparedStatement)
    {
        super(preparedStatement);
        this.preparedStatement = preparedStatement;
    }
       
    // .. PreparedStatement ..................................................................... 
    
    /**
     * {@inheritDoc}
     */
    public void addBatch()
        throws SQLException
    {
        preparedStatement.addBatch();
    }

    /**
     * {@inheritDoc}
     */
    public void clearParameters()
        throws SQLException
    {
        preparedStatement.clearParameters();
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute()
        throws SQLException
    {
        return preparedStatement.execute();
    }

    /**
     * {@inheritDoc}
     */
    public ResultSet executeQuery()
        throws SQLException
    {
        return preparedStatement.executeQuery();
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate()
        throws SQLException
    {
        return preparedStatement.executeUpdate();
    }

    /**
     * {@inheritDoc}
     */
    public ResultSetMetaData getMetaData()
        throws SQLException
    {
        return preparedStatement.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    public ParameterMetaData getParameterMetaData()
        throws SQLException
    {
        return preparedStatement.getParameterMetaData();
    }

    /**
     * {@inheritDoc}
     */
    public void setArray(int i, Array x)
        throws SQLException
    {
        preparedStatement.setArray(i, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setBigDecimal(int parameterIndex, BigDecimal x)
        throws SQLException
    {
        preparedStatement.setBigDecimal(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setBlob(int i, Blob x)
        throws SQLException
    {
        preparedStatement.setBlob(i, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBoolean(int parameterIndex, boolean x)
        throws SQLException
    {
        preparedStatement.setBoolean(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setByte(int parameterIndex, byte x)
        throws SQLException
    {
        preparedStatement.setByte(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBytes(int parameterIndex, byte[] x)
        throws SQLException
    {
        preparedStatement.setBytes(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
        throws SQLException
    {
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setClob(int i, Clob x)
        throws SQLException
    {
        preparedStatement.setClob(i, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(int parameterIndex, Date x, Calendar cal)
        throws SQLException
    {
        preparedStatement.setDate(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(int parameterIndex, Date x)
        throws SQLException
    {
        preparedStatement.setDate(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDouble(int parameterIndex, double x)
        throws SQLException
    {
        preparedStatement.setDouble(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setFloat(int parameterIndex, float x)
        throws SQLException
    {
        preparedStatement.setFloat(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setInt(int parameterIndex, int x)
        throws SQLException
    {
        preparedStatement.setInt(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setLong(int parameterIndex, long x)
        throws SQLException
    {
        preparedStatement.setLong(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(int paramIndex, int sqlType, String typeName)
        throws SQLException
    {
        preparedStatement.setNull(paramIndex, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(int parameterIndex, int sqlType)
        throws SQLException
    {
        preparedStatement.setNull(parameterIndex, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
        throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType)
        throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x)
        throws SQLException
    {
        preparedStatement.setObject(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setRef(int i, Ref x)
        throws SQLException
    {
        preparedStatement.setRef(i, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setShort(int parameterIndex, short x)
        throws SQLException
    {
        preparedStatement.setShort(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setString(int parameterIndex, String x)
        throws SQLException
    {
        preparedStatement.setString(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(int parameterIndex, Time x, Calendar cal)
        throws SQLException
    {
        preparedStatement.setTime(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(int parameterIndex, Time x)
        throws SQLException
    {
        preparedStatement.setTime(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
        throws SQLException
    {
        preparedStatement.setTimestamp(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(int parameterIndex, Timestamp x)
        throws SQLException
    {
        preparedStatement.setTimestamp(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setURL(int parameterIndex, URL x)
        throws SQLException
    {
        preparedStatement.setURL(parameterIndex, x);
    }
}
