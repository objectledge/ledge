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
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.objectledge.database.DatabaseUtils;

/**
 * An implementation of {@link DefaultOutputRecord}.
 */
public class DefaultOutputRecord
    implements OutputRecord
{
    /** The persistent object. */
    private final Persistent object;

    /** The fields. */
    private final Map<String, Object> fields = new LinkedHashMap<String, Object>();

    private Map<String, Integer> typeMap = null;

    /**
     * Constructs an <code>OutputRecordImpl</code>.
     * 
     * @param object a Presistent object.
     */
    public DefaultOutputRecord(Persistent object)
    {
        this(object, null);
    }

    /**
     * Constructs an <code>OutputRecordImpl</code>.
     * 
     * @param object a Persistent object.
     * @param typeMap mapping of column names to SQLTypes. Type map is using for setting SQL NULL
     *        values PreparedStatement parameters. When not provided, types will be retrieved from
     *        database on demand. Caller should strive to provide the mapping though for performance
     *        reasons.
     */
    public DefaultOutputRecord(Persistent object, Map<String, Integer> typeMap)
    {
        this.object = object;
    }

    /**
     * Returns SQL type of the specified column. This method initializes {@link #typeMap} on demand,
     * when missing.
     * 
     * @param table table name.
     * @param column column name.
     * @param conn database connection.
     * @return SQL type of the column.
     * @throws SQLException
     */
    private int getSQLType(String table, String column, Connection conn)
        throws SQLException
    {
        if(typeMap == null)
        {
            typeMap = new HashMap<String, Integer>();

            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getColumns(null, null,
                DatabaseUtils.adjustIdentifierCase(table, conn), "%");
            try
            {
                while(rs.next())
                {
                    typeMap.put(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"));
                }
            }
            finally
            {
                DatabaseUtils.close(rs);
            }
        }
        return typeMap.get(DatabaseUtils.adjustIdentifierCase(column, conn));
    }

    /**
     * Sets a <code>boolean</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setBoolean(String field, boolean value)
        throws SQLException
    {
        fields.put(field, value ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Sets a <code>byte</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setByte(String field, byte value)
        throws SQLException
    {
        fields.put(field, Byte.valueOf(value));
    }

    /**
     * Sets a <code>short</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setShort(String field, short value)
        throws SQLException
    {
        fields.put(field, Short.valueOf(value));
    }

    /**
     * Sets an <code>int</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setInteger(String field, int value)
        throws SQLException
    {
        fields.put(field, Integer.valueOf(value));
    }

    /**
     * Sets a <code>long</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setLong(String field, long value)
        throws SQLException
    {
        fields.put(field, Long.valueOf(value));
    }

    /**
     * Sets a <code>BigDecimal</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setBigDecimal(String field, BigDecimal value)
        throws SQLException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }
        fields.put(field, value);
    }

    /**
     * Sets a <code>float</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setFloat(String field, float value)
        throws SQLException
    {
        fields.put(field, new Float(value));
    }

    /**
     * Sets a <code>double</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setDouble(String field, double value)
        throws SQLException
    {
        fields.put(field, new Double(value));
    }

    /**
     * Sets a <code>String</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setString(String field, String value)
        throws SQLException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }
        fields.put(field, value);
    }

    /**
     * Sets a <code>byte</code> array field value.
     * <p>
     * String value read from the database will be BASE64 decoded to obtain byte array.
     * </p>
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setBytes(String field, byte[] value)
        throws SQLException
    {
        try
        {
            Base64 encoder = new Base64();
            String encoded = new String(encoder.encode(value), "US-ASCII");
            fields.put(field, encoded);
        }
        catch(Exception e)
        {
            throw new SQLException("Failed to encode field " + field, e);
        }
    }

    /**
     * Sets a <code>Date</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setDate(String field, Date value)
        throws SQLException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }
        fields.put(field, new java.sql.Date(value.getTime()));
    }

    /**
     * Sets a <code>Time</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setTime(String field, Date value)
        throws SQLException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }
        fields.put(field, new Time(value.getTime()));
    }

    /**
     * Sets a <code>Timestamp</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setTimestamp(String field, Date value)
        throws SQLException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }
        fields.put(field, new Timestamp(value.getTime()));
    }

    /**
     * Sets a <code>URL</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setURL(String field, URL value)
        throws SQLException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>Object</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setObject(String field, Object value)
        throws SQLException
    {
        fields.put(field, value);
    }

    /**
     * Sets a field to <code>SQL NULL</code> value.
     * 
     * @param field the name of the field.
     * @throws SQLException if the field could not be set to the specified value.
     */
    public void setNull(String field)
        throws SQLException
    {
        fields.put(field, null);
    }

    // Implementation specific ///////////////////////////////////////////

    @Override
    public <T> void set(String field, T value)
        throws SQLException
    {
        fields.put(field, value);
    }

    /**
     * Builds <code>WHERE</code> clause with contained data.
     * 
     * @return the where clause.
     * @throws SQLException if the clause could not be built.
     */
    String getWhereClause()
        throws SQLException
    {
        StringBuilder buff = new StringBuilder();
        Iterator<String> i = getKeyFields().iterator();
        while(i.hasNext())
        {
            buff.append(i.next()).append(" = ?");
            if(i.hasNext())
            {
                buff.append(" AND ");
            }
        }
        return buff.toString();
    }

    /**
     * Builds an insert statement with contained data.
     * 
     * @param conn database connection.
     * @return the statement.
     * @throws SQLException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    PreparedStatement getInsertStatement(Connection conn)
        throws SQLException
    {
        StringBuilder buff = new StringBuilder();
        StringBuilder buff2 = new StringBuilder();
        buff.append("INSERT INTO ");
        buff.append(object.getTable());
        buff.append(" (");
        for(Iterator<String> i = fields.keySet().iterator(); i.hasNext();)
        {
            String field = i.next();
            buff.append(field);
            buff2.append("?");
            if(i.hasNext())
            {
                buff.append(", ");
                buff2.append(", ");
            }
        }
        buff.append(") VALUES (");
        buff.append(buff2.toString());
        buff.append(")");
        PreparedStatement stmt = conn.prepareStatement(buff.toString());
        setValues(stmt, true, true, 1);
        return stmt;
    }

    /**
     * Builds an update statement with contained data.
     * 
     * @param conn database connection.
     * @return the statement.
     * @throws SQLException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    PreparedStatement getUpdateStatement(Connection conn)
        throws SQLException
    {
        Set<String> keyFields = getKeyFields();
        StringBuilder buff = new StringBuilder();
        buff.append("UPDATE ");
        buff.append(object.getTable());
        buff.append(" SET ");
        Iterator<String> i = fields.keySet().iterator();
        while(i.hasNext())
        {
            String field = i.next();
            if(!keyFields.contains(field))
            {
                buff.append(field);
                buff.append(" = ? ");
                buff.append(", ");
            }
        }
        // remove superfluous ", "
        buff.setLength(buff.length() - 2);
        buff.append(" WHERE ");
        buff.append(getWhereClause());
        PreparedStatement stmt = conn.prepareStatement(buff.toString());
        // set non-key values first
        int wherePos = setValues(stmt, false, true, 1);
        // set key values
        setValues(stmt, true, false, wherePos);
        return stmt;
    }

    /**
     * @return set of key field names.
     */
    private Set<String> getKeyFields()
    {
        return new LinkedHashSet<String>(Arrays.asList(object.getKeyColumns()));
    }

    /**
     * Builds a <code>DELETE</code> statement with contained data.
     * 
     * @param conn database connection.
     * @return the statement.
     * @throws SQLException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    PreparedStatement getDeleteStatement(Connection conn)
        throws SQLException
    {
        StringBuilder buff = new StringBuilder();
        buff.append("DELETE FROM ");
        buff.append(object.getTable());
        buff.append(" WHERE ");
        buff.append(getWhereClause());
        PreparedStatement stmt = conn.prepareStatement(buff.toString());
        setValues(stmt, true, false, 1);
        return stmt;
    }

    /**
     * Sets prepared statement's positional parameters to non-string field values.
     * 
     * @param stmt the statement.
     * @param includeKeys <code>true</code> to set key values.
     * @param includeNonKeys <code>true</code> to set non-key values.
     * @throws SQLException if a value couldn't be set.
     * @return next available parameter position
     */
    int setValues(PreparedStatement stmt, boolean includeKeys, boolean includeNonKeys, int startPos)
        throws SQLException
    {
        Set<String> keyFields = getKeyFields();
        int pos = startPos;
        for(Iterator<String> i = fields.keySet().iterator(); i.hasNext();)
        {
            String field = i.next();
            boolean isKey = keyFields.contains(field);
            if((isKey && includeKeys) || (!isKey && includeNonKeys))
            {
                Object value = fields.get(field);
                setValue(pos++, value, getSQLType(object.getTable(), field, stmt.getConnection()),
                    stmt);
            }
        }
        return pos;
    }

    /**
     * Sets prepared statement's positional parameters to non-string field values.
     * 
     * @param pos parameter position (1 based).
     * @param value parameter value.
     * @param stmt the statement.
     */
    public static void setValue(int pos, Object value, int sqlType, PreparedStatement stmt)
        throws SQLException
    {
        if(value == null)
        {
            stmt.setNull(pos, sqlType);
            return;
        }
        else if(value instanceof Time)
        {
            stmt.setTime(pos, (Time)value);
        }
        else if(value instanceof Timestamp)
        {
            stmt.setTimestamp(pos, (Timestamp)value);
        }
        else if(value instanceof java.sql.Date)
        {
            stmt.setDate(pos, (java.sql.Date)value);
        }
        else if(value instanceof java.util.Date)
        {
            stmt.setTimestamp(pos, new java.sql.Timestamp(((java.util.Date)value).getTime()));
        }
        else if(value instanceof URL)
        {
            stmt.setURL(pos, (URL)value);
        }
        else if(value instanceof Boolean)
        {
            stmt.setBoolean(pos, ((Boolean)value).booleanValue());
        }
        else if(value instanceof String)
        {
            stmt.setString(pos, (String)value);
        }
        else if(value instanceof Long)
        {
            stmt.setLong(pos, ((Long)value).longValue());
        }
        else if(value instanceof Integer)
        {
            stmt.setInt(pos, ((Integer)value).intValue());
        }
        else if(value instanceof Short)
        {
            stmt.setShort(pos, ((Short)value).shortValue());
        }
        else if(value instanceof Byte)
        {
            stmt.setByte(pos, ((Byte)value).byteValue());
        }
        else if(value instanceof Double)
        {
            stmt.setDouble(pos, ((Double)value).doubleValue());
        }
        else if(value instanceof Float)
        {
            stmt.setFloat(pos, ((Float)value).byteValue());
        }
        else if(value instanceof BigDecimal)
        {
            stmt.setBigDecimal(pos, (BigDecimal)value);
        }
        else
        {
            throw new IllegalArgumentException("unsupported type " + value.getClass());
        }
    }

    boolean containsValue(String field)
    {
        return fields.containsKey(field);
    }

    <T> T getValue(String field, Class<T> clazz)
    {
        T value = (T)fields.get(field);
        return value;
    }

    boolean hasNonKeyValues()
    {
        boolean hasNonKeyValues = false;
        Set<String> keyFields = getKeyFields();
        for(String field : fields.keySet())
        {
            if(!keyFields.contains(field))
            {
                hasNonKeyValues = true;
            }
        }
        return hasNonKeyValues;
    }

    @Override
    public String toString()
    {
        return fields.toString();
    }

    public static PreparedStatement getInsertStatement(Persistent object, Connection conn)
        throws SQLException
    {
        DefaultOutputRecord record = new DefaultOutputRecord(object);
        object.getData(record);
        return record.getInsertStatement(conn);
    }

    public static PreparedStatement getUpdateStatement(Persistent object, Connection conn)
        throws SQLException
    {
        DefaultOutputRecord record = new DefaultOutputRecord(object);
        object.getData(record);
        return record.getUpdateStatement(conn);
    }

    public static PreparedStatement getDeleteStatement(Persistent object, Connection conn)
        throws SQLException
    {
        DefaultOutputRecord record = new DefaultOutputRecord(object);
        object.getData(record);
        return record.getDeleteStatement(conn);
    }

    public static void refeshInsertStatement(Persistent object, PreparedStatement statement)
        throws SQLException
    {
        DefaultOutputRecord record = new DefaultOutputRecord(object);
        object.getData(record);
        record.setValues(statement, false, true, record.getKeyFields().size() + 1);
    }
}
