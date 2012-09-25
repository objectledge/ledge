// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

import java.sql.SQLException;

/**
 * Implemented by objects that are made persistent using a relational
 * database.
 *
 * @version $Id: PersistentObject.java,v 1.3 2004-12-23 07:16:56 rafal Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public abstract class PersistentObject
    implements Persistent
{
    // Member objects ////////////////////////////////////////////////////////

    /** The 'saved' flag state. */
    protected boolean saved;

    // Persistent interface //////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public abstract String getTable();

    /**
     * {@inheritDoc}
     */
    public abstract String[] getKeyColumns();

    /**
     * {@inheritDoc}
     */
    public abstract void getData(OutputRecord record)
        throws SQLException;
    
    /**
     * {@inheritDoc}
     */
    public abstract void setData(InputRecord record)
        throws SQLException;
    
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
        this.saved = true;
    }
}
