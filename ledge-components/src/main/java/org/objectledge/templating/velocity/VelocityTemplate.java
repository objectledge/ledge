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
	
	/** native velocity template */
	private org.apache.velocity.Template template;
	
    /**
     * Public constructor.
     * 
     * @param template the velocity template.
     * @param templating the templating system.
     */
    public VelocityTemplate(Templating templating, org.apache.velocity.Template template)
    {
		this.templating = templating;
		this.template = template;	
    }
    
    /**
     * Merge the template with context.
     *
     * @param context the context.
     * @return the output.
     * @throws MergingException if problem appears.
     */
    public String merge(TemplatingContext context)
    	throws MergingException
    {
		StringWriter sw = new StringWriter();
		templating.merge(context, this, sw);
		return sw.toString();
    }
    
    org.apache.velocity.Template getTemplate()
    {
    	return template;
    }
}
