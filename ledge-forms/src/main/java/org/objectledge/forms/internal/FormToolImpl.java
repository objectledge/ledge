package org.objectledge.forms.internal;

import org.objectledge.forms.FormTool;
import org.objectledge.forms.Instance;
import org.objectledge.forms.internal.model.InstanceImpl;
import org.objectledge.forms.internal.ui.UIConstants;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.tools.LinkTool;


/** This a form tool context tool implementation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormToolImpl.java,v 1.8 2008-07-24 17:06:55 rafal Exp $
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
        Template template = templating.getTemplate("forms/"+skinName);       
        // 5. merge template
        return template.merge(templatingContext);
    }


    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException
    {
        return generateUI(instance, formLink, "Default");
    }
}
