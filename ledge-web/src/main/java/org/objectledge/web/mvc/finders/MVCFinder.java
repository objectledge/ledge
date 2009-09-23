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

import org.jcontainer.dna.Logger;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.web.mvc.builders.Builder;
import org.objectledge.web.mvc.components.Component;
import org.picocontainer.MutablePicoContainer;

/**
 * Implementation of MVC finding services.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MVCFinder.java,v 1.36 2005-07-29 11:57:18 rafal Exp $
 */
public class MVCFinder implements MVCTemplateFinder, MVCClassFinder
{
    /**
     * Different kinds of UI elements handled by the finder.
     */
    protected enum Kind
    {
        /** View element. */
        VIEW("views"),
        /** Action element. */
        ACTION("actions"),
        /** Component element. */
        COMPONENT("components");
        
        private Kind(String infix)
        {
            this.infix = infix;
        }
        
        private final String infix;
        
        /**
         * Returns the path infix used by name sequences.
         * 
         * @return the path infix used by name sequences.
         */
        public String getInfix()
        {
            return infix;
        }
    }
    
	/** Internal constant for choosing "views" package name part. */
	private static final String VIEWS = "views";
	/** Internal constant for choosing "actions" package name part. */
	private static final String ACTIONS = "actions";
	/** Internal constant for choosing "components" package name part. */
	private static final String COMPONENTS = "components";
	
	private MutablePicoContainer container;
	
    /** the logger. */
    private Logger logger;
    
    /** The Templating component. */
    private Templating templating;
	
    /** The name sequence factory. */
    private NameSequenceFactory nameSequenceFactory;
    
    /** The class instances cache */
    private HashMap<String, Object> classInstanceCache = new HashMap<String, Object>();

