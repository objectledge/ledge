package pl.caltha.forms;


/** This a Context tool used to generate Form interfaces.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: FormTool.java,v 1.1 2005-01-19 06:55:23 pablo Exp $
 */
public interface FormTool extends ContextTool
{
    /** Generates form UI.
     * @param instance form User data which is used to generate the UI.
     * @param actionName Name of an action which will be used in a link
     * stored in <code>action</code> attribute of the form.
     * @param skinName name of a FormTool velocity template to use.
     * @throws MergingException Thrown on any problems when generating UI.
     * @return generated UI markup.
     */
    public String generateUI(Instance instance, LinkTool formLink, String skinName)
    throws MergingException, TemplateNotFoundException;

    /** Generates form UI, uses default Form look.
     * @param instance form User data which is used to generate the UI.
     * @param actionName Name of an action which will be used in a link
     * stored in <code>action</code> attribute of the form.
     * @throws MergingException Thrown on any problems when generating UI.
     * @return generated UI markup.
     */
    public String generateUI(Instance instance, LinkTool formLink)
    throws MergingException, TemplateNotFoundException;
}
