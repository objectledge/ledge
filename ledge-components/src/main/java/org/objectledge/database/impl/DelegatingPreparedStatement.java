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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A delegation pattern wrapper for java.sql.PreparedStatement.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: DelegatingPreparedStatement.java,v 1.4 2005-10-10 09:44:28 rafal Exp $
 */
@SuppressWarnings("deprecation")
public class DelegatingPreparedStatement
    extends DelegatingStatement
    implements PreparedStatement
{
    private final PreparedStatement preparedStatement;
    
    private final String sql;
    
    private final ArrayList<Object> parameterList = new ArrayList<Object>();
    
    /**
     * Creates a new DelegatingPreparedStatement instance.
     *
     * @param preparedStatement the delegate prepared statement.
     * @param sql the statement body.
     */
    public DelegatingPreparedStatement(final PreparedStatement preparedStatement, final String sql)
    {
        super(preparedStatement);
        this.preparedStatement = preparedStatement;
        this.sql = sql;
    }
    
    private void setParameter(int parameterIndex, Object value)
    {
        parameterList.ensureCapacity(parameterIndex);
        if(parameterIndex <= parameterList.size())
        {
            parameterList.set(parameterIndex - 1, value);
        }
        else
        {
            for(int i  = parameterList.size() + 1; i < parameterIndex; i++)
            {
                parameterList.add(null);
            }
            parameterList.add(value);
        }
    }
    
    /**
     * Returns the statements SQL string.
     * 
     * @return the statements SQL string.
     */
    protected String getSQL()
    {
        return sql;
    }
    
    /**
     * Returns the current parameter list.
     * 
     * @return the current parameter list.
     */
    protected List<Object> getParameterList()
    {
        return parameterList;
    }
    
    /**
     * Clear parmateter information stored by the wrapper.
     * 
     * <p>remember to invoke super.clearParameters2()</p>
     */
    protected void clearParameters2()
    {
        parameterList.clear();        
    }

    /**
     * Returns the statement body with current parameters.
     * 
     * @return the statement body with current parameters.
     */
    protected String getBody()
    {
        StringBuilder body = new StringBuilder();
        body.append(sql);
        if(!parameterList.isEmpty())
        {
            body.append(" with ");
            body.append(parameterList.toString());
        }
        return body.toString();
    }
       
    // .. PreparedStatement ..................................................................... 
    
    /**
     * {@inheritDoc}
     */
    public void addBatch()
        throws SQLException
    {
        addToBatchBuffer(getBody());
        preparedStatement.addBatch();
    }

    /**
     * {@inheritDoc}
     */
    public void clearParameters()
        throws SQLException
    {
        clearParameters2();
        preparedStatement.clearParameters();
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute()
        throws SQLException
    {
        clearParameters2();
        return preparedStatement.execute();
    }

    /**
     * {@inheritDoc}
     */
    public ResultSet executeQuery()
        throws SQLException
    {
        clearParameters2();
        return preparedStatement.executeQuery();
    }

    /**
     * {@inheritDoc}
     */
    public int executeUpdate()
        throws SQLException
    {
        clearParameters2();
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
    public void setArray(int parameterIndex, Array x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setArray(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setBigDecimal(int parameterIndex, BigDecimal x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setBigDecimal(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setBlob(int parameterIndex, Blob x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setBlob(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBoolean(int parameterIndex, boolean x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setBoolean(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setByte(int parameterIndex, byte x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setByte(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBytes(int parameterIndex, byte[] x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setBytes(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
        throws SQLException
    {
        setParameter(parameterIndex, reader);
        preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setClob(int parameterIndex, Clob x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setClob(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(int parameterIndex, Date x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setDate(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(int parameterIndex, Date x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setDate(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDouble(int parameterIndex, double x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setDouble(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setFloat(int parameterIndex, float x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setFloat(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setInt(int parameterIndex, int x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setInt(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setLong(int parameterIndex, long x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setLong(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(int parameterIndex, int sqlType, String typeName)
        throws SQLException
    {
        setParameter(parameterIndex, "NULL");
        preparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(int parameterIndex, int sqlType)
        throws SQLException
    {
        setParameter(parameterIndex, "NULL");
        preparedStatement.setNull(parameterIndex, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(int parameterIndex, Object x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setObject(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setRef(int parameterIndex, Ref x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setRef(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setShort(int parameterIndex, short x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setShort(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setString(int parameterIndex, String x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setString(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(int parameterIndex, Time x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setTime(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(int parameterIndex, Time x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setTime(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setTimestamp(parameterIndex, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(int parameterIndex, Timestamp x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setTimestamp(parameterIndex, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setUnicodeStream(parameterIndex, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setURL(int parameterIndex, URL x)
        throws SQLException
    {
        setParameter(parameterIndex, x);
        preparedStatement.setURL(parameterIndex, x);
    }
}
