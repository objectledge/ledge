package pl.caltha.forms;


/**
 * FormTool service is used to generate and validate interactive HTML forms.
 * As for now it is a partial <strong>Server-Side</strong> XForms implementation.
 *
 * @author    <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version   $Id: FormsService.java,v 1.4 2002/05/28 18:02:20 zwierzem Exp
 *      $
 */
public interface FormsService 
{
    /** The service name. */
    public final static String SERVICE_NAME = "formtool";

    /**
     * XML namespace for ui definition scheme.<br />
     * <code>http://www.cyklotron.net/2001/11/formtool-ui</code>.
     */
    public final static String ACCEPTED_NS_UI =
            "http://www.cyklotron.net/2001/11/formtool-ui";
    /**
     * XML namespace for form definition scheme.<br />
     * <code>http://www.cyklotron.net/2001/11/formtool-form</code>.
     */
    public final static String ACCEPTED_NS_FORM =
            "http://www.cyklotron.net/2001/11/formtool-form";


    /**
     * Returns a Form definition object based on it's definition URI. Internally
     * it builds this object and caches it for future use. This method also
     * creates a name mapping for returned form definition object. It is
     * allowed to map the same form under multiple names. When tere exists a
     * different form (one with different deifinition URI) with the same name
     * as one given in the parameters an exception is thrown.
     *
     * @param formDefinitionURI          URI of a form definition file
     * @param formName                   Name mapping for this form definition
     * @return                           Form definition object
     * @exception ConstructionException  Thrown on errors when building the
     *      form definition object
     * @exception FormsException      Thrown on ambiguos formName =&gt;
     *      form definition mapping
     */
    public Form getForm( String formDefinitionURI, String formName )
        throws ConstructionException, FormsException;


    /**
     * Returns an {@link pl.caltha.forms.Instance} object depending
     * on RunData parameters. If this object cannot be found it creates one
     * depending on a given {@link pl.caltha.forms.Form} object.
     *
     * @param formName            Form's system wide identifier, this one is
     *      used to allow same form definitions to be used in different site
     *      contexts.
     * @param data                RunData for current request.
     * @return                    found or newly created Instance object.
     * @throws FormsException  thrown when a found Instance is not an
     *      instance for a given Form definition.
     */
    public Instance getInstance( String formName, RunData data )
        throws FormsException;


    /**
     * Returns an Instance object creating it from a given saved state.
     *
     * @param formName    Form's system wide identifier, this one is used to
     *      allow same form definitions to be used in different site contexts.
     * @param data        RunData for current request - created instance will
     *      be stored in this user's session.
     * @param savedState  Serialized Instance data.
     * @return            Deserialized Instance object.
     * @throws Exception  thrown on problems with deserialization.
     */
    public Instance getInstance( String formName, RunData data, byte[] savedState )
        throws Exception;


    /**
     * Removes an instance from users session - it should be used after
     * instance processing is finished. Otherwise heavy instance data will be
     * kept during whole user session.
     *
     * @param data      Description of Parameter
     * @param instance  Description of Parameter
     */
    public void removeInstance( RunData data, Instance instance );
}

