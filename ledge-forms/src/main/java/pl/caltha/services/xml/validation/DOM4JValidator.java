package pl.caltha.services.xml.validation;
/** Interface for DOM4J trees validation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: DOM4JValidator.java,v 1.1 2005-01-20 16:44:51 pablo Exp $
 */
public interface DOM4JValidator
{
    /** Validates a Document.
     *
     * @param document DOM4J document to check for validity
     * @param grammarID public or system ID of XML schemata to be used to validate
     * @param errorAndContentHandler used to report error messages during validation,
     * and to inform about their location in DOM4J tree.
     * @throws ValidationException Thrown on fatal problems with validation.
     * @throws Exception on problems when getting grammar for validation.
     * @return <code>true</code> when the document was ok,
     * <code>false</code> otherwise.
     */
    public boolean validate(org.dom4j.Document document, String schemaID,
                            DOM4JValidationErrorCollector errorAndContentHandler)
    throws ValidationException, Exception;
}
