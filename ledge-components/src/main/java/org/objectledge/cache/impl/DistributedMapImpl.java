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

package org.objectledge.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.objectledge.cache.NoLongerValidException;
import org.objectledge.cache.Refreshable;
import org.objectledge.cache.spi.CachingSPI;
import org.objectledge.cache.spi.ConfigurableMap;
import org.objectledge.cache.spi.DistributedMap;
import org.objectledge.notification.Notification;
import org.objectledge.notification.NotificationReceiver;

/**
 * An implementation of {@link DistributedMap} using the {@link Notification}.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DistributedMapImpl.java,v 1.1 2004-02-12 11:41:26 pablo Exp $
 */
public class DistributedMapImpl
    extends DelegateMap
    implements DistributedMap, ConfigurableMap,
               NotificationReceiver
{
    // constants /////////////////////////////////////////////////////////////

    /** The channel base name. */
    public static final String CHANNEL_NAME = "ledge.cache:1.0";

    // instance variables ////////////////////////////////////////////////////

    /** The notification */
    private Notification notification;
    
    /** The actual channel name. */
    private String channel;

    // Initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a DistributedMapImpl.
     */
    public DistributedMapImpl()
    {
        super();
    }

    // ConfigurableMap interface /////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void configure(CachingSPI caching, String name, String config)
    {
        attach(caching.getNotification(), name);
    }    

    // DistributedMap SPI inteface ///////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void attach(Notification notification, String name)
    {
        this.notification = notification;
        channel = CHANNEL_NAME+"/"+name;
        notification.addReceiver(channel, this);
    }   

    /**
     * {@inheritDoc}
     */
    public void detach()
    {
        if(notification != null)
        {
            notification.removeReceiver(channel, this);
            notification = null;
        }
    }

    // DistributedMap interface //////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void updated(Object key)
    {
        notify(true, key);
    }

    // Map interface /////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public Object put(Object key, Object value)
    {
        notify(false, key);
        return delegate.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key)
    {
        notify(false, key);
        return delegate.remove(key);
    }

    // NotificationReceiver interface ////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void receive(String channel, byte[] message)
    {
        boolean update;
        Object key;
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(message);
            ObjectInputStream is = new ObjectInputStream(bais);
            update = is.readBoolean();
            key = is.readObject();
        }
        catch(Exception e)
        {
            throw new RuntimeException("failed to decode message", e);
        }
        if(update)
        {
            Object value = delegate.get(key);
            if(value != null)
            {
                if(value instanceof Refreshable)
                {
                    try
                    {
                        ((Refreshable)value).refresh();
                        return;
                    }
                    catch(NoLongerValidException e)
                    {
                        delegate.remove(key);
                    }
                }
            }
        }
        // not update || non refreshable
        delegate.remove(key);
    }

    // implementation ////////////////////////////////////////////////////////
    
    private void notify(boolean update, Object key)
    {
        byte[] message;
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeBoolean(update);
            os.writeObject(key);
            os.flush();
            message = baos.toByteArray();
        }
        catch(IOException e)
        {
            throw new RuntimeException("failed to encode requeuest", e);
        }
        notification.sendNotification(channel, message, false);
    }
}
