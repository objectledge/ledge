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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.jcontainer.dna.Logger;
import org.objectledge.datatype.xml.XMLSchemaDatatypeSetFactory;
import org.objectledge.xml.XMLGrammarCache;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DatatypeSetManager.java,v 1.1 2004-05-12 09:54:03 zwierzem Exp $
 */
public class DatatypeSetManager
{
	/** logger */
	protected Logger logger; 
	
	/** the provided datatype sets */
	protected Map datatypeSets = new Hashtable();

	/** the datatype sets' factories */
	protected List factories = new ArrayList();
	
	/**
	 * Creates an abstract datatype set manager.
	 * @param logger the logger
	 * @param grammarCache XML grammar cache for retrieval of XML schema datatypes
	 */
	public DatatypeSetManager(Logger logger, XMLGrammarCache grammarCache)
	{
		this.logger = logger;
		// TODO Register factories in a "dynamic" or configurable way using PicoContainer
		factories.add(new XMLSchemaDatatypeSetFactory(grammarCache));
	}
	
	/**
	 * Returns a named set of datatypes.
	 * 
	 * @param name name of the datatype set
	 * @return the datatype set or null if the datatype set does not exist
	 * @throws DatatypeSetCreationException thrown when datatype set name is known but 
	 * 		the set cannot be created.
	 */
	public synchronized DatatypeSet getDatatypeSet(String name)
		throws DatatypeSetCreationException
	{
		DatatypeSet set = (DatatypeSet) datatypeSets.get(name);
		if(set == null)
		{
			for (Iterator iter = factories.iterator(); iter.hasNext();)
        	{
            	DatatypeSetFactory element = (DatatypeSetFactory) iter.next();
				set = element.createDatatypeSet(name);
				if(set != null)
				{
					break;
				}
        	}
			datatypeSets.put(name, set);
		}
		return set;
	}
	
	/**
	 * Set a parent - child relation between sets of datatypes. This relation is used to inherit
	 * datatypes between sets. Child datatype set inherits datatypes from parent one.
	 * 
	 * @param child child datatype set
	 * @param parent parent datatype set 
	 * @throws CircularDependencyException on dependency cycle creation
	 */
    public synchronized void setParent(DatatypeSet child, DatatypeSet parent)
		throws CircularDependencyException
    {
    	DatatypeSet set = parent;
    	while(set != null && set != child)
    	{
    		set = ((AbstractDatatypeSet) set).getParent();
    	}
    	if(set == child)
    	{
    		throw new CircularDependencyException("datatype set '"+parent.getName()
    			+"' is a child of datatype set '"+child.getName()+"'");
    	}
    	((AbstractDatatypeSet) child).setParent(parent);
    }

	/**
	 * Clears a parent - child relation on a child set of datatypes.
	 * 
	 * @param child child datatype set
	 */
	public void clearParent(DatatypeSet child)
	{
		((AbstractDatatypeSet) child).setParent(null);
	}

	/*	
	public void addDatatype(DatatypeSet set, Datatype datatype);
	public void removeDatatype(DatatypeSet set, Datatype datatype);
	public void clearDatatypes(DatatypeSet set);
	*/
}
