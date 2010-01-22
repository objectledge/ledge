package org.objectledge.authentication.jaas;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.ComponentInitializationError;
import org.picocontainer.MutablePicoContainer;

public class LoginConfigurationParameters
    implements javax.security.auth.login.Configuration.Parameters
{
    private final MutablePicoContainer container;

    private Map<String, AppConfigurationEntry[]> map;

    public LoginConfigurationParameters(MutablePicoContainer container, Configuration config)
        throws ConfigurationException
    {
        this.container = container;
        Configuration[] loginConfigs = config.getChildren("loginConfiguration");
        for (Configuration loginConfig : loginConfigs)
        {
            Configuration[] moduleConfigs = loginConfig.getChildren("loginModule");
            AppConfigurationEntry[] entries = new AppConfigurationEntry[moduleConfigs.length];
            map.put(loginConfig.getAttribute("name"), entries);
            int i = 0;
            for (Configuration moduleConfig : moduleConfigs)
            {
                Map<String, Object> options = new HashMap<String, Object>();
                entries[i++] = new AppConfigurationEntry(moduleConfig.getAttribute("class"),
                    getFlag(moduleConfig.getAttribute("flag"), moduleConfig.getLocation()), options);
                Configuration[] optionConfigs = moduleConfig.getChildren();
                for (Configuration optionConfig : optionConfigs)
                {
                    if("option".equals(optionConfig.getName()))
                    {
                        options.put(optionConfig.getAttribute("name"), optionConfig
                            .getAttribute("value"));
                    }
                    else if("component".equals(optionConfig.getName()))
                    {
                        String className = optionConfig.getAttribute("class");
                        options.put(className, getComponent(className));
                    }
                }
            }
        }
    }
    
    AppConfigurationEntry[] engineGetAppConfigurationEntry(String loginConfig)
    {
        return map.get(loginConfig);
    }

    private AppConfigurationEntry.LoginModuleControlFlag getFlag(String flag, String location)
    {
        if("REQUIRED".equals(flag))
        {
            return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
        }
        if("REQUISITE".equals(flag))
        {
            return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
        }
        if("SUFFICIENT".equals(flag))
        {
            return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
        }
        if("OPTIONAL".equals(flag))
        {
            return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
        }
        throw new IllegalArgumentException("invalid LoginModuleControlFlag " + flag + " at "
            + location);
    }

    private Object getComponent(String className)
    {
        Class<? > clazz;
        try
        {
            clazz = Class.forName(className);
            return container.getComponentInstance(clazz);
        }
        catch(ClassNotFoundException e)
        {
            throw new ComponentInitializationError(e);
        }
    }
}
