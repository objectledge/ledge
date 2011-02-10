package org.objectledge.web.captcha;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.i18n.I18nContext;

/**
 * Context tool for including CAPTCHA widget in a form,
 * 
 * @author rafal
 */
public class CaptchaTool
{
    private final CaptchaService captchaService;
    
    private final Context context;

    /**
     * Creates a new instance of CaptchaTool
     * 
     * @param captchaService CaptchaService
     */
    public CaptchaTool(Context context, CaptchaService captchaService)
    {
        this.captchaService = captchaService;
        this.context = context;
    }

    /**
     * Create CAPTCHA widget with current locale and default options.
     * 
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget()
    {
        Map<String,String> options = Collections.emptyMap();
        return createCaptchaWidget(options);
    }
    
    /**
     * Create CAPTCHA widget with current locale and specified options.
     * 
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget(Map<String,String> options)
    {
        I18nContext i18nContext = context.getAttribute(I18nContext.class);
        Locale locale = i18nContext != null ? i18nContext.getLocale() : null;        
        return captchaService.createCaptchaWidget(locale, options);        
    }
    

    /**
     * Create CAPTCHA widget with specified locale and options.
     * 
     * @param options implementation specific options.
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget(Locale locale, Map<String, String> options)
    {
        return captchaService.createCaptchaWidget(locale, options);
    }
}
