package org.objectledge.web.cors;

/**
 * CrossOriginRequestValidator implementation that delegates decisions to multiple downstream
 * implementations. A request is accepted when it is accepted by any of the delegates.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class CombinedCrossOriginRequestValidator
    implements CrossOriginRequestValidator
{
    private final CrossOriginRequestValidator[] delegates;

    public CombinedCrossOriginRequestValidator(CrossOriginRequestValidator[] delegates)
    {
        this.delegates = new CrossOriginRequestValidator[delegates.length];
        System.arraycopy(delegates, 0, this.delegates, 0, delegates.length);
    }

    @Override
    public boolean isAllowed(String originUri)
    {
        for(CrossOriginRequestValidator delegate : delegates)
        {
            if(delegate.isAllowed(originUri))
            {
                return true;
            }
        }
        return false;
    }
}
