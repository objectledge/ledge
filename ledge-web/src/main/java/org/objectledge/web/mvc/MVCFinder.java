// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc;

import org.objectledge.templating.Template;
import org.objectledge.web.mvc.actions.DefaultAction;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.picocontainer.MutablePicoContainer;

/**
 * Implementation of MVC finding services.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCFinder.java,v 1.1 2003-12-30 17:26:25 zwierzem Exp $
 */
public class MVCFinder implements TemplateFinder, MVCClassFinder
{
	/** Internal constant for choosing "builders" package name part. */
	private static final int BUILDERS = 0;
	/** Internal constant for choosing "actions" package name part. */
	private static final int ACTIONS = 1;
	/** Internal constant for choosing "components" package name part. */
	private static final int COMPONENTS = 2;
	
	private String[] packageNameParts = { "builders", "actions", "components" };
	
	private MutablePicoContainer container;
	private Runnable defaultAction;
	private DefaultBuilder defaultBuilder;
	
	/** Prefixes for builder classes. */
	private String[] classPrefixes;
	
	public MVCFinder()
	{
		// TODO  get container from somewhere
		container.registerComponentImplementation(DefaultBuilder.class);
		container.registerComponentImplementation(DefaultAction.class);
		//container.registerComponentImplementation(DefaultComponent.class);
		
		defaultBuilder = (DefaultBuilder) container.getComponentInstance(DefaultBuilder.class);
		defaultAction = (DefaultAction) container.getComponentInstance(DefaultAction.class);
	}

	/**
	 * {@inheritDoc}
	 */
    public Template findBuilderTemplate(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    public Template findEnclosingBuilderTemplate(Template builder)
    {
        // TODO Auto-generated method stub
        return null;
    }

	// MVCClassFinder -----------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public Runnable findAction(String actionName)
	{
		if(actionName == null || actionName.length() == 0)
		{
			return defaultAction;
		}
		return (Runnable) getInstance(ACTIONS, actionName);
	}

	/**
	 * {@inheritDoc}
	 */
    public Builder findBuilder(String builderName)
    {
		if(builderName == null || builderName.length() == 0)
		{
			return defaultBuilder;
		}
    	// TODO Add builder name defaulting
    	Builder builder = (Builder) getInstance(BUILDERS, builderName);
    	return builder;
    }

	/**
	 * {@inheritDoc}
	 */
    public Builder findEnclosingBuilder(Builder builder)
    {
        // TODO Auto-generated method stub
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    public String findName(Class clazz)
    {
        // TODO Auto-generated method stub
        return null;
    }

    // implementation -----------------------------------------------------------------------------

	/**
	 * Gets an instance of an object depending on it's finder name and type
	 */
	private Object getInstance(int componentType, String className)
	{
		StringBuffer buf = new StringBuffer(100);
		buf.append(packageNameParts[componentType]);
		if(className.charAt(0) != '.')
		{
			buf.append('.');
		}
		String classNamePart = buf.append(className).toString();
		
		if(!container.hasComponent(classNamePart))
		{
			for (int i = 0; i < classPrefixes.length; i++)
			{
				buf.setLength(0);
				try
				{
					buf.append(classPrefixes[i]).append(classNamePart);
					Class componentClass = Class.forName(buf.toString());
					container.registerComponentImplementation(classNamePart, componentClass);
				}
				catch (ClassNotFoundException e)
				{
					// ignore and get another one
				}
			}
		}
		// will throw exception on non existing component
		return container.getComponentInstance(classNamePart);
	}    
}
