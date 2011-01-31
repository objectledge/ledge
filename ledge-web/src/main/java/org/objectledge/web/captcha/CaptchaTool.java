package org.objectledge.web.captcha;

import java.util.Map;

/**
 * Context tool for including CAPTCHA widget in a form,
 * 
 * @author rafal
 */
public class CaptchaTool
{
    private final CaptchaService captchaService;

    /**
     * Creates a new instance of CaptchaTool
     * 
     * @param captchaService CaptchaService
     */
    public CaptchaTool(CaptchaService captchaService)
    {
        this.captchaService = captchaService;
    }

    /**
     * Create CAPTCHA widget with default options
     * 
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget()
    {
        return captchaService.createCaptchaWidget();
    }

    /**
     * Create CAPTCHA widget with specified options.
     * 
     * @param options implementation specific options.
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget(Map<String, String> options)
    {
        return captchaService.createCaptchaWidget(options);
    }
}
