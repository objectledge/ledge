package org.objectledge.authentication.identity;

import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

public class TransientIdentityStore
    extends BaseIdentityStore
{
    private Config config;

    private Map<String, Entry> association = new ConcurrentHashMap<>();

    public TransientIdentityStore(Config config, ThreadPool threadPool)
    {
        super(config.getTokenLength());
        this.config = config;
        threadPool.runDaemon(new ExpiryTask());
    }

    public TransientIdentityStore(Configuration config, ThreadPool threadPool)
        throws ConfigurationException
    {
        this(new Config(config), threadPool);
    }

    @Override
    public String save(Principal principal, int validityTime)
    {
        String token;
        synchronized(association)
        {
            do
            {
                token = newToken();
            }
            while(!association.containsKey(token));
        }
        int validity = validityTime;
        if(validity == 0)
        {
            validity = config.getMaximumValidityTime();
        }
        if(validity < 0)
        {
            validity = config.getDefaultValidityTime();
        }
        validity = Math.min(validity, config.getMaximumValidityTime());
        Entry entry = new Entry(principal, validity);
        association.put(token, entry);
        return token;
    }

    @Override
    public Principal load(String identity)
    {
        Entry entry = association.get(identity);
        return entry != null ? entry.getPrincipal() : null;
    }

    @Override
    public void remove(String identity)
    {
        association.remove(identity);
    }

    @Override
    public void remove(Principal principal)
    {
        Iterator<Entry> i = association.values().iterator();
        while(i.hasNext())
        {
            Entry entry = i.next();
            if(entry.getPrincipal().equals(principal))
            {
                i.remove();
            }
        }
    }

    public static class Config
    {
        private final int tokenLength;

        private final int defaultValidityTime;

        private final int maximumValidityTime;

        private final int evictionInterval;

        public Config(int tokenLength, int defaultValidityTime, int maximumValidityTime,
            int evictionInterval)
        {
            this.tokenLength = tokenLength;
            this.defaultValidityTime = defaultValidityTime;
            this.maximumValidityTime = maximumValidityTime;
            this.evictionInterval = evictionInterval;
        }

        public Config(Configuration config)
            throws ConfigurationException
        {
            this.tokenLength = config.getChild("tokenLength").getValueAsInteger();
            this.defaultValidityTime = config.getChild("defaultValidityTime").getValueAsInteger();
            this.maximumValidityTime = config.getChild("maximumValidityTime").getValueAsInteger();
            this.evictionInterval = config.getChild("evictionInterval").getValueAsInteger();
        }

        public int getTokenLength()
        {
            return tokenLength;
        }

        public int getDefaultValidityTime()
        {
            return defaultValidityTime;
        }

        public int getMaximumValidityTime()
        {
            return maximumValidityTime;
        }

        public int getEvictionInterval()
        {
            return evictionInterval;
        }
    }

    private static class Entry
    {
        private final Principal principal;

        private long expires;

        public Entry(Principal principal, int validityTime)
        {
            this.principal = principal;
            this.expires = System.currentTimeMillis() + validityTime * 1000;
        }

        public Principal getPrincipal()
        {
            return principal;
        }

        public boolean isValid()
        {
            return this.expires > System.currentTimeMillis();
        }
    }

    private class ExpiryTask
        extends Task
    {
        @Override
        public String getName()
        {
            return "Identity store expiry";
        }

        @Override
        public void process(Context context)
            throws ProcessingException
        {
            while(!Thread.interrupted())
            {
                Iterator<Entry> i = association.values().iterator();
                while(i.hasNext())
                {
                    Entry entry = i.next();
                    if(!entry.isValid())
                    {
                        i.remove();
                    }
                }
                try
                {
                    Thread.sleep(config.getEvictionInterval() * 1000);
                }
                catch(InterruptedException e)
                {
                    return;
                }
            }
        }
    }
}
