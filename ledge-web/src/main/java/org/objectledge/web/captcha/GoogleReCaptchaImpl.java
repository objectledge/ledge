package org.objectledge.web.captcha;

import java.util.Locale;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleReCaptchaImpl
    extends ReCaptchaImpl
    implements ReCaptcha
{
    public static final String HTTPS_SERVER = "https://www.google.com/recaptcha/";
    
    public static final String CAPTHA_API_PATH = "api.js";
    
    public static final String CAPTHA_VERIFY_PATH = "api/siteverify";
    
    public static final String PROPERTY_LOCALE = "hl";

    public static final String PROPERTY_ONLOAD = "onload";

    public static final String PROPERTY_RENDER = "render";
    
    

    private String privateKey = "";

    private String publicKey = "";

    private String recaptchaServer = HTTP_SERVER;

    public String createRecaptchaHtml(Locale locale, String onLoad, String render)
    {
        Properties options = new Properties();
        if(locale != null)
        {
            options.setProperty(PROPERTY_LOCALE, locale.getLanguage());
        }
        if(onLoad != null && !onLoad.isEmpty())
        {
            options.setProperty(PROPERTY_ONLOAD, onLoad);
        }
        if("explicit".equals(render) || "onload".equals(render))
        {
            options.setProperty(PROPERTY_RENDER, render);
        }
        return createRecaptchaHtml(null, options);
    }

    public String createRecaptchaHtml(String errorMessage, Properties options)
    {
        String message = "";
        String recaptcha_opts = "";
        String script_opts = "";
        if(options == null)
        {
            options = new Properties();
        }
        options.setProperty("data-sitekey", publicKey);
        for(Object key : options.keySet())
        {
            if(key.toString().startsWith("data-"))
            {
                recaptcha_opts += key.toString() + "='" + options.get(key).toString() + "' ";
            }
            if(PROPERTY_RENDER.equals(key.toString()) || PROPERTY_LOCALE.equals(key.toString())
                || PROPERTY_ONLOAD.equals(key.toString()))
            {
                script_opts += script_opts.isEmpty() ? "?" : "&";
                script_opts += key.toString() + "=" + options.get(key).toString();
            }
        }
        message += "<div class='g-recaptcha' " + recaptcha_opts + "></div>\r\n";
        message += "<script src='" + recaptchaServer + CAPTHA_API_PATH + script_opts
            + "' async defer></script>";
        return message;
    }

    @Override
    public ReCaptchaResponse checkAnswer(String remoteAddr, String challenge, String response)
    {
        try
        {
            Client client = ClientBuilder.newClient();
            Form form = new Form().param("secret", privateKey).param("response", response)
                .param("remoteip", remoteAddr);
            Response httpResponse = client.target(recaptchaServer + CAPTHA_VERIFY_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));
            String result = httpResponse.readEntity(String.class);
            if(httpResponse.getStatus() == 200)
            {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree = mapper.readTree(result);
                boolean valid = tree.has("success") && tree.get("success").asBoolean();
                String errorMessage = tree.has("error-codes") && tree.get("error-codes").isArray() ? tree
                    .get(0).asText() : null;
                return new GoogleReCaptchaResponse(valid, errorMessage);
            }
            else
            {
                return new GoogleReCaptchaResponse(false, httpResponse.getStatusInfo()
                    .getReasonPhrase());
            }
        }
        catch(Exception e)
        {
            return new GoogleReCaptchaResponse(false, e.getMessage());
        }
    }

    @Override
    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }

    @Override
    public void setPublicKey(String publicKey)
    {
        this.publicKey = publicKey;
    }

    @Override
    public void setRecaptchaServer(String recaptchaServer)
    {
        this.recaptchaServer = recaptchaServer;
    }
   
    private class GoogleReCaptchaResponse
        extends ReCaptchaResponse
    {
        protected GoogleReCaptchaResponse(boolean valid, String errorMessage)
        {
            super(valid, errorMessage);
        }
    }
}
