package pl.caltha.forms.internal;

import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.ui.UIConstants;
import net.labeo.services.ServiceBroker;
import pl.caltha.forms.Instance;
import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.services.templating.Template;
import net.labeo.services.templating.TemplateNotFoundException;
import net.labeo.services.templating.TemplatingService;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.LinkTool;
import net.labeo.webcore.RunData;

/** This a form tool context tool implementation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormToolImpl.java,v 1.1 2005-01-19 06:55:20 pablo Exp $
 */
public class FormToolImpl extends net.labeo.services.pool.RecyclableObject
implements pl.caltha.forms.FormTool
{
    private TemplatingService templatingService;
    private RunData data;

    public String generateUI(Instance instance, LinkTool formLink, String skinName)
    throws MergingException, TemplateNotFoundException
    {
        // 1. get UI from Instance, get rootNode
        // 2. put root node into the context
        // 3. put instance into the context
        Context context = data.getContext();
        context.put("formtool-instance", instance);
        context.put("formtool-form", ((InstanceImpl)instance).getForm().getUI().getUIRoot());
        context.put("formtool-link", formLink);
        context.put("formtool-const", UIConstants.getInstance());

        // 4. get the skin == template
        Template template = null;
        // try to get a template overriden by the application
        try
        {
            template = templatingService.getTemplate(data.getApplication(), "forms/"+skinName);
        }
        catch(TemplateNotFoundException e)
        {
            // pass through - look for default skin
            template = null;
        }
        // this one throws an exception - we cannot generate UI without any templates.
        if(template == null)
        {
            template = templatingService.getTemplate("", "forms/"+skinName);
        }

        // 5. merge template
        return template.merge(context);
    }


    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException
    {
        return generateUI(instance, formLink, "default");
    }

    /** Initializes the context tool.
     *
     * <p>Initialization specific to tool definition is performed here.</p>
     *
     * @param broker Labeo service broker
     * @param conf tool's configuration
     */
    public void init(ServiceBroker broker, Configuration conf)
    {
        if(templatingService == null)
        {
            templatingService = (TemplatingService)broker.getService(TemplatingService.SERVICE_NAME);
        }
    }

    /** Prepares the context tool instance for work witin a specific request.
     *
     * @param data the RunData.
     */
    public void prepare(RunData data)
    {
        this.data = data;
    }

    /** Resets the object's internal state.
     *
     * <p> This method prepares the object instance for reuse. After this method
     * returns, the object should be equivalent to a newly created one. </p>
     */
    public void reset()
    {
        data = null;
    }
}
