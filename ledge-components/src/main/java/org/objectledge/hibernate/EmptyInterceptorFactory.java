// 
// Copyright (c) 2006, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.hibernate;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;

/**
 * The empty interceptor factory. It is used to configure 
 * {@link HibernateSessionFactory} to not use any interceptors.
 * 
 * @author <a href="mailto:mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: EmptyInterceptorFactory.java,v 1.1 2009-03-26 14:26:17 mgolebsk Exp $
 */
public class EmptyInterceptorFactory
    implements InterceptorFactory
{
    /**
     * Creates <code>EmptyInterceptorFactory</code> objects.
     */
    public EmptyInterceptorFactory()
    {
    }

    /**
     * Creates the interceptor.
     * 
     * @param sessionFactory the hibernate session factory. 
     * @return the newly instantiated picofier  object.
     */
    @SuppressWarnings("unused")
    public Interceptor createInterceptor(final SessionFactory sessionFactory)
    {
        return null;
    }
}
