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

package org.objectledge.naming;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.jcontainer.dna.Logger;

/**
 * Helps you in looking up objects in a context using their fully qualified
 * distinguished names.
 *
 * <p>It is a very common situation that your directory provider URL contains
 * a non empty base name. Because of that, whenever you need to perform a
 * directory lookup using a fully qualified DN, you need to transform the DN
 * to be relative to the directory providers base DN. This class simplifies
 * this task.</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ContextHelper.java,v 1.2 2006-06-01 06:37:49 rafal Exp $
 */
public class ContextHelper
{
    // instance variables ////////////////////////////////////////////////////

    private final ContextFactory contextFactory;
    
    private final String contextAlias;
    
    /** The wrapped context. */
    private Context context;

    /** Fully qualified name of the wrapped context. */
    private final Name baseName;

    /** The name parser. */
    private final NameParser parser;
    
    /** The logger. */
    private final Logger logger;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a lookup helper.
     *
     * @param context the context to construct helper for.
     * @throws NamingException if failed.
     */
    public ContextHelper(ContextFactory contextFactory, String contextAlias, Logger logger)
        throws NamingException
    {
        this.contextFactory = contextFactory;
        this.contextAlias = contextAlias;
        this.context = contextFactory.getContext(contextAlias);
        this.parser = context.getNameParser("");
        this.baseName = parser.parse(context.getNameInNamespace());
        this.logger = logger;
    }
    
    // public interface /////////////////////////////////////////////////////
    
    /**
     * Checks if a name is within wrapped context's namespace.
     *
     * @param name the fully qualified name.
     * @return <code>true</code> if the name is within wrapped context's
     *         namespace.
     * @throws NamingException if failed. 
     */
    public boolean isInNamespace(String name)
        throws NamingException
    {
        return isInNamespace(parser.parse(name));
    }

    /**
     * Checks if a name is within wrapped context's namespace.
     *
     * @param name the fully qualified name.
     * @return <code>true</code> if the name is within wrapped context's
     *         namespace.
     * @throws NamingException if failed. 
     */
    public boolean isInNamespace(Name name)
        throws NamingException
    {
        return name.startsWith(baseName);
    }

    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name.
     * @return an Object.
     * @throws NamingException if failed.
     */
    public Object lookup(Name name)
        throws NamingException
    {
        Name relativeName;
        if(name.isEmpty())
        {
            relativeName = name;
        }
        else
        {
            if(!name.startsWith(baseName))
            {
                throw new InvalidNameException(name+" is not in "+baseName+" namespace");
            }
            relativeName = name.getSuffix(baseName.size());
        }
        try
        {
            return context.lookup(relativeName);
        }
        catch(Exception e)
        {
            logger.error("context lookup failed, will attempt to reconnect", e);
            reconnect();
            logger.info("reconnect successful");
            return context.lookup(relativeName);
        }
    }
    
    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name. 
     * @return an Object.
     * @throws NamingException if failed.
     */
    public Object lookup(String name)
        throws NamingException
    {
        return lookup(parser.parse(name));
    }
    
    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name.
     * @return a Context.
     * @throws NamingException if failed.
     */
    public Context lookupContext(String name)
        throws NamingException
    {
        return (Context)lookup(name);
    }
    
    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name.
     * @return a Context.
     * @throws NamingException if failed.
     */
    public Context lookupContext(Name name)
        throws NamingException
    {
        return (Context)lookup(name);
    }

    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name.
     * @return a DirContext.
     * @throws NamingException if failed.
     */
    public DirContext lookupDirContext(String name)
        throws NamingException
    {
        return (DirContext)lookup(name);
    }
    
    /**
     * Performs a lookup using a fully qualified DN.
     *
     * @param name the fully qualified name.
     * @return a DirContext.
     * @throws NamingException if failed.
     */
    public DirContext lookupDirContext(Name name)
        throws NamingException
    {
        return (DirContext)lookup(name);
    }

    /**
     * Returns get base context.
     *
     * @return the base context.
     */
    public Context getBaseContext() throws NamingException
    {
        return (Context)lookup(parser.parse(""));
    }

    /**
     * Returns the base directory context.
     *
     * @return the base directory context.
     */
    public DirContext getBaseDirContext() throws NamingException
    {
        return (DirContext)lookup(parser.parse(""));
    }

    /**
     * Get the base name.
     * 
     * @return the base name.
     */    
    public Name getBaseName()
    {
        return baseName;
    }
    
    /**
     * Converts the name to be relative to the lookupCtx.
     *
     * @param name the name to convert.
     * @return the relative name.
     * @throws NamingException if name is not in lookupCtx's namespace.
     */
    public Name getRelativeName(String name)
        throws NamingException
    {
        Name parsedName = parser.parse(name);
        if(!parsedName.startsWith(baseName))
        {
            throw new InvalidNameException(parsedName+" is not in "+baseName+" namespace");
        }
        return parsedName.getSuffix(baseName.size());
    }    
    
    private void reconnect() throws NamingException
    {
        contextFactory.reconnect(contextAlias);
        context = contextFactory.getContext(contextAlias);
    }
}
