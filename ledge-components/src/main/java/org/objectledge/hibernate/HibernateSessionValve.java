// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

import org.hibernate.Session;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

/**
 * Pipeline processing valve that initialize hibernate session.
 *
 * @author <a href="mgolebsk@elka.pw.edu.pl">Marcin Golebski</a>
 * @version $Id: HibernateSessionValve.java,v 1.4 2006-05-04 13:25:04 zwierzem Exp $
 */
public class HibernateSessionValve 
    implements Valve
{
    private Context context;
	private HibernateSessionFactory hibernateSessionFactory;
	
	/**
	 * Constructor.
	 * 
     * @param userManager the user manager component.
	 */
	public HibernateSessionValve(Context context, HibernateSessionFactory hibernateSessionFactory)
	{
        this.context = context;
		this.hibernateSessionFactory = hibernateSessionFactory;
	}

    /**
     * Alternative way of accessing the hibenrate session. Allows no session initialisation
     * if it is not needed. For this to work, the valve should not be used.
     * @return
     */
    public Session getSession()
    {
        HibernateSessionContext hibernateSessionContext = context
            .getAttribute(HibernateSessionContext.class);
        if(hibernateSessionContext == null)
        {
            try
            {
                process(context);
            }
            catch(ProcessingException e)
            {
                throw new RuntimeException(e); // should not happen, push up anyway
            }
            hibernateSessionContext = context.getAttribute(HibernateSessionContext.class);
        }
        return hibernateSessionContext.getSession();
    }

    public void closeSession()
    {
        HibernateSessionContext hibernateSessionContext = context
            .getAttribute(HibernateSessionContext.class);
        if(hibernateSessionContext != null)
        {
            Session session = hibernateSessionContext.getSession();
            if(session != null)
            {
                session.close();
            }
            context.removeAttribute(HibernateSessionContext.class);
        }
    }
    
    /**
     * Run the pipeline valve - create session.
     * 
     * @param context the thread's processing context.
     * @throws ProcessingException if authentication failed.
     */
    public void process(Context context)
        throws ProcessingException
    {
        Session session = hibernateSessionFactory.openHibernateSession();
        HibernateSessionContext hibernateSessionContext = new HibernateSessionContext(session);
        context.setAttribute(HibernateSessionContext.class, hibernateSessionContext);
    }
}
