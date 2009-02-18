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
 * @version $Id: NodeControlDate.java,v 1.2 2005-03-23 05:36:31 zwierzem Exp $
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

    /** This method breakes the contract for returning <code>null</code> for undefined value.
     *  That is why the hasValue method had to be reimplemented.
     */
    public Object getValue(InstanceImpl instance)
    {
        Object value = super.getValue(instance);
        if(value != null && value instanceof String && ((String)value).length() > 0)
        {
            String stringValue = (String) value;
            Date dateValue = null;
            
            if(stringValue.startsWith("now"))
            {
                dateValue = new Date();
            }
            else
            if(stringValue.startsWith("today-start"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                dateValue = calendar.getTime();
            }
            else
            if(stringValue.startsWith("today-end"))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                dateValue = calendar.getTime();
            }
            else
            {
                try
                {
                    int end = stringValue.indexOf('/');
                    end = (end == -1) ? stringValue.length() : end; 
                    dateValue = new Date(Long.parseLong(stringValue.substring(0, end)));
                }
                catch(NumberFormatException e)
                {
                    dateValue = null; // value undefined
                }
            }

            return (optionalSelection)
                ? new DateValue(dateValue, !stringValue.endsWith("/disabled"))
                : new DateValue(dateValue);
        }
        return new DateValue(null);
    }
    
    public boolean hasValue(InstanceImpl instance)
    {
        Object value = getValue(instance);
        return ((DateValue)value).getValue() != null ;
    }
    
    public class DateValue
    {
        private Date value;
        private boolean enabled;
        
        public DateValue(Date value)
        {
            this(value, true);
        }

        public DateValue(Date value, boolean enabled)
        {
            this.value = value;
            this.enabled = enabled;
        }

        public boolean getEnabled()
        {
            return enabled;
        }
        public Date getValue()
        {
            return value;
        }
    }
}