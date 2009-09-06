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

package org.objectledge.authentication;

import java.io.IOException;
import java.io.Reader;
import java.security.Principal;

import javax.naming.InvalidNameException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.hsqldb.jdbc.jdbcDataSource;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.JotmTransaction;
import org.objectledge.database.persistence.DefaultPersistence;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LedgeDOMConfigurator;
import org.objectledge.naming.ContextFactory;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;
import org.objectledge.utils.LedgeTestCase;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryUserManagerTest extends LedgeTestCase
{
    private FileSystem fs = null;

    private ContextFactory contextFactory;

    private UserManager userManager;
    
    public void setUp()
        throws Exception
    {
        fs = FileSystem.getStandardFileSystem("src/test/resources");
        InputSource source = new InputSource(fs.getInputStream(
            "config/org.objectledge.logging.LoggingConfigurator.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document logConfig = builder.parse(source);
        LedgeDOMConfigurator configurator = new LedgeDOMConfigurator(fs);
        configurator.doConfigure(logConfig.getDocumentElement(), LogManager.getLoggerRepository());
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(ContextFactory.class));
        DataSource ds = getDataSource();
        DefaultPicoContainer container = new DefaultPicoContainer();
        IdGenerator idGenerator = new IdGenerator(ds);
        JotmTransaction transaction = new JotmTransaction(0, 120, new Context(), logger, null);
        Database database = new DefaultDatabase(ds, idGenerator, transaction);
        Persistence persistence = new DefaultPersistence(database, logger);
        container.registerComponentInstance(Persistence.class, persistence);        
        
        Configuration config = getConfig("naming/dbNaming.xml");
        contextFactory = new ContextFactory(container, config, logger);
        PasswordGenerator passwordGenerator = new PasswordGenerator();
        PasswordDigester passwordDigester = new PasswordDigester("md5");
        config = getConfig("config/org.objectledge.authentication.NamingPolicy.xml");
        checkSchema("config/org.objectledge.authentication.NamingPolicy.xml", 
                            "org/objectledge/authentication/NamingPolicy.rng");        
        NamingPolicy namingPolicy = new NamingPolicy(config);
        config = getConfig("config/org.objectledge.authentication.LoginVerifier.xml");
        checkSchema("config/org.objectledge.authentication.LoginVerifier.xml", 
                    "org/objectledge/authentication/LoginVerifier.rng");
        LoginVerifier loginVerifier = new LoginVerifier(config);
        config = getConfig("config/org.objectledge.authentication.DirectoryUserManager.xml");
        userManager = new DirectoryUserManager(config, logger, namingPolicy, loginVerifier, 
            passwordGenerator, passwordDigester, contextFactory, new UserManagementParticipant[]{});
    }

    public void testUserExists()
        throws Exception
    {
        assertEquals(userManager.userExists("uid=foo,ou=people,dc=objectledge,dc=org"),false);
        assertEquals(userManager.userExists("uid=foo,ou=people,dc=objectledge2,dc=org"),false);
        Parameters params = new DefaultParameters();
        params.add("uid","foo");
        String dn = userManager.createDN(params);
        assertEquals(dn,"uid=foo,ou=people,dc=objectledge,dc=org");
        userManager.createAccount("foo",dn, "bar");
        assertEquals(userManager.userExists("uid=foo,ou=people,dc=objectledge,dc=org"),true);
    }

    public void testCreateAccount()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        assertEquals(dn,"uid=foo,ou=people,dc=objectledge,dc=org");
        Principal principal = userManager.createAccount("foo",dn, "bar");
        assertEquals(principal, userManager.getUserByName(dn));
        assertEquals(principal.getName(), dn);
        assertEquals(principal.equals(""), false);
        principal.toString();
        try
        {
            userManager.createAccount("foo",dn, "bar");
            fail("should throw the exception");
        }
        catch(UserAlreadyExistsException e)
        {
            //ok!
        }
        params.set("uid","root");
        dn = userManager.createDN(params);
        assertEquals(dn,"uid=root,ou=people,dc=objectledge,dc=org");
        try
        {
            userManager.createAccount("root",dn, "bar");
            fail("should throw the exception");
        }
        catch(AuthenticationException e)
        {
            //ok!
        }
    }

    public void testRemoveAccount()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        assertEquals(dn,"uid=foo,ou=people,dc=objectledge,dc=org");
        Principal principal = userManager.createAccount("foo",dn, "bar");
        userManager.removeAccount(principal);
        try
        {
            userManager.removeAccount(principal);
            fail("should throw the exception");
        }
        catch(UserUnknownException e)
        {
            //ok!
        }
    }

    public void testGetUserByLogin()
        throws Exception
    {
        //TODO test id
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        Principal principal = userManager.createAccount("foo",dn, "bar");
        Parameters parameters = new DirectoryParameters(userManager.getPersonalData(principal));
        assertEquals(parameters.get("uid"),"foo");
        userManager.getUserByLogin("foo");
        try
        {
            userManager.getUserByLogin("bar");
            fail("should throw the exception");
        }
        catch(UserUnknownException e)
        {
            //ok!
        }        
    }

    public void testGetAnonymousAccount()
        throws Exception
    {
        Principal anonymous = userManager.getAnonymousAccount();
        assertEquals(anonymous.getName(),"uid=anonymous,ou=people,dc=objectledge,dc=org");
    }

    public void testGetSuperuserAccount()
        throws Exception
    {
        Principal root = userManager.getSuperuserAccount();
        assertEquals(root.getName(),"uid=root,ou=people,dc=objectledge,dc=org");        
    }

    public void testChangeUserPassword()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        Principal principal = userManager.createAccount("foo",dn, "bar");
        assertEquals(userManager.checkUserPassword(principal, "bar"),true);
        assertEquals(userManager.checkUserPassword(principal, "foo"),false);
        userManager.changeUserPassword(principal, "foo");
        assertEquals(userManager.checkUserPassword(principal, "bar"),false);
        assertEquals(userManager.checkUserPassword(principal, "foo"),true);
        try
        {
            userManager.checkUserPassword(new DefaultPrincipal("foo"),"bar");
            fail("should throw the exception");
        }
        catch(AuthenticationException e)
        {
            //ok!
        }        
    }

    public void testGetPersonalData()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        Principal principal = userManager.createAccount("foo",dn, "bar");
        params = new DirectoryParameters(userManager.getPersonalData(principal));
        assertEquals(params.get("uid"),"foo");
        try
        {
            userManager.getPersonalData(new DefaultPrincipal("foo"));
            fail("should throw the exception");
        }
        catch(AuthenticationException e)
        {
            //ok!
        }        
    }

    public void testLookupAccountsStringString()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        userManager.createAccount("foo",dn, "bar");
        Principal[] results = userManager.lookupAccounts("foo","bar");
        assertEquals(results.length,0);
        results = userManager.lookupAccounts("uid","foo");
        assertEquals(results.length,1);
    }

    public void testLookupAccountsString()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        userManager.createAccount("foo",dn, "bar");
        /*
        Principal[] results = userManager.lookupAccounts("(foo=bar)");
        assertEquals(results.length,0);
        results = userManager.lookupAccounts("(uid=foo)");
        assertEquals(results.length,1);
        */        
    }

    public void testUserManager()
    {
        assertNotNull(userManager);
    }

    public void testCheckLogin()
    {
        assertEquals(userManager.checkLogin(""),true);
        assertEquals(userManager.checkLogin("foo"),true);
    }

    public void testValidateLogin()
    {
        assertEquals(userManager.validateLogin(""),false);
        assertEquals(userManager.validateLogin("foo"),true);
    }

    public void testCreateDN()
    {
        Parameters params = new DefaultParameters();
        params.add("uid","foo");
        String dn = userManager.createDN(params);
        assertEquals(dn,"uid=foo,ou=people,dc=objectledge,dc=org");
    }

    public void testGetLogin()
        throws Exception
    {
        Parameters params = new DefaultParameters();
        params.set("uid","foo");
        String dn = userManager.createDN(params);
        Principal principal = userManager.createAccount("foo",dn, "bar");
        assertEquals(userManager.getLogin(principal),"foo");
        assertEquals(userManager.getLogin(dn),"foo");
        try
        {
            userManager.getLogin("bar");
            fail("should throw the exception");
        }
        catch(InvalidNameException e)
        {
            //ok!
        }
    }

    public void testCreateRandomPassword()
    {
        String pass = userManager.createRandomPassword(6,8);
        assertEquals(pass.length()>=6,true);
        assertEquals(pass.length()<=8,true);
    }

    public void testDefaultPrincipal()
    {
        Principal principal = new DefaultPrincipal(null);
        principal.toString();
        assertEquals(principal.hashCode(), 0);
    }

    /////////////// private 
    private DataSource getDataSource()
        throws Exception
    {
        jdbcDataSource ds = new jdbcDataSource();
        ds.setDatabase("jdbc:hsqldb:.");
        ds.setUser("sa");
        ds.setPassword("");
        if(!DatabaseUtils.hasTable(ds, "ledge_id_table"))
        {
            DatabaseUtils.runScript(ds, getScript("sql/database/IdGeneratorTables.sql"));
        }
        if(!DatabaseUtils.hasTable(ds, "ledge_naming_context"))
        {        
            DatabaseUtils.runScript(ds, getScript("sql/naming/db/DBNamingTables.sql"));
        }
        DatabaseUtils.runScript(ds, getScript("sql/naming/db/DBNamingTest.sql"));
        return ds;
    }    
    
    private Reader getScript(String path)
        throws IOException
    {
        return fs.getReader(path, "UTF-8");
    }    

    private Configuration getConfig(String name)
        throws Exception
    {
        return getConfig(fs, name);
    }
}
