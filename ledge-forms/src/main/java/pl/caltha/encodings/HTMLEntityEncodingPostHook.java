package pl.caltha.encodings;

import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.Action;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.SecurityException;

/**
 * Encodes build results using HTML entities to avoid missing characters in encoded output.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLEntityEncodingPostHook.java,v 1.1 2005-01-19 06:55:41 pablo Exp $
 */
public class HTMLEntityEncodingPostHook implements Action
{
    /* (non-Javadoc)
     * @see net.labeo.webcore.Action#execute(net.labeo.webcore.RunData)
     */
    public void execute(RunData data)
        throws ProcessingException, SecurityException, NotFoundException
    {
        HTMLEntityEncoder encoder = new HTMLEntityEncoder();
        String result = data.getBuildResult();
        data.setBuildResult(encoder.encodeHTML(result, data.getEncoding()));
    }
}
