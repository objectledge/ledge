package pl.caltha.forms.internal.ui;

import java.util.Calendar;
import java.util.Date;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.model.InstanceImpl;
import pl.caltha.forms.internal.util.Util;

/**
 * Implementation of Date controls.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: NodeControlDate.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeControlDate extends NodeControl
{
    public NodeControlDate(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

		optionalSelection = Util.createBooleanAttribute(atts, "optionalSelection", false);
    }

    //------------------------------------------------------------------------
    //attributes
    private boolean optionalSelection;

    //------------------------------------------------------------------------
    //access methods for attributes
    public boolean getOptionalSelection()
    {
        return optionalSelection;
    }

    /* (non-Javadoc)
     * @see pl.caltha.forms.internal.ui.Control#getValue(pl.caltha.forms.internal.model.InstanceImpl)
     */
    public Object getValue(InstanceImpl instance)
    {
		Object value = super.getValue(instance);
		if(value != null && value instanceof String && ((String)value).length() > 0)
		{
            if(value.equals("now"))
            {
                Date date = new Date(); 
                return new Long(date.getTime());
            }
            else
            if(value.equals("today-start"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return new Long(calendar.getTimeInMillis());
            }
            else
            if(value.equals("today-end"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                return new Long(calendar.getTimeInMillis());
            }
            else
            {
    			try
    			{
    				return new Long((String)value);
    			}
    			catch(NumberFormatException e)
    			{
    				return null;
    			}
            }
		}
		return null;
    }
}
