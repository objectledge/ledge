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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jcontainer.dna.Logger;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.IdGenerator;

/**
 * Provides Object-Relational DB mapping.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Persistence.java,v 1.4 2004-02-09 11:14:15 fil Exp $
 */
public class Persistence
{
    /** The DataSource. */
    private DataSource dataSource;

    /** The IdGenerator. */
    private IdGenerator idGenerator;
    
    /** The logger */
    private Logger logger;

    /**
     * Component constructor.
     * 
     * @param database the database.
     * @param idGenerator the id generator. 
     * @param logger the logger.
     */
    public Persistence(DataSource database, IdGenerator idGenerator, Logger logger)
    {
        this.logger = logger;
        this.dataSource = database;
        this.idGenerator = idGenerator;
    }

    // PersistenceService interface //////////////////////////////////////////

    /**
     * Loads an object from the database.
     *
     * @param id the identifier of the object.
     * @param factory the object instance factory.
     * @return the presistent object.
     * @throws PersistenceException if any exception occured.
     */
    public Persistent load(long id, PersistentFactory factory) throws PersistenceException
    {
        Connection conn = null;
        try
        {
            conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            Persistent obj = factory.newInstance();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + obj.getTable() + " WHERE " +
                                                  obj.getKeyColumns()[0] + " = " + id);
            if (!rs.next())
            {
                return null;
            }
            InputRecord record = new InputRecord(rs);
            obj.setData(record);
            obj.setSaved(record.getLong(obj.getKeyColumns()[0]));
            return obj;
        }
        catch (Exception e)
        {
            throw new PersistenceException("Failed to retrieve object", e);
        }
        finally
        {
            DatabaseUtils.close(conn);
        }
    }

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
    public List load(String where, PersistentFactory factory) throws PersistenceException
    {
        Connection conn = null;
        try
        {
            conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            Persistent obj = factory.newInstance();
            ResultSet rs = null;
            if (where != null)
            {
                rs = statement.executeQuery("SELECT * FROM " + obj.getTable() + " WHERE " + where);
            }
            else
            {
                rs = statement.executeQuery("SELECT * FROM " + obj.getTable());
            }
            InputRecord record = new InputRecord(rs);
            ArrayList list = new ArrayList();
            while (rs.next())
            {
                obj.setData(record);
                obj.setSaved(record.getLong(obj.getKeyColumns()[0]));
                list.add(obj);
                obj = factory.newInstance();
            }
            return list;
        }
        catch (Exception e)
        {
            throw new PersistenceException("Failed to retrieve object", e);
        }
        finally
        {
            DatabaseUtils.close(conn);
        }
    }

    /**
     * Saves an object in the database.
     *
     * @param object the object to be saved.
     * @throws PersistenceException if any exception occured.
     */
    public void save(Persistent object) throws PersistenceException
    {
        synchronized (object)
        {
            OutputRecord record = new OutputRecord(object);
            String table = object.getTable();
            String[] keys = object.getKeyColumns();
            object.getData(record);
            Connection conn = null;

            try
            {
                conn = dataSource.getConnection();
                PreparedStatement statement;
                if (object.getSaved())
                {
                    statement = record.getUpdateStatement(conn);
                    statement.execute();
                }
                else
                {
                    long id;
                    if (keys.length == 1)
                    {
                        id = idGenerator.getNextId(table);
                        record.setLong(keys[0], id);
                    }
                    else
                    {
                        id = -1;
                    }
                    statement = record.getInsertStatement(conn);
                    statement.execute();
                    object.setSaved(id);
                }
            }
            catch (Exception e)
            {
                throw new PersistenceException("Failed to save object", e);
            }
            finally
            {
                DatabaseUtils.close(conn);
            }
        }
    }

    /**
     * Reverts the object to the saved state.
     *
     * @param object the object to have it's state restored.
     * @throws PersistenceException if any exception occured.
     * @throws IllegalStateException if no state has been saved yet for the
     *         object in question.
     */
    public void revert(Persistent object) throws PersistenceException, IllegalStateException
    {
        synchronized (object)
        {
            if (!object.getSaved())
            {
                throw new IllegalStateException("no state has been saved yet");
            }
            Connection conn = null;
            try
            {
                conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                OutputRecord orecord = new OutputRecord(object);
                object.getData(orecord);
                ResultSet rs = statement.executeQuery("SELECT * FROM " + object.getTable() + 
                                  " WHERE " + orecord.getWhereClause());
                if (!rs.next())
                {
                    throw new PersistenceException("saved state was lost");
                }
                InputRecord irecord = new InputRecord(rs);
                object.setData(irecord);
            }
            catch (Exception e)
            {
                throw new PersistenceException("failed to revert object's state", e);
            }
            finally
            {
                DatabaseUtils.close(conn);
            }
        }
    }

    /**
     * Removes an object from the database.
     *
     * @param object the object to be removed.
     * @throws PersistenceException if any exception occured.
     */
    public void delete(Persistent object) throws PersistenceException
    {
        synchronized (object)
        {
            Connection conn = null;
            try
            {
                conn = dataSource.getConnection();
                OutputRecord record = new OutputRecord(object);
                object.getData(record);
                PreparedStatement statement = record.getDeleteStatement(conn); 
                statement.execute();
            }
            catch (Exception e)
            {
                throw new PersistenceException("Failed to delete object", e);
            }
            finally
            {
                DatabaseUtils.close(conn);
            }
        }
    }

    /**
     * An utility method for checking for existence of rows.
     *
     * @param table the table to be checked.
     * @param where the condition.
     * @return <code>true</code> if the <code>condition</code> is true for one
     *         or more rows in the <code>table</code>.
     * @throws PersistenceException if any exception occured.
     */
    public boolean exists(String table, String where) throws PersistenceException
    {
        Connection conn = null;
        try
        {
            conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs;
            if (where != null)
            {
                rs = statement.executeQuery("SELECT DISTINCT 1 FROM " + table + " WHERE " + where);
            }
            else
            {
                rs = statement.executeQuery("SELECT DISTINCT 1 FROM " + table);
            }
            return (rs.next());
        }
        catch (Exception e)
        {
            throw new PersistenceException("Failed to execute query", e);
        }
        finally
        {
            DatabaseUtils.close(conn);
        }
    }

    /**
     * An utility method for checking the number of matching rows.
     *
     * @param table the table to be chcked.
     * @param where the condition.
     * @return the number of <code>table</code> matching the condition.
     * @throws PersistenceException if any exception occured.
     */
    public int count(String table, String where) throws PersistenceException
    {
        Connection conn = null;
        try
        {
            conn = dataSource.getConnection();
            Statement statement = conn.createStatement();
            ResultSet rs;
            if (where != null)
            {
                rs = statement.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + where);
            }
            else
            {
                rs = statement.executeQuery("SELECT COUNT(*) FROM " + table);
            }
            if (!rs.next())
            {
                throw new PersistenceException("internal error - no data???");
            }
            return (rs.getInt(1));
        }
        catch (Exception e)
        {
            throw new PersistenceException("Failed to execute query", e);
        }
        finally
        {
            DatabaseUtils.close(conn);
        }
    }
}
