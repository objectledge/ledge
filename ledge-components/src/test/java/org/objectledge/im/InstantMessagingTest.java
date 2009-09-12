// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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

package org.objectledge.im;

import org.jcontainer.dna.Configuration;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.test.LedgeTestCase;

/**
 *
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: InstantMessagingTest.java,v 1.2 2005-07-29 05:32:53 rafal Exp $
 */
public class InstantMessagingTest
    extends LedgeTestCase
{
    private InstantMessaging im;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        FileSystem fs = getFileSystem();
        Configuration config = getConfig(fs, "config/org.objectledge.im.InstantMessaging.xml");
        im = new InstantMessaging(config);
    }
    
    public void testConfig()
    {
        // test setUp()
    }
    
    public void testProtocolRegistry()
    {
        assertEquals(4, im.getProtocols().size());
        assertEquals(4, im.getProtocolsById().size());
        assertNotNull(im.getProtocol("icq"));
        assertTrue(im.getProtocolsById().containsKey("icq"));
    }
    
    public void testProtocolMethods()
    {
        InstantMessagingProtocol icq = im.getProtocol("icq");
        assertEquals("ICQ", icq.getName());
        assertEquals(null, icq.getIconUrl());
        assertEquals("http://icq.com/", icq.getInfoUrl());
        assertEquals("http://status.icq.com/online.gif?icq=3657252&img=5", icq.getStatusUrl("3657252"));
        assertEquals(true, icq.isValidScreenName("3657252"));
        assertEquals(false, icq.isValidScreenName("filem0n"));        
    }
    
    public void testContacts()
    {
        Parameters pd = new DefaultParameters();
        InstantMessagingProtocol icq = im.getProtocol("icq");
        InstantMessagingProtocol tlen = im.getProtocol("tlen");
        im.addContact(pd, new InstantMessagingContact(icq, "3657252"));
        im.addContact(pd, new InstantMessagingContact(tlen, "filem0n"));        
        assertEquals(2, im.getContacts(pd).size());
        for(InstantMessagingContact contact : im.getContacts(pd))
        {
            if(contact.getProtocol().getId().equals("icq"))
            {
                assertEquals(contact.getScreenName(), "3657252");
            }
            if(contact.getProtocol().getId().equals("tlen"))
            {
                assertEquals(contact.getScreenName(), "filem0n");
            }
        }
        im.removeContact(pd, new InstantMessagingContact(tlen, "filem0n"));
        assertEquals(1, im.getContacts(pd).size());
    }
}
