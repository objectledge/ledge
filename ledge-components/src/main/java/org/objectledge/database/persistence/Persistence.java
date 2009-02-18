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

import java.util.List;

import org.objectledge.database.Database;

/**
 * Provides Object-Relational DB mapping.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Persistence.java,v 1.10 2004-02-27 12:23:23 pablo Exp $
 */
public interface Persistence
{
    // PersistenceSystem interface //////////////////////////////////////////

    /**
     * Loads an object from the database.
     *
     * @param id the identifier of the object.
     * @param factory the object instance factory.
     * @return the presistent object.
     * @throws PersistenceException if any exception occured.
     */
    public Persistent load(long id, PersistentFactory factory)
        throws PersistenceException;

    /**
     * Loads objects from the database.
     *
     * <p>Note that joins are not supported. This package provides a means of
     * converting objects to rows in a table and vice versa. If you want more,
     * you need some different tool.</p>
     *
     * @param where the where clause to be used in the query
     * @param factory the object instance factory.
     * @return the list of presistent objects.
     * @throws PersistenceException if any exception occured.
     */
    public List load(String where, PersistentFactory factory) throws PersistenceException;

    /**
     * Saves an object in the database.
     *
     * @param object the object to be saved.
     * @throws PersistenceException if any exception occured.
     */
    public void save(Persistent object) throws PersistenceException;

    /**
     * Reverts the object to the saved state.
     *
     * @param object the object to have it's state restored.
     * @throws PersistenceException if any exception occured.
     * @throws IllegalStateException if no state has been saved yet for the
     *         object in question.
     */
    public void revert(Persistent object)
        throws PersistenceException, IllegalStateException;

    /**
     * Removes an object from the database.
     *
     * @param object the object to be removed.
     * @throws PersistenceException if any exception occured.
     */
    public void delete(Persistent object) throws PersistenceException;

    /**
     * Removes the objects from the database.
     *
     * @param where the where clause to be used in the query
     * @param factory the object instance factory.
     * @throws PersistenceException if any exception occured.
     */
    public void delete(String where, PersistentFactory factory) throws PersistenceException;

    /**
     * An utility method for checking for existence of rows.
     *
     * @param table the table to be checked.
     * @param where the condition.
     * @return <code>true</code> if the <code>condition</code> is true for one
     *         or more rows in the <code>table</code>.
     * @throws PersistenceException if any exception occured.
     */
    public boolean exists(String table, String where) throws PersistenceException;
    
    /**
     * An utility method for checking the number of matching rows.
     *
     * @param table the table to be chcked.
     * @param where the condition.
     * @return the number of <code>table</code> matching the condition.
     * @throws PersistenceException if any exception occured.
     */
    public int count(String table, String where) throws PersistenceException;
    
    /**
     * Get the database component used by persistence.
     * @return the database.
     */
    public Database getDatabase();
}