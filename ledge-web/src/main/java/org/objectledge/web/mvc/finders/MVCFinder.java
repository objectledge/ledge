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
package org.objectledge.web.mvc.finders;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.actions.DefaultAction;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.picocontainer.MutablePicoContainer;

/**
 * Implementation of MVC finding services.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCFinder.java,v 1.7 2004-01-19 14:21:55 zwierzem Exp $
 */
public class MVCFinder implements MVCTemplateFinder, MVCClassFinder
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
	private Map classCache = new HashMap();
	
	/** Prefixes for builder classes. */
	private String[] classPrefixes;
    
    /** Prefixes for templates */
    private String[] templatePrefixes;
    
    /** The Templating component. */
    private Templating templating;
	
    /** The name sequence factory. */
    private NameSequenceFactory nameSequenceFactory;
    
    /**
     * Creates a MVCFinder component.
     * 
     * @param container the container to store loaded classes.
     * @param templating the templating component.
     * @param nameSequenceFactory the name sequence factory component.
     */
	public MVCFinder(MutablePicoContainer container, Templating templating, 
        NameSequenceFactory nameSequenceFactory)
	{
		this.container = container;
        this.templating = templating;
        
		container.registerComponentImplementation(DefaultBuilder.class);
		container.registerComponentImplementation(DefaultAction.class);
		// TODO container.registerComponentImplementation(DefaultComponent.class);
		
		defaultBuilder = (DefaultBuilder) container.getComponentInstance(DefaultBuilder.class);
		defaultAction = (DefaultAction) container.getComponentInstance(DefaultAction.class);
	}

    // builder templates ////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public Template getTemplate(String templateName)
	{
		// TODO Auto-generated method stub
		return null;
	}

  	/**
	 * {@inheritDoc}
	 */
    public Template findBuilderTemplate(String name)
    {
        
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    public Template findEnclosingBuilderTemplate(Template builderTemplate)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String findViewName(Template builderTemplate)
    {
        // TODO Auto-generated method stub
        return null;
    }

	// actions //////////////////////////////////////////////////////////////////////////////////

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

    // builders /////////////////////////////////////////////////////////////////////////////////

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
    public String findViewName(Builder builder)
    {
        // TODO Auto-generated method stub
        return null;
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

	/**
	 * Gets an instance of an object depending on it's finder name and type
	 */
	private Object getInstance(int componentType, String className)
	{
		Class clazz = getClass(componentType, className);
		if(!container.hasComponent(clazz))
		{
			container.registerComponentImplementation(clazz);
		}
		return container.getComponentInstance(clazz);
	}    

	/**
	 * Gets an class depending on it's finder name and type, uses defaulting
	 */
	private Class findClass(int componentType, String name)
	{
		// TODO name breaking and finding 
		return null;
	}    

	/**
	 * Gets an class depending on it's finder name and type
	 */
	private Class getClass(int componentType, String name)
	{
		StringBuffer buf = new StringBuffer(100);
		buf.append(packageNameParts[componentType]);
		if(name.charAt(0) != '.')
		{
			buf.append('.');
		}
		String classNamePart = buf.append(name).toString();
		
		if(!classCache.containsKey(classNamePart))
		{
			for (int i = 0; i < classPrefixes.length; i++)
			{
				buf.setLength(0);
				try
				{
					buf.append(classPrefixes[i]).append(classNamePart);
					Class componentClass = Class.forName(buf.toString());
					classCache.put(classNamePart, componentClass);
					break;
				}
				catch (ClassNotFoundException e)
				{
					// ignore and get another one
				}
			}
		}
		// will return null on non existing class
		return (Class) classCache.get(classNamePart);
	}
}
