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

import junit.framework.TestCase;

import org.jcontainer.dna.impl.DefaultConfiguration;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: HsqldbDataSourceTest.java,v 1.1 2004-02-04 09:39:08 fil Exp $
 */
public class HsqldbDataSourceTest extends TestCase
{

    /**
     * Constructor for HsqldbDataSourceTest.
     * @param arg0
     */
    public HsqldbDataSourceTest(String arg0)
    {
        super(arg0);
    }
    
    public void testInMemory()
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config","","/");
        DefaultConfiguration url = new DefaultConfiguration("url","","/config");
        url.setValue("jdbc:hsqldb:."); 
        conf.addChild(url);    
        DefaultConfiguration user = new DefaultConfiguration("user","","/config");
        user.setValue("sa");
        conf.addChild(user);
        HsqldbDataSource dataSource = new HsqldbDataSource(conf);
        Connection conn = dataSource.getConnection();
        assertNotNull(conn);
        conn.close();
    }

    public void testInMemoryWithPassword()
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config","","/");
        DefaultConfiguration url = new DefaultConfiguration("url","","/config");
        url.setValue("jdbc:hsqldb:."); 
        conf.addChild(url);    
        DefaultConfiguration user = new DefaultConfiguration("user","","/config");
        user.setValue("sa");
        conf.addChild(user);
        DefaultConfiguration password = new DefaultConfiguration("password","","/config");
        password.setValue("");
        conf.addChild(password);
        HsqldbDataSource dataSource = new HsqldbDataSource(conf);
        Connection conn = dataSource.getConnection();
        assertNotNull(conn);
        conn.close();
    }
}
