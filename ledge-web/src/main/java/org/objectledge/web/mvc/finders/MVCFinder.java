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

import org.jcontainer.dna.Logger;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.actions.DefaultAction;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.builders.DefaultBuilder;
import org.objectledge.web.mvc.builders.DefaultTemplate;
import org.picocontainer.MutablePicoContainer;

/**
 * Implementation of MVC finding services.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCFinder.java,v 1.10 2004-01-19 16:04:54 fil Exp $
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
    private DefaultTemplate defaultTemplate = new DefaultTemplate();
	private Map classCache = new HashMap();
	
    /** the logger. */
    private Logger logger;
    
    /** The Templating component. */
    private Templating templating;
	
    /** The name sequence factory. */
    private NameSequenceFactory nameSequenceFactory;
    
    /**
     * Creates a MVCFinder component.
     * 
     * @param container the container to store loaded classes.
     * @param logger the logger to use.
     * @param templating the templating component.
     * @param nameSequenceFactory the name sequence factory component.
     */
	public MVCFinder(MutablePicoContainer container, Logger logger, Templating templating, 
        NameSequenceFactory nameSequenceFactory)
	{
		this.container = container;
        this.logger = logger;
        this.templating = templating;
        this.nameSequenceFactory = nameSequenceFactory;
        
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
    public Template findBuilderTemplate(String view)
    {
        if(view != null || view.length() != 0)
        {
            Sequence sequence = nameSequenceFactory.
                getTemplateNameSequence(packageNameParts[BUILDERS], view, true);
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug("findBuilderTemplate: trying "+name);
                try
                {
                    return templating.getTemplate(name);
                }
                catch(TemplateNotFoundException e)
                {
                    // go on
                }
            }
        }
        return defaultTemplate;
    }

	/**
	 * {@inheritDoc}
	 */
    public Template findEnclosingBuilderTemplate(Template builderTemplate)
    {
        String view = findViewName(builderTemplate);
        Sequence sequence = nameSequenceFactory.
            getTemplateNameSequence(packageNameParts[BUILDERS], view, true);
        if(sequence.hasNext())
        {
            sequence.next();
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug("findEnclosingBuilderTemplate: trying "+name);
                try
                {
                    return templating.getTemplate(name);
                }
                catch(TemplateNotFoundException e)
                {
                    // go on
                }
            }
        }
        return defaultTemplate;
    }
    
    /**
     * {@inheritDoc}
     */
    public String findViewName(Template builderTemplate)
    {
        return nameSequenceFactory.getView(packageNameParts[BUILDERS], builderTemplate);
    }

	// actions //////////////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public Runnable findAction(String actionName)
        throws ClassNotFoundException
	{
        if(actionName != null && actionName.length() == 0)
        {
            Sequence sequence = nameSequenceFactory.
                getTemplateNameSequence(packageNameParts[BUILDERS], actionName, false);
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug("findAction: trying "+name);
                try
                {
                    return (Runnable)getClassInstance(actionName);
                }
                catch(ClassNotFoundException e)
                {
                    // go on
                }
            }
        }
        throw new IllegalArgumentException("action "+actionName+" is not available");
	}

    // builders /////////////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
    public Builder findBuilder(String view)
    {
		if(view != null && view.length() == 0)
		{
            Sequence sequence = nameSequenceFactory.
                getTemplateNameSequence(packageNameParts[BUILDERS], view, true);
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug("findBuilder: trying "+name);
                try
                {
                    return (Builder)getClassInstance(name);
                }
                catch(ClassNotFoundException e)
                {
                    // go on
                }
            }
        }
    	return defaultBuilder;
    }

	/**
	 * {@inheritDoc}
	 */
    public Builder findEnclosingBuilder(Builder builder)
    {
        String view = findViewName(builder);
        Sequence sequence = nameSequenceFactory.
            getTemplateNameSequence(packageNameParts[BUILDERS], view, true);
        if(sequence.hasNext())
        {
            sequence.next();
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug("findEnclosingBuilder: trying "+name);
                try
                {
                    return (Builder)getClassInstance(name);
                }
                catch(ClassNotFoundException e)
                {
                    // go on
                }
            }
        }
        return defaultBuilder;
    }

	/**
	 * {@inheritDoc}
	 */
    public String findViewName(Builder builder)
    {
        return nameSequenceFactory.getView(packageNameParts[BUILDERS], builder.getClass());
    }

    // implementation ///////////////////////////////////////////////////////////////////////////

	/**
	 * Gets an instance of an object depending on it's finder name and type
	 */
	private Object getClassInstance(String className)
        throws ClassNotFoundException
	{
        Class clazz = Class.forName(className);
        if(!container.hasComponent(clazz))
        {
            container.registerComponentImplementation(clazz);
        }
        return container.getComponentInstance(clazz);
	}    
}
