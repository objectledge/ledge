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
 * @version $Id: MVCFinder.java,v 1.28 2005-02-28 10:47:46 rafal Exp $
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
    public Template findBuilderTemplate(String view)
    {
		return findTemplate(Kind.VIEW, view, true, "findBuilderTemplate");
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
    public Builder findBuilder(String view)
    {
		return (Builder) findObject(Kind.VIEW, view, true, "findBuilder");
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

                try
                {
                    Object builder = getClassInstance(className);
                    break;
                }
                catch(ClassNotFoundException e)
                {
                    // go on
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
		}
		return null;
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
				try
				{
					return getClassInstance(name);
				}
				catch(ClassNotFoundException e)
				{
					// go on
				}
			}
		}
		return null;
	}

	/**
	 * Gets an instance of an object depending on it's finder name and type
	 */
	private Object getClassInstance(String className)
        throws ClassNotFoundException
	{
        Class clazz = Class.forName(className);
        if(container.getComponentInstance(clazz) == null)
        {
            container.registerComponentImplementation(clazz);
        }
        return container.getComponentInstance(clazz);
	}
}
