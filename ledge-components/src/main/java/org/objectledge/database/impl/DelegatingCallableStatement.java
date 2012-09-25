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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A delegation pattern wrapper for java.sql.CallableStatement.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: DelegatingCallableStatement.java,v 1.3 2008-06-04 22:55:44 rafal Exp $
 */
@SuppressWarnings("deprecation")
public class DelegatingCallableStatement
    extends DelegatingPreparedStatement
    implements CallableStatement
{

    private final CallableStatement callableStatement;
    
    private final Map<String, Object> parameterMap = new HashMap<String, Object>();

    /**
     * Creates a new DelegatingCallableStatement instance.
     *
     * @param callableStatement the delegate callable statement.
     * @param sql the statement body.
     */
    public DelegatingCallableStatement(CallableStatement callableStatement, String sql)
    {
        super(callableStatement, sql);
        this.callableStatement = callableStatement;
    }
    
    private void setParameter(String parameterName, Object value)
    {
        parameterMap.put(parameterName, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void clearParameters2()
    {
        super.clearParameters2();
        parameterMap.clear();        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBody()
    {
        List<Object> parameterList = getParameterList();
        StringBuilder body = new StringBuilder();
        body.append(getSQL());
        if(!parameterList.isEmpty() || !parameterMap.isEmpty())
        {
            body.append(" with ");
        }
        if(!parameterList.isEmpty())
        {
            body.append(parameterList.toString());
        }
        if(!parameterList.isEmpty() && !parameterMap.isEmpty())
        {
            body.append(", ");
        }
        if(!parameterMap.isEmpty())
        {
            body.append(parameterMap.toString());
        }
        return body.toString();
    }

    // .. CallableStatement .....................................................................
    
    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(int parameterIndex, int sqlType)
        throws SQLException
    {
        callableStatement.registerOutParameter(parameterIndex, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(int parameterIndex, int sqlType, int scale)
        throws SQLException
    {
        callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
    }

    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(int paramIndex, int sqlType, String typeName)
        throws SQLException
    {
        callableStatement.registerOutParameter(paramIndex, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(String parameterName, int sqlType)
        throws SQLException
    {
        callableStatement.registerOutParameter(parameterName, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(String parameterName, int sqlType, int scale)
        throws SQLException
    {
        callableStatement.registerOutParameter(parameterName, sqlType, scale);
    }

    /**
     * {@inheritDoc}
     */
    public void registerOutParameter(String parameterName, int sqlType, String typeName)
        throws SQLException
    {
        callableStatement.registerOutParameter(parameterName, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean wasNull()
        throws SQLException
    {
        return callableStatement.wasNull();
    }

    /**
     * {@inheritDoc}
     */
    public String getString(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getString(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getBoolean(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public byte getByte(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getByte(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public short getShort(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getShort(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getInt(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getLong(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getFloat(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public double getDouble(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getDouble(parameterIndex);
    }
    
    /**
     * {@inheritDoc}
     */
    public BigDecimal getBigDecimal(int parameterIndex)
    throws SQLException
    {
        return callableStatement.getBigDecimal(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getBigDecimal(int parameterIndex, int scale)
        throws SQLException
    {
        return callableStatement.getBigDecimal(parameterIndex, scale);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getBytes(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getBytes(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getDate(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Time getTime(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getTime(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Timestamp getTimestamp(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getTimestamp(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getObject(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(int parameterIndex, Map<String, Class<?>> typeMap)
        throws SQLException
    {
        return callableStatement.getObject(parameterIndex, typeMap);
    }

    /**
     * {@inheritDoc}
     */
    public Ref getRef(int i)
        throws SQLException
    {
        return callableStatement.getRef(i);
    }

    /**
     * {@inheritDoc}
     */
    public Blob getBlob(int i)
        throws SQLException
    {
        return callableStatement.getBlob(i);
    }

    /**
     * {@inheritDoc}
     */
    public Clob getClob(int i)
        throws SQLException
    {
        return callableStatement.getClob(i);
    }

    /**
     * {@inheritDoc}
     */
    public Array getArray(int i)
        throws SQLException
    {
        return callableStatement.getArray(i);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(int parameterIndex, Calendar cal)
        throws SQLException
    {
        return callableStatement.getDate(parameterIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    public Time getTime(int parameterIndex, Calendar cal)
        throws SQLException
    {
        return callableStatement.getTime(parameterIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    public Timestamp getTimestamp(int parameterIndex, Calendar cal)
        throws SQLException
    {
        return callableStatement.getTimestamp(parameterIndex, cal);
    }

    /**
     * {@inheritDoc}
     */
    public URL getURL(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getURL(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public String getString(String parameterName)
        throws SQLException
    {
        return callableStatement.getString(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String parameterName)
        throws SQLException
    {
        return callableStatement.getBoolean(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public byte getByte(String parameterName)
        throws SQLException
    {
        return callableStatement.getByte(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public short getShort(String parameterName)
        throws SQLException
    {
        return callableStatement.getShort(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(String parameterName)
        throws SQLException
    {
        return callableStatement.getInt(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(String parameterName)
        throws SQLException
    {
        return callableStatement.getLong(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String parameterName)
        throws SQLException
    {
        return callableStatement.getFloat(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public double getDouble(String parameterName)
        throws SQLException
    {
        return callableStatement.getDouble(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getBytes(String parameterName)
        throws SQLException
    {
        return callableStatement.getBytes(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String parameterName)
        throws SQLException
    {
        return callableStatement.getDate(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Time getTime(String parameterName)
        throws SQLException
    {
        return callableStatement.getTime(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Timestamp getTimestamp(String parameterName)
        throws SQLException
    {
        return callableStatement.getTimestamp(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getBigDecimal(String parameterName)
        throws SQLException
    {
        return callableStatement.getBigDecimal(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(String parameterName)
        throws SQLException
    {
        return callableStatement.getObject(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject(String parameterName, Map<String, Class<?>> typeMap)
        throws SQLException
    {
        return callableStatement.getObject(parameterName, typeMap);
    }

    /**
     * {@inheritDoc}
     */
    public Ref getRef(String parameterName)
        throws SQLException
    {
        return callableStatement.getRef(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Blob getBlob(String parameterName)
        throws SQLException
    {
        return callableStatement.getBlob(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Clob getClob(String parameterName)
        throws SQLException
    {
        return callableStatement.getClob(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Array getArray(String parameterName)
        throws SQLException
    {
        return callableStatement.getArray(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Date getDate(String parameterName, Calendar cal)
        throws SQLException
    {
        return callableStatement.getDate(parameterName, cal);
    }

    /**
     * {@inheritDoc}
     */
    public Time getTime(String parameterName, Calendar cal)
        throws SQLException
    {
        return callableStatement.getTime(parameterName, cal);
    }

    /**
     * {@inheritDoc}
     */
    public Timestamp getTimestamp(String parameterName, Calendar cal)
        throws SQLException
    {
        return callableStatement.getTimestamp(parameterName, cal);
    }

    /**
     * {@inheritDoc}
     */
    public URL getURL(String parameterName)
        throws SQLException
    {
        return callableStatement.getURL(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public void setURL(String parameterName, URL x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setURL(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(String parameterName, int sqlType)
        throws SQLException
    {
        setParameter(parameterName, "NULL");
        callableStatement.setNull(parameterName, sqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setBoolean(String parameterName, boolean x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBoolean(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setByte(String parameterName, byte x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setByte(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setShort(String parameterName, short x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setShort(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setInt(String parameterName, int x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setInt(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setLong(String parameterName, long x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setLong(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setFloat(String parameterName, float x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setFloat(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDouble(String parameterName, double x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setDouble(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBigDecimal(String parameterName, BigDecimal x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBigDecimal(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setString(String parameterName, String x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setString(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setBytes(String parameterName, byte[] x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBytes(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(String parameterName, Date x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setDate(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(String parameterName, Time x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setTime(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(String parameterName, Timestamp x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setTimestamp(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setAsciiStream(String parameterName, InputStream x, int length)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setAsciiStream(parameterName, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setBinaryStream(String parameterName, InputStream x, int length)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBinaryStream(parameterName, x, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(String parameterName, Object x, int targetSqlType, int scale)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setObject(parameterName, x, targetSqlType, scale);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(String parameterName, Object x, int targetSqlType)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setObject(parameterName, x, targetSqlType);
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(String parameterName, Object x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setObject(parameterName, x);
    }

    /**
     * {@inheritDoc}
     */
    public void setCharacterStream(String parameterName, Reader reader, int length)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setCharacterStream(parameterName, reader, length);
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(String parameterName, Date x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setDate(parameterName, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTime(String parameterName, Time x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setTime(parameterName, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setTimestamp(parameterName, x, cal);
    }

    /**
     * {@inheritDoc}
     */
    public void setNull(String parameterName, int sqlType, String typeName)
        throws SQLException
    {
        setParameter(parameterName, "NULL");
        callableStatement.setNull(parameterName, sqlType, typeName);
    }

    /**
     * {@inheritDoc}
     */
    public Reader getCharacterStream(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getCharacterStream(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Reader getCharacterStream(String parameterName)
        throws SQLException
    {
        return callableStatement.getCharacterStream(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public Reader getNCharacterStream(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getNCharacterStream(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Reader getNCharacterStream(String parameterName)
        throws SQLException
    {
        return callableStatement.getNCharacterStream(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public NClob getNClob(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getNClob(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public NClob getNClob(String parameterName)
        throws SQLException
    {
        return callableStatement.getNClob(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public String getNString(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getNString(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public String getNString(String parameterName)
        throws SQLException
    {
        return callableStatement.getNString(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public RowId getRowId(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getRowId(parameterIndex);
    }

    /**
     * {@inheritDoc}
     */
    public RowId getRowId(String parameterName)
        throws SQLException
    {
        return callableStatement.getRowId(parameterName);
    }

    /**
     * {@inheritDoc}
     */
    public SQLXML getSQLXML(int parameterIndex)
        throws SQLException
    {
        return callableStatement.getSQLXML(parameterIndex);
    }
    
    /**
     * {@inheritDoc}
     */
    public SQLXML getSQLXML(String parameterName)
        throws SQLException
    {
        return callableStatement.getSQLXML(parameterName);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAsciiStream(String parameterName, InputStream x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setAsciiStream(parameterName, x);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAsciiStream(String parameterName, InputStream x, long length)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setAsciiStream(parameterName, x, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBinaryStream(String parameterName, InputStream x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBinaryStream(parameterName, x);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBinaryStream(String parameterName, InputStream x, long length)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBinaryStream(parameterName, x, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBlob(String parameterName, Blob x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setBlob(parameterName, x);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBlob(String parameterName, InputStream inputStream)
        throws SQLException
    {
        setParameter(parameterName, inputStream);
        callableStatement.setBlob(parameterName, inputStream);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBlob(String parameterName, InputStream inputStream, long length)
        throws SQLException
    {
        setParameter(parameterName, inputStream);
        callableStatement.setBlob(parameterName, inputStream, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCharacterStream(String parameterName, Reader reader)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setCharacterStream(parameterName, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCharacterStream(String parameterName, Reader reader, long length)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setCharacterStream(parameterName, reader, length);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setClob(String parameterName, Clob x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setClob(parameterName, x);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setClob(String parameterName, Reader reader)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setClob(parameterName, reader);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setClob(String parameterName, Reader reader, long length)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setClob(parameterName, reader, length);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNCharacterStream(String parameterName, Reader reader)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setNCharacterStream(parameterName, reader);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNCharacterStream(String parameterName, Reader reader, long length)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setNCharacterStream(parameterName, reader, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNClob(String parameterName, NClob value)
        throws SQLException
    {
        setParameter(parameterName, value);
        callableStatement.setNClob(parameterName, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNClob(String parameterName, Reader reader)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setNClob(parameterName, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNClob(String parameterName, Reader reader, long length)
        throws SQLException
    {
        setParameter(parameterName, reader);
        callableStatement.setNClob(parameterName, reader, length);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setNString(String parameterName, String value)
        throws SQLException
    {
        setParameter(parameterName, value);
        callableStatement.setNString(parameterName, value);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setRowId(String parameterName, RowId x)
        throws SQLException
    {
        setParameter(parameterName, x);
        callableStatement.setRowId(parameterName, x);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSQLXML(String parameterName, SQLXML xmlObject)
        throws SQLException
    {
        setParameter(parameterName, xmlObject);
        callableStatement.setSQLXML(parameterName, xmlObject);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isWrapperFor(Class<? > iface)
        throws SQLException
    {
        if(iface.equals(Statement.class) || iface.equals(PreparedStatement.class)
            || iface.equals(CallableStatement.class))
        {
            return true;
        }
        else
        {
            return callableStatement.isWrapperFor(iface);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T>T unwrap(Class<T> iface)
        throws SQLException
    {
        if(iface.equals(Statement.class) || iface.equals(PreparedStatement.class)
            || iface.equals(CallableStatement.class))
        {
            return iface.cast(this);
        }
        else
        {
            return callableStatement.unwrap(iface);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(int parameterIndex, Class<T> type)
        throws SQLException
    {
        return callableStatement.getObject(parameterIndex, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(String parameterName, Class<T> type)
        throws SQLException
    {
        return callableStatement.getObject(parameterName, type);
    }
}
