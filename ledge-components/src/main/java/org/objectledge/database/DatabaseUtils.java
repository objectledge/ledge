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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;

/**
 * A set of utility functions for working with JDBC databases.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DatabaseUtils.java,v 1.21 2005-03-30 09:20:04 rafal Exp $
 */
public class DatabaseUtils
{
    private static final String SQL_KEYWORDS_RESOURCE = "SQL2003.txt";

    private static final String SQL_2003_KEYWORDS = loadSql2003Keywords();

    /** A logger. Bypasses LogFactory. */
    private static Logger log = 
        new Log4JLogger(org.apache.log4j.Logger.getLogger(DatabaseUtils.class));
    
    private static final Map<String, Set<String>> reservedWords = new ConcurrentHashMap<String, Set<String>>();

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
     * Escape ' characters in string that goes to statement.
     * 
     * @param input the input string.
     * @return the result string.
     */
    public static String escapeSqlString(String input)
    {
        return input == null ? null : input.replaceAll("'", "''");
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
	        StringBuilder buff = new StringBuilder();
	        int start;
	        
	        while(script.ready())
	        {
	            buff.setLength(0);
	            String line = script.readLine().trim();
	            if(line.length() == 0 || line.charAt(0) == '#' || line.startsWith("--"))
	            {
	                continue;
	            }
	            start = script.getLineNumber();
	            while(script.ready() && line.charAt(line.length()-1) != ';')
	            {
	                buff.append(line).append('\n');
	                line = script.readLine().trim();
	                if(line.length() == 0 || line.charAt(0) == '#' || line.startsWith("--"))
	                {
	                    if(script.ready())
	                    {
	                        line = script.readLine().trim();
	                        continue;
	                    }
	                    else
	                    {
	                        break;
	                    }
	                }
	            }
	            if(line.length() == 0 || line.charAt(line.length()-1) != ';')
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

    /**
     * Transfer data from one database to another.
     * <p>
     * inputQuery and outputStatement must agree in number and sequence of columns.
     * </p>
     * <p>
     * outputStatement may be an {@code insert} or {@code update} statement.
     * </p>
     * <p>
     * Note that moving data between tables in a single database can be done much faster with plain
     * {@code insert ... select ...} SQL statement without the roundtrip to JVM and back.
     * </p>
     * 
     * @param inputConn input connection.
     * @param outputConn output connection.
     * @param inputQuery input query.
     * @param outputStatement output statement.
     * @param batchSize batch size, if positive, negative to disable batching.
     * @param batchCommits should a commit be issued after each batch.
     * @return number of copied rows.
     * @throws SQLException
     */
    public static int transfer(final Connection inputConn, final Connection outputConn,
        final String inputQuery, final String outputStatement, final int batchSize,
        boolean batchCommits)
        throws SQLException
    {
        int rowCount = 0;
        final Statement inStmt = inputConn.createStatement();
        try
        {
            final ResultSet in = inStmt.executeQuery(inputQuery);
            try
            {
                outputConn.setAutoCommit(false);
                final PreparedStatement outStmt = outputConn.prepareStatement(outputStatement);
                try
                {
                    final ResultSetMetaData rsMetaData = in.getMetaData();
                    if(batchSize > 0)
                    {
                        int batchCount = 0;
                        while(!in.isAfterLast())
                        {
                            while(in.next())
                            {
                                for(int i = 1; i <= rsMetaData.getColumnCount(); i++)
                                {
                                    Object val = in.getObject(i);
                                    outStmt.setObject(i, val, rsMetaData.getColumnType(i));
                                }
                                outStmt.addBatch();
                                rowCount++;
                                batchCount++;
                                if(batchCount == batchSize)
                                {
                                    break;
                                }
                            }
                            if(batchCount > 0)
                            {
                                outStmt.executeBatch();
                                if(batchCommits)
                                {
                                    outputConn.commit();
                                }
                                batchCount = 0;
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        while(in.next())
                        {
                            for(int i = 1; i <= rsMetaData.getColumnCount(); i++)
                            {
                                Object val = in.getObject(i);
                                outStmt.setObject(i, val, rsMetaData.getColumnType(i));
                            }
                            outStmt.execute();
                        }
                    }
                }
                finally
                {
                    if(batchSize < 0 || !batchCommits)
                    {
                        outputConn.commit();
                    }
                    outStmt.close();
                    outputConn.setAutoCommit(true);
                }
            }
            finally
            {
                in.close();
            }
        }
        finally
        {
            inStmt.close();
        }
        return rowCount;
    }

    /**
     * Transfer a table from one database to another.
     * <p>
     * Target table must exist and must agree in number and type of columns with the source table.
     * </p>
     * <p>
     * {@code null} may be used as wildcard for catalog and schema names.
     * </p>
     * 
     * @param inputConn input connection.
     * @param outputConn output connection.
     * @param catalog input database catalog.
     * @param schema input database schema.
     * @param tableName table to be moved.
     * @param sourceWhereClause optional {@code WHERE} clause, may be {@code null}.
     * @param truncate should the output table be truncated.
     * @param batchSize batch size to use, negative to disable batching.
     * @param batchCommit should a {@code COMMIT} be issued after transferring each batch.
     * @return
     * @throws SQLException
     */
    public static int transferTable(Connection inputConn, Connection outputConn, String catalog,
        String schema, String tableName, String sourceWhereClause, boolean truncate, int batchSize,
        boolean batchCommit)
        throws SQLException
    {
        // discover columns
        List<String> columns = new ArrayList<String>();
        ResultSet colRs = inputConn.getMetaData().getColumns(
            adjustIdentifierCase(catalog, inputConn), adjustIdentifierCase(schema, inputConn),
            adjustIdentifierCase(tableName, inputConn), "%");
        try
        {
            while(colRs.next())
            {
                columns.add(colRs.getString("COLUMN_NAME"));
            }
        }
        finally
        {
            colRs.close();
        }
    
        // build input query
        StringBuilder buff = new StringBuilder();
        buff.append("SELECT ");
        for(int i = 0; i < columns.size(); i++)
        {
            buff.append(columns.get(i));
            if(i < columns.size() - 1)
            {
                buff.append(", ");
            }
        }
        buff.append(" FROM ").append(tableName);
        if(sourceWhereClause != null)
        {
            buff.append(" WHERE ").append(sourceWhereClause);
        }
        final String inputQuery = buff.toString();
    
        // build output statement
        buff.setLength(0);
        buff.append("INSERT INTO ");
        buff.append(tableName).append(" (");
        for(int i = 0; i < columns.size(); i++)
        {
            buff.append(columns.get(i));
            if(i < columns.size() - 1)
            {
                buff.append(", ");
            }
        }
        buff.append(") VALUES (");
        for(int i = 0; i < columns.size(); i++)
        {
            buff.append("?");
            if(i < columns.size() - 1)
            {
                buff.append(", ");
            }
        }
        buff.append(")");
        final String outputStatement = buff.toString();
    
        // truncate output table
        if(truncate)
        {
            Statement truncStmt = outputConn.createStatement();
            try
            {
                truncStmt.execute("TRUNCATE TABLE " + tableName);
            }
            finally
            {
                truncStmt.close();
            }
        }
    
        // transfer data
        return transfer(inputConn, outputConn, inputQuery, outputStatement,
            batchSize, batchCommit);
    }

    /**
     * Attempt to shut down an Java in-process database.
     * 
     * @param dataSource an SQL {@link javax.sql.DataSource}
     * @throws SQLException
     */
    public static void shutdown(DataSource dataSource)
        throws SQLException
    {
        DatabaseType dbType = DatabaseType.detect(dataSource);
        if(dbType == DatabaseType.HSQL || dbType == DatabaseType.H2)
        {
            Connection conn = dataSource.getConnection();
            try
            {
                Statement stmt = conn.createStatement();
                try
                {
                    stmt.execute("SHUTDOWN");
                }
                finally
                {
                    stmt.close();
                }
            }
            finally
            {
                conn.close();
            }
        }
    }

    /**
     * Adjusts an SQL identifier to correct case according to the database convention.
     * 
     * @param identifier a SQL identifier.
     * @param conn a JDBC connection
     * @return case-adjusted identifier
     * @throws SQLException
     */
    public static String adjustIdentifierCase(String identifier, Connection conn)
        throws SQLException
    {
        DatabaseMetaData md = conn.getMetaData();
        if(!md.storesMixedCaseIdentifiers() && identifier != null)
        {
            if(md.storesUpperCaseIdentifiers())
            {
                return identifier.toUpperCase();
            }
            if(md.storesLowerCaseIdentifiers())
            {
                return identifier.toLowerCase();
            }
        }
        return identifier;
    }

    public static Set<String> reservedWords(Connection conn)
        throws SQLException
    {
        DatabaseMetaData md = conn.getMetaData();
        String id = getDatabaseId(md);
        Set<String> words = reservedWords.get(id);
        if(words == null)
        {
            words = new HashSet<String>();
            words.addAll(Arrays.asList(SQL_2003_KEYWORDS.toUpperCase().split("\n")));
            words.addAll(Arrays.asList(md.getSQLKeywords().toUpperCase().split(",")));
            words.remove("");
            reservedWords.put(id, words);
        }
        return words;
    }

    public static String quotedIdentifier(String id, Connection conn)
        throws SQLException
    {
        Set<String> reserved = reservedWords(conn);
        if(reserved.contains(id.toUpperCase()))
        {
            DatabaseMetaData md = conn.getMetaData();
            String q = md.getIdentifierQuoteString();
            return q + id + q;
        }
        else
        {
            return id;
        }
    }

    private static String getDatabaseId(DatabaseMetaData md)
        throws SQLException
    {
        return md.getDatabaseProductName() + " " + md.getDatabaseProductVersion();
    }

    private static String loadSql2003Keywords()
    {
        InputStream is = DatabaseUtils.class.getResourceAsStream(SQL_KEYWORDS_RESOURCE);
        StringWriter w = new StringWriter();
        Reader r = new InputStreamReader(new BufferedInputStream(is));
        try
        {
            int i;
            while((i = r.read()) > 0)
            {
                w.write(i);
            }
            return w.toString();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Can't load " + SQL_KEYWORDS_RESOURCE + " from classpath", e);
        }
        finally
        {
            try
            {
                r.close();
                w.close();
            }
            catch(IOException e)
            {
                // ignore
            }
        }
    }
}