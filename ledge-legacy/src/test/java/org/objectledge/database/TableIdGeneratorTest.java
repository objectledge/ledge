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

import java.io.FileInputStream;

import javax.sql.DataSource;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.objectledge.filesystem.FileSystem;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: IdGeneratorTest.java,v 1.8 2005-02-16 20:00:46 rafal Exp $
 */
public class TableIdGeneratorTest extends DatabaseTestCase
{
    private DataSource dataSource;
    
    /**
     * Constructor for IdGeneratorTest.
     * @param arg0
     */
    public TableIdGeneratorTest(String arg0)
        throws Exception
    {
        super(arg0);
        dataSource = getDataSource();
    }
    
    public void testNextId()
        throws Exception
    {
        TableIdGenerator idGenerator = new TableIdGenerator(dataSource);
        assertEquals(1, idGenerator.getNextId("test_table_one"));
        assertEquals(2, idGenerator.getNextId("test_table_one"));
        assertEquals(0, idGenerator.getNextId("test_table_two"));
        assertEquals(1, idGenerator.getNextId("test_table_two"));
        idGenerator.stop();
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////

    protected IDatabaseConnection getConnection() throws Exception
    {
        return new DatabaseDataSourceConnection(dataSource);
    }
    
    protected IDataSet getDataSet() throws Exception
    {
        return new XmlDataSet(new FileInputStream("src/test/resources/database/IdGenerator.xml"));
    }        
    
    private DataSource getDataSource()
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration("config","","/");
        DefaultConfiguration url = new DefaultConfiguration("url","","/config");
        url.setValue("jdbc:hsqldb:."); 
        conf.addChild(url);    
        DefaultConfiguration user = new DefaultConfiguration("user","","/config");
        user.setValue("sa");
        conf.addChild(user);
        DataSource ds = new HsqldbDataSource(conf);    
        if(!DatabaseUtils.hasTable(ds, "ledge_id_table"))
        {
            FileSystem fs = FileSystem.getStandardFileSystem(".");
            DatabaseUtils.runScript(ds, fs.getReader("sql/database/IdGeneratorTables.sql", 
                "UTF-8"));
        }
        return ds;
    }
}
