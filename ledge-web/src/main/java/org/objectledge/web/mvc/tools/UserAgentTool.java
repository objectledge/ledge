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

package org.objectledge.web.mvc.tools;

import javax.servlet.http.HttpServletRequest;

import org.jcontainer.dna.Logger;
import org.objectledge.web.HttpContext;

/**
 * A context tool for retrieving user agent info.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UserAgentTool.java,v 1.3 2004-02-02 16:56:36 zwierzem Exp $
 */
public class UserAgentTool
{
    private Logger logger;
    
    /** http request UA header */
    private String ua;
    /** UA platform */
    private String platform = "";
    /** UA family */
    private String family = "";
    /** UA org */
    private String org = "";
    /** UA version */
    private String version = "";
    /** UA major version */
    private int majorVersion;
    /** UA minor version */
    private int minorVersion;
    /** UA micro version */
    private int microVersion;
    /** UA extra version */
    private String extraVersion = null;
    
    // public interface ///////////////////////////////////////////////////////

	/**
	 * Constructs a UserAgentTool for a given http context.
	 * 
	 * @param httpContext context for a current request
	 * @param logger a logger used for error reporting
	 */
	public UserAgentTool(HttpContext httpContext, Logger logger)
	{
		this.logger = logger;
		
        prepareUAInfo(httpContext);
    }
    
    /**
     * Return the http UA header.
     *
     * @return the UA header.
     */
    public String getBrowserName()
    {
        return ua;
    }

    /**
     * Return the UA Family
     *
     * @return the UA family.
     */
    public String getFamily()
    {
        return family;
    }
    
    /**
     * Return the http UA org.
     *
     * @return the UA org.
     */
    public String getOrg()
    {
        return org;
    }

    /**
     * Return the http UA version.
     *
     * @return the UA version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Return the http UA major version.
     *
     * @return the UA major version.
     */
    public int getMajorVersion()
    {
        prepareUAVersionInfo(version);
        return majorVersion;
    }

    /**
     * Return the http UA minor version.
     *
     * @return the UA minor version.
     */
    public int getMinorVersion()
    {
        prepareUAVersionInfo(version);
        return minorVersion;
    }

    /**
     * Return the http UA micro version.
     *
     * @return the UA micro version.
     */
    public int getMicroVersion()
    {
        prepareUAVersionInfo(version);
        return microVersion;
    }

    /**
     * Return the http UA extra version.
     *
     * @return the UA extra version.
     */
    public String getExtraVersion()
    {
        prepareUAVersionInfo(version);
        return extraVersion;
    }

    /**
     * Return the http UA platform.
     *
     * @return the UA platform.
     */
    public String getPlatform()
    {
        return platform;
    }


    /** 
     * Return the UA platform name.
     *
     * @param header the UA http request header.
     * @return the UA platform name.
     */
    private String getPlatform(String header) 
    {
        if (header.indexOf("win95") > -1)
        	{return "Windows 95";} 
        else if (header.indexOf("win16") > -1)
        	{return "Windows 3.1";}
        else if (header.indexOf("win98") > -1)
            {return "Windows 98";} 
        else if (header.indexOf("winnt") > -1)
            {return "Windows NT 4.0";}
        else if (header.indexOf("win2000") > -1)
            {return "Windows 2000";}
        else if (header.indexOf("winme") > -1)
            {return "Windows Me";}
        else if (header.indexOf("macppc") > -1)
            {return "MacPPC";} 
        else if (header.indexOf("68k") > -1)
            {return "Mac68K";}
        else if (header.indexOf("mac") > -1)
            {return "Some Mac";}
        else if (header.indexOf("sunos") > -1)
            {return "UNIX";}
        else if (header.indexOf("irix64") > -1)
            {return "UNIX";} 
        else if (header.indexOf("unix") > -1)
            {return "UNIX";}
        else if (header.indexOf("linux") > -1)
            {return "UNIX";} 
        else
            {return "UNKNOWN";}
    }

