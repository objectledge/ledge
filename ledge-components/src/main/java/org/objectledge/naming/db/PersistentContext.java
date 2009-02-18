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
public class PersistentContext implements Persistent
{
    // constants /////////////////////////////////////////////////////////////

    /** The table name. */
    public static final String TABLE_NAME = "ledge_naming_context";

    /** The key columns. */
    public static final String[] KEY_COLUMNS = new String[] { "context_id" };

    /** the object factory. */
    public static final PersistentFactory FACTORY = new PersistentFactory()
    {
        public Persistent newInstance()
        {
            return new PersistentContext(null, -1);
        }
    };
    
    // instance variables ////////////////////////////////////////////////////

    /** The context id. */
    private long contextId = -1;
    
    /** The dn */
    private String dn;
    
    /** The parent context id */
    private long parentId = -1;

    /**
     * Persistent context constructor.
     * 
     * @param dn the dn of the context.
     * @param parentId the id of the parent context.
     */
    public PersistentContext(String dn, long parentId)
    {
        this.dn = dn;
        this.parentId = parentId;
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
        record.setString("dn", getDN());
        record.setLong("parent", parentId);
    }

    /**
     * {@inheritDoc}
     */
    public void setData(InputRecord record) throws PersistenceException
    {
        contextId = record.getLong("context_id");
        dn = record.getString("dn");
        parentId = record.getLong("parent");
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSaved()
    {
        return contextId != -1L;
    }

    /**
     * {@inheritDoc}
     */
    public void setSaved(long id)
    {
        this.contextId = id;
    }
    
    /**
     * Get the context id.
     * 
     * @return the context id.
     */
    public long getContextId()
    {
        return contextId;
    }
        
    /**
     * Return the dn.
     * 
     * @return the dn.
     */
    public String getDN()
    {
        return dn;
    }
    
    /**
     * Set the dn.
     * 
     * @param dn the dn.
     */
    public void setDN(String dn)
    {
        this.dn = dn;
    }
    
    /**
     * Get the parent id.
     * 
     * @return the parent context id.
     */
    public long getParent()
    {
        return parentId;
    }
}
