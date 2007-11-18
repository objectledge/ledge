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

import org.jcontainer.dna.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;

/**
 * GrammarReaderController that logs all warnings and throws exceptions on errors.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: LoggingGrammarReaderController.java,v 1.2 2007-11-18 21:19:58 rafal Exp $
 */
public class LoggingGrammarReaderController extends ExceptionGrammarReaderController
{
    /** Used to log warnings. */
    private Logger logger;

	/**
	 * Creates a logging grammar reader controller.
	 * 
	 * @param grammarUri URI of a loaded grammar - provided for better warning description
	 * @param entityResolver optional entity resolver.
	 * @param logger logger used to log warnings
	 */
    public LoggingGrammarReaderController(
        String grammarUri,
        EntityResolver entityResolver,
		Logger logger)
    {
    	super(grammarUri, entityResolver);
        this.logger = logger;
    }

	/**
	 * Creates a logging grammar reader controller.
	 * 
	 * @param grammarUri URI of a loaded grammar - provided for better warning description
	 * @param logger logger used to log warnings
	 */
    public LoggingGrammarReaderController(String grammarUri, Logger logger)
    {
        this(grammarUri, null, logger);
    }

    //------------------------------------------------------------------------
    // GrammarReaderController methods

    /**
     * Logs a grammar warning.
     * 
     * @param loc warning location info
     * @param errorMessage warning message
     */
    public void warning(Locator[] loc, String errorMessage)
    {
    	if(logger != null)
    	{
			logger.warn(getLocationMessage("warning", loc, errorMessage));
		}
    }
}
