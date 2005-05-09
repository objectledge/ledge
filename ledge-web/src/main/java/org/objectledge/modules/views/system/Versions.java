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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Versions.java,v 1.1 2005-05-09 08:32:14 rafal Exp $
 */
public class Versions
    extends PolicyProtectedBuilder
{
    private final FileSystem fileSystem;
    
    /**
     * Creates new Versions view instance.
     * 
     * @param fileSystemArg the FileSystem component.
     * @param contextArg the Context component.
     * @param policySystemArg the PolicySystem component.
     */
    public Versions(FileSystem fileSystemArg, Context contextArg, PolicySystem policySystemArg)
    {
        super(contextArg, policySystemArg);
        fileSystem = fileSystemArg;
    }
    
    /**
     * {@inheritDoc}
     */
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        
        /////////////////////////////////////////////////////////////////////////////////////////

        try
        {
            List temp = new ArrayList();
            String dir = "/META-INF/versions/";
            String[] groups = fileSystem.list(dir);
            if(groups != null)
            {
                Arrays.sort(groups);
                for(int i = 0; i < groups.length; i++)
                {
                    String[] artifacts = fileSystem.list(dir + groups[i]);
                    if(artifacts != null)
                    {
                        Arrays.sort(artifacts);
                        for(int j = 0; j < artifacts.length; j++)
                        {
                            temp.add(fileSystem.read(dir + groups[i] + "/" + artifacts[j], 
                                "UTF-8"));
                        }
                    }
                }
            }
            templatingContext.put("versions", temp);
        }
        catch(IOException e)
        {
            throw new ProcessingException("FS access failed", e);
        }
        
        /////////////////////////////////////////////////////////////////////////////////////////

        Package[] packages = Package.getPackages();
        Arrays.sort(packages, new Comparator()
            {
                public int compare(Object o1, Object o2)
                {
                    return ((Package)o1).getName().compareTo(((Package)o2).getName());
                }
            });
        List topLevelPackages = new ArrayList();
        outer: for(int i=0; i<packages.length; i++)
        {
            for(int j=i-1; j>=0; j--)
            {
                if(packages[i].getName().startsWith(packages[j].getName()))
                {
                    continue outer;
                }
            }
            topLevelPackages.add(packages[i]);
        }
        templatingContext.put("packages", topLevelPackages);

        /////////////////////////////////////////////////////////////////////////////////////////

        return super.build(template, embeddedBuildResults);
    }
}
