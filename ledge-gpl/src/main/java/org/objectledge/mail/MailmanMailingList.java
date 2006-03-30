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

import org.objectledge.parameters.Parameters;

/**
 * Mailman mailing list.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: MailmanMailingList.java,v 1.2 2006-03-30 14:46:42 pablo Exp $
 */
public class MailmanMailingList implements MailingList
{
    /** list manager */
    private MailmanMailingListsManager manager;
    
    /** list name */
    private String listName;

    /** mailman admin password */
    private String adminPassword;
    
    /**
     * Mailman mailing list constructor.
     *
     * @param manager the ml manager.
     * @param listName the listName.
     * @param adminPassword the list administrator password.
     */
    public MailmanMailingList(MailmanMailingListsManager manager, 
        String listName, String adminPassword)
    {
        this.manager = manager;
        this.listName = listName;
        this.adminPassword = adminPassword;
    }

    /**
     * {@inheritDoc}
     */
    public int addMember(String address, String name, String password, 
        boolean digest, boolean ignoreCreationPolicy)
        throws MailingListsException
    {
        return manager.addMember(listName, adminPassword, address, name, password, digest, ignoreCreationPolicy);
    }

    /**
     * {@inheritDoc}
     */
    public void addMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException
    {
        manager.changeMemberAddress(listName, adminPassword, oldAddress, newAddress, true);
    }

    /**
     * {@inheritDoc}
     */
    public void changeMemberAddress(String oldAddress, String newAddress)
        throws MailingListsException
    {
        manager.changeMemberAddress(listName, adminPassword, oldAddress, newAddress, false);
    }

    /**
     * {@inheritDoc}
     */
    public int deleteMember(String address, boolean ignoreDeletingPolicy) throws MailingListsException
    {
        return manager.deleteMember(listName, adminPassword, address, ignoreDeletingPolicy); 
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getMembers() throws MailingListsException
    {
        return manager.getMembers(listName, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public Parameters getOptions() throws MailingListsException
    {
        return manager.getOptions(listName, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    public void setOptions(Parameters options) throws MailingListsException
    {
        manager.setOptions(listName, adminPassword, options);
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(String password) throws MailingListsException
    {
        this.adminPassword = manager.setPassword(listName, adminPassword, password); 
    }
}