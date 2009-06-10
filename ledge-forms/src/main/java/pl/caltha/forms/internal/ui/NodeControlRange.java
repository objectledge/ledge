package pl.caltha.forms.internal.ui;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.util.Util;

/**
 * Range Control - <i>WARNING!</i> it has no Velocity implementation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: NodeControlRange.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public class NodeControlRange extends NodeControl
{
    public NodeControlRange(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);

        startValue = Util.createDoubleAttribute(atts, "start");
        endValue = Util.createDoubleAttribute(atts, "end");
        stepSize = Util.createDoubleAttribute(atts, "stepSize");
    }

    private double startValue;
    private double endValue;
    private double stepSize;

    /** Getter for property startValue.
     * @return Value of property startValue.
     */
    public double getStartValue()
    {
        return startValue;
    }

    /** Getter for property endValue.
     * @return Value of property endValue.
     */
    public double getEndValue()
    {
        return endValue;
    }

    /** Getter for property stepSize.
     * @return Value of property stepSize.
     */
    public double getStepSize()
    {
        return stepSize;
    }
}
