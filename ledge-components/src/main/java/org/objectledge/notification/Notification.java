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

package org.objectledge.notification;

/**
 * Notification component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class Notification
{
    /**
     * Registers a notification receiver for a specified channel.
     *
     * @param channel the chnnel name.
     * @param receiver the receiver.
     */
    public void addReceiver(String channel, NotificationReceiver receiver)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Unregisters a notification receiver for a specified channel.
     *
     * @param channel the chnnel name.
     * @param receiver the receiver.
     */
    public void removeReceiver(String channel, NotificationReceiver receiver)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * Sends a notification to a specified channel.
     *
     * @param channel the channel to send message to.
     * @param message the notification.
     * @param localEcho <code>true</code> if the notification should be passed
     * to local receivers.    
     */
    public void sendNotification(String channel, byte[] message, boolean localEcho)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
}
