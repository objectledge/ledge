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
package org.objectledge.datatype.xml;

import java.util.Hashtable;
import java.util.Map;

import org.objectledge.datatype.AbstractDatatypeSet;
import org.objectledge.datatype.Datatype;
import org.objectledge.xml.XMLGrammarCache;

import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.util.ExpressionWalker;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: XMLSchemaDatatypeSet.java,v 1.1 2004-05-12 09:54:03 zwierzem Exp $
 */
public class XMLSchemaDatatypeSet extends AbstractDatatypeSet
{
	private Map datatypes = new Hashtable();
	
	/**
	 * Creates a XML schema backed datatype set.
	 * 
	 * @param grammarUri URI of the grammar containing datatype definitions for this datatype set
	 * @param grammarCache grammar cache is used to load and cache the grammar
	 * @throws Exception thrown on problems when loading the grammar
	 */
    public XMLSchemaDatatypeSet(String grammarUri, XMLGrammarCache grammarCache)
    	throws Exception
    {
        super(grammarUri);
		Grammar gramar = grammarCache.getGrammar(grammarUri);
		gramar.getTopLevel().visit(new DatatypeFinder());
    }

    /**
     * {@inheritDoc}
     */
    protected Datatype getDatatypeInternal(String name)
    {
		return (Datatype) datatypes.get(name);
    }

	// implementation -----------------------------------------------------------------------------

	/**
	 * An expression walker which looks for datatype expressions. 
	 */
    private class DatatypeFinder extends ExpressionWalker
    {
		public void onData( DataExp exp )
		{
			Datatype datatype = new XMLSchemaDatatype(exp.getName().localName, exp.getType());
			datatypes.put(datatype.getName(), datatype);
		}
    }
}
