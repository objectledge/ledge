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
import java.sql.SQLException;
import java.util.Date;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a> To change the template for this
 *         generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class TestObject
    implements Persistent
{
    private long id;

    private String value;

    private Date date;

    private boolean valueBoolean;

    private float valueFloat;

    private BigDecimal valueDecimal;

    private Date valueTime;

    private Date valueTimestamp;

    private short valueShort;

    private byte valueByte;

    public TestObject()
    {
        this("", null);
    }

    public TestObject(String value, Date date)
    {
        id = -1;
        this.date = date;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getTable()
    {
        return "test_object";
    }

    /**
     * {@inheritDoc}
     */
    public String[] getKeyColumns()
    {
        return new String[] { "id" };
    }

    /**
     * {@inheritDoc}
     */
    public void getData(OutputRecord record)
        throws SQLException
    {
        record.setLong("id", id);
        record.setString("value", value);
        record.setDate("date", date);
        record.setBoolean("value_boolean", valueBoolean);
        record.setFloat("value_float", valueFloat);
        record.setBigDecimal("value_decimal", valueDecimal);
        record.setTime("value_time", valueTime);
        record.setTimestamp("value_timestamp", valueTimestamp);
        record.setShort("value_short", valueShort);
        record.setByte("value_byte", valueByte);
    }

    /**
     * {@inheritDoc}
     */
    public void setData(InputRecord record)
        throws SQLException
    {
        id = record.getLong("id");
        value = record.getString("value");
        date = record.getDate("date");
        valueBoolean = record.isNull("value_boolean") ? false : record.getBoolean("value_boolean");
        valueFloat = record.isNull("value_float") ? 0f : record.getFloat("value_float");
        valueDecimal = record.isNull("value_decimal") ? BigDecimal.valueOf(0) : record
            .getBigDecimal("value_decimal");
        valueTime = record.isNull("value_time") ? null : record.getDate("value_time");
        valueTimestamp = record.isNull("value_timestamp") ? null : record
            .getDate("value_timestamp");
        valueShort = record.isNull("value_short") ? null : record.getShort("value_short");
        valueByte = record.isNull("value_byte") ? null : record.getByte("value_byte");
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSaved()
    {
        return (id != -1);
    }

    /**
     * {@inheritDoc}
     */
    public void setSaved(long id)
    {
        this.id = id;
    }

    public long getId()
    {
        return id;
    }

    public String getValue()
    {
        return value;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
