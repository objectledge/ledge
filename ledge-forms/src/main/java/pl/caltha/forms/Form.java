package pl.caltha.forms;


/**
 * Represents a form definition. Objects of this class are built upon an
 * XML definition files.
 * This objects is built from two functional elements:
 * <ul>
 *  <li><strong>User Interface</strong> - also built upon XML definition file,
 *  which is referenced in form's definition (element
 *  <code>&lt;interface&gt;</code>, attribute <code>href</code>).</li>
 *  <li><strong>Form's Data Model</strong> - form's data model is created by
 *  three following parts:
 *      <ul>
 *          <li><strong>XML shemata</strong> which describes what type of data
 *              users input into the form - referenced in form's definition
 *              (element <code>&lt;model&gt;</code>,
 *              attribute <code>href</code>). Input data is kept as
 *              an XML document stored in {@link Instance} object.</li>
 *          <li><strong>Default Instance Document</strong> this is an XML document
 *              containing default input data for this form - it is also
 *              referenced in form's definition
 *              (element <code>&lt;instance&gt;</code>,
 *              attribute <code>href</code>).</li>
 *          <li><strong>Bind expressions</strong> which are defined by
 *          <code>&lt;bind&gt;</code> elements in form defintion file.</li>
 *      </ul>
 *  </li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Form.java,v 1.1 2005-01-19 06:55:23 pablo Exp $
 */
public interface Form
{
    /** Processes a user request merging it's data with an Instance object.
     * <p>Processing consists of following steps:</p>
     * <ol>
     *  <li><b>Value extraction (from the request) and are application on an Instance.</b>
     *      <p>This step is first because form-tool is a server-side tool.
     *      All values must be applied before Instance is validated, or
     *      any actions, including setting values dynamically are executed
     *      (If for instance setValue action would be executed before
     *       applying values, the result of this action could be lost).
     *      </p>
     *      <p>This action triggers bind property cache clean up
     *         ({@link pl.caltha.forms.internal.model.InstanceImpl})
     *      </p>
     *  </li>
     *  <li><b>Event extraction (from request) and dispatching.</b>
     *      <p>Event dispatching triggers actions connected to controls.
     *      These actions may also trigger another events and so on.</p>
     *      <p>TODO: Implement event - action loop protection.</p>
     *
     *      <p>Right now only two kinds of events are extracted:</p>
     *      <ol>
     *          <li><code>activate</code> - control activation,
     *          in this case button control activation.</li>
     *          <li><code>submit</code> - submit button control activation.</li>
     *      </ol>
     *  </li>
     *  <li><b>Instance validation</b>
     *      <p>In client-side XForms implementation this step should be
     *      triggered by a changed value. In server-side processing value
     *      comparison would have to be implemented to check if any of them
     *      was changed. It is not very wise to do value comparison because
     *      there will be cases in which it will be faster to do validation
     *      than to compare all values in the Instance. That's why validation
     *      is always executed.
     *      </p>
     *  </li>
     * </ol>
     */
    public void process(Instance instance, net.labeo.webcore.RunData data)
    throws Exception;

    /** Returns ID for this form's definition file. This is a form definition URI
     * ({@link getFormDefinitionURI}) changed to be compatible with XHTML IDs. */
    public String getId();
    /** Returns URI for this form's definition file. */
    public String getDefinitionURI();
    /** Returns URI for this form's User Interface definition file. */
    public String getUIDefinitionURI();
    /** Returns URI for this form's XML schemata for Instances. */
    public String getInstanceSchemaURI();
    /** Returns URI for this form's Default Instance document. */
    public String getDefaultInstanceURI();
}
