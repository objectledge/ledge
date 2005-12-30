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

package org.objectledge.web.mvc.security;

import java.security.Principal;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.LedgeWebTestCase;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.i18n.LocaleLoaderValve;
import org.objectledge.security.RoleChecking;

public class PolicySystemTest extends LedgeWebTestCase
{
    /**
     * number of policies defined in the config file.
     */
    private static final int NUM_POLICIES = 1;
    
    private PolicySystem policySystem;
    
    private FileSystem fs;
    
    private Logger logger;

    public void setUp() throws Exception
    {
        fs = getFileSystem("src/test/resources/config");
        logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(LocaleLoaderValve.class));
        Configuration config = getConfig(fs, PolicySystem.class, PolicySystem.class);
        setupPolicySystem(config);
    }

    private void setupPolicySystem(Configuration config)
        throws ConfigurationException
    {
        RoleChecking roleChecking = new RoleChecking()
        {
            public String[] getRoles(Principal user) throws UserUnknownException
            {
                if (user.getName() == "root")
                {
                    return new String[] { "admin", "moderator" };
                }
                if (user.getName() == "user")
                {
                    return new String[] { "user" };
                }
                if (user.getName() == "anon")
                {
                    return null;
                }
                throw new UserUnknownException("unknown user: " + user.getName());
            }
        };
        policySystem = new PolicySystem(config, logger, roleChecking);
    }
    
    public void testGetGlobalSSL()
    {
        assertEquals(policySystem.getGlobalSSL(), false);
    }

    public void testGetGlobalLogin()
    {
        assertEquals(policySystem.getGlobalLogin(), false);
    }

    public void testSetGlobalSSL()
    {
        policySystem.setGlobalSSL(true);
        assertEquals(policySystem.getGlobalSSL(), true);
    }

    public void testSetGlobalLogin()
    {
        policySystem.setGlobalLogin(true);
        assertEquals(policySystem.getGlobalLogin(), true);
    }

    public void testGetGlobalAccess()
    {
        assertEquals(policySystem.getGlobalAccess(), true);
    }

    public void testSetGlobalAccess()
    {
        policySystem.setGlobalAccess(false);
        assertEquals(policySystem.getGlobalAccess(), false);
    }

    /*
     * Test for Policy getPolicy(String)
     */
    public void testGetPolicyString()
    {
        Policy policy = policySystem.getPolicy("bar");
        assertNotNull(policy);
        try
        {
            policy = policySystem.getPolicy("foo");
            fail("should throw the exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
    }

    public void testGetPolicies()
    {
        Policy[] policies = policySystem.getPolicies();
        assertNotNull(policies);
        assertEquals(NUM_POLICIES, policies.length);
    }

    /*
     * Test for Policy getPolicy(String, String)
     */
    public void testGetPolicyStringString()
    {
        Policy policy = policySystem.getPolicy("action", "view");
        assertNull(policy);
        policy = policySystem.getPolicy("view", "action");
        assertNotNull(policy);
    }

    public void testAddPolicy()
    {
        Policy[] policies = policySystem.getPolicies();
        assertEquals(NUM_POLICIES, policies.length);
        policySystem.addPolicy("foo", false, false, 
            new String[] { "admin" }, new String[] { "view" }, new String[] { "action" });
        policies = policySystem.getPolicies();
        assertEquals(NUM_POLICIES + 1, policies.length);
    }

    public void testRemovePolicy()
    {
        Policy[] policies = policySystem.getPolicies();
        assertEquals(NUM_POLICIES, policies.length);
        policySystem.addPolicy("foo", false, false, 
            new String[] { "admin" }, new String[] { "view" }, new String[] { "action" });
        policies = policySystem.getPolicies();
        assertEquals(NUM_POLICIES + 1, policies.length);
        policySystem.removePolicy("foo");
        policies = policySystem.getPolicies();
        assertEquals(NUM_POLICIES, policies.length);
        try
        {
            policySystem.removePolicy("foo2");
            fail("should throw the exception");
        }
        catch (IllegalArgumentException e)
        {
            //ok!
        }
    }

    public void testCheckPolicy()
    {
        Policy policy = policySystem.getPolicy("bar");
        assertEquals(policySystem.checkPolicy(new DefaultPrincipal("root"), true, policy), true);
        policy = new Policy("test", false, true, 
            new String[] { "admin" }, new String[] { "foo" }, new String[] { "bar" });
        assertEquals(policySystem.checkPolicy(new DefaultPrincipal("anon"), false, policy), false);
        policy = new Policy("test", false, false, 
            new String[] { "admin" }, new String[] { "foo" }, new String[] { "bar" });
        assertEquals(policySystem.checkPolicy(new DefaultPrincipal("anon"), false, policy), true);
    }

    public void testRequiresSSL()
    {
        policySystem.addPolicy("foo", true, true, 
            new String[] { "admin" }, new String[] { "foo" }, new String[] { "bar" });
        assertEquals(policySystem.requiresSSL("view", "action"), false);
        assertEquals(policySystem.requiresSSL("foo", "bar"), true);
        assertEquals(policySystem.requiresSSL("foo2", "bar2"), false);
    }

    public void testRequiresLogin()
    {
        policySystem.addPolicy("foo", true, true, 
            new String[] { "admin" }, new String[] { "foo" }, new String[] { "bar" });
        assertEquals(policySystem.requiresLogin("view", "action"), true);
        assertEquals(policySystem.requiresLogin("foo", "bar"), true);
        assertEquals(policySystem.requiresLogin("foo2", "bar2"), false);
    }

    public void testCheckAccess()
    {
        assertEquals(policySystem.checkAccess("view", "action", 
            new DefaultPrincipal("root"), true), true);
        assertEquals(policySystem.checkAccess("view", "action", 
            new DefaultPrincipal("user"), true), false);
        assertEquals(policySystem.checkAccess("foo2", "bar2", 
            new DefaultPrincipal("user"), true), true);
        assertEquals(policySystem.checkAccess("view", "action", 
            new DefaultPrincipal("anon"), true), false);
        assertEquals(policySystem.checkAccess("view", "action", 
            new DefaultPrincipal("foo"), true), false);
    }

    public void testPolicyTest()
    {
        Policy policy = new Policy("test", false, true, 
            new String[] { "admin" }, new String[] { "foo*" }, new String[] { "bar*" });
        assertEquals(policy.matchesRequest("foo", "bar"), true);
        assertEquals(policy.matchesRequest("foo2", "bar"), true);
        assertEquals(policy.matchesRequest("bar.foo", "xxx"), false);
        assertEquals(policy.matchesRequest("foo.foo", "bar.foo"), true);
        assertEquals(policy.matchesRequest("bar.foo", "bar.bar"), true);
    }
    
    public void testExcept()
        throws Exception
    {
        setupPolicySystem(getConfig(fs, PolicySystem.class.getName()+"-alt", PolicySystem.class));
        Policy withExcept = policySystem.getPolicy("withExcept");
        assertTrue(withExcept.matchesRequest("admin.Home", null));
        assertFalse(withExcept.matchesRequest("public.Home", null));
    }
    
    public void testRegex() 
        throws Exception
    {
        setupPolicySystem(getConfig(fs, PolicySystem.class.getName()+"-alt", PolicySystem.class));
        Policy withExcept = policySystem.getPolicy("withRegex");
        assertTrue(withExcept.matchesRequest("Home", null));
        assertFalse(withExcept.matchesRequest("admin.Home", null));        
    }

    public void testNoView()
        throws Exception
    {
        setupPolicySystem(getConfig(fs, PolicySystem.class.getName()+"-alt", PolicySystem.class));
        Policy withExcept = policySystem.getPolicy("withNoView");
        assertTrue(withExcept.matchesRequest(null, null));
        assertFalse(withExcept.matchesRequest("Home", null));        
    }
}