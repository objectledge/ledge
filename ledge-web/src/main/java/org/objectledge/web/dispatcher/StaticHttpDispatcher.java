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

package org.objectledge.web.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.web.HttpDispatcher;

/**
 * A HTTP dispatcher that returns hard coded responses for URLs matching a regular expression.
 * 
 * <p>
 * Status code, content type and response body text can be configured.
 * </p>
 * 
 * <ul>
 * <li><a href="http://objectledge.org/viewcvs.cgi/ledge-web/src/main/java/org/objectledge/web/dispatcher/StaticHttpDispatcher.rng?rev=HEAD&content-type=text/vnd.viewcvs-markup">
 * the configuration file schema</a></li>
 * <li><a href="returns hard coded responses for URLs matching a regular expression. Status code, content type and response body text can be configured.">
 * a configuration example</a></li> 
 * </ul>
 *
 * <p>Created on Dec 23, 2003</p>
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: StaticHttpDispatcher.java,v 1.3 2005-07-07 08:29:25 zwierzem Exp $
 */
public class StaticHttpDispatcher 
    implements HttpDispatcher
{
    private Pattern[] patterns;
    
    private int[] statuses;
    
    private String[] types;
    
    private String[] responses;
    
    /**
     * Creates a new dipspatcher.
     * 
     * @param config the configuration.
     * @throws ConfigurationException if the configuration is malformed.
     * @throws PatternSyntaxException if the configuration contains invalid regular expression.
     */
    public StaticHttpDispatcher(Configuration config)
        throws ConfigurationException, PatternSyntaxException
    {
        Configuration[] items = config.getChildren("response");
        patterns = new Pattern[items.length];
        statuses = new int[items.length];
        types = new String[items.length];
        responses = new String[items.length];
        for(int i=0; i<items.length; i++)
        {
            patterns[i] = Pattern.compile(items[i].getAttribute("pattern"));
            statuses[i] = items[i].getAttributeAsInteger("code", 200);
            types[i] = items[i].getAttribute("type", "text/plain");
            responses[i] = items[i].getValue();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        for(int i=0; i<patterns.length; i++)
        {
            if(patterns[i].matcher(request.getRequestURL()).matches())
            {
                response.setStatus(statuses[i]);
                response.setContentType(types[i]);
                PrintWriter pw = response.getWriter();
                pw.print(responses[i]);
                pw.close();
                return true;
            }
        }
        return false;
    }
}
