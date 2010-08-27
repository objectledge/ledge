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
package org.objectledge.database.persistence;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.util.Date;

/**
 * An interface that exposes minimal set methods necessary for retrieving Persistent object 
 * field information from JDBC database entry.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: InputRecord.java,v 1.6 2004-12-27 04:43:24 rafal Exp $
 */
public interface InputRecord
{
    /**
     * Returns a <code>boolean</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as boolean.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract boolean getBoolean(String field) throws PersistenceException;
    /**
     * Returns a <code>byte</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as byte.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract byte getByte(String field) throws PersistenceException;
    /**
     * Returns a <code>short</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as short.     
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract short getShort(String field) throws PersistenceException;
    /**
     * Returns an <code>int</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as integer.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract int getInteger(String field) throws PersistenceException;
    /**
     * Returns a <code>long</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as long.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract long getLong(String field) throws PersistenceException;
    /**
     * Returns a <code>BigDecimal</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as big decimal.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract BigDecimal getBigDecimal(String field) throws PersistenceException;
    /**
     * Returns a <code>float</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as float.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract float getFloat(String field) throws PersistenceException;
    /**
     * Returns a <code>double</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as double.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract double getDouble(String field) throws PersistenceException;
    /**
     * Returns a <code>String</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as string.
     * @throws PersistenceException if the field is missing or otherwise unaccessible.
     */
    public abstract String getString(String field) throws PersistenceException;
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
    public abstract byte[] getBytes(String field) throws PersistenceException;
    /**
     * Returns a <code>Date</code> field value.
     *
     * @param field the name of the field.
     * @return the field value as date.
     * @throws PersistenceException if the field is missing or otherwise
     *         unaccessible. 
     */
    public abstract Date getDate(String field) throws PersistenceException;
    /**
     * gets a <code>Array</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract Array getArray(String field) throws PersistenceException;
    /**
     * Returns a <code>Blob</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract Blob getBlob(String field) throws PersistenceException;
    /**
     * Returns a <code>Clob</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract Clob getClob(String field) throws PersistenceException;
    /**
     * Returns a <code>Ref</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract Ref getRef(String field) throws PersistenceException;
    /**
     * Returns a <code>URL</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract URL getURL(String field) throws PersistenceException;
    /**
     * Returns a <code>Object</code> field value.
     * 
     * @param field the name of the field.
     * @return value the value of the filed.
     * @throws PersistenceException if the field could not be get to the
     *         specified value. 
     */
    public abstract Object getObject(String field) throws PersistenceException;
    /**
     * Returns <code>true</code> if the field has <code>SQL NULL</code>
     * value. 
     *
     * @param field the name of the field.
     * @return <code>true</code> if null.
     * @throws PersistenceException if the field is missing or otherwise
     *         unaccessible. 
     */
    public abstract boolean isNull(String field) throws PersistenceException;
}