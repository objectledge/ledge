package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/**
 * Formatted control implementation. Includes:
 * <ul>
 *      <li><code>input</code></li>
 *      <li><code>textarea</code></li>
 *      <li><code>secret</code></li>
 *      <li><code>output</code></li>
 * </ul>
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControlFormatted.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeControlFormatted extends NodeControl
{
    public NodeControlFormatted(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        format = Util.getSAXAttributeVal(atts, "format");
        if(format != null)
        {
            //TODO: build a format depending on its form and field datatype.
            // SimpleDateFormat
            // DecimalFormat
            // MessageFormat ??
            //
        }
    }

    private String format;

    //------------------------------------------------------------------------
    // Control methods
    //
    public Object getValue(InstanceImpl instance)
    {
        Object value = super.getValue(instance);
		//if(value != null && value instanceof String && ((String)value).length() > 0)
		//{
			//TODO: use a format
		//}
        return value;
    }
}
