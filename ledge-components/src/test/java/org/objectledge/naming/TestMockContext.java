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

package org.objectledge.naming;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import com.mockobjects.naming.MockContext;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class TestMockContext extends MockContext
    implements DirContext
{
    public Object lookup(String name)
        throws NamingException
    {
        return this;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void rebind(Name name, Object obj, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Object obj, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(Name name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public Attributes getAttributes(String name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(Name name, int modOp, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(String name, int modOp, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Name name, Object obj, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public void bind(String name, Object obj, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchema(Name name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchema(String name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchemaClassDefinition(Name name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public DirContext getSchemaClassDefinition(String name) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(Name name, Attributes matchingAttributes, 
        String[] attributesToReturn) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(Name name, Attributes matchingAttributes) 
        throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(Name name, String filterExpr, Object[] filterArgs, 
        SearchControls cons) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(Name name, String filter, SearchControls cons) 
        throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(String name, Attributes matchingAttributes, 
        String[] attributesToReturn) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(String name, Attributes matchingAttributes) 
        throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(String name, String filterExpr, Object[] filterArgs,
        SearchControls cons) throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration search(String name, String filter, SearchControls cons) 
        throws NamingException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("not implemented yet");
    }

}