    /** Special marker object for the classInstanceCache */
    private static final Object MISSING = new Object();    
    
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
	}

	// templates ////////////////////////////////////////////////////////////////////////////////

    // builder templates ////////////////////////////////////////////////////////////////////////

  	/**
	 * {@inheritDoc}
	 */
    public MVCTemplateFinder.Result findBuilderTemplate(String view)
    {
		return findBuilderTemplate(view, "findBuilderTemplate");
    }

	// component templates ////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public Template getComponentTemplate(String name)
	{
        return findTemplate(Kind.COMPONENT, name, false, "getComponentTemplate");
	}

	// actions //////////////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public Valve getAction(String actionName)
	{
		return (Valve)findObject(Kind.ACTION, actionName, false, "getAction");
	}

    // builders /////////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public MVCClassFinder.Result findBuilder(String view)
    {
        return findBuilder(view, "findBuilder");
    }

	/**
	 * {@inheritDoc}
	 */
    public String findEnclosingViewName(String viewName)
    {
        if(viewName != null && viewName.length() != 0)
        {
            Sequence classSequence = nameSequenceFactory.
                getClassNameSequence(VIEWS, viewName, true, true);

            Sequence templateSequence = nameSequenceFactory.
                getTemplateNameSequence(VIEWS, viewName, true, true);

            while(classSequence.hasNext() && templateSequence.hasNext())
            {
                String className = classSequence.next();
                String templateName = templateSequence.next();
                logger.debug("findEnclosingViewName: trying "+className+" and "+templateName);

                if(getClassInstance(className) != null)
                {
                    break;
                }

                if(templating.templateExists(templateName))
                {
                    break;
                }
            }
            
            String classSequenceViewName = classSequence.currentView();
            String templateSequenceViewName = templateSequence.currentView();
            if((classSequenceViewName == templateSequenceViewName) // both nulls
               || classSequenceViewName.equals(templateSequenceViewName))
            {
                return classSequenceViewName;
            }
            else
            {
                throw new IllegalStateException("Sequences produced different view names: "+
                    classSequenceViewName+" (class sequence) "+
                    templateSequenceViewName+" (template sequence)");
            }
        }
        return null;
    }

	// components ///////////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	public Component getComponent(String componentName)
	{
		return (Component)
            findObject(Kind.COMPONENT, componentName, false, "getComponent");
	}    

    // implementation ///////////////////////////////////////////////////////////////////////////

	private Template findTemplate(Kind kind, String templateName, boolean fallback,
        String methodName)
	{
		if(templateName != null && templateName.length() != 0)
		{
			Sequence sequence = getTemplateNameSequence(kind, templateName, fallback, false);
			while(sequence.hasNext())
			{
				String name = sequence.next();
				logger.debug(methodName+": trying "+name);
                if(templating.templateExists(name))
                {
                    logger.debug(methodName+": found "+name);
                    try
                    {
                        return templating.getTemplate(name);
                    }
                    catch(TemplateNotFoundException e)
                    {
                        throw new IllegalStateException("Existing template disappeared", e);
                    }
                }
			}
            logger.debug(methodName+": not found");
            return null;
		}
		logger.debug(methodName+": empty name");
		return null;
	}

    private MVCTemplateFinder.Result findBuilderTemplate(String templateName, String methodName)
    {
        if(templateName != null && templateName.length() != 0)
        {
            Sequence sequence = getTemplateNameSequence(Kind.VIEW, templateName, true, false);
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug(methodName+": trying "+name);
                if(templating.templateExists(name))
                {
                    logger.debug(methodName+": found "+name);
                    try
                    {
                        Template template = templating.getTemplate(name);
                        return new MVCTemplateFinder.Result(
                            templateName, template, sequence.currentView(), !sequence.hasNext());
                    }
                    catch(TemplateNotFoundException e)
                    {
                        throw new IllegalStateException("Existing template disappeared", e);
                    }
                }
            }
            logger.debug(methodName+": not found");
            return new MVCTemplateFinder.Result(templateName, null, sequence.currentView(), true);
        }
        logger.debug(methodName+": empty name");
        return new MVCTemplateFinder.Result(templateName, null, null, true);
    }

    /**
     * Return a template lookup sequence.
     * 
     * @param kind the kind of template being looked up.
     * @param templateName the view name.
     * @param fallback perform fallback.
     * @param enclosing obsolete paramter.
     * @return a lookup sequence.
     */
    protected Sequence getTemplateNameSequence(Kind kind, String templateName,
        boolean fallback, boolean enclosing)
    {
        return nameSequenceFactory.
        	getTemplateNameSequence(kind.getInfix(), templateName, fallback, enclosing);
    }

    private Object findObject(Kind kind, String className, boolean fallback, String methodName)
	{
		if(className != null && className.length() != 0)
		{
			Sequence sequence = nameSequenceFactory.
				getClassNameSequence(kind.getInfix(), className, fallback, false);
			while(sequence.hasNext())
			{
				String name = sequence.next();
				logger.debug(methodName+": trying "+name);
				Object obj = getClassInstance(name);
                if(obj != null)
                {
                    logger.debug(methodName+": found "+name);
                    return obj;
                }
			}
		}
		logger.debug(methodName+": not found");
		return null;
	}

    private MVCClassFinder.Result findBuilder(String className, String methodName)
    {
        if(className != null && className.length() != 0)
        {
            Sequence sequence = nameSequenceFactory.
                getClassNameSequence(Kind.VIEW.getInfix(), className, true, false);
            while(sequence.hasNext())
            {
                String name = sequence.next();
                logger.debug(methodName+": trying "+name);
                Object obj = getClassInstance(name);
                if(obj != null)
                {
                    logger.debug(methodName+": found "+name);
                    return new MVCClassFinder.Result(className, (Builder)obj, sequence
                        .currentView(), !sequence.hasNext());
                }
            }
            logger.debug(methodName+": not found");
            return new MVCClassFinder.Result(className, (Builder) null, sequence.currentView(), true);
        }
        logger.debug(methodName+": empty name");
        return new MVCClassFinder.Result(className, (Builder) null, null, true);
    }
    
    private Object getClassInstance(String className)
    {
        Object instance;
        synchronized(classInstanceCache)
        {
            instance = classInstanceCache.get(className);
            if(instance == MISSING)
            {
                return null;
            }
            if(instance == null)
            {
                try
                {
                    Class<?> clazz = Class.forName(className);
                    if(container.getComponentAdapter(clazz) == null)
                    {
                        container.registerComponentImplementation(clazz);
                    }
                    instance = container.getComponentInstance(clazz);
                    classInstanceCache.put(className, instance);
                }
                catch(ClassNotFoundException e)
                {
                    classInstanceCache.put(className, MISSING);
                    return null;
                }
            }
            return instance;
        }
    }
}
