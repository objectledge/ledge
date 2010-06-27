// 
//Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.reader.GrammarReaderController;

/**
 * GrammarReaderController that logs all warnings and throws exceptions on errors.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseGrammarReaderController.java,v 1.3 2005-02-21 16:27:51 zwierzem Exp $
 */
public abstract class BaseGrammarReaderController implements GrammarReaderController
{
	/** Loaded grammar URI. */
	private String grammarUri;
	
    /** Entity resolution is delegated to this object, it can be <code>null</null>. */
    private EntityResolver entityResolver;

    /** Used to compose information of location of errors and warnings. */
    private StringBuilder locationMessage = new StringBuilder(64);

	/**
	 * Creates a grammar reader controller.
	 * 
	 * @param grammarUri URI of a loaded grammar - provided for better warning description
	 * @param entityResolver optional entity resolver.
	 */
    public BaseGrammarReaderController(
        String grammarUri,
        EntityResolver entityResolver)
    {
    	this.grammarUri = grammarUri;
        this.entityResolver = entityResolver;
    }

	/**
	 * Creates a grammar reader controller without entity resolver.
	 * 
	 * @param grammarUri URI of a loaded grammar - provided for better warning description
	 */
    public BaseGrammarReaderController(String grammarUri)
    {
        this(grammarUri, null);
    }
	
    //------------------------------------------------------------------------
    // EntityResolver methods

    /**
     * Resolves an entity, uses externaly set EntityResolver or returns <code>null</code>.
     * 
     * @param publicId public id of an entity 
     * @param systemId system id of an entity
     * @return resolved entity's input source 
     * @throws java.io.IOException on IO errors while resolving entity
     * @throws SAXException on parsing errors while resolving entity
     */
    public InputSource resolveEntity( String publicId, String systemId )
	    throws java.io.IOException, SAXException
    {
        if(entityResolver!=null)
        {
            return entityResolver.resolveEntity(publicId, systemId);
        }
        else
        {
            return null;
        }
    }

    //------------------------------------------------------------------------
    // GrammarReaderController methods

    /**
     * Receives warning notification - does nothing.
     * 
     * @param loc warning location info
     * @param errorMessage warning message
     */
    public void warning(Locator[] loc, String errorMessage)
    {
        // pass to the log?
    }

    /**
     * Receives error notification - does nothing.
     * 
     * @param loc error location info
     * @param errorMessage error message
     * @param nestedException exception nested in error information
     */
    public void error( Locator[] loc, String errorMessage, Exception nestedException )
    {
        // TODO pass the log?
    }

    //------------------------------------------------------------------------
    // Utility methods

	/**
	 * Prepares a location message.
	 * 
	 * @param type message type - 'error' or 'warning'
	 * @param loc locator of the error or warning
	 * @param errorMessage a message
	 * @return formatted location message
	 */
    protected String getLocationMessage(String type, Locator[] loc, String errorMessage)
    {
        // init buffer
        locationMessage.setLength(0);

		
		locationMessage.append(type);
		locationMessage.append(" on schema '").append(grammarUri).append("' ; ");

        locationMessage.append(errorMessage);
        locationMessage.append(" ; ");

        if(loc == null || loc.length == 0)
        {
            locationMessage.append("location unknown");
        }
        else
        {
            for( int i=0; i<loc.length; i++ )
            {
                getLocation(loc[i]);
            }
        }

        return locationMessage.toString();
    }

    private void getLocation(Locator loc)
    {
        if(loc.getColumnNumber()>=0)
        {
            locationMessage.append("column:");
            locationMessage.append(loc.getColumnNumber());
        }

        locationMessage.append(" line:");
        locationMessage.append(loc.getLineNumber());
        locationMessage.append("   ");
        locationMessage.append(loc.getSystemId());
        locationMessage.append("  ");
    }
}
