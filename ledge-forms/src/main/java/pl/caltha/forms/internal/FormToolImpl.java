package pl.caltha.forms.internal;

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
 * @version $Id: FormToolImpl.java,v 1.5 2005-03-22 06:16:43 zwierzem Exp $
 */
public class FormToolImpl 
implements FormTool
{
    private Templating templating;
    
    private TemplatingContext templatingContext;
    
    public FormToolImpl(Templating templating, TemplatingContext templatingContext)
    {
        this.templating = templating;
        this.templatingContext = templatingContext;
    }
    
    
    public String generateUI(Instance instance, LinkTool formLink, String skinName)
    throws MergingException, TemplateNotFoundException
    {
        // 1. get UI from Instance, get rootNode
        // 2. put root node into the context
        // 3. put instance into the context
        templatingContext.put("formtool-instance", instance);
        templatingContext.put("formtool-form", ((InstanceImpl)instance).getForm().getUI().getUIRoot());
        templatingContext.put("formtool-link", formLink);
        templatingContext.put("formtoolConst", UIConstants.getInstance());

        // 4. get the skin == template
        Template template = null;
        // this one throws an exception - we cannot generate UI without any templates.
        if(template == null)
        {
            template = templating.getTemplate("forms/"+skinName);
        }

        // 5. merge template
        return template.merge(templatingContext);
    }


    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException
    {
        return generateUI(instance, formLink, "default");
    }
}
