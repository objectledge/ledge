package org.objectledge.web.captcha;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;

/**
 * A service that provides CAPTCHA functionality for web applications.
 * 
 * @author rafal 
 */
public interface CaptchaService
{    
    public static final String CAPTCHA_FOR_EVERYONE = "all";
    
    public static final String CAPTCHA_FOR_ANONYMOUS = "anonymous";
    
    public static final String CAPTCHA_DISABLED = "";
    
    public static enum CaptchaApiVersion {
        API_V1{
            @Override
            public String toString()
            {
                return "API_V1";
            }
        },
        API_V2{
            @Override
            public String toString()
            {
                return "API_V2";
            }
        };
        
        public static CaptchaApiVersion getVersion(String version)
        {
            if(CaptchaApiVersion.API_V2.toString().equals(version))
            {
                return CaptchaApiVersion.API_V2;
            }
            else
            {
                // as default
                return CaptchaApiVersion.API_V1;
            }
        }
    }
    
    /**
     * Create CAPTCHA widget with specified options.
     * 
     * @param options implementation specific options.
     * @return HTML markup of CAPTCHA widget.
     */
    public String createCaptchaWidget(Locale locale, Map<String, String> properties);

    /**
     * Create CAPTCHA Mailhide widget.
     * 
     * @param email email address.
     * @return HTML markup of CAPTCHA hideMail widget.
     */
    public String createrCaptchaEmailWidget(String email);

    /**
     * Verify CAPTCHA solved by the user.
     * 
     * @param remoteAddr IP address of the user
     * @param challenge challenge generated by CAPTHA widget.
     * @param response response entered by the user.
     * @return true if the solution is correct.
     */
    public boolean checkCaptcha(String remoteAddr, String challenge, String response, CaptchaApiVersion version);

    /**
     * Verify CAPTCHA solved by the user.
     * 
     * @param parameters request parameters that contain CAPTCHA challenge and response. Parameter
     *        names are implementation specific.
     * @return
     */
    public boolean checkCaptcha(HttpContext httpContext, RequestParameters parameters);
    
    /**
     * Verify if CAPTCHA required by the principal.
     * 
     * @param parameters component or application configuration.
     *        principal subject's principal.
     * @return true if CAPTCHA required otherwise false.
     */
    public boolean isCaptchaRequired(Parameters config, Principal principal) throws Exception;
    
    /**
     * Return Captcha API version from config
     * 
     * @param parameters component or application configuration.
     * @return CaptchaApiVersion.
     */
    public CaptchaApiVersion getApiVersion(Parameters config);
}
