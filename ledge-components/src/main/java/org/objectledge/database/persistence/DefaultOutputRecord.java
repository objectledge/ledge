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
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

/**
 * An implementation of {@link DefaultOutputRecord}.
 *
 * TODO get rid of sun.misc.Base64Encoder
 */
public class DefaultOutputRecord implements OutputRecord
{
    /** The persistent object. */
    private Persistent object;
    
    /** The fields. */
    private HashMap<String,Object> fields;
        
    /**
     * Constructs an <code>OutputRecordImpl</code>.
     * 
     * @param object a Presistent object.
     */
    public DefaultOutputRecord(Persistent object)
    {
        fields = new HashMap<String,Object>();
        this.object = object;
    }
        
    /**
     * Sets a <code>boolean</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setBoolean(String field, boolean value)
        throws PersistenceException
    {
        fields.put(field, value ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Sets a <code>byte</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     * specified value.
     */
    public void setByte(String field, byte value)
        throws PersistenceException
    {
        fields.put(field, new Byte(value));
    }

    /**
     * Sets a <code>short</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setShort(String field, short value)
        throws PersistenceException
    {
        fields.put(field, new Short(value));
    }

    /**
     * Sets an <code>int</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setInteger(String field, int value)
        throws PersistenceException
    {
        fields.put(field, new Integer(value));
    }

    /**
     * Sets a <code>long</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setLong(String field, long value)
        throws PersistenceException
    {
        fields.put(field, new Long(value));
    }        

    /**
     * Sets a <code>BigDecimal</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setBigDecimal(String field, BigDecimal value)
        throws PersistenceException
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
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setFloat(String field, float value)
        throws PersistenceException
    {
        fields.put(field, new Float(value));
    }

    /**
     * Sets a <code>double</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the 
     *         specified value.
     */
    public void setDouble(String field, double value)
        throws PersistenceException
    {
        fields.put(field, new Double(value));
    }

    /**
     * Sets a <code>String</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setString(String field, String value)
        throws PersistenceException
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
     *
     * <p>String value read from the database will be BASE64 decoded to obtain
     * byte array.</p>
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setBytes(String field, byte[] value)
        throws PersistenceException
    {
        try
        {
            Base64 encoder = new Base64();
            String encoded = new String(encoder.encode(value), "US-ASCII");
            fields.put(field, encoded);
        }
        catch(Exception e)
        {
            throw new PersistenceException("Failed to encode field "+field, e);
        }
    }

    /**
     * Sets a <code>Date</code> field value.
     *
     * @param field the name of the field.
     * @param value the value of the field.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setDate(String field, Date value)
        throws PersistenceException
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
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setTime(String field, Date value)
        throws PersistenceException
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
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setTimestamp(String field, Date value)
        throws PersistenceException
    {
        if(value == null)
        {
            setNull(field);
            return;
        }        
        fields.put(field, new Timestamp(value.getTime()));
    }

    /**
     * Sets a <code>Array</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setArray(String field, Array value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>Blob</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setBlob(String field, Blob value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>Clob</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setClob(String field, Clob value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>Ref</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setRef(String field, Ref value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>URL</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setURL(String field, URL value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a <code>Object</code> field value.
     * 
     * @param field the name of the field.
     * @param value the value of the filed.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setObject(String field, Object value)
        throws PersistenceException
    {
        fields.put(field, value);
    }

    /**
     * Sets a field to <code>SQL NULL</code> value.
     *
     * @param field the name of the field.
     * @throws PersistenceException if the field could not be set to the
     *         specified value. 
     */
    public void setNull(String field)
        throws PersistenceException
    {
        fields.put(field, null);
    }

    // Implementation specific ///////////////////////////////////////////

    /**
     * Builds <code>WHERE</code> clause with contained data.
     *
     * @return the where clause.
     * @throws PersistenceException if the clause could not be built.
     */
    public String getWhereClause()
        throws PersistenceException
    {
        Set keyFields = getKeyFields();
        StringBuilder buff = new StringBuilder();
        for(Iterator i = fields.keySet().iterator(); i.hasNext();)
        {
            String field = (String)i.next();
            if(keyFields.contains(field))
            {
                buff.append(field);
                Object value = fields.get(field);
                buff.append(value == null ? " IS " : " = ");
                appendValueString(buff, value);
                buff.append(" AND ");
            }
        }
        // remove trailing " AND "
        buff.setLength(buff.length() - 5);
        return buff.toString();
    }

