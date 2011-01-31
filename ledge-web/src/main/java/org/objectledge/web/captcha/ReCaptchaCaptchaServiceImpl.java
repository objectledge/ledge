package org.objectledge.web.captcha;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class ReCaptchaCaptchaServiceImpl
    implements CaptchaService
{
    private static final String PARAMETER_CHALLENGE = "recaptcha_challenge_field";
    
    private static final String PARAMETER_RESPONSE = "recaptcha_response_field";
    
    private final Logger log;

    private final ReCaptchaImpl reCaptcha;

    private final String defaultTheme;

    private final String errorMessage;

    public ReCaptchaCaptchaServiceImpl(Configuration config, Logger log)
        throws ConfigurationException
    {
        this.log = log;
        this.reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPublicKey(config.getChild("publicKey").getValue());
        reCaptcha.setPrivateKey(config.getChild("privateKey").getValue());
        String recaptchaServer = config.getChild("recapthaServer", true).getValue(null);
        if(recaptchaServer != null)
        {
            reCaptcha.setRecaptchaServer(recaptchaServer);
        }
        boolean includeNoscript = config.getChild("includeNoScript", true).getValueAsBoolean(false);
        reCaptcha.setIncludeNoscript(includeNoscript);
        this.defaultTheme = config.getChild("defaultTheme", true).getValue(null);
        this.errorMessage = config.getChild("errorMessage", true).getValue(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String createCaptchaWidget()
    {
        return createCaptchaWidget(Collections.EMPTY_MAP);
    }

    @Override
    public String createCaptchaWidget(Map<String, String> options)
    {
        Properties properties = new Properties();
        for(String option : options.keySet())
        {
            properties.setProperty(option, options.get(option));
        }
        if(!options.containsKey(ReCaptchaImpl.PROPERTY_THEME) && defaultTheme != null)
        {
            properties.put(ReCaptchaImpl.PROPERTY_THEME, defaultTheme);
        }
        return reCaptcha.createRecaptchaHtml(errorMessage, properties);
    }

    @Override
    public boolean checkCaptcha(String remoteAddr, String challenge, String response)
    {
        ReCaptchaResponse result = reCaptcha.checkAnswer(remoteAddr, challenge, response);
        if(!result.isValid() && result.getErrorMessage().equals("incorrect-captcha-sol"))
        {
            log.error("recaptcha verification failed: "+result.getErrorMessage());
        }
        return result.isValid();
    }

    @Override
    public boolean checkCaptcha(HttpContext httpContext, RequestParameters parameters)
    {
        String remoteAddr = httpContext.getRequest().getRemoteAddr();
        String challenge = parameters.get(PARAMETER_CHALLENGE, "");
        String response = parameters.get(PARAMETER_RESPONSE, "");
        return checkCaptcha(remoteAddr, challenge, response);
    }
}
