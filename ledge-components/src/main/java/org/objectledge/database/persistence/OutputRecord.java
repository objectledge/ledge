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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.objectledge.database.DatabaseUtils;
import org.objectledge.utils.StringUtils;

/**
 * An implementation of {@link OutputRecord}.
 */
public class OutputRecord
{
    /** The fields. */
    private HashMap fields;
        
    /**
     * Constructs an <code>OutputRecordImpl</code>.
     */
    OutputRecord()
    {
        fields = new HashMap();
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
        fields.put(field, value ? "1" : "0");
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
        fields.put(field, Byte.toString(value));
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
        fields.put(field, Short.toString(value));
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
        fields.put(field, Integer.toString(value));
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
        fields.put(field, Long.toString(value));
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
        fields.put(field, value.toString());
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
        fields.put(field, Float.toString(value));
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
        fields.put(field, Double.toString(value));
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
        if(value != null)
        {
            fields.put(field,"'"+StringUtils.backslashEscape(value,"\\'")+"'");
        }
        else
        {
            fields.put(field, "NULL");
        }
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
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            String encoded = encoder.encodeBuffer(value);
            fields.put(field,"'"+encoded+"'");
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
        fields.put(field,"'"+DatabaseUtils.format(value)+"'");
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
     * @param keys the names of colums to appear in the clause.
     * @return the where clause.
     * @throws PersistenceException if the clause could not be built.
     */
    public String getWhereClause(String[] keys)
        throws PersistenceException
    {
        StringBuffer buff = new StringBuffer();
        for(int i=0; i<keys.length; i++)
        {
            buff.append(keys[i]);
            String value = (String)fields.get(keys[i]);
            if(value == null)
            {
                buff.append(" IS NULL");
            }
            else
            {
                buff.append(" = ").append(value);
            }
            if(i < keys.length - 1)
            {    
                buff.append(" AND ");
            }
        }
        return buff.toString();
    }

    /**
     * Builds an insert statement with contained data.
     *
     * @param table the table name.
     * @return the statement body.
     * @throws PersistenceException if the statement could not be built.
     */
    public String getInsertStatement(String table)
        throws PersistenceException
    {
        StringBuffer buff = new StringBuffer();
        StringBuffer buff2 = new StringBuffer();
        buff.append("INSERT INTO ");
        buff.append(table);
        buff.append(" (");
        Iterator i = fields.keySet().iterator();
        while(i.hasNext())
        {
            String field = (String)i.next();
            buff.append(field);
            String value = (String)fields.get(field);
            if(value != null)
            {
                buff2.append(value);
            }
            else
            {
                buff2.append("NULL");
            }
            if(i.hasNext())
            {
                buff.append(", ");
                buff2.append(", ");
            }
        }
        buff.append(") VALUES (");
        buff.append(buff2.toString());
        buff.append(")");
        return buff.toString();
    }

    /**
     * Builds an update statement with contained data.
     *
     * @param table the table name.
     * @param keys the names of colums to appear in the where clause.
     * @return the statement body.
     * @throws PersistenceException if the statement could not be built.
     */
    public String getUpdateStatement(String table, String[] keys)
        throws PersistenceException
    {
        HashSet keySet = new HashSet();
        for(int i=0; i<keys.length; i++)
        {
            keySet.add(keys[i]);
        }
        StringBuffer buff = new StringBuffer();
        buff.append("UPDATE ");
        buff.append(table);
        buff.append(" SET ");
        Iterator i=fields.keySet().iterator();
        while(i.hasNext())
        {
            String field = (String)i.next();
            if(!keySet.contains(field))
            {
                buff.append(field);
                buff.append(" = ");
                String value = (String)fields.get(field);
                if(value != null)
                {
                    buff.append(value);
                }
                else
                {
                    buff.append("NULL");
                }
                buff.append(", ");
            }
        }
        // remove superfluous ", "
        buff.setLength(buff.length() - 2);
        buff.append(" WHERE ");
        buff.append(getWhereClause(keys));
        return buff.toString();
    }

    /**
     * Builds a <code>DELETE</code> statement with contained data.
     *
     *
     * @param table the table name.
     * @param keys the names of colums to appear in the where clause.
     * @return the statement body.
     * @throws PersistenceException if the statement could not be built.
     */
    public String getDeleteStatement(String table, String[] keys)
        throws PersistenceException
    {
        StringBuffer buff = new StringBuffer();
        buff.append("DELETE FROM ");
        buff.append(table);
        buff.append(" WHERE ");
        buff.append(getWhereClause(keys));
        return buff.toString();
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
}