    /**
     * Builds an insert statement with contained data.
     *
     * @param conn database connection.
     * @return the statement.
     * @throws PersistenceException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    public PreparedStatement getInsertStatement(Connection conn)
        throws PersistenceException, SQLException
    {
        StringBuilder buff = new StringBuilder();
        StringBuilder buff2 = new StringBuilder();
        buff.append("INSERT INTO ");
        buff.append(object.getTable());
        buff.append(" (");
        for(Iterator i = fields.keySet().iterator(); i.hasNext();)
        {
            String field = (String)i.next();
            buff.append(field);
            Object value = fields.get(field);
            appendValueString(buff2, value);
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
        setValues(stmt, true, true);
        return stmt;
    }
    
    /**
     * Builds an update statement with contained data.
     *
     * @param conn database connection.
     * @return the statement.
     * @throws PersistenceException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    public PreparedStatement getUpdateStatement(Connection conn)
        throws PersistenceException, SQLException
    {
        Set keyFields = getKeyFields();
        StringBuilder buff = new StringBuilder();
        buff.append("UPDATE ");
        buff.append(object.getTable());
        buff.append(" SET ");
        Iterator i = fields.keySet().iterator();
        while(i.hasNext())
        {
            String field = (String)i.next();
            if(!keyFields.contains(field))
            {
                buff.append(field);
                buff.append(" = ");
                Object value = fields.get(field);
                appendValueString(buff, value);
                buff.append(", ");
            }
        }
        // remove superfluous ", "
        buff.setLength(buff.length() - 2);
        buff.append(" WHERE ");
        buff.append(getWhereClause());
        PreparedStatement stmt = conn.prepareStatement(buff.toString());
        // set non-key values first
        setValues(stmt, false, true);
        // set key values
        setValues(stmt, true, false);
        return stmt;
    }

    /**
     * @return set of key field names.
     */
    private Set getKeyFields()
    {
        return new HashSet<String>(Arrays.asList(object.getKeyColumns()));
    }
    
    /**
     * Appends string token apropriate for the value in the statement body to a given buffer.
     *
     * @param buff the buffer to append to.
     * @param object the object.
     */
    private void appendValueString(StringBuilder buff, Object object)
    {
        if(object == null)
        {
            buff.append("NULL");
        }
        else
        {
            if(object instanceof Number)
            {
                buff.append(object.toString());
            }
            else
            {
                buff.append('?');
            }
        }        
    }

    /**
     * Builds a <code>DELETE</code> statement with contained data.
     *
     *
     * @param conn database connection.
     * @return the statement.
     * @throws PersistenceException if the statement could not be built.
     * @throws SQLException if the statement could not be created.
     */
    public PreparedStatement getDeleteStatement(Connection conn)
        throws PersistenceException, SQLException
    {
        StringBuilder buff = new StringBuilder();
        buff.append("DELETE FROM ");
        buff.append(object.getTable());
        buff.append(" WHERE ");
        buff.append(getWhereClause());
        PreparedStatement stmt = conn.prepareStatement(buff.toString());
        setValues(stmt, true, false);
        return stmt;
    }

    /**
     * Returns a value of a field.
     *
     * <p>Note! String and Date values will be returned enclosed in single
     * quotes, byte array values will be returned BASE64 encoded and enclosed
     * in single quotes.</p>
     *
     * @param name the name of the field
     * @return stringied and possibly quoted value of the field, or
     *         <code>null</code> if unset.
     */
    public String getField(String name)
    {
        return (String)fields.get(name);
    }
    
    /**
     * Sets prepared statement's positional parameters to non-string field values.
     * 
     * @param stmt the statement.
     * @param includeKeys <code>true</code> to set key values.
     * @param includeNonKeys <code>true</code> to set non-key values.
     * @throws SQLException if a value couldn't be set.
     */
    public void setValues(PreparedStatement stmt, boolean includeKeys, boolean includeNonKeys)
        throws SQLException
    {
        Set keyFields = getKeyFields();
        int pos = 1;
        for(Iterator i = fields.keySet().iterator(); i.hasNext();)
        {
            Object field = i.next();
            boolean isKey = keyFields.contains(field);
            if((isKey && includeKeys) || (!isKey && includeNonKeys))
            {
                Object value = fields.get(field);
                if(value != null)
                {
                    if(!(value instanceof Number))
                    {
                        setValue(pos++, value, stmt);
                    }
                }
            }
        }
    }
    
    /**
     * Sets prepared statement's positional parameters to non-string field values.
     * 
     * @param pos parameter position (1 based).
     * @param value parameter value.
     * @param stmt the statement.
     */
    private void setValue(int pos, Object value, PreparedStatement stmt)
        throws SQLException
    {
        if(value instanceof Array)
        {
            stmt.setArray(pos, (Array)value);
        }
        else if(value instanceof Blob)
        {
            stmt.setBlob(pos, (Blob)value);
        }
        else if(value instanceof Clob)
        {
            stmt.setClob(pos, (Clob)value);
        }
        else if(value instanceof java.sql.Date)
        {
            stmt.setDate(pos, (java.sql.Date)value);
        }
        else if(value instanceof Time)
        {
            stmt.setTime(pos, (Time)value);        
        }
        else if(value instanceof Timestamp)
        {
            stmt.setTimestamp(pos, (Timestamp)value);        
        }
        else if(value instanceof Ref)
        {
            stmt.setRef(pos, (Ref)value);
        }
        else if(value instanceof URL)
        {
            stmt.setURL(pos, (URL)value);
        }
        else if(value instanceof Boolean)
        {
            stmt.setInt(pos, ((Boolean)value).booleanValue() ? 1 : 0);
        }
        else if(value instanceof String)
        {
            stmt.setString(pos, (String)value);
        }
        else
        {
            stmt.setObject(pos, object);
        }
    }
}
