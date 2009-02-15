package pl.caltha.forms.internal.ui.actions;

import java.util.List;

import org.xml.sax.Attributes;

import pl.caltha.forms.ConstructionException;
import pl.caltha.forms.internal.ui.NodeRepeat;
import pl.caltha.forms.internal.ui.ReferenceSingle;
import pl.caltha.forms.internal.ui.UI;
import pl.caltha.forms.internal.util.Util;

/** Action that removes a repeat instance trees.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseInsertDeleteAction.java,v 1.2 2005-02-08 20:33:35 rafal Exp $
 */
public abstract class BaseInsertDeleteAction
extends BaseReferenceAction
{
    public BaseInsertDeleteAction(String type, Attributes atts)
    throws ConstructionException
    {
        super(type, atts);
        // delete/insert for which repeat
        repeatId = Util.getSAXAttributeVal(atts, "repeat");
        // index of node to delete/insert evaluation expression
        ref = new ReferenceSingle(atts, this, "at");
    }

    //------------------------------------------------------------------------
    //attributes
    /** <code>repeat</code> attribute value - an XML IDREF to a
     * <code>repeat</code> element. */
    protected String repeatId;
    /** {@link pl.caltha.forms.internal.ui.NodeRepeat} - <code>repeat</code>
     * element referenced in <code>at</code> attribute. */
    protected NodeRepeat repeat;

    protected int clipIndex(Number result, List contextNodes)
    {
        // the index is being decreased because in XPath it is 1 based
        int index = result.intValue() - 1;
        // clip index
        if(index < 0)
        {
            index = 0;
        }
        if(index > contextNodes.size()-1)
        {
            index = contextNodes.size()-1;
        }
        return index;
    }

    //------------------------------------------------------------------------
    // methods used by UIBuilder in the same sequence they are called

    /** Inits an insert/delete action node - connects a referenced <code>repeat</code>
     * element..
     * @throws ConstructionException Thrown on initialisation errors
     */
    protected void init(UI ui)
    throws ConstructionException
    {
        super.init(ui);
        repeat = (NodeRepeat)(getNodeById(ui, repeatId));
    }
}
