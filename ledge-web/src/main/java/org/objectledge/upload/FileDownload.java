// 
// Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.web.HttpContext;

/**
 * An utility for the file download functionality.
 *
 * @author <a href="dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FileDownload.java,v 1.3 2004-12-22 08:58:44 rafal Exp $
 */
public class FileDownload
{
    /** Octet steram MIME type. */
    public static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";
    
    // instance variables ///////////////////////////////////////////////////////////////////////
    
    /** the thread's processing context. */
    private Context context;

    // initialization ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a FileDownload component.
     *
     * @param context the context.
     */
    public FileDownload(Context context)
    {
        this.context = context;
    }
    
    // public API ///////////////////////////////////////////////////////////////////////////////

    /**
     * Dumps the <code>InputStream</code> contents as a direct response.
     * 
     * @param is the input stream of data to be downloaded
     * @param contentType the content-type of downloaded data
     * @param lastModified the last modification date of dowloaded data.
     * @param bytesSize number of bytes that will be sent (size of data from input stream) the file
     *  cannot be larger than 4GB
     * @throws IOException thrown on errors while downloading
     */
    public void dumpData(InputStream is, String contentType, long lastModified, int bytesSize)
        throws IOException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        // TODO: Decide whether to put default content type into HttpContext
        if(contentType == null || contentType.length() == 0)
        {
            contentType = OCTET_STREAM_CONTENT_TYPE;
        }
        httpContext.setContentType(contentType);
        if(bytesSize > 0)
        {
            httpContext.getResponse().addIntHeader("Content-Length", bytesSize);
        }
        httpContext.getResponse().addDateHeader("Last-Modified", lastModified);
        OutputStream os = httpContext.getOutputStream();
        byte[] buffer = new byte[is.available() > 0 ? is.available() : 4096];
        int count = 0;
        while(count >= 0)
        {
            count = is.read(buffer,0,buffer.length);
            if(count > 0)
            {
                os.write(buffer, 0, count);
            }
        }
        is.close();
        os.flush();
    }

    /**
     * Dumps the <code>InputStream</code> contents as a direct response with unknown size.
     * 
     * @param is the input stream of data to be downloaded
     * @param contentType the content-type of downloaded data
     * @param lastModified the last modification date of dowloaded data.
     * @throws IOException thrown on errors while downloading
     */
    public void dumpData(InputStream is, String contentType, long lastModified)
        throws IOException
    {
        dumpData(is, contentType, lastModified, -1);
    }

    /**
     * Dumps the <code>InputStream</code> contents as a direct response with current time as last
     * modification time and unknown size.
     * 
     * @param is the input stream of data to be downloaded
     * @param contentType the content-type of downloaded data
     * @throws IOException thrown on errors while downloading
     */
    public void dumpData(InputStream is, String contentType)
        throws IOException
    {
        Date date = new Date();
        dumpData(is, contentType, date.getTime());
    }
}
