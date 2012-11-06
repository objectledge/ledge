package org.objectledge.authentication.sso;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.net.CIDRBlock;
import org.objectledge.net.IPAddressUtil;

public class ServerApiRestrictions
{
    private final Logger log;

    private final List<ApiRestriction> apiRestrictions;

    private final Base64 base64 = new Base64();

    public enum Status
    {
        AUTHORIZED, UNAUTHORIZED, UNDEFINED
    }

    private enum RequestMethod
    {
        POST, GET, PUT, DELETE, HEAD, OPTIONS, ANY
    }

    private final Map<String, RequestMethod> requestMethodMap = new HashMap<String, RequestMethod>()
        {
            {
                put("GET", RequestMethod.GET);
                put("POST", RequestMethod.POST);
                put("PUT", RequestMethod.PUT);
                put("DELETE", RequestMethod.DELETE);
                put("OPTIONS", RequestMethod.OPTIONS);
                put("HEAD", RequestMethod.HEAD);
            }
        };

    private static class ApiRestriction
    {
        private final String path;

        private final Set<RequestMethod> methods;

        private final boolean enabled;

        private final boolean requireSsl;

        private final String userName;

        private final String secret;

        private final Set<CIDRBlock> addressRanges;

        public ApiRestriction(String path, Set<RequestMethod> methods, boolean enabled,
            boolean requireSsl, String userName, String secret, Set<CIDRBlock> addressRanges)
        {
            this.path = path;
            this.methods = methods;
            this.enabled = enabled;
            this.requireSsl = requireSsl;
            this.userName = userName;
            this.secret = secret;
            this.addressRanges = addressRanges;
        }

        public String getPath()
        {
            return path;
        }

        public Set<RequestMethod> getMethods()
        {
            return methods;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public boolean isRequireSsl()
        {
            return requireSsl;
        }

        public String getUserName()
        {
            return userName;
        }

        public String getSecret()
        {
            return secret;
        }

        public Set<CIDRBlock> getAddressRanges()
        {
            return addressRanges;
        }
    }

