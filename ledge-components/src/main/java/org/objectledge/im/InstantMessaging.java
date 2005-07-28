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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.parameters.Parameters;

/**
 * Provides services related to Instant Messaging tools and protocols.
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: InstantMessaging.java,v 1.2 2005-07-28 13:10:48 rafal Exp $
 */
public class InstantMessaging
{
    private final Map<String,InstantMessagingProtocol> protocols;

    private static final String IM_ATTRIBUTE = "instantMessaging";

    /**
     * Creates a new InstantMessaging instance.
     *
     * @param config component configuration.
     * @throws ConfigurationException if the configuration is incorrect.
     */
    public InstantMessaging(Configuration config) throws ConfigurationException
    {
        Map<String,InstantMessagingProtocol> tmp = new HashMap<String,InstantMessagingProtocol>();
        for(Configuration protocolConfig : config.getChildren("protocol"))
        {
            InstantMessagingProtocol protocol = new InstantMessagingProtocol(protocolConfig);
            tmp.put(protocol.getId(), protocol);
        }
        protocols = Collections.unmodifiableMap(tmp);
    }
 
    /**
     * Returns the descriptions of the available protocols.
     * 
     * @return the descriptions of available protocols.
     */
    public Collection<InstantMessagingProtocol> getProtocols()
    {
        return protocols.values();
    }

    /**
     * Returns the descriptions of the available protocols keyed by id.
     * 
     * @return the descriptions of available protocols keyed by id.
     */
    public Map<String,InstantMessagingProtocol> getProtocolsById()
    {
        return protocols;
    }

    /**
     * Returns the description of protocol with the specified id.
     * 
     * @param id protocol id.
     * @return the protocol description.
     */
    public InstantMessagingProtocol getProtocol(String id)
    {
        return protocols.get(id);
    }
    
    /**
     * Returns the the defined IM contacts for the user.
     * 
     * @param personalData user's personal data.
     * @return the defined IM contatacs.
     * @throws IllegalArgumentException if the personald data contains corrupted information.
     */
    public Collection<InstantMessagingContact> getContacts(Parameters personalData)
        throws IllegalArgumentException
    {
        Collection<InstantMessagingContact> contacts = new ArrayList<InstantMessagingContact>();
        for(String item : personalData.getStrings(IM_ATTRIBUTE))
        {
            int p = item.indexOf(':');
            if(p < 0)
            {
                throw new IllegalArgumentException("malformed "+IM_ATTRIBUTE+" value: "+item);
            }
            String protocolId = item.substring(0, p);
            String screenName = item.substring(p+1);
            InstantMessagingProtocol protocol = protocols.get(protocolId);
            if(protocol == null)
            {
                throw new IllegalArgumentException("unknonw protocol "+protocolId);
            }
            contacts.add(new InstantMessagingContact(protocol, screenName));
        }
        return contacts;
    }
    
    /**
     * Adds an IM contact to the user's personal data.
     * 
     * @param personalData the personal data to modify.
     * @param contact contact to add.
     */
    public void addContact(Parameters personalData, InstantMessagingContact contact)
    {
        personalData.add(IM_ATTRIBUTE, contact.toString());
    }
    
    /**
     * Removes an IM contact from the user's personal data.
     * 
     * @param personalData the personal data to modify.
     * @param contact contact to add.
     */
    public void removeContact(Parameters personalData, InstantMessagingContact contact)
    {
        personalData.remove(IM_ATTRIBUTE, contact.toString());        
    }
}
