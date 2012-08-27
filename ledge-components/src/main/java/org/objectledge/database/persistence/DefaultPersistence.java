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

import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;

/**
 * Provides Object-Relational DB mapping.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DefaultPersistence.java,v 1.5 2005-05-05 11:13:02 rafal Exp $
 */
public class DefaultPersistence implements Persistence
{
    /** The Database. */
    private Database database;
    
    /**
     * Component constructor.
     * 
     * @param database the database.
     * @param logger the logger.
     */
    public DefaultPersistence(Database database, Logger logger)
    {
        this.database = database;
    }

    // PersistenceSystem interface //////////////////////////////////////////

    /**
     * Loads an object from the database.
     * @param factory the object instance factory.
     * @param id the identifier of the object.
     *
     * @return the presistent object.
     * @throws PersistenceException if any exception occured.
     */
    public <V extends Persistent> V load(PersistentFactory<V> factory, long id) throws PersistenceException
    {
        Connection conn = null;
        PreparedStatement statement = null;
        try
        {
            conn = database.getConnection();
            V obj = factory.newInstance();
            statement = DefaultInputRecord.getSelectStatement(obj, conn, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next())
            {
                return null;
            }
            InputRecord record = new DefaultInputRecord(rs);
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
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }

    /**
     * Loads objects from the database.
     *
     * <p>Note that joins are not supported. This package provides a means of
     * converting objects to rows in a table and vice versa. If you want more,
     * you need some different tool.</p>
     * @param factory the object instance factory.
     * @param where the where clause to be used in the query
     *
     * @return the list of peristent objects.
     * @throws PersistenceException if any exception occured.
     */
    public <V extends Persistent> List<V> load(PersistentFactory<V> factory)
        throws PersistenceException
    {
        return load(factory, null, (Object[])null);
    }

    /**
     * Loads objects from the database.
     * <p>
     * Note that joins are not supported. This package provides a means of converting objects to
     * rows in a table and vice versa. If you want more, you need some different tool.
     * </p>
     * 
     * @param factory the object instance factory.
     * @param where the where clause to be used in the query
     * @param parameters positional parameters used in where clause.
     * @return the list of persistent objects.
     * @throws PersistenceException if any exception occured.
     */
    public <V extends Persistent> List<V> load(PersistentFactory<V> factory, String where,
        Object... parameters)
        throws PersistenceException
    {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            conn = database.getConnection();
            V obj = factory.newInstance();
            statement = DefaultInputRecord.getSelectStatement(obj, conn, where, parameters);
            rs = statement.executeQuery();
            List<V> list = new ArrayList<V>();
            while(rs.next())
            {
                InputRecord record = new DefaultInputRecord(rs);
                obj.setData(record);
                obj.setSaved(record.getLong(obj.getKeyColumns()[0]));
                list.add(obj);
                obj = factory.newInstance();
            }
            return list;
        }
        catch(Exception e)
        {
            throw new PersistenceException("Failed to retrieve object", e);
        }
        finally
        {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }

    /**
     * Loads data from the database.
     * 
     * @param template a Persistent object that the select statement table and columns are derived
     *        from.
     * @param where where clause to be used in the query.
     * @param parameters positional parameters used in where clasue.
     * @return a list of {@link InputRecord} objects, possibly empty.
     * @throws PersistenceException
     */
    public List<InputRecord> loadInputRecords(Persistent template, String where,
        Object... parameters)
        throws PersistenceException
    {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try
        {
            conn = database.getConnection();
            statement = DefaultInputRecord.getSelectStatement(template, conn, where, parameters);
            rs = statement.executeQuery();
            List<InputRecord> list = new ArrayList<InputRecord>();
            while(rs.next())
            {
                list.add(new DefaultInputRecord(rs));
            }
            return list;
        }
        catch(Exception e)
        {
            throw new PersistenceException("Failed to retrieve objects", e);
        }
        finally
        {
            DatabaseUtils.close(rs);
            DatabaseUtils.close(statement);
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
            DefaultOutputRecord record = new DefaultOutputRecord(object);
            String table = object.getTable();
            String[] keys = object.getKeyColumns();
            object.getData(record);
            Connection conn = null;
            PreparedStatement statement = null;

            try
            {
                conn = database.getConnection();
                if (object.getSaved())
                {
                    statement = record.getUpdateStatement(conn);
                    statement.execute();
                }
                else
                {
                    long id;
                    if(record.containsValue(keys[0]))
                    {
                        id = record.getValue(keys[0], Long.class);
                    }
                    else
                    {
                        if(keys.length == 1)
                        {
                            id = database.getNextId(table);
                            record.setLong(keys[0], id);
                        }
                        else
                        {
                            id = -1;
                        }
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
                DatabaseUtils.close(statement);
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
            PreparedStatement statement = null;
            ResultSet rs = null;
            try
            {
                conn = database.getConnection();
                statement = DefaultInputRecord.getSelectStatement(object, conn);
                rs = statement.executeQuery();
                if (!rs.next())
                {
                    throw new PersistenceException("saved state was lost");
                }
                InputRecord irecord = new DefaultInputRecord(rs);
                object.setData(irecord);
            }
            catch (Exception e)
            {
                throw new PersistenceException("failed to revert object's state", e);
            }
            finally
            {
                DatabaseUtils.close(rs);
                DatabaseUtils.close(statement);
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
            PreparedStatement statement = null;
            try
            {
                conn = database.getConnection();
                DefaultOutputRecord record = new DefaultOutputRecord(object);
                object.getData(record);
                statement = record.getDeleteStatement(conn); 
                statement.execute();
                if(statement.getUpdateCount() != 1)
                {
                    throw new PersistenceException("unsuccessful DELETE statement");
                }
            }
            catch (Exception e)
            {
                if(e instanceof PersistenceException)
                {
                    throw (PersistenceException)e;
                }
                throw new PersistenceException("Failed to delete object", e);
            }
            finally
            {
                DatabaseUtils.close(statement);
                DatabaseUtils.close(conn);
            }
        }
    }

    /**
     * Removes the objects from the database.
     *
     * @param where the where clause to be used in the query
     * @param factory the object instance factory.
     * @throws PersistenceException if any exception occured.
     */
    public <V extends Persistent> void delete(String where, PersistentFactory<V> factory) throws PersistenceException
    {
        Connection conn = null;
        PreparedStatement statement = null;
        try
        {
            conn = database.getConnection();
            Persistent obj = factory.newInstance();
            statement = conn.prepareStatement("DELETE FROM " + obj.getTable() + 
                                                                " WHERE " + where);
            statement.executeUpdate();
        }
        catch (Exception e)
        {
            throw new PersistenceException("Failed to retrieve object", e);
        }
        finally
        {
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
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
        Statement statement = null;
        ResultSet rs = null;
        try
        {
            conn = database.getConnection();
            statement = conn.createStatement();
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
            DatabaseUtils.close(rs);
            DatabaseUtils.close(statement);
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
        Statement statement = null;
        ResultSet rs = null;
        try
        {
            conn = database.getConnection();
            statement = conn.createStatement();
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
            DatabaseUtils.close(rs);
            DatabaseUtils.close(statement);
            DatabaseUtils.close(conn);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Database getDatabase()
    {
        return database;
    }
}
