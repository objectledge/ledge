package org.objectledge.web.captcha;

import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.i18n.I18n;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class ReCaptchaCaptchaServiceImpl
    implements CaptchaService
{        
    private static final String PARAMETER_CHALLENGE = "recaptcha_challenge_field";

    private static final String PARAMETER_RESPONSE = "recaptcha_response_field";
    
    private static final String CAPTCHA_CACHE = "captcha_cache";

    private static final String I18n_PREFIX = "captcha";
    
    private final Logger log;

    private final ReCaptchaImpl reCaptcha;

    private final Map<String, String> defaultOptions = new HashMap<String, String>();

    private final String errorMessage;
    
    private final String recaptchaServer;
    
    private final boolean includeNoscript;
    
    private final String publicKey;
    
    private final long cacheTimeLimit;
    
    private final long cacheHitLimit;

    private final I18n i18n;
    
    private final UserManager userManager;

    public ReCaptchaCaptchaServiceImpl(I18n i18n, Configuration config, Logger log, UserManager userManager)
        throws ConfigurationException
    {
        this.i18n = i18n;
        this.log = log;
        this.reCaptcha = new ReCaptchaImpl();
        publicKey = config.getChild("publicKey").getValue();
        reCaptcha.setPublicKey(publicKey);
        reCaptcha.setPrivateKey(config.getChild("privateKey").getValue());
        recaptchaServer = config.getChild("recapthaServer", true).getValue("http://api.recaptcha.net");
        if(recaptchaServer != null)
        {
            reCaptcha.setRecaptchaServer(recaptchaServer);
        }
        includeNoscript = config.getChild("includeNoScript", true).getValueAsBoolean(false);
        reCaptcha.setIncludeNoscript(includeNoscript);
        this.errorMessage = config.getChild("errorMessage", true).getValue(null);
        Configuration options = config.getChild("options", true);
        for(Configuration option : options.getChildren())
        {
            defaultOptions.put(option.getAttribute("name"), option.getValue().trim());
        }
        cacheTimeLimit = config.getChild("cacheValidity").getChild("timeLimit").getValueAsLong(60000);
        cacheHitLimit = config.getChild("cacheValidity").getChild("hitLimit").getValueAsLong(2);
        this.userManager = userManager;
    }

    @Override
    public String createCaptchaWidget(Locale locale, Map<String, String> options)
    {
        Properties properties = new Properties();
        for(String option : defaultOptions.keySet())
        {
            if(!options.containsKey(option))
            {
                properties.setProperty(option, defaultOptions.get(option));
            }
        }
        for(String option : options.keySet())
        {
            properties.setProperty(option, options.get(option));
        }

        properties.put("custom_translations", getTranslations(locale));
        
        return createRecaptchaHtml(errorMessage, properties);
    }

    @Override
    public boolean checkCaptcha(String remoteAddr, String challenge, String response)
    {
        ReCaptchaResponse result = reCaptcha.checkAnswer(remoteAddr, challenge, response);
       
        if(!result.isValid() && result.getErrorMessage().equals("incorrect-captcha-sol"))
        {
            log.error("recaptcha verification failed: " + result.getErrorMessage());
        }
        return result.isValid();
    }

    @Override
    public boolean checkCaptcha(HttpContext httpContext, RequestParameters parameters)
    {
        String remoteAddr = httpContext.getRequest().getRemoteAddr();
        String challenge = parameters.get(PARAMETER_CHALLENGE, "");
        String response = parameters.get(PARAMETER_RESPONSE, "");
        
        Map<CaptchaCacheKey, CaptchaCacheValue> captchaCacheMap = (HashMap<CaptchaCacheKey, CaptchaCacheValue>)httpContext
            .getSessionAttribute(CAPTCHA_CACHE);

        CaptchaCacheKey captchaCacheKey = new CaptchaCacheKey(remoteAddr, challenge, response);
        CaptchaCacheValue captchaCacheValue = null;

        if(captchaCacheMap != null)
        {
            if(captchaCacheMap.containsKey(captchaCacheKey))
            {
                captchaCacheValue = captchaCacheMap.get(captchaCacheKey);
                long cacheValidityEnd = (new Date()).getTime() + this.cacheTimeLimit;
                if(captchaCacheValue.getTimestamp() > cacheValidityEnd
                    && captchaCacheValue.getCounter() > this.cacheHitLimit)
                {
                    captchaCacheValue = new CaptchaCacheValue(checkCaptcha(remoteAddr, challenge,
                        response));
                    captchaCacheMap.put(captchaCacheKey, captchaCacheValue);
                    httpContext.setSessionAttribute(CAPTCHA_CACHE, captchaCacheMap);
                }
            }
            else
            {
                captchaCacheValue = new CaptchaCacheValue(checkCaptcha(remoteAddr, challenge,
                    response));
                captchaCacheMap.put(captchaCacheKey, captchaCacheValue);
                httpContext.setSessionAttribute(CAPTCHA_CACHE, captchaCacheMap);
            }
        }
        else
        {

            captchaCacheMap = new HashMap<CaptchaCacheKey, CaptchaCacheValue>();
            captchaCacheValue = new CaptchaCacheValue(checkCaptcha(remoteAddr, challenge, response));

            captchaCacheMap.put(captchaCacheKey, captchaCacheValue);
            httpContext.setSessionAttribute(CAPTCHA_CACHE, captchaCacheMap);
        }

        return captchaCacheValue.getValue();
    }
    
    /**
     * Verify if CAPTCHA required by the principal.
     * 
     * @param parameters component or application configuration.
     *        principal subject's principal.
     * @return true if CAPTCHA required otherwise false.
     */
    public boolean isCaptchaRequired(Parameters config, Principal principal)
    throws Exception
    {
        if(config != null)
        {
            String captcha_verification = config.get("captcha_verification", CAPTCHA_DISABLED);
            Principal anonymous = userManager.getAnonymousAccount();

            if(CAPTCHA_FOR_EVERYONE.equals(captcha_verification))
            {
                return true;
            }
            else if(CAPTCHA_FOR_ANONYMOUS.equals(captcha_verification)
                && anonymous.equals(principal))
            {
                return true;
            }
        }
        return false; 
    }
    
    /**
     * Produces javascript array with the RecaptchaOptions encoded.
     * 
     * @param properties
     * @return
     */
    private String fetchJSOptions(Properties properties) {

        if (properties == null || properties.size() == 0) {
            return "";
        }

        StringBuffer jsOptions = new StringBuffer();
        jsOptions.append("<script type=\"text/javascript\">\r\n" + 
            "var RecaptchaOptions = ");
            
        appendDictionary(jsOptions, properties);
        
        jsOptions.append(";\r\n</script>\r\n");

        return jsOptions.toString();
    } 
    
    /**
     * Appends javascript dictionary representation of a Properties object to given StringBuffer 
     * 
     * @param jsOptions the target StringBuffer object.
     * @param properties a Properties object.
     */
    private void appendDictionary(StringBuffer jsOptions, Properties properties)
    {
        jsOptions.append("\r\n{");
        for (Enumeration e = properties.keys(); e.hasMoreElements(); ) {
            Object property = e.nextElement();
            jsOptions.append((String)property).append(": ");
            
            Object value = properties.get(property);
            if(value instanceof Properties) {
                appendDictionary(jsOptions, (Properties)value);
            } else {
                jsOptions.append("'").append(value).append("'");
            }
            
            if (e.hasMoreElements()) {
                jsOptions.append(",\r\n");
            }
        }
        jsOptions.append("}");
    }
    
    private String createRecaptchaHtml(String errorMessage, Properties options) {

        String errorPart = (errorMessage == null ? "" : "&amp;error=" + URLEncoder.encode(errorMessage));

        String message = fetchJSOptions(options);

        message += "<script type=\"text/javascript\" src=\"" + recaptchaServer + "/challenge?k=" + publicKey + errorPart + "\"></script>\r\n";

        if (includeNoscript) {
            String noscript = "<noscript>\r\n" + 
                    "   <iframe src=\""+recaptchaServer+"/noscript?k="+publicKey + errorPart + "\" height=\"300\" width=\"500\" frameborder=\"0\"></iframe><br>\r\n" + 
                    "   <textarea name=\"recaptcha_challenge_field\" rows=\"3\" cols=\"40\"></textarea>\r\n" + 
                    "   <input type=\"hidden\" name=\"recaptcha_response_field\" value=\"manual_challenge\">\r\n" + 
                    "</noscript>";
            message += noscript;
        }
        
        return message;
    }
    
    private Properties getTranslations(Locale locale)
    {
        Locale actualLocale = locale != null ? locale : i18n.getDefaultLocale();
        Properties translations = new Properties();
        for(String key : i18n.getKeys(actualLocale))
        {
            if(key.startsWith(I18n_PREFIX))
            {
                translations.setProperty(key.substring(I18n_PREFIX.length() + 1), i18n.get(
                    actualLocale, key));
            }
        }
        return translations;
    }
}
