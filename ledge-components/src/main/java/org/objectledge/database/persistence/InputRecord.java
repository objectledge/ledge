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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * An implementation of {@link InputRecord} that wraps a
 * <code>java.sql.ResultSet</code>.
 */
public class InputRecord
{
    // Member objects ////////////////////////////////////////////////////////

    /** The wrapped result set. */
    private ResultSet rs;

    // Initialization ////////////////////////////////////////////////////////

    /**
     * Constructs an <code>InputRecordImpl</code>.
     *
     * @param rs the <code>ResultSet</code>.
     */
    InputRecord(ResultSet rs)
    {
        this.rs = rs;
    }

    // InputRecord interface /////////////////////////////////////////////////

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
        try
        {
            return rs.getBoolean(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getByte(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getShort(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getInt(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getLong(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getBigDecimal(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getFloat(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getDouble(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
        try
        {
            return rs.getString(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>byte</code> array field value.
     *
     * <p>String value read from the database will be BASE64 decoded to obtain
     * byte array.</p>
     *
     * @param field the name of the field.
     * @return the field value as array of byte.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public byte[] getBytes(String field)
        throws PersistenceException        
    {
        try
        {
            String encoded = rs.getString(field);
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            return decoder.decodeBuffer(encoded);
        }
        catch(Exception e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>Date</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as date.
     * @throws PersistenceException if the field is missing or otherwise
     *         unaccessible. 
     */
    public Date getDate(String field)
        throws PersistenceException        
    {
        try
        {
            return rs.getTimestamp(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * gets a <code>Array</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public Array getArray(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getArray(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>Blob</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public Blob getBlob(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getBlob(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>Clob</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public Clob getClob(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getClob(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>Ref</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public Ref getRef(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getRef(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>URL</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public URL getURL(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getURL(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns a <code>Object</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public Object getObject(String field)
        throws PersistenceException
    {
        try
        {
            return rs.getObject(field);
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
    }

    /**
     * Returns <code>true</code> if the field has <code>SQL NULL</code>
     * value. 
     *
     * @param field the name of the field.
     * @return <code>true</code> if null.
     * @throws PersistenceException if the field is missing or otherwise
     *         unaccessible. 
     */
    public boolean isNull(String field)
        throws PersistenceException
    {
        try
        {
            rs.getString(field);
            return rs.wasNull();
        }
        catch(SQLException e)
        {
            throw new PersistenceException("Failed to read field "+field, e);
        }
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
    public static PreparedStatement getSelectStatements(Persistent object, Connection conn)
        throws PersistenceException, SQLException
    {
        OutputRecord out = new OutputRecord(object);
        object.getData(out);
        return conn.prepareStatement("SELECT * FROM " + object.getTable() + 
            " WHERE " + out.getWhereClause());
    }
    
    /**
     * Creates a select statement for fetching an object from the database.
     * 
     * @param key the key value.
     * @param object Persistent object.
     * @param conn the connection to use.
     * @return a prepared statement.
     * @throws SQLException if there is a problem creating the statement.
     */
    public static PreparedStatement getSelectStatement(long key, Persistent object, Connection conn)
        throws SQLException
    {
        return conn.prepareStatement("SELECT * FROM " + object.getTable() + " WHERE " +
            object.getKeyColumns()[0] + " = " + key);
    }
    
    /**
     * Creates a select statement for fetching an object from the database.
     * 
     * @param where where clause, or <code>null</code> to fetch all objects.
     * @param object Persistent object.
     * @param conn the connection to use.
     * @return a prepared statement.
     * @throws SQLException if there is a problem creating the statement.
     */
    public static PreparedStatement getSelectStatement(String where, Persistent object, 
        Connection conn)
        throws SQLException
    {
        if (where != null)
        {
            return conn.prepareStatement("SELECT * FROM " + object.getTable() + " WHERE " + where);
        }
        else
        {
            return conn.prepareStatement("SELECT * FROM " + object.getTable());
        }
    }
}
