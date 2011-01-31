package org.objectledge.web.captcha;

import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.tools.ContextToolFactory;

/**
 * {@link ContextToolFactory} implementation for {@link CaptchaTool}.
 * 
 * @author rafal 
 */
public class CaptchaToolFactory
    implements ContextToolFactory
{
    private static final String KEY = "captcha";

    private final CaptchaService captchaService;

    /**
     * Creates a new instance of CaptchaToolFactory.
     * 
     * @param captchaService CaptchaService implementation.
     */
    public CaptchaToolFactory(CaptchaService captchaService)
    {
        this.captchaService = captchaService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey()
    {
        return KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getTool()
        throws ProcessingException
    {
        return new CaptchaTool(captchaService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recycleTool(Object tool)
        throws ProcessingException
    {
        // no pooling implemented
    }
}