    /**
     * Prepare the UA Information.
     *
     * @param data the run data.
     */
    private void prepareUAInfo(HttpContext httpContext)
    {
        HttpServletRequest request = httpContext.getRequest();
        ua = request.getHeader("User-Agent");
        ua = ua.toLowerCase();
        ua.trim();
        platform = getPlatform(ua);
        int i = 0;
        if (ua.indexOf("opera") != -1)
        {
            i = ua.indexOf("opera");
            family  = "opera";
            org    = "opera";
            version  = ua.substring(i+6);
        }
        else if ((i = ua.indexOf("msie")) != -1)
        {
            org    = "microsoft";
            int end = ua.indexOf(";",i+5);
            version  = ua.substring(i+5, end);
            family = "ie";
        }
        else if (ua.indexOf("gecko") != -1)
        {
            family = "gecko";
            int rvStart = ua.indexOf("rv:") + 3;
            int rvEnd = ua.indexOf(")", rvStart);
            String rv = ua.substring(rvStart, rvEnd);
            version = rv;
            if (ua.indexOf("netscape") != -1)
                {org = "netscape";} 
            else if (ua.indexOf("compuserve") != -1)
                {org = "compuserve";}
            else
                {org = "mozilla";}
        }
        else if ((ua.indexOf("mozilla") !=-1) && (ua.indexOf("spoofer")==-1)
                 && (ua.indexOf("compatible") == -1) && (ua.indexOf("opera")==-1)&&
                 (ua.indexOf("webtv")==-1) && (ua.indexOf("hotjava")==-1))
        {
            i = ua.lastIndexOf("/");
            int end = ua.indexOf(".",i);
            version = ua.substring(i+1, end+3);
            org = "netscape";
            family = "nn";
        }
        else if((ua.indexOf("mozilla") !=-1) && (ua.indexOf("konqueror")!=-1))
        {
            family  = "konqueror";
            org    = "konqueror";
            version  = "UNKNOWN";
        }
        else if ((i = ua.indexOf("aol")) != -1 )
        {
            // aol
            family  = "aol";
            org    = "aol";
            version  = ua.substring(i+4);
        }
        else if ((i = ua.indexOf("hotjava")) != -1 )
        {
            // hotjava
            family  = "hotjava";
            org    = "sun";
            version = "UNKNOWN";
            //version  = parseFloat(navigator.appVersion);
        }
        else
        {
            family = "UNKNOWN";
            org = "UNKNOWN";
            version = "UNKNOWN";
        }
        majorVersion = -1;
        minorVersion = -1;
        microVersion = -1;
        extraVersion = null;
    }
    
    /*
     * Prepare the version info
     *
     * @param data the run data.
     */
    private void prepareUAVersionInfo(String version)
    {
        if(extraVersion == null)
        {
            try
            {
                int i = 0;
                while(i < version.length())
                {
                    StringBuffer sb = new StringBuffer();
                    while(i < version.length())
                    {
                        if(Character.isDigit(version.charAt(i)))
                        {
                            sb.append(version.charAt(i));
                            i++;
                        }
                        else
                        {
                            break;
                        }
                    }
                    if(sb.length()>0)
                    {
                        if(majorVersion == -1)
                        {
                            majorVersion = Integer.parseInt(sb.toString());
                        }
                        else
                        {
                            if(minorVersion == -1)
                            {
                                minorVersion = Integer.parseInt(sb.toString());
                            }
                            else
                            {
                                if(microVersion == -1)
                                {
                                    microVersion = Integer.parseInt(sb.toString());
                                }
                                else
                                {
                                    extraVersion = sb.toString();
                                    if(i < version.length())
                                    {
                                        extraVersion = extraVersion
                                        	+ version.substring(i, version.length());
                                    }
                                    break;
                                }
                            }
                        }
                        sb.setLength(0);
                    }
                    if(i < version.length())
                    {
                        if(version.charAt(i) == '.')
                        {
                            i++;
                        }
                        else
                        {
                            extraVersion = version.substring(i,version.length());
                            break;
                        }
                    }
                }
            }
            catch(Exception e)
            {
                logger.error("Couldn't parse detailed Version info", e);
            }
            if(extraVersion == null)
            {
                extraVersion = "";
            }
        }
    }
}
