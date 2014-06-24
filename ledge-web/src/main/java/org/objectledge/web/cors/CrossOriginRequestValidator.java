package org.objectledge.web.cors;

/**
 * Enables Cross Origin Resource Sharing. In general, Ledge web application is not able to determine
 * all valid HTTP URIs through which it is available, due to virtual hosting and DNS configuration,
 * possibly including wildcards. To provide CORS authentication application needs to verify that
 * AJAX requests are coming from the application page loaded through one of the valid URIs. This
 * service allows delegating this verification to configuration files managed by the administrator
 * and/or application components that have knowledge of valid URIs.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface CrossOriginRequestValidator
{
    /**
     * Returns true if the page hosted at origin URI address is allowed to make requests to this
     * application.
     * 
     * @param originUri URI sent in Origin HTTP header
     * @return {@code true} if the request should be allowed.
     */
    boolean isAllowed(String originUri);
}
