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

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.picocontainer.lifecycle.Stoppable;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: JotmTransactionTest.java,v 1.1 2004-02-04 16:06:44 fil Exp $
 */
public class JotmTransactionTest extends TestCase
{
    private Transaction transaction;

    /**
     * Constructor for JotmTransactionTest.
     * @param arg0
     */
    public JotmTransactionTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
        throws Exception
    {
        Logger log = new Log4JLogger(org.apache.log4j.Logger.getLogger(JotmTransactionTest.class));
        transaction = new JotmTransaction(log);
    }
    
    public void tearDown()
    {
        ((Stoppable)transaction).stop(); 
    }
    
    public void testGetUserTransaction()
        throws Exception
    {
        UserTransaction ut = transaction.getUserTransaction();
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
        ut.begin();
        assertEquals(Status.STATUS_ACTIVE, ut.getStatus());
        ut.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, ut.getStatus());
    }
    
    public void testCommit()
        throws Exception
    {
        boolean controller = transaction.begin();
        assertTrue(controller);
        transaction.commit(controller);
    }
    
    public void testRollback()
        throws Exception
    {
        boolean controller = transaction.begin();
        assertTrue(controller);
        transaction.rollback(controller);
    }
    
    public void testNestedCommit()
        throws Exception
    {
        boolean controller1 = transaction.begin();
        assertTrue(controller1);
        {
            boolean controller2 = transaction.begin();
            assertFalse(controller2);
            transaction.commit(controller2);
        }
        transaction.commit(controller1);
    }

    public void testNestedRollback()
        throws Exception
    {
        boolean controller1 = transaction.begin();
        assertTrue(controller1);
        {
            boolean controller2 = transaction.begin();
            assertFalse(controller2);
            transaction.rollback(controller2);
        }
        transaction.rollback(controller1);
    }

    public void testNestedMixed()
        throws Exception
    {
        boolean controller1 = transaction.begin();
        assertTrue(controller1);
        {
            boolean controller2 = transaction.begin();
            assertFalse(controller2);
            transaction.rollback(controller2);
        }
        try
        {
            transaction.commit(controller1);
            fail("exception expected");
        }
        catch(Exception e)
        {
            assertEquals("commit failed", e.getMessage());
        }
    }
}
