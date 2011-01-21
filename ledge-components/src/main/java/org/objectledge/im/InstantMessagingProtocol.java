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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * Describes an instant messaging protocol.
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: InstantMessagingProtocol.java,v 1.1 2005-07-28 12:08:08 rafal Exp $
 */
public class InstantMessagingProtocol
{
    /** internal identifier. */
    private final String id;

    /** human readable name. */
    private final String name;

    /** pseudo schema without. */
    private final String schema;

    /**
     * url to brand icon of the protocol, will be treated as context root relative unless fully
     * qualified.
     */
    private final String iconUrl;

    /** url to the protocol info page. */
    private final String infoUrl;

    /** url to the status icons. use %s for screen name. */
    private final String statusUrlFormat;

    /** java.util.regex pattern for verification of screen names validity. */
    private final Pattern screenNamePattern;

    /**
     * Returns the iconUrl value.
     * 
     * @return the iconUrl.
     */
    public String getIconUrl()
    {
        return iconUrl;
    }

    /**
     * Creates a new InstantMessagingProtocol instance.
     * 
     * <p>See InstantMessaging.rng for configuration syntax details.</p>
     * 
     * @param config the configuration element.
     * @throws ConfigurationException if the configuration is incorrect.
     */
    public InstantMessagingProtocol(Configuration config)
        throws ConfigurationException
    {
        id = config.getChild("id").getValue();
        name = config.getChild("name").getValue();
        schema = config.getChild("schema").getValue(null);
        iconUrl = config.getChild("iconUrl").getValue(null);
        infoUrl = config.getChild("infoUrl").getValue();
        Configuration statusUrlFormatConfig = config.getChild("statusUrlFormat");  
        statusUrlFormat = statusUrlFormatConfig.getValue();
        if(!statusUrlFormat.contains("%s"))
        {
            throw new ConfigurationException("statusUrlFormat must contain %s sequence",
                statusUrlFormatConfig.getPath(), statusUrlFormatConfig.getLocation());
        }
        Configuration screenNamePatternConfig = config.getChild("screenNamePattern");
        try
        {
            screenNamePattern = Pattern.compile(screenNamePatternConfig.getValue());
        }
        catch(PatternSyntaxException e)
        {
            throw new ConfigurationException(e.getMessage(), screenNamePatternConfig.getPath(),
                screenNamePatternConfig.getLocation());
        }
    }

    /**
     * Returns the id value.
     * 
     * @return the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the infoUrl value.
     * 
     * @return the infoUrl.
     */
    public String getInfoUrl()
    {
        return infoUrl;
    }

    /**
     * Returns the name value.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the schema value.
     * 
     * @return the schema.
     */
    public String getSchema()
    {
        return schema;
    }

    /**
     * Returns the status icon url for the specified screen name.
     * 
     * @param screenName the screen name.
     * @return the status icon url.
     */
    public String getStatusUrl(String screenName)
    {
        return String.format(statusUrlFormat, screenName);
    }

    /**
     * Checks if the given screen name is in the correct format for this protocol.
     * 
     * @param screenName the screen name to check.
     * @return <code>true</code> if the screen name is in the correct format.
     */
    public boolean isValidScreenName(String screenName)
    {
        return screenNamePattern.matcher(screenName).matches();
    }
}
