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

package org.objectledge.fax;

import java.io.InputStream;

import org.objectledge.parameters.Parameters;

/**
 * Fax manager component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FaxManager.java,v 1.2 2004-12-22 08:35:08 rafal Exp $
 */
public interface FaxManager 
{
    /**
     * Send simple fax with default settings.
     *
     * @param destinationAddress the number to dial up.
     * @param content the body of the fax.
     * @param encoding the content encoding.
     * @param notify the notification switch.
     * @param notificationAddress the address to send the notification.
     * @param highResolution the resolution mode switch.
     * @param from the fax sender header.
     * @throws FaxManagerException if fax seding failed.
     */
    public void sendFax(String destinationAddress, String content, String encoding, boolean notify, 
                        String notificationAddress, boolean highResolution, String from)
    	throws FaxManagerException;
    
    /**
     * Send fax.
     *
     * @param destinationAddress the number to dial up.
     * @param from the fax sender header.
     * @param content the body of the fax.
     * @param parameters the fax sending paramters.
     * @throws FaxManagerException if fax seding failed.
     */
    public void sendFax(String destinationAddress, String from, 
    					InputStream content, Parameters parameters)
        throws FaxManagerException;
}
