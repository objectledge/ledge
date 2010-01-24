// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.modules.views.system;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Versions.java,v 1.4 2005-07-26 12:13:29 rafal Exp $
 */
public class Versions
    extends PolicyProtectedBuilder
{
    /**
     * Creates new Versions view instance.
     * 
     * @param fileSystemArg the FileSystem component.
     * @param contextArg the Context component.
     * @param policySystemArg the PolicySystem component.
     */
    public Versions(Context contextArg, PolicySystem policySystemArg)
    {
        super(contextArg, policySystemArg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        try
        {
            List<Map<String, String>> mavenArtifacts = new ArrayList<Map<String, String>>();
            ClassLoader cl = getClass().getClassLoader();
            while(cl != null)
            {
                if(cl instanceof URLClassLoader)
                {
                    jarLoop: for(URL url : ((URLClassLoader)cl).getURLs())
                    {
                        if(url.getProtocol().equals("file") && url.getPath().endsWith(".jar"))
                        {
                            JarFile jar = new JarFile(url.getPath());
                            Attributes jarManifest = null;
                            if(jar.getManifest() != null)
                            {
                                jar.getManifest().getMainAttributes();
                            }
                            if(jarManifest != null
                                && "Java Runtime Environment".equals(jarManifest
                                    .getValue("Implementation-Title")))
                            {
                                continue jarLoop;
                            }
                            Enumeration<JarEntry> entries = jar.entries();
                            while(entries.hasMoreElements())
                            {
                                JarEntry entry = entries.nextElement();
                                if(entry.getName().matches("META-INF/maven/.*/pom.properties"))
                                {
                                    Properties pomProperties = new Properties();
                                    pomProperties.load(jar.getInputStream(entry));
                                    Map<String, String> properties = new HashMap<String, String>();
                                    for(Object key : pomProperties.keySet())
                                    {
                                        properties.put((String)key, pomProperties
                                            .getProperty((String)key));
                                    }
                                    if(jarManifest != null)
                                    {
                                        for(Object key : jarManifest.keySet())
                                        {
                                            properties.put((String)key, (String)jarManifest
                                                .getValue((String)key));
                                        }
                                    }
                                    mavenArtifacts.add(properties);
                                    continue jarLoop;
                                }
                            }
                        }
                    }
                }
                cl = cl.getParent();
            }
            Collections.sort(mavenArtifacts, new Comparator<Map<String, String>>()
                {

                    @Override
                    public int compare(Map<String, String> m1, Map<String, String> m2)
                    {
                        int i = m1.get("groupId").compareTo(m2.get("groupId"));
                        if(i == 0)
                        {
                            i = m1.get("artifactId").compareTo(m2.get("artifactId"));
                        }
                        return i;
                    }
                });
            templatingContext.put("mavenArtifacts", mavenArtifacts);
        }
        catch(Exception e)
        {
            throw new ProcessingException("maven artifacts discovery failed", e);
        }

        // ///////////////////////////////////////////////////////////////////////////////////////

        Package[] packages = Package.getPackages();
        Arrays.sort(packages, new Comparator<Package>()
            {
                public int compare(Package p1, Package p2)
                {
                    return p1.getName().compareTo(p2.getName());
                }
            });
        List<Package> topLevelPackages = new ArrayList<Package>();
        outer: for(int i = 0; i < packages.length; i++)
        {
            for(int j = i - 1; j >= 0; j--)
            {
                if(packages[i].getName().startsWith(packages[j].getName()))
                {
                    continue outer;
                }
            }
            topLevelPackages.add(packages[i]);
        }
        templatingContext.put("packages", topLevelPackages);
    }

}
