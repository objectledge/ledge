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

import java.util.List;
import java.util.Locale;

/**
 * Mailing manager component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MailingListsManager.java,v 1.1 2006-03-29 15:54:48 pablo Exp $
 */
public interface MailingListsManager 
{
    /**
     * Create mailing list.
     *
     * @param name the name of the list.
     * @param domain the email domain.
     * @param moderated moderated if <code>true</code>
     * @param administrators list of administrators's email addresses
     * @param password the administrator password, auto generated if <code>null</code>
     * @param notify if <code>true</code> send notification about list creation.
     * @param locale mailing list locale.
     * @throws MailingListsException if ml creation failed.
     */
    public MailingList createList(String name, String domain, boolean moderated, 
        String[] administrators, String password, boolean notify, Locale locale)
    	throws MailingListsException;
    
    /**
     * Delete mailing list.
     *
     * @param list the list to be deleted.
     * @throws MailingListsException if ml deletion failed.
     */
    public void deleteList(MailingList list)
        throws MailingListsException;
    
    /**
     * Get mailing list.
     * 
     * @param name name of the list.
     * @param password the password.
     */
    public MailingList getList(String name, String password)
        throws MailingListsException;
    
    /**
     * Get all advertised lists.
     * 
     * @return the list of public lists.
     * @throws MailingListsException if something goes wrong.
     */
    public List<MailingList> getPublicLists()
        throws MailingListsException;
    
    /**
     * Get all lists.
     * 
     * @return the list of all lists.
     * @throws MailingListsException if something goes wrong;
     */
    public List<MailingList> getLists()
        throws MailingListsException;
    
}
