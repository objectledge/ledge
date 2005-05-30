// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.upload;

import java.util.Map;

import org.jcontainer.dna.Configuration;
import org.objectledge.context.Context;

/**
 * An application access point to the HTML form file upload functionality. For more information
 * see {@link org.objectledge.upload.FileUploadValve}.
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FileUpload.java,v 1.8 2005-05-30 09:10:13 pablo Exp $
 */
public class FileUpload
{
    // constants ////////////////////////////////////////////////////////////////////////////////
    
    /** context key to store the upload map. */
    public static final String UPLOAD_CONTEXT_KEY =
        "org.objectledge.upload.FileUpload.uploadMap";

    /** the default upload limit. */
    public static final int DEFAULT_UPLOAD_LIMIT = 4194304;

    // instance variables ///////////////////////////////////////////////////////////////////////
    
    /** the thread's processing context. */
    private Context context;

    /** the upload size limit */
    private int uploadLimit;

    // initialization ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a FileUpload component.
     *
     * @param config the configuration. 
     * @param context the context.
     */
    public FileUpload(Configuration config, Context context)
    {
        uploadLimit = config.getChild("upload_limit").getValueAsInteger(DEFAULT_UPLOAD_LIMIT);
        this.context = context;
    }
    
    // public API ///////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve the upload container, this method should be called in the first place by action
     * valves before retrieving any multipart POST parameters from the request. This call will
     * allow identification of file upload size limit exceeding problems.
     *
     * @param name the name of the item.
     * @return the upload container, or <code>null</code> if not available.
     * @throws UploadLimitExceededException thrown on upload limit exceeding
     */
    public UploadContainer getContainer(String name)
    throws UploadLimitExceededException
    {
        Object value = context.getAttribute(UPLOAD_CONTEXT_KEY);

        // no upload performed
        if(value == null)
        {
            return null;
        }
        else if(value instanceof UploadLimitExceededException)
        {
            // upload limit exceeded - message is the limit value
            throw (UploadLimitExceededException) value;
        }
        else if(value instanceof Map)
        {
            // upload successful - return a requested container
            // (it may also be null for not uploaded items)
            Map<String, UploadContainer> map = (Map<String, UploadContainer>) value;
            return map.get(name);
        }
        else
        {
            throw new RuntimeException("Probably a valve conflicting with FileUploadValve exists");
        }
    }
    
    /**
     * Get the upload size limit. 
     *
     * @return the upload limit. 
     */
    public int getUploadLimit()
    {
        return uploadLimit;
    }
}
