package pl.caltha.forms.internal;

import org.objectledge.context.Context;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.tools.LinkTool;

import pl.caltha.forms.FormTool;
import pl.caltha.forms.Instance;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.UIConstants;

/** This a form tool context tool implementation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormToolImpl.java,v 1.3 2005-01-27 00:58:23 pablo Exp $
 */
public class FormToolImpl 
implements FormTool
{
    private Templating templating;
    
    private Context context;
    
    public FormToolImpl(Context context, Templating templating)
    {
        this.templating = templating;
    }
    
    
    public String generateUI(Instance instance, LinkTool formLink, String skinName)
    throws MergingException, TemplateNotFoundException
    {
        // 1. get UI from Instance, get rootNode
        // 2. put root node into the context
        // 3. put instance into the context
        TemplatingContext tContext = (TemplatingContext)
            context.getAttribute(TemplatingContext.class);
        tContext.put("formtool-instance", instance);
        tContext.put("formtool-form", ((InstanceImpl)instance).getForm().getUI().getUIRoot());
        tContext.put("formtool-link", formLink);
        tContext.put("formtool-const", UIConstants.getInstance());

        // 4. get the skin == template
        Template template = null;
        // this one throws an exception - we cannot generate UI without any templates.
        if(template == null)
        {
            template = templating.getTemplate("forms/"+skinName);
        }

        // 5. merge template
        return template.merge(tContext);
    }


    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException
    {
        return generateUI(instance, formLink, "default");
    }
}
