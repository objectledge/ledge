// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//   
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//   
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package org.objectledge.database.persistence;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.objectledge.database.DatabaseUtils;

/**
 * An implementation of {@link DefaultInputRecord} that reads data from a
 * <code>java.sql.ResultSet</code>.
 */
public class DefaultInputRecord
    implements InputRecord
{
    // Member objects ////////////////////////////////////////////////////////

    private final Map<String, Object> data;

    /** flyweight marker for SQL NULL values */
    private static final Object NULL = new Object();

    private static final Set<String> BOOLEAN_TRUE_LITERALS = new HashSet<String>(Arrays.asList(
        "true", "t", "y"));

    // Initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a <code>DefaultInputRecord</code> instance.
     * <p>
     * {@code ResultSet} must be open and in a valid position to read data.
     * </p>
     * <p>
     * The constructor will not move the {@code ResultSet} position, close it or otherwise modify
     * it. This object will not store a reference to the result set.
     * </p>
     * 
     * @param rs the <code>ResultSet</code>.
     * @throws SQLException
     */
    public DefaultInputRecord(ResultSet rs)
        throws SQLException
    {
        ResultSetMetaData md = rs.getMetaData();
        data = new HashMap<String, Object>(md.getColumnCount());
        for(int i = 1; i <= md.getColumnCount(); i++)
        {
            final Object value = rs.getObject(i);
            final String name = md.getColumnName(i).toUpperCase();
            if(rs.wasNull())
            {
                data.put(name, NULL);
            }
            else
            {
                data.put(name, value);
            }
        }
    }

    /**
     * @param field
     * @return
     * @throws PersistenceException
     */
    private Object getNotNull(String field)
        throws PersistenceException
    {
        Object value = data.get(field.toUpperCase());
        if(value == null)
        {
            throw new PersistenceException(field + " not found");
        }
        if(value == NULL)
        {
            throw new PersistenceException(field + " is NULL");
        }
        return value;
    }

    // DefaultInputRecord interface /////////////////////////////////////////////////

    /**
     * Returns a <code>boolean</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as boolean.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public boolean getBoolean(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Boolean)
        {
            return ((Boolean)value).booleanValue();
        }
        if(value instanceof Number)
        {
            return ((Number)value).intValue() != 0;
        }
        if(value instanceof String)
        {
            return BOOLEAN_TRUE_LITERALS.contains(((String)value).toLowerCase());
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>byte</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as byte.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public byte getByte(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).byteValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>short</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as short.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public short getShort(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).shortValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns an <code>int</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as integer.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public int getInteger(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).intValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>long</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as long.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public long getLong(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).longValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>BigDecimal</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as big decimal.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public BigDecimal getBigDecimal(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof BigDecimal)
        {
            return (BigDecimal)value;
        }
        if(value instanceof Integer || value instanceof Long)
        {
            return BigDecimal.valueOf(((Number)value).longValue());
        }
        if(value instanceof Float || value instanceof Double)
        {
            return BigDecimal.valueOf(((Number)value).doubleValue());
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>float</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as float.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public float getFloat(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).floatValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>double</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as double.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public double getDouble(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Number)
        {
            return ((Number)value).doubleValue();
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>String</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as string.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public String getString(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof String)
        {
            return DatabaseUtils.unescapeSqlString((String)value);
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>byte</code> array field value.
     * <p>
     * String value read from the database will be BASE64 decoded to obtain byte array.
     * </p>
     * 
     * @param field the name of the field.
     * @return the field value as array of byte.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public byte[] getBytes(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value.getClass().isArray() && value.getClass().getComponentType().equals(Byte.TYPE))
        {
            return (byte[])value;
        }
        if(value instanceof String)
        {
            Base64.decodeBase64((String)value);
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>Date</code> field value.
     * 
     * @param field the name of the field.
     * @return the field value as date.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public Date getDate(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof Date)
        {
            return new Date(((Date)value).getTime());
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>URL</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the specified value.
     */
    public URL getURL(String field)
        throws PersistenceException
    {
        Object value = getNotNull(field);
        if(value instanceof URL)
        {
            return (URL)value;
        }
        if(value instanceof String)
        {
            try
            {
                return new URL((String)value);
            }
            catch(MalformedURLException e)
            {
                throw new PersistenceException("invalid URL value " + value + " for " + field, e);
            }
        }
        throw new PersistenceException(field + " has unsupported type " + value.getClass());
    }

    /**
     * Returns a <code>Object</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the specified value.
     */
    public Object getObject(String field)
        throws PersistenceException
    {
        return getNotNull(field);
    }

    /**
     * Returns <code>true</code> if the field has <code>SQL NULL</code> value.
     * 
     * @param field the name of the field.
     * @return <code>true</code> if null.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public boolean isNull(String field)
        throws PersistenceException
    {
        return data.get(field.toUpperCase()) == NULL;
    }

    // statements ///////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a select statement for fetching an object from the database.
     * 
     * @param object Persistent object.
     * @param conn the connection to use.
     * @return a prepared statement.
     * @throws PersistenceException if there is a problem retrieving key values from the object.
     * @throws SQLException if there is a problem creating the statement.
     */
    static PreparedStatement getSelectStatement(Persistent object, Connection conn)
        throws PersistenceException, SQLException
    {
        DefaultOutputRecord out = new DefaultOutputRecord(object);
        object.getData(out);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + object.getTable()
            + " WHERE " + out.getWhereClause());
        out.setValues(stmt, true, false, 1);
        return stmt;
    }

    /**
     * Creates a select statement for fetching an object from the database.
     * 
     * @param object Persistent object.
     * @param conn the connection to use.
     * @param key the key value.
     * @return a prepared statement.
     * @throws SQLException if there is a problem creating the statement.
     */
    static PreparedStatement getSelectStatement(Persistent object, Connection conn, long key)
        throws SQLException
    {
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + object.getTable()
            + " WHERE " + object.getKeyColumns()[0] + " = ?");
        pstmt.setLong(1, key);
        return pstmt;
    }

    /**
     * Creates a select statement for fetching an object from the database.
     * 
     * @param object Persistent object.
     * @param conn the connection to use.
     * @param where WHERE clause, or <code>null</code> to fetch all objects.
     * @param parameters optional values for parameter placeholders in the where clause. Null values
     *        are not supported, because {@link PreparedStatement#setNull(int, int)} requires SQL
     *        Type specifier, and SQL NULL values don't play nice with equality operator commonly
     *        found in simple WHERE clauses.
     * @return a prepared statement.
     * @throws SQLException if there is a problem creating the statement.
     */
    static PreparedStatement getSelectStatement(Persistent object, Connection conn,
        String where, Object... parameters)
        throws SQLException
    {
        if(where != null)
        {
            final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM "
                + object.getTable() + " WHERE " + where);
            if(parameters != null)
            {
                int placeholderCount = 0;
                for(int i = 0; i < where.length(); i++)
                {
                    if(where.charAt(i) == '?')
                    {
                        placeholderCount++;
                    }
                }
                if(placeholderCount != parameters.length)
                {
                    throw new IllegalArgumentException("where clause specifies " + placeholderCount
                        + " parameter placeholders but " + parameters.length
                        + " values were provideded");
                }
                for(int i = 0; i < parameters.length; i++)
                {
                    if(parameters[i] == null)
                    {
                        throw new IllegalArgumentException(
                            "null parameter values are not supported");
                    }
                    DefaultOutputRecord.setValue(i + 1, parameters[i], java.sql.Types.OTHER, stmt);
                }
            }
            return stmt;
        }
        else
        {
            return conn.prepareStatement("SELECT * FROM " + object.getTable());
        }
    }
}
