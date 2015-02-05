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
    public static final String HTTP_SERVER = "";

    public static final String PROPERTY_LOCALE = "hl";

    public static final String PROPERTY_ONLOAD = "onload";

    public static final String PROPERTY_RENDER = "render";

    private String privateKey = "https://www.google.com/recaptcha/api/siteverify";

    private String publicKey = "";

    private String recaptchaServer = HTTP_SERVER;

    private boolean includeNoscript = false;

    public String createRecaptchaHtml(Locale locale, String onLoad, String render)
    {
        Properties options = new Properties();
        if(locale != null)
        {
            options.setProperty(PROPERTY_LOCALE, locale.getLanguage());
        }
        if(onLoad != null && !onLoad.isEmpty())
        {
            options.setProperty(PROPERTY_ONLOAD, locale.getLanguage());
        }
        if("explicit".equals(render) || "onload".equals(render))
        {
            options.setProperty(PROPERTY_RENDER, locale.getLanguage());
        }
        return createRecaptchaHtml(null, options);
    }

    @Override
    public String createRecaptchaHtml(String errorMessage, Properties options)
    {
        String message = "";
        if(includeNoscript)
        {
            message += "<div class='g-recaptcha' data-sitekey='" + this.publicKey + "'></div>\r\n";
        }
        message += "<script src='https://www.google.com/recaptcha/api.js' async defer></script>";
        return message;
    }

    @Override
    public ReCaptchaResponse checkAnswer(String remoteAddr, String challenge, String response)
    {
        try
        {
            Client client = ClientBuilder.newClient();
            Form form = new Form().param("secret", this.privateKey).param("response", response)
                .param("remoteip", remoteAddr);
            Response httpResponse = client.target(this.recaptchaServer)
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

    private class GoogleReCaptchaResponse
        extends ReCaptchaResponse
    {
        protected GoogleReCaptchaResponse(boolean valid, String errorMessage)
        {
            super(valid, errorMessage);
        }
    }
}
