// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
package org.objectledge.test;

import java.lang.reflect.Array;
import java.util.List;

import org.jmock.core.Invocation;
import org.jmock.core.SelfDescribing;
import org.jmock.core.Stub;

/**
 * A Stub for returing values of a predefined list as an array. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ReturnListValuesAsArray.java,v 1.2 2004-12-22 08:35:04 rafal Exp $
 */
public class ReturnListValuesAsArray<T>
	implements SelfDescribing, Stub
{
    private List<T> list;
    
    /**
     * Creates new ReturnListValuesAsArray Stub instance.
     * 
     * @param list the list to be returned.
     */
    public ReturnListValuesAsArray(List<T> list)
    {
        this.list = list;
    }
    
    /**
     * {@inheritDoc}
     */
    public StringBuffer describeTo(StringBuffer buff)
    {
        return buff.append("getListValuesAsArray("+list+")");
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Invocation invocation) throws Throwable
    {
        Class<?> type = invocation.invokedMethod.getReturnType().getComponentType();
        if(type == null)
        {
            throw new IllegalStateException("not an array returing method called");
        }
        Object result = Array.newInstance(type, list.size());
        list.toArray((Object[])result);
        return result;
    }
}