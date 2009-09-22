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
package org.objectledge.web.mvc.finders;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.templating.Template;

/**
 * A configurable factory of {@link org.objectledge.web.mvc.finders.Sequence} objects.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: NameSequenceFactory.java,v 1.10 2005-07-22 17:25:53 pablo Exp $
 */
public class NameSequenceFactory
{
    private char viewSeparator;
    
    private char classSeparator;
    
    private String classDefaultSuffix;
    
    private String[] classPrefices;

    private char templateSeparator;
    
    private String templateDefaultSuffix;
    
    private String[] templatePrefices;
    
    /**
     * Constructs a new sequence factory.
     * 
     * @param config the configuration.
     * @throws ConfigurationException if the configuration is invalid.
     */
    public NameSequenceFactory(Configuration config)
        throws ConfigurationException
    {
        viewSeparator = config.getChild("views").getAttribute("separator").trim().charAt(0);
        Configuration classesConfig = config.getChild("classes");
        classSeparator = classesConfig.getAttribute("separator").trim().charAt(0);
        classDefaultSuffix = classesConfig.getAttribute("default-suffix", "Default");
        classPrefices = getPrefices(classesConfig, classSeparator);
        Configuration templatesConfig = config.getChild("templates");
        templateSeparator = templatesConfig.getAttribute("separator").trim().charAt(0);
        templateDefaultSuffix = templatesConfig.getAttribute("default-suffix", "Default");
        templatePrefices = getPrefices(templatesConfig, templateSeparator);
    }
    
    /**
     * Extracts and validates prefices configuration.
     * 
     * @param config the configuration subtree to parse
     * @return an array of prefices.
     */
    private String[] getPrefices(Configuration config, char separator)
        throws ConfigurationException
    {
        Configuration[] children = config.getChildren("prefix");
        String[] result = new String[children.length];
        for (int i = 0; i < result.length; i++)
        {
            String prefix = children[i].getValue();
            // cut off separators at the end
            if (prefix.charAt(prefix.length()-1) == separator)
            {
                prefix = prefix.substring(0, prefix.length()-1);
            }
			result[i] = prefix;
        }
        // check for overlaps
		for (int i = 0; i < result.length; i++)
        {
            String prefix1 = result[i];
            for (int j = 0; j < result.length; j++)
            {
                String prefix2 = result[j];
                if (i != j
                	&& prefix1.length() > prefix2.length()
                	&& prefix1.startsWith(prefix2)
                	&& prefix1.charAt(prefix2.length()) == separator)
                {
					Configuration childConfig = children[i];
                	throw new ConfigurationException("Prefix "+ prefix1 +" overlaps "+ prefix2,
                		childConfig.getPath(), childConfig.getLocation());
                }
            }
        }
        return result;
    }

    /**
     * Produces a class name sequence for the specified view. 
     * @param infix the path infix ("actions", "views","components")
     * @param view the view.
     * @param fallback <code>true</code> to perform scoping fallback
     * @param skipFirst <code>true</code> to skip first result in scoping fallback - 
     *   used for looking up enclosing views. If fallback is <code>false</code> has no effect.
     * 
     * @return name sequence.
     */
    public Sequence getClassNameSequence(String infix, String view, boolean fallback, 
        boolean skipFirst)
    {
        Sequence fallbackSequence;
        if(fallback)
        {
            fallbackSequence = getClassNameFallbackSequence(view, skipFirst);        
        }
        else
        {
            fallbackSequence = getClassNameFixedSequence(view);
        }
        return new ViewLookupSequence(classPrefices, classSeparator, infix, fallbackSequence);
    }
    
    /**
     * Produces a template name sequence for the specified view. 
     * @param infix the path infix ("views", "components")
     * @param view the view.
     * @param fallback <code>true</code>pefrorm scoping fallback
     * @param skipFirst <code>true</code> to skip first result in scoping fallback - 
     *   used for looking up enclosing views. If fallback is <code>false</code> has no effect.
     * 
     * @return name sequence.
     */
    public Sequence getTemplateNameSequence(String infix, String view, boolean fallback, 
        boolean skipFirst)
    {
        Sequence fallbackSequence; 
        if(fallback)
        {
            fallbackSequence = getTemplateNameFallbackSequence(view, skipFirst);        
        }
        else
        {
            fallbackSequence = getTemplateNameFixedSequence(view);
        }
        return new ViewLookupSequence(templatePrefices, templateSeparator, infix, fallbackSequence);
    }

    /**
     * Returns the view name for the specified template.
     * 
     * @param template the template.
     * @param infix the path infix ("views", "components")
     * @return the view name.
     */    
    public String getView(String infix, Template template)
    {
        return getView(template.getName(), "template", infix, templatePrefices, templateSeparator);
    }

    /**
     * Returns the view name for the specified class.
     * 
     * @param clazz the class.
     * @param infix the path infix ("actions", "views","components")
     * @return the view name.
     */    
    public String getView(String infix, Class clazz)
    {
        return getView(clazz.getName(), "class", infix, classPrefices, classSeparator);
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////

    /**
     * Produces the class name fallback sequence.
     * 
     * <p>This is a planned extension point.</p>
     * @param view the view.
     * @param skipFirst skip first result - used for looking up enclosing views.
     * 
     * @return fallback sequence.
     */    
    protected Sequence getClassNameFallbackSequence(String view, boolean skipFirst)
    {
        return new ViewFallbackSequence(view, viewSeparator,classSeparator, classDefaultSuffix, 
            skipFirst);
    }

    /**
     * Produces the class name fixed sequence.
     * 
     * @param view the view.
     * @return fallback sequence.
     */    
    protected Sequence getClassNameFixedSequence(String view)
    {
        return new FixedSequence(view.replace(viewSeparator, classSeparator));
    }

    /**
     * Produces the template name fallback sequence.
     * 
     * <p>This is a planned extension point.</p>
     * @param view the view.
     * @param skipFirst skip first result - used for looking up enclosing views.
     * 
     * @return fallback sequence.
     */    
    protected Sequence getTemplateNameFallbackSequence(String view, boolean skipFirst)
    {
        return new ViewFallbackSequence(view, viewSeparator,templateSeparator, 
            templateDefaultSuffix, skipFirst);
    }

    /**
     * Produces the class name fixed sequence.
     * 
     * @param view the view.
     * @return fallback sequence.
     */    
    protected Sequence getTemplateNameFixedSequence(String view)
    {
        return new FixedSequence(view.replace(viewSeparator, templateSeparator));
    }

    /**
     * Resolves the view name for the specified class or template.
     * 
     * @param name the object name.
     * @param what the type of object ("class"/"template") used for exception message.
     * @param infix the path infix ("actions", "views","components")
     * @param prefices the prefices to search.
     * @param separator the separator to replace.
     * @return the view name.
     */
    protected String getView(String name, String what, 
        String infix, String[] prefices, char separator)
    {
        if(prefices.length > 0)
        {
            for(int i=0; i<prefices.length; i++)
            {
                if(name.startsWith(prefices[i]) && 
                    name.substring(prefices[i].length()+1).startsWith(infix))
                {
                    return name.substring(prefices[i].length()+infix.length()+2).
                        replace(separator, viewSeparator);
                }
            }
            throw new IllegalArgumentException(what+" "+name+" outside defined prefices or "+infix);
        }
        else
        {
            if(name.startsWith(infix))
            {
                return name.substring(infix.length()+1).replace(separator, viewSeparator);
            }
            else
            {
                throw new IllegalArgumentException(what+" "+name+" not within "+infix);
            }
        }
    }
}
