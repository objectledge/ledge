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

package org.objectledge.mail;

import java.util.List;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.Store;

/**
 * A dummy implmentation MailingListsManager.
 * 
 * This operation reports UNAVAILABLE status
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: DummyMailingListsManager.java,v 1.5 2006-04-26 10:36:43 rafal Exp $
 */
public class DummyMailingListsManager
    implements MailingListsManager
{
    /**
     * Exception thrown when methods other than getStaus() is called. 
     */
    private static final MailingListsException SUPPORT_UNAVAILABLE_EXCEPTION = 
        new MailingListsException("Mailing lists support is not available");

    /**
     * {@inheritDoc}
     */
    public Status getStatus()
    {
        return Status.UNAVAILABLE;
    }

    /**
     * {@inheritDoc}
     */
    public String createList(String name, String domain, String[] administrators, String password,
        boolean notify, Locale locale, boolean moderated)
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteList(String name, boolean deleteArchived)
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(String name, String password)
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(String name)
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getPublicLists()
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getLists()
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public List<Locale> getAvailableLocales()
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAvailableDomains()
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;        
    }
    
    /**
     * {@inheritDoc}
     */
    public Store getMessageStore()
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

    /**
     * {@inheritDoc}
     */
    public MailingList getList(Message message)
        throws MailingListsException
    {
        throw SUPPORT_UNAVAILABLE_EXCEPTION;
    }

}
