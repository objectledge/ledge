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

package org.objectledge.parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

/**
 * An implementation of parameters container which sorts parameter keys.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SortedParameters.java,v 1.2 2006-02-08 18:24:45 zwierzem Exp $
 */
public class SortedParameters extends DefaultParameters
{
    /**
     * The chosen map implementation is a <code>TreeMap</code>.
     */
    protected void setupMap()
    {
        map = new TreeMap<String, String[]>();
    }

    /**
     * Create the empty container.
     */
    public SortedParameters()
    {
        super();
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param configuration the string representation of the container. 
     */
    public SortedParameters(String configuration)
    {
        super(configuration);
    }

    /**
     * Create the container and feed it with configuration given as string. 
     * 
     * @param is the stream with byte representation of the container.
     * @param encoding the encoding of the source.
     * @throws UnsupportedEncodingException if the specified encoding is not supported by the JVM.
     * @throws IOException if there is an error reading data from the stream.
     */
    public SortedParameters(InputStream is, String encoding)
        throws IOException, UnsupportedEncodingException
    {
        super(is, encoding);
    }

    /**
     * Create the container as a copy of source container. 
     * 
     * @param source the source container.
     */
    public SortedParameters(Parameters source)
    {
        super(source);
    }
}
