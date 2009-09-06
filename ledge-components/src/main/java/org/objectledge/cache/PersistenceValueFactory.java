//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, 
//are permitted provided that the following conditions are met:
//
//* Redistributions of source code must retain the above copyright notice, 
//this list of conditions and the following disclaimer.
//* Redistributions in binary form must reproduce the above copyright notice, 
//this list of conditions and the following disclaimer in the documentation 
//and/or other materials provided with the distribution.
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//nor the names of its contributors may be used to endorse or promote products 
//derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
//POSSIBILITY OF SUCH DAMAGE.
//

package org.objectledge.cache;

import org.jcontainer.dna.Configuration;
import org.objectledge.cache.spi.CacheFactorySPI;
import org.objectledge.cache.spi.ConfigurableValueFactory;
import org.objectledge.database.persistence.Persistence;
import org.objectledge.database.persistence.PersistenceException;
import org.objectledge.database.persistence.Persistent;
import org.objectledge.database.persistence.PersistentFactory;

/**
 * An implementation of ValueFactory interface that uses the persistency.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PersistenceValueFactory.java,v 1.5 2005-02-10 17:49:17 rafal Exp $
 */
public class PersistenceValueFactory<K extends Number, V extends Persistent>
    implements ConfigurableValueFactory<K, V>
{
    /**
     * The persistent factory.
     */
    private PersistentFactory factory;

    /**
     * The persistence.
     */
    private Persistence persistence;

    /**
     * Initializes the factory.
     *
     * @param persistence the persistence.
     * @param cl the class of the values.
     */
    public void init(final Class<V> cl, Persistence persistence)
    {
        this.persistence = persistence;
        if(!Persistent.class.isAssignableFrom(cl))
        {
            throw new IllegalArgumentException(cl.getName()+" does not implement " +                                                "Persistent interface");
        }
        factory = new PersistentFactory()
            {
                public Persistent newInstance()
                    throws PersistenceException
                {
                    try
                    {
                        return cl.newInstance();
                    }
                    catch(Exception e)
                    {
                        throw new PersistenceException("failed to instantiate "+cl.getName());
                    }
                }
            };
    }

    /**
     * Produces a value that corresponds to a given key.
     *
     * <p>This metod expects that the key extends the
     * <code>java.lang.Number</code> interface.</p>
     * 
     * @param key the key.
     * @return the object.
     */
    public V getValue(K key)
    {
        if(key instanceof Number)
        {
            try
            {
                return (V)persistence.load(key.longValue(), factory);
            }
            catch(PersistenceException e)
            {
                throw new RuntimeException("failed to produce value", e);
            }
        }
        throw new IllegalArgumentException(key.getClass().getName()+
                                            " does not extend java.lang.Number");
    }

    /**
     * {@inheritDoc}
     */
    public void configure(CacheFactorySPI caching, String name, Configuration config)
    {
        Configuration[] parameters = config.getChildren("parameter");
        String vClass = null;
        for(int i = 0; i < parameters.length; i++)
        {
            if(parameters[i].getAttribute("name","").equals("valueClass"))
            {
                vClass = parameters[i].getAttribute("value","");
            }
        }
        if(vClass == null || vClass.length()==0)
        {
            throw new IllegalArgumentException("required parameter valueClass is missing");
        }
        Class<V> cl = null;
        try
        {
            cl = (Class<V>)Class.forName(vClass);
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("cannot instantiate the class"+e.getMessage());
        }
        init(cl, caching.getPersistence());
    }
}
