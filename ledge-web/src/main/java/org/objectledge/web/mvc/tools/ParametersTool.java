//
//Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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

package org.objectledge.web.mvc.tools;

import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.ParametersRead;

/**
 * Give a read only access to request parameters.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ParametersTool.java,v 1.1 2005-02-22 20:06:27 zwierzem Exp $
 */
public class ParametersTool implements ParametersRead
{
    private Parameters parameters;

    /**
     * Creates the parameters tool for a given set of parameters
     * @param requestParameters
     */
    public ParametersTool(Parameters parameters)
    {
       this.parameters = parameters; 
    }

    /**
     * {@inheritDoc}
     */
    public String get(String name)
    {
        return parameters.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public String get(String name, String defaultValue)
    {
        return parameters.get(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getStrings(String name)
    {
        return parameters.getStrings(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name)
    {
        return parameters.getBoolean(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        return parameters.getBoolean(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public boolean[] getBooleans(String name)
    {
        return parameters.getBooleans(name);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String name) throws NumberFormatException
    {
        return parameters.getFloat(name);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloat(String name, float defaultValue)
    {
        return parameters.getFloat(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public float[] getFloats(String name) throws NumberFormatException
    {
        return parameters.getFloats(name);
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(String name) throws NumberFormatException
    {
        return parameters.getInt(name);
    }

    /**
     * {@inheritDoc}
     */
    public int getInt(String name, int defaultValue)
    {
        return parameters.getInt(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public int[] getInts(String name) throws NumberFormatException
    {
        return parameters.getInts(name);
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(String name) throws NumberFormatException
    {
        return parameters.getLong(name);
    }

    /**
     * {@inheritDoc}
     */
    public long[] getLongs(String name) throws NumberFormatException
    {
        return parameters.getLongs(name);
    }

    /**
     * {@inheritDoc}
     */
    public long getLong(String name, long defaultValue)
    {
        return parameters.getLong(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getParameterNames()
    {
        return parameters.getParameterNames();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDefined(String name)
    {
        return parameters.isDefined(name);
    }
}
