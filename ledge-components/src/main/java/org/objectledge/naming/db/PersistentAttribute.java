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

package org.objectledge.naming.db;

import org.objectledge.database.persistence.InputRecord;
import org.objectledge.database.persistence.OutputRecord;
import org.objectledge.database.persistence.PersistenceException;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.database.persistence.PersistentFactory;

/**
 * Persistent representation of java.naming.Context.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class PersistentAttribute implements Persistent
{
    // constants /////////////////////////////////////////////////////////////

    /** The table name. */
    public static final String TABLE_NAME = "ledge_naming_attribute";

    /** The key columns. */
    public static final String[] KEY_COLUMNS = new String[] { "context_id", "name", "value" };
    
    /** the object factory. */
    public static final PersistentFactory FACTORY = new PersistentFactory()
    {
        public Persistent newInstance()
        {
            return new PersistentAttribute();
        }
    };

    // instance variables ////////////////////////////////////////////////////
    
    /** The context id. */
    private long contextId = -1;
    
    /** The attribute name */
    private String name = "";

    /** The attribute value */
    private String value = "";

    /** The state */
    private boolean saved = false;
    
    /**
     * The constructor.
     */
    public PersistentAttribute()
    {
        // default constructor
    }

    /**
     * The constructor.
     * 
     * @param contextId the context id.
     * @param name the attribute name.
     * @param value the attribute value.
     */
    public PersistentAttribute(long contextId, String name, String value)
    {
        this.contextId = contextId;
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getTable()
    {
        return TABLE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getKeyColumns()
    {
        return KEY_COLUMNS;
    }

    /**
     * {@inheritDoc}
     */
    public void getData(OutputRecord record) throws PersistenceException
    {
        record.setLong("context_id", contextId);
        record.setString("name", getName());
        record.setString("value", getValue());
    }

    /**
     * {@inheritDoc}
     */
    public void setData(InputRecord record) throws PersistenceException
    {
        contextId = record.getLong("context_id");
        name = record.getString("name");
        value = record.getString("value");
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSaved()
    {
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    public void setSaved(long id)
    {
        saved = true;
    }
    
    /**
     * Return the name of the attribute.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Return the value of the attribute.
     * 
     * @return the value.
     */
    public String getValue()
    {
        return value;
    }
}
