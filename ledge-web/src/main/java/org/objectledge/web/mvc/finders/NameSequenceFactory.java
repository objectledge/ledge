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
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: NameSequenceFactory.java,v 1.1 2004-01-19 11:43:10 fil Exp $
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
        classPrefices = getPrefices(classesConfig);
        Configuration templatesConfig = config.getChild("templates");
        templateSeparator = templatesConfig.getAttribute("separator").trim().charAt(0);
        templateDefaultSuffix = templatesConfig.getAttribute("default-suffix", "Default");
        templatePrefices = getPrefices(templatesConfig);
    }
    
    /**
     * Extracts and validates prefices configuration.
     * 
     * @param config the configuration subtree to parse
     * @return an array of prefices.
     */
    private String[] getPrefices(Configuration config)
        throws ConfigurationException
    {
        Configuration[] children = config.getChildren("prefix");
        String[] result = new String[children.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = children[i].getValue();
        }
        // check for overlaps
        return result;
    }

    /**
     * Produces a class name sequence for the specified view. 
     * 
     * @param view the view.
     * @return name sequence.
     */
    public Sequence getClassNameSequence(String view)
    {
        Sequence fallback = getClassNameFallbackSequence(view);        
        return new ViewLookupSequence(classPrefices, classSeparator, fallback);
    }
    
    /**
     * Produces a template name sequence for the specified view. 
     * 
     * @param view the view.
     * @return name sequence.
     */
    public Sequence getTemplateNameSequence(String view)
    {
        Sequence fallback = getTemplateNameFallbackSequence(view);        
        return new ViewLookupSequence(templatePrefices, templateSeparator, fallback);
    }

    /**
     * Returns the view name for the specified template.
     * 
     * @param template the template.
     * @return the view name.
     */    
    public String getView(Template template)
    {
        return getView(template.getName(), "template", templatePrefices, templateSeparator);
    }

    /**
     * Returns the view name for the specified class.
     * 
     * @param clazz the class.
     * @return the view name.
     */    
    public String getView(Class clazz)
    {
        return getView(clazz.getName(), "class", classPrefices, classSeparator);
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////

    /**
     * Produces the class name fallback sequence.
     * 
     * <p>This is a planned extension point.</p>
     * 
     * @param view the view.
     * @return fallback sequence.
     */    
    protected Sequence getClassNameFallbackSequence(String view)
    {
        return new ViewFallbackSequence(view, viewSeparator,classSeparator, classDefaultSuffix);
    }

    /**
     * Produces the template name fallback sequence.
     * 
     * <p>This is a planned extension point.</p>
     * 
     * @param view the view.
     * @return fallback sequence.
     */    
    protected Sequence getTemplateNameFallbackSequence(String view)
    {
        return new ViewFallbackSequence(view, viewSeparator,templateSeparator, 
            templateDefaultSuffix);
    }

    /**
     * Resolves the view name for the specified class or template.
     * 
     * @param name the object name.
     * @param what the type of object ("class"/"template") used for exception message.
     * @param prefices the prefices to search.
     * @param separator the separator to replace.
     * @return the view name.
     */
    protected String getView(String name, String what, String[] prefices, char separator)
    {
        if(prefices.length > 0)
        {
            for(int i=0; i<prefices.length; i++)
            {
                if(name.startsWith(prefices[i]))
                {
                    return name.substring(prefices[i].length()+1).
                        replace(separator, viewSeparator);
                }
            }
            throw new IllegalArgumentException(what+" "+name+" outside defined prefices");
        }
        else
        {
            return name.replace(separator, viewSeparator);
        }
    }
}
