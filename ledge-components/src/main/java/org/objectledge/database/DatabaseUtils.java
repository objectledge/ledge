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
package org.objectledge.database;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.utils.StringUtils;

/**
 * A set of utility functions for working with JDBC databases.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DatabaseUtils.java,v 1.19 2005-02-10 17:47:01 rafal Exp $
 */
public class DatabaseUtils
{
    /** A logger. Bypasses LogFactory. */
    private static Logger log = 
        new Log4JLogger(org.apache.log4j.Logger.getLogger(DatabaseUtils.class));
    
    ///CLOVER:OFF
    /**
     * Private constructor to prevent subclassing and enforce static access.
     */
    private DatabaseUtils()
    {
        // static access only
    }
    ///CLOVER:ON

    // utilities //////////////////////////////////////////////////////////////////////////////

    /**
     * Closes the connection.
     * 
     * @param conn the connection. 
     */
    public static void close(Connection conn)
    {
        try
        {
            if (conn != null)
            {
                conn.close();
            }
        }
        ///CLOVER:OFF
        catch (SQLException e)
        {
            log.error("failed to close connection", e);
        }
        ///CLOVER:ON
    }
    
    /**
     * Close the statement.
     * 
     * @param stmt the statement. 
     */
    public static void close(Statement stmt)
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
        }
        ///CLOVER:OFF
        catch (SQLException e)
        {
            log.error("failed to close statement", e);
        }
        ///CLOVER:ON
    }
    

    /**
     * Close the result set.
     * 
     * @param rs the result set. 
     */
    public static void close(ResultSet rs)
    {
        try
        {
            if (rs != null)
            {
                rs.close();
            }
        }
        ///CLOVER:OFF
        catch (SQLException e)
        {
            log.error("failed to close result set", e);
        }
        ///CLOVER:ON
    }

    /**
     * Close the connection.
     * 
     * @param conn the connection.
     * @param stmt the statement.
     * @param rs the result set. 
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs)
    {
        close(rs);
        close(stmt);
        close(conn);
    }
    
    /**
     * Unescape the string that comes from query.
     * 
     * @param input the input string.
     * @return the result string.
     */
    public static String unescapeSqlString(String input)
    {
        return input;
    }
    
    /**
     * Escape the \ and ' in string that goes to statement.
     * 
     * @param input the input string.
     * @return the result string.
     */
    public static String escapeSqlString(String input)
    {
        return StringUtils.backslashEscape(input, "'\\");
    }
    
    /**
     * Executes an SQL script.
     * 
     * <p>
     *   <ul>
     *     <li>Lines starting with # are ignored</li>
     *     <li>Statements may span multiple lines, line with a semicolon as the last charcter 
     *         terminates the statement.</li>
     *     <li>Script execution is aborted when execution of an statement throws an exception.</li>
     *   </ul>
     * </p>
     * 
     * @param dataSource source of connections to the database.
     * @param reader the reader to read script from.
     * @throws IOException if the script cannot be read.
     * @throws SQLException if there is a problem executing the script. 
     */
    public static void runScript(DataSource dataSource, Reader reader)
        throws IOException, SQLException
    {
        Connection conn = dataSource.getConnection();
        try
        {
            runScript(conn, reader);
        }
        finally
        {
            close(conn);
        }
    }

    /**
     * Executes an SQL script.
     * 
     * @see #runScript(Connection, Reader)
     * @param conn the connections to the database.
     * @param reader the reader to read script from.
     * @throws IOException if the script cannot be read.
     * @throws SQLException if there is a problem executing the script. 
     */    
    public static void runScript(Connection conn, Reader reader)
    	throws IOException, SQLException
    {
        Statement stmt = conn.createStatement();
        try
        {
	        LineNumberReader script = new LineNumberReader(reader);
	        StringBuffer buff = new StringBuffer();
	        int start;
	        
	        while(script.ready())
	        {
	            buff.setLength(0);
	            String line = script.readLine();
	            if( line.trim().length() == 0 || line.charAt(0) == '#' || line.startsWith("--"))
	            {
	                continue;
	            }
	            start = script.getLineNumber();
	            while(script.ready() && line.charAt(line.length()-1) != ';')
	            {
	                buff.append(line);
	                line = script.readLine();
	                if(line.trim().length() == 0 || line.charAt(0) == '#' || line.startsWith("--"))
	                {
	                    if(script.ready())
	                    {
	                        line = script.readLine();
	                        continue;
	                    }
	                    else
	                    {
	                        break;
	                    }
	                }
	            }
	            if(line.length() == 0 || line.trim().charAt(line.trim().length()-1) != ';')
	            {
	                throw new SQLException("unterminated statement at line "+start);
	            }
	            buff.append(line);                
	            buff.setLength(buff.length()-1); // remove ;
	            try
	            {
	                stmt.execute(buff.toString());
	            }
	            catch(SQLException e)
	            {
	                throw (SQLException)
	                    new SQLException("error executing statement at line "+start).initCause(e);
	            }
	        }
        }
        finally
        {
            close(stmt);
        }        
    }
    
    
    /**
     * Checks if the given database contains a table with the given name.
     * 
     * @param ds DataSource for creating connections to database in question.
     * @param table table name, case insensitive.
     * @return <code>true</code> if the database contains the table.
     * @throws SQLException if there is a problem executing the check.
     */
    public static boolean hasTable(DataSource ds, String table)
        throws SQLException
    {
        Connection conn = null;
        ResultSet tables = null;
        try
        {
            conn = ds.getConnection();
            DatabaseMetaData md = conn.getMetaData();
            tables = md.getTables(null, null, null, null);
            boolean result = false;
            while(tables.next())
            {
                if(tables.getString("TABLE_NAME").equalsIgnoreCase(table))
                {
                    result = true;
                }
            }
            return result;
        }
        finally
        {
            close(tables);
            close(conn);
        }
    }    
}