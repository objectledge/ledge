package org.objectledge.forms;

import org.objectledge.templating.MergingException;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.web.mvc.tools.LinkTool;


/** This a Context tool used to generate Form interfaces.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormTool.java,v 1.3 2005-02-08 20:33:22 rafal Exp $
 */
public interface FormTool
{
    /** Generates form UI.
     * @param instance form User data which is used to generate the UI.
     * @param formLink the linkTool to use.
     * @param skinName name of a FormTool velocity template to use.
     * @throws MergingException Thrown on any problems when generating UI.
     * @return generated UI markup.
     */
    public String generateUI(Instance instance, LinkTool formLink, String skinName)
    throws MergingException, TemplateNotFoundException;

    /** Generates form UI, uses default Form look.
     * @param instance form User data which is used to generate the UI.
     * @param formLink the linkTool to use.
     * stored in <code>action</code> attribute of the form.
     * @throws MergingException Thrown on any problems when generating UI.
     * @return generated UI markup.
     */
    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException;
}
