package org.objectledge.templating.velocity;

import java.io.StringWriter;

import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

/**
 * Simple velocity template wrapper.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 */
public class VelocityTemplate implements Template
{
	/** reference to templating system */
	private Templating templating;
    
    /** the name of the template. */
    private String name;
	
	/** native velocity template */
	private org.apache.velocity.Template template;
	
    /**
     * Constructs a template instance.
     * 
     * @param template the velocity template.
     * @param name the name of the template.
     * @param templating the templating system.
     */
    public VelocityTemplate(Templating templating, String name, 
        org.apache.velocity.Template template)
    {
		this.templating = templating;
        this.name = name;
		this.template = template;	
    }
    
    /**
     * {@inheritDoc}
     */
    public String merge(TemplatingContext context)
    	throws MergingException
    {
		StringWriter sw = new StringWriter();
		templating.merge(context, this, sw);
		return sw.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }
    
    // implementation ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the native Velocity template.
     *
     * @return the native Velocity template.
     */
    org.apache.velocity.Template getTemplate()
    {
    	return template;
    }
}
