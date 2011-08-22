package org.objectledge.modules.views.sso;



import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.EnclosingView;

public abstract class AbstractSsoView
    extends AbstractBuilder
{
    protected final Logger log;

    public AbstractSsoView(Context context, Logger log)
    {
        super(context);
        this.log = log;
    }

    protected String formatReply(String callback, String status, String ticket)
    {
        String jsonObject = "{ status : \"" + status + "\",\n ticket : \""
            + (ticket != null ? ticket : "NONE") + "\" }";
        return callback != null ? (callback + "(" + jsonObject + ");") : jsonObject;
    }
    
    @Override
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return EnclosingView.TOP;
    }
}
