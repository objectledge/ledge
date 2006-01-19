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
package org.objectledge.hibernate;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;

/**
 * The hibernate session factory component which modifies HSQL connection.url 
 * string to place the database in the webapp directory.
 * 
 * <p>
 * The property of the SQL connection.url has to have following form:
 * </p>
 * <pre>
 *     &lt;property name="connection.url">jdbc:hsqldb:{LEDGE_FS_ROOT}databaseDir&lt;/property>
 * </pre>
 * <p>
 * The string <code>{LEDGE_FS_ROOT}</code> will be replaced with current webapp root.
 * </p>
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HSQLSimpleConfigHibernateSessionFactory.java,v 1.1 2006-01-19 16:06:43 zwierzem Exp $
 */
public class HSQLSimpleConfigHibernateSessionFactory 
extends SimpleConfigHibernateSessionFactory
{
    public HSQLSimpleConfigHibernateSessionFactory(Configuration config, Logger logger,
        final FileSystem fs, final String locaFileSystemProviderName,
        InterceptorFactory interceptorFactory)
        throws ConfigurationException
    {
        super(config, new ConfigurationModifier() 
        {
            public String modify(String name, String value)
            {
                    if(name.equals("connection.url"))
                    {
                        LocalFileSystemProvider provider = (LocalFileSystemProvider)fs
                            .getProvider(locaFileSystemProviderName);
                        return value.replaceFirst("\\{LEDGE_FS_ROOT\\}",
                            provider.getFile("/").getAbsolutePath());
                    }
                return value;
            }
        },
        logger, fs, interceptorFactory);
    }
    
    /**
     * Creates the session factory with default <code>LocalFileSystemProvider</code> name ("local").
     * 
     * @param config
     * @param logger
     * @param fs
     * @param interceptorFactory
     * @throws ConfigurationException
     */
    public HSQLSimpleConfigHibernateSessionFactory(Configuration config, Logger logger,
        FileSystem fs, InterceptorFactory interceptorFactory) throws ConfigurationException
    {
        this(config, logger, fs, "local", interceptorFactory);
    }
    
}
