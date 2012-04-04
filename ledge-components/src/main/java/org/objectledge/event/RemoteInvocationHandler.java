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

package org.objectledge.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * An invocation handler that maps an interface to it's <code>Remote</code>
 * counterpart. 
 *
 * <p>The remote class must have methods that have the same name, parameter
 * types, return type and thrown exceptions as the methods defined in the
 * interface, except that the methods of the remote interface must throw
 * <code>RemoteException</code>.</p>  
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: RemoteInvocationHandler.java,v 1.2 2004-12-23 02:15:02 rafal Exp $
 */
public class RemoteInvocationHandler implements InvocationHandler
{
    /** The remote object. */
    private Remote remote;
    
    /** The method map. */
    private Map<Method, Method> methodMap = new HashMap<Method, Method>();

    /**
     * Constructs the invocation handler.
     *
     * @param iface the interface.
     * @param remote the remote object.
     * @throws IllegalArgumentException if the interface cannot be mapped on
     *        the remote object.
     */
    public RemoteInvocationHandler(Class<?> iface, Remote remote)
        throws IllegalArgumentException
    {
        if(!iface.isInterface())
        {
            throw new IllegalArgumentException(iface.getName()+" is not an interface");
        }
        Class<? extends Remote> remoteClass = remote.getClass();
        Method[] methods = iface.getMethods();
        for(int i=0; i<methods.length; i++)
        {
            try
            {
                Method remoteMethod = remoteClass.
                    getMethod(methods[i].getName(), methods[i].getParameterTypes());

                if(!methods[i].getReturnType().equals(remoteMethod.getReturnType()))
                {
                    throw new NoSuchMethodException("return type mismatch");
                }

                Class<?>[] remoteMethodExceptions = remoteMethod.getExceptionTypes();
                Class<?>[] ifaceMethodExceptions = methods[i].getExceptionTypes();
                loop: for(int j=0; j<remoteMethodExceptions.length; j++)
                {
                    if(!remoteMethodExceptions[j].equals(RemoteException.class))
                    {
                        for(int k=0; k<ifaceMethodExceptions.length; k++)
                        {
                            if(ifaceMethodExceptions[k].equals(remoteMethodExceptions[j]))
                            {
                                continue loop;
                            }
                        }
                        throw new NoSuchMethodException("thrown exceptions mismatch");
                    }
                }           
            }
            catch(NoSuchMethodException e)
            {
                throw new IllegalArgumentException(remoteClass.getName()+
                                                   " does not have method matching "+
                                                   methods[i].toString(), e);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        try
        {
            return methodMap.get(method).invoke(remote, args);
        }
        catch(VirtualMachineError t)
        {
            throw t;
        }
        catch(ThreadDeath t)
        {
            throw t;
        }
        catch(Throwable t)
        {
            if(t instanceof RemoteException)
            {
                Exception ex = new RuntimeException("Failed to invoke remote method");
                throw new InvocationTargetException(ex);
            }
            throw new InvocationTargetException(t);
        }
    }
}
