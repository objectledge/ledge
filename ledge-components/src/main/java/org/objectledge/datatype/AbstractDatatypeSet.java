// 
// Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.datatype;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AbstractDatatypeSet.java,v 1.1 2004-05-12 09:54:03 zwierzem Exp $
 */
public abstract class AbstractDatatypeSet implements DatatypeSet
{
	/** Name of this datatype set. */ 
	protected String name;
	
	/** Parent of this datatype set. */ 
	protected DatatypeSet parent;

	/**
	 * Creates a datatype set with a given name.
	 * 
	 * @param name name of the set.
	 */
    public AbstractDatatypeSet(String name)
    {
    	this.name = name;
    }

	// DatatypeSet API ----------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return name;
    }

	/**
	 * {@inheritDoc}
	 */
	public Datatype getDatatype(String name)
	{
		Datatype datatype = this.getDatatypeInternal(name);
		if(datatype == null && parent != null)
		{
			return parent.getDatatype(name); 
		}
		return datatype;
	}

	// NonPublic API -----------------------------------------------------------------------------

	DatatypeSet getParent()
	{
		return parent;
	}

    void setParent(DatatypeSet parent)
    {
		this.parent = parent;
    }

	// Override API ------------------------------------------------------------------------------

	/**
	 * Internal method for getting datatypes left for implmentation by subclasses.
	 * @param name name of the datatype
	 * @return found datatype or <code>null</code>.
	 */
	protected abstract Datatype getDatatypeInternal(String name);
}
