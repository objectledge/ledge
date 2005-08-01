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

/**
 * Holds information about protocol and screen name, sufficient to initiate an IM session.
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: InstantMessagingContact.java,v 1.2 2005-08-01 09:44:06 rafal Exp $
 */
public class InstantMessagingContact
{
    private final InstantMessagingProtocol protocol;

    private final String screenName;

    /**
     * Creates a new InstantMessagingContact instance.
     * 
     * @param protocol the protocol.
     * @param screenName the screen name.
     * @throws IllegalArgumentException if the screen name does not follow format required by the
     *         protocol.
     */
    public InstantMessagingContact(InstantMessagingProtocol protocol, String screenName)
        throws IllegalArgumentException
    {
        if(!protocol.isValidScreenName(screenName))
        {
            throw new IllegalArgumentException(screenName
                + " is not a valid screen name for protocol " + protocol.getId());
        }
        this.protocol = protocol;
        this.screenName = screenName;
    }

    /**
     * Returns the protocol value.
     * 
     * @return the protocol.
     */
    public InstantMessagingProtocol getProtocol()
    {
        return protocol;
    }

    /**
     * Returns the screenName value.
     * 
     * @return the screenName.
     */
    public String getScreenName()
    {
        return screenName;
    }

    /**
     * Returns the status icon url for this contact.
     * 
     * @return the status icon url.
     */
    public String getStatusUrl()
    {
        return protocol.getStatusUrl(screenName);
    }
    
    /**
     * Returns representation of the contact.
     * 
     * @return String representation of the contact.
     */
    public String toString()
    {
        return protocol.getId() + ":" + screenName;
    }

    /**
     * Creates an InstantMessagingContact from the string representation.
     * 
     * @param stringForm the string representation.
     * @param instantMessaging the InstantMessaging component, for resolving protocol.
     * @return a new InstantMessagingContact instance.
     * @throws IllegalArgumentException if the stringFrom contains invalid representation.
     */
    public static InstantMessagingContact fromString(String stringForm,
        InstantMessaging instantMessaging) 
        throws IllegalArgumentException
    {
        String[] token = stringForm.split(":");
        if(token.length != 2)
        {
            throw new IllegalArgumentException("malfromed string representation: "+stringForm);
        }
        InstantMessagingProtocol protocol = instantMessaging.getProtocol(token[0]);
        if(protocol == null)
        {
            throw new IllegalArgumentException("unknown protocol: "+token[0]);
        }
        return new InstantMessagingContact(protocol, token[1]);
    }
}
