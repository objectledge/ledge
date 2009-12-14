package pl.caltha.forms;

/**
 * Defines container for user inputed form data and form's UI state.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Instance.java,v 1.3 2008-07-24 17:06:37 rafal Exp $
 */
public interface Instance
{
    /** Getter for session wide instance id.
     * @return Value of instance id. */
    public String getId();

    /** Returns <code>true</code> if instance has been changed by the user. */
    public boolean isDirty();

    /** Sets a dirty flag on instance object - for use when changing instance's
     * internal data - like dom4j document. */
    public void setDirty(boolean dirtyFlag);

    /** Returns <code>true</code> if instance is valid and the user requested
     * a submit operation on this instance. */
    public boolean isSubmitted();

    /** Return <code>true</code> if instance has valid values and all
     * of required values are inputed. */
    public boolean isValid();

    /** Return <code>true</code> if instance has all required values. */
    public boolean hasRequired();
    
    /** Returns XML schemata URI for this Instance object. */
    public String getSchemaURI();
    
    /** Returns DOM4J Document object with this Instance's user inputed data. */
    public org.dom4j.Document getDocument();
    
    /** Return WYSIWIG editor Name */
    public String getEditorName();
    
}
