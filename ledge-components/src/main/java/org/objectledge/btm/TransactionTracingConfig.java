package org.objectledge.btm;

public class TransactionTracingConfig
{
    private int depth;

    private int defaultTimeout;

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public int getDefaultTimeout()
    {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout)
    {
        this.defaultTimeout = defaultTimeout;
    }
}
