// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.web.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities shared by classs from this package.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Utils.java,v 1.1 2005-03-17 08:14:37 rafal Exp $
 */
class Utils
{
    /**
     * Non instantiable utility class.
     */
    private Utils()
    {
        // prevent instantiation & subclassing.
    }
    
    // -- actualView ----------------------------------------------------------------------------
    
    /** A pattern for picking out actualView value. */
    private static final Pattern ACTUAL_VIEW_PATTERN = 
        Pattern.compile("<!-- actualView:(\\S+) -->");  

    /** A pattern for picking out actionResult value. */
    private static final Pattern ACTION_RESULT_PATTERN = 
        Pattern.compile("<!-- actionResult:(\\S+) -->");
    
    /**
     * Returns the actualView, as reported in the response body.
     * 
     * @param responseBody the response body.
     * @return the actualView, as reported in the response body.
     */
    public static String getActualView(String responseBody) 
    {
        Matcher match = ACTUAL_VIEW_PATTERN.matcher(responseBody);
        if(match.find())
        {
            return match.group(1);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the actionResult, as reported in the response body.
     * 
     * @param responseBody the response body.
     * @return the actionResult, as reported in the response body.
     */
    public static String getActionResult(String responseBody) 
    {
        Matcher match = ACTION_RESULT_PATTERN.matcher(responseBody);
        if(match.find())
        {
            return match.group(1);
        }
        else
        {
            return null;
        }
    }
}
