// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.finders;

import java.util.NoSuchElementException;

/**
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ViewLookupSequence.java,v 1.1 2004-01-16 15:01:16 fil Exp $
 */
public class ViewLookupSequence
{
    private String outSeparator;

    private String[] prefices;
    
    private ViewFallbackSequence viewFallbackSequence;
    
    private int position = 0;
    
    private StringBuffer buff = new StringBuffer();

    /**
     * Constructs a view lookup sequence.
     * 
     * @param prefices the path prefices.
     * @param viewFallbackSequence the fallback sequence.
     * @param outSeparator separator to use in generated paths.
     */
    public ViewLookupSequence(String[] prefices, ViewFallbackSequence viewFallbackSequence, 
        String outSeparator)
    {
        this.prefices = prefices;
        this.viewFallbackSequence = viewFallbackSequence;
        this.outSeparator = outSeparator;
    }

    /**
     * Checks if there are more elements in the sequence.
     * 
     * @return <code>true</code> if there are more elements in the sequence.
     */
    public boolean hasNext()
    {
        return position < prefices.length-1 || viewFallbackSequence.hasNext();
    }
    
    /**
     * Reset the sequence to the beginning.
     */
    public void reset()
    {
        position = 0;
        viewFallbackSequence.reset();
    }
    
    /**
     * Returns the next path in the sequence.
     * 
     * @return the next path in the sequence.
     */
    public String next()
    {
        if(!viewFallbackSequence.hasNext())
        {
            if(position < prefices.length)
            {
                viewFallbackSequence.reset();
                position++;
            }
            else
            {
                throw new NoSuchElementException("no more paths");
            }
        }
        buff.setLength(0);
        buff.append(prefices[position]);
        buff.append(outSeparator);
        buff.append(viewFallbackSequence.next());
        return buff.toString();
    }
}
