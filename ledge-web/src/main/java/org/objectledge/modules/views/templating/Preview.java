package org.objectledge.modules.views.templating;

import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * A class that enforces policy protection for this view.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class Preview
    extends PolicyProtectedBuilder
{
    /** templating manager */
    private Templating templating;
    
    /** file system */
    private FileSystem fileSystem;
    
    /**
     * Creates new VM view instance.
     * 
     * @param context the Context component.
     * @param policySystemArg the PolicySystem component.
     */
    public Preview(Context context, PolicySystem policySystemArg,
        Templating templating, FileSystem fileSystem)
    {
        super(context, policySystemArg);
        this.templating = templating;
        this.fileSystem = fileSystem;
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if(parameters.isDefined("name"))
        {
            String name = parameters.get("name");
            String fsName = parameters.get("fsname");
            boolean exists = templating.templateExists(name);
            boolean fsExists = false;
            if(fsName.length() > 0)
            {
                fsExists = fileSystem.exists(fsName);
            }
            templatingContext.put("exists", exists);
            templatingContext.put("fsExists", fsExists);
            if(exists)
            {
                Template template = null;
                try
                {
                    template = templating.getTemplate(name);
                }
                catch(TemplateNotFoundException e)
                {
                    throw new ProcessingException("exception occured", e);
                }
                try
                {
                    String result = template.merge(templating.createContext());
                    templatingContext.put("result", result.replaceAll(">","&gt;").replaceAll("<","&lt;"));
                }
                catch(MergingException e)
                {
                    templatingContext.put("result", new StackTrace(e));
                }
            }
            if(fsExists)
            {
                try
                {
                    String content = fileSystem.read(fsName, templating.getTemplateEncoding());
                    templatingContext.put("content", content);
                }
                catch(Exception e)
                {
                    templatingContext.put("content", new StackTrace(e));
                }
            }
            templatingContext.put("show", true);
            templatingContext.put("name", name);
            templatingContext.put("fsname", fsName);
        }
        else
        {
            templatingContext.put("show", false);
        }
    }  
}
