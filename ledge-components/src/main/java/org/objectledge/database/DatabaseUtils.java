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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.objectledge.utils.StringUtils;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DatabaseUtils.java,v 1.3 2004-01-22 15:53:14 pablo Exp $
 */
public class DatabaseUtils
{
    /** date format for PLSQL92 databases */
    private static SimpleDateFormat df = 
        new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("en","US"));
    
    private DatabaseUtils()
    {
    }

    // utilities //////////////////////////////////////////////////////////////////////////////

    /**
     * Close the connection
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
        catch (SQLException e)
        {
            //TODO report the exception to log.
            throw new Error("Couldn't close the connection - " +                " this error will be replaced be silent error log",e);    
        }
    }

    /**
     * Close the connection
     * 
     * @param conn the connection.
     * @param stmt the statement.
     * @param rs the result set. 
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    
    /**
     * Unescape the string that comes from query.
     * 
     * @param input the input string.
     * @return the result string.
     */
    public static String unescapeSqlString(String input)
    {
        return StringUtils.expandUnicodeEscapes(input);
    }
    
    /**
     * Escape the \ and ' in string that goes to statement.
     * 
     * @param input the input string.
     * @return the result string.
     */
    public static String escapeSqlString(String input)
    {
        return StringUtils.backslashEscape(StringUtils.escapeNonASCIICharacters(input), "'\\");
    }
    
    /**
     * Formate date to string acceptable by sql.  
     * 
     * @param date the date.
     * @return the string representation of date.
     */
    public static String format(Date date)
    {
        return df.format(date);
    }
    
    /**
     * Parse date from string.  
     * 
     * @param source the string representation of date.
     * @return the string representation of date.
     * @throws ParseException if invalid format.
     */
    public static Date parse(String source)
        throws ParseException
    {
        return df.parse(source);
    }
}
