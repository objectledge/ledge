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

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.database.impl.DelegatingDataSource;

/**
 * An implementation of DataSource interface using HSQLDB.
 *  
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: HsqldbDataSource.java,v 1.5 2004-05-27 11:35:14 fil Exp $
 */
public class HsqldbDataSource extends DelegatingDataSource
{
    /**
     * Constructs a DataSource instance.
     * 
     * @param config the data source configuration.
     * @throws ConfigurationException if the configuration is invalid.
     */
    public HsqldbDataSource(Configuration config)
        throws ConfigurationException
    {
        super(getDataSource(config));
    }

    /**
     * Constructs a DataSource instance.
     * 
     * @param url a JDBC url
     * @param user user name
     * @param password password
     * @throws ConfigurationException if the configuration is invalid.
     */
    public HsqldbDataSource(String url, String user, String password)
    {
        super(getDataSource(url, user, password));
    }

    public static DataSource getDataSource(String url, String user, String password)
    {
        JDBCDataSource dataSource;
        dataSource = new JDBCDataSource();
        dataSource.setDatabase(url);
        dataSource.setUser(user);
        if(password != null)
        {
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    private static DataSource getDataSource(Configuration config)
        throws ConfigurationException
    {
        String url = config.getChild("url").getValue();
        String user = config.getChild("user").getValue(null);
        String password = config.getChild("password").getValue(null);
        return getDataSource(url, user, password);
    }
}
