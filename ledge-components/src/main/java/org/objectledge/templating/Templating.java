//
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
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

package org.objectledge.templating;

import java.io.Reader;
import java.io.Writer;

import org.objectledge.ComponentInitializationError;

/**
 * Templating component.
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: Templating.java,v 1.8 2005-01-28 02:42:24 pablo Exp $
 */
///CLOVER:OFF
public interface Templating
{
    /**
     * Restart the templating service.
     * 
     * @throws ComponentInitializationError if anything goes wrong.
     * @throws VirtualMachineError if anything goes wrong.
     * @throws ThreadDeath if anything goes wrong.
     */
    public void restart() 
        throws ComponentInitializationError, VirtualMachineError, ThreadDeath;
    
	/**
	 * Create an empty {@link TemplatingContext} object.
	 *
	 * @return a Context.
	 */
	public TemplatingContext createContext();

	/**
	 * Checks if the template with a given name exists.
	 *
	 * @param name template name.
	 * @return <code>true</code> if exists.
	 */
	public boolean templateExists(String name);

	/**
	 * Get the specified template.
	 *
	 * @param name template name.
	 * @return the template.
	 * @throws TemplateNotFoundException if does not exist.
	 */
	public Template getTemplate(String name)
		throws TemplateNotFoundException;

	/**
	 * Merge a template dynamically.
	 *
	 * @param context the templating context.
	 * @param source the reader with template source.
	 * @param target the writer to write the result.
	 * @param name the template name for logging purposes.
	 * @throws MergingException if something goes wrong.
	 */
	public void merge(TemplatingContext context, Reader source, 
						  Writer target, String name) 
		throws MergingException;
		
	/**
	 * Merge a template.
	 *
	 * @param context the templating context.
	 * @param template the template.
	 * @param target the writer to write the result.
	 * @throws MergingException if something goes wrong.
	 */
	public void merge(TemplatingContext context, Template template, Writer target) 
		throws MergingException;	
		
	/**
	 * Return the character encoding of the template files.
	 *  
	 * @return character encoding name.
	 */
	public String getTemplateEncoding();
    
    /**
     * Invalidate template in local cache.
     * 
     * @param name the template name.
     */
    public void invalidateTemplate(String name);
}