    public ServerApiRestrictions(Configuration config, Logger log)
        throws ConfigurationException
    {
        this.log = log;
        this.apiRestrictions = new ArrayList<ApiRestriction>();

        Configuration[] restrictsConfig = config.getChildren("restrict");

        for(Configuration restrictConfig : restrictsConfig)
        {
            String path = restrictConfig.getAttribute("path", null);
            String methods = restrictConfig.getAttribute("methods", null);
            boolean enabled = restrictConfig.getAttributeAsBoolean("enabled", false);
            boolean requireSsl = restrictConfig.getAttributeAsBoolean("requireSsl", false);
            String userName = restrictConfig.getChild("httpBasic").getAttribute("user", null);
            String secret = restrictConfig.getChild("httpBasic").getAttribute("secret", null);
            Configuration authorizedClientsConfig = restrictConfig.getChild("authorizedClients",
                false);
            Set<CIDRBlock> addressRanges;

            if(authorizedClientsConfig == null)
            {
                addressRanges = null;
            }
            else
            {
                addressRanges = new HashSet<CIDRBlock>();
                for(Configuration addressRangeConfig : authorizedClientsConfig.getChildren())
                {
                    String[] rangeDef = addressRangeConfig.getValue().split("/");
                    if(rangeDef.length != 2)
                    {
                        throw new ConfigurationException("invalid CIDR block specification "
                            + addressRangeConfig.getValue(), addressRangeConfig.getPath(),
                            addressRangeConfig.getLocation());
                    }
                    InetAddress networkPrefix;
                    try
                    {
                        networkPrefix = IPAddressUtil.byAddress(rangeDef[0]);
                    }
                    catch(IllegalArgumentException e)
                    {
                        throw new ConfigurationException("invalid IP address " + rangeDef[0],
                            addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                    }
                    catch(UnknownHostException e)
                    {
                        throw new ConfigurationException("invalid IP address " + rangeDef[0],
                            addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                    }
                    int prefixLength;
                    try
                    {
                        prefixLength = Integer.parseInt(rangeDef[1]);
                    }
                    catch(NumberFormatException e)
                    {
                        throw new ConfigurationException("invalid prefix length",
                            addressRangeConfig.getPath(), addressRangeConfig.getLocation(), e);
                    }
                    try
                    {
                        addressRanges.add(new CIDRBlock(networkPrefix, prefixLength));
                    }
                    catch(IllegalArgumentException e)
                    {
                        throw new ConfigurationException("invalid CIDR block specification "
                            + addressRangeConfig.getValue(), addressRangeConfig.getPath(),
                            addressRangeConfig.getLocation(), e);
                    }
                }
            }

            Set<RequestMethod> requestMethods = new HashSet<RequestMethod>();
            if(methods != null)
            {
                for(String method : methods.split(","))
                {
                    if(requestMethodMap.containsKey(method.toUpperCase()))
                    {
                        requestMethods.add(requestMethodMap.get(method));
                    }
                }
            }
            else
            {
                requestMethods.add(RequestMethod.ANY);
            }
            apiRestrictions.add(new ApiRestriction(path, requestMethods, enabled, requireSsl,
                userName, secret, addressRanges));
        }
    }

    public Status validateApiRequest(String path, String method, String httpAuthorizationHeader,
        String remoteAddr, boolean secure)
    {
        String userName = null;
        String secret = null;
        if(httpAuthorizationHeader != null)
        {
            String[] authorizationParts = httpAuthorizationHeader.split(" ", 2);
            if("Basic".equals(authorizationParts[0]) && authorizationParts[1] != null)
            {
                String basicAuthorization = new String(base64.decode(authorizationParts[1]
                    .getBytes()));
                String[] principal = basicAuthorization.split(":", 2);
                userName = principal[0];
                secret = principal[1];
            }
        }
        Status status = Status.UNDEFINED;
        for(ApiRestriction apiRestriction : apiRestrictions)
        {
            status = validateApiRequest(apiRestriction, userName, secret, remoteAddr, secure, path,
                requestMethodMap.get(method));
            if(Status.UNDEFINED != status)
                break;
        }
        return status;
    }

    public boolean validateApiRequest(String userName, String secret, String remoteAddr,
        boolean secure)
    {
        if(apiRestrictions.size() > 1)
        {
            log.warn("DECLINED API call ambiguous config parameters");
            return false;
        }
        if(apiRestrictions.size() == 0)
        {
            log.debug("ACCEPTED API call from " + remoteAddr);
            return true;
        }
        return Status.AUTHORIZED == validateApiRequest(apiRestrictions.get(0), userName, secret,
            remoteAddr, secure, null, RequestMethod.ANY);
    }

    public Status validateApiRequest(ApiRestriction apiRestriction, String userName, String secret,
        String remoteAddr, boolean secure, String path, RequestMethod method)
    {
        if(apiRestriction.getPath() != null
            && !(path != null && path.matches(apiRestriction.getPath())))
        {
            return Status.UNDEFINED;
        }
        if(!apiRestriction.getMethods().contains(method))
        {
            return Status.UNDEFINED;
        }

        String declineReason;
        if(apiRestriction.isEnabled())
        {
            if(secure || !apiRestriction.isRequireSsl())
            {
                if(apiRestriction.getUserName() == null
                    || apiRestriction.getUserName().equals(userName))
                {
                    if(apiRestriction.getSecret() == null
                        || apiRestriction.getSecret().equals(secret))
                    {
                        if(apiRestriction.addressRanges != null)
                        {
                            try
                            {
                                InetAddress remote = IPAddressUtil.byAddress(remoteAddr);
                                for(CIDRBlock addressRange : apiRestriction.addressRanges)
                                {
                                    try
                                    {
                                        if(addressRange.contains(remote))
                                        {
                                            log.debug("ACCEPTED API call from " + remoteAddr);
                                            return Status.AUTHORIZED;
                                        }
                                    }
                                    catch(IllegalArgumentException e)
                                    {
                                        declineReason = "failed to match IP address to CIDR block "
                                            + addressRange;
                                        log.error(declineReason, e);
                                    }
                                }
                                declineReason = "IP does not match any of the authorized ranges";
                            }
                            catch(IllegalArgumentException e)
                            {
                                declineReason = "failed to parse IP address";
                                log.error(declineReason, e);
                            }
                            catch(UnknownHostException e)
                            {
                                declineReason = "failed to parse IP address";
                                log.error(declineReason, e);
                            }
                        }
                        else
                        {
                            log.debug("ACCEPTED API call from " + remoteAddr);
                            return Status.AUTHORIZED;
                        }
                    }
                    else if(secret == null)
                    {
                        declineReason = "no secret provided";
                    }
                    else
                    {
                        declineReason = "invalid secret provided";
                    }
                }
                else
                {
                    declineReason = "invalid userName provided";
                }
            }
            else
            {
                declineReason = "not using https";
            }
        }
        else
        {
            declineReason = "API access disabled";
        }
        log.warn("DECLINED API call from " + remoteAddr + " " + declineReason);
        return Status.UNAUTHORIZED;
    }
}
