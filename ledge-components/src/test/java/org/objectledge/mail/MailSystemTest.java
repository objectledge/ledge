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

package org.objectledge.mail;

import java.util.Locale;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.SAXParserFactory;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.jcontainer.dna.impl.SAXConfigurationHandler;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.threads.ThreadPool;
import org.objectledge.utils.LedgeTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class MailSystemTest extends LedgeTestCase
{
    private FileSystem fs = null;

    private MailSystem mailSystem;

    public void setUp()
        throws Exception
    {
        super.setUp();
        fs = getFileSystem();
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(MailSystem.class));
        Configuration config = 
            getConfig("config/org.objectledge.templating.velocity.VelocityTemplating.xml");
        Templating templating = new VelocityTemplating(config, logger, fs);
        Context context = new Context();
        config = getConfig("config/org.objectledge.threads.ThreadPool.xml");
        ThreadPool threadPool = new ThreadPool(null, context,config, logger);
        config = getConfig("config/org.objectledge.mail.MailSystem.xml");
        checkSchema(
            "config/org.objectledge.mail.MailSystem.xml","org/objectledge/mail/MailSystem.rng");
        mailSystem = new MailSystem(config, logger, fs, templating, threadPool);
    }


    public void testMailSystem()
    {
        assertNotNull(mailSystem);
    }

    /*
     * Test for Session getSession()
     */
    public void testGetSession()
    {
        assertNotNull(mailSystem.getSession());
    }

    /*
     * Test for Session getSession(String)
     */
    public void testGetSessionString()
    {
        assertNull(mailSystem.getSession("foo"));
    }

    public void testGetDataSource()
    {
        DataSource ds = mailSystem.getDataSource("foo");
        assertNotNull(ds);
    }

    /*
     * Test for LedgeMessage newMessage()
     */
    public void testNewMessage() throws Exception
    {
        LedgeMessage message = mailSystem.newMessage();
        assertNotNull(message);
        Session session = mailSystem.getSession();
        Message msg = new MimeMessage(session);
        message.setMessage(msg);
        assertEquals(msg, message.getMessage());
        message = mailSystem.newMessage();
        message.setText("foo");
        message.setEncoding("ISO-8859-2");
        message.prepare();
        msg = message.getMessage();
        assertEquals("foo",msg.getContent());
        assertEquals("text/plain",msg.getContentType());
        
        message = mailSystem.newMessage();
        message.setTemplate(new Locale("pl","PL"), "PLAIN", "Foo");
        TemplatingContext context = message.getContext();
        context = message.getContext();
        assertNotNull(context);
        context.put("foo","bar");
        message.prepare();
        msg = message.getMessage();
        assertEquals("foo=bar",msg.getContent());
        
        message = mailSystem.newMessage();
        assertEquals(0, message.getAttachments().size());
        assertEquals(0, message.getRelatedContent().size());
    }   

    /*
     * Test for LedgeMessage newMessage(String)
     */
    public void testNewMessageString()
    {
        LedgeMessage message = mailSystem.newMessage("foo");
        assertNotNull(message);
    }

    public void testSend()
        throws Exception
    {
        LedgeMessage message = mailSystem.newMessage();
        try
        {
            mailSystem.send(message, true);
            fail("shoild throw the exception");
        }
        catch(MessagingException e)
        {
            // ok!
        }
    }

    public void testGetContentType()
    {
        assertEquals("text/html", mailSystem.getContentType("foo.html"));
        assertEquals("application/octet-stream", mailSystem.getContentType("xxx.xxx"));
    }

    public void testIsValidEmailAddress()
    {
        assertEquals(true, mailSystem.isValidEmailAddress("foo@bar.foo"));
        assertEquals(false, mailSystem.isValidEmailAddress("foo@bar_foo"));
    }

    private Configuration getConfig(String name)
        throws Exception
    {
        InputSource source = new InputSource(fs.
            getInputStream(name));
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        XMLReader reader = parserFactory.newSAXParser().getXMLReader();
        SAXConfigurationHandler handler = new SAXConfigurationHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.parse(source);
        return handler.getConfiguration();
    }

}
