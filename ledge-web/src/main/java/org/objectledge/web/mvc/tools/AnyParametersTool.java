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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.objectledge.parameters.Parameters;

/**
 * Give a read only access to a set of parameters.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AnyParametersTool.java,v 1.1 2005-08-22 14:13:37 zwierzem Exp $
 */
public class AnyParametersTool
{
    protected Parameters parameters;

    /**
     * Creates the parameters tool for a given set of parameters.
     * 
     * @param parameters the parameters to be represented by the tool
     */
    public AnyParametersTool(Parameters parameters)
    {
       this.parameters = parameters; 
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name)
    {
        return parameters.get(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name, String defaultValue)
    {
        return parameters.get(name, defaultValue);
    }

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the string values of the parameter.
    */
    public String[] getStrings(String name)
    {
        return parameters.getStrings(name);
    }

    /**
     * Return all values of the parameter with specified name as a list of strings. 
     * 
     * @param name the name of the parameters.
     * @return the list of the string values of the parameter.
    */
    public List<String> getStringsList(String name)
    {
        String[] a = parameters.getStrings(name);
        List<String> l = new ArrayList(a.length);
        l.addAll(Arrays.asList(a));
        return l;
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name)
    {
        return parameters.getBoolean(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        return parameters.getBoolean(name, defaultValue);
    }

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the boolean values of the parameter.
    */
    public boolean[] getBooleans(String name)
    {
        return parameters.getBooleans(name);
    }

    /**
     * Return the parameter with specified name.
     * The assumed String value of this parameter is a decimal representation of a Unix time-stamp.
     * 
     * @param name the name of the parameter.
     * @return the date value of the parameter.
     */
    public Date getDate(String name)
    {
        return parameters.getDate(name);
    }

    /**
     * Return the parameter with specified name. 
     * The assumed String value of this parameter is a decimal representation of a Unix time-stamp.
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the date value of the parameter.
     */
    public Date getDate(String name, Date defaultValue)
    {
        return parameters.getDate(name, defaultValue);
    }

    /**
     * Return all values of the parameter with specified name as an array. 
     * The assumed String values of this parameter is are decimal representations of Unix
     * time-stamps.
     * 
     * @param name the name of the parameters.
     * @return the array of the date values of the parameter.
    */
    public Date[] getDates(String name)
    {
        return parameters.getDates(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the float value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public float getFloat(String name) throws NumberFormatException
    {
        return parameters.getFloat(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the float value of the parameter.
     */
    public float getFloat(String name, float defaultValue)
    {
        return parameters.getFloat(name, defaultValue);
    }

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the float values of the parameter.
     * @throws NumberFormatException if anyone of the values is not a number. 
    */
    public float[] getFloats(String name) throws NumberFormatException
    {
        return parameters.getFloats(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public int getInt(String name) throws NumberFormatException
    {
        return parameters.getInt(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public int getInt(String name, int defaultValue)
    {
        return parameters.getInt(name, defaultValue);
    }

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the integer values of the parameter.
     * @throws NumberFormatException if anyone of the values is not a number.
    */
    public int[] getInts(String name) throws NumberFormatException
    {
        return parameters.getInts(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public long getLong(String name) throws NumberFormatException
    {
        return parameters.getLong(name);
    }

    /**
     * Return the parameter as array of long values. 
     * 
     * @param name the name of the parameter.
     * @return the array of parameter values.
     * @throws NumberFormatException if parameter is not a number.
     */
    public long[] getLongs(String name) throws NumberFormatException
    {
        return parameters.getLongs(name);
    }

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public long getLong(String name, long defaultValue)
    {
        return parameters.getLong(name, defaultValue);
    }

    /**
     * Return the names of all parameters.
     * 
     * @return the parameter names.
     */
    public List<String> getParameterNames()
    {
        return Arrays.asList(parameters.getParameterNames());
    }

    /**
     * Checks whether parameter is defined.
     * 
     * @param name the name of the parameter.
     * @return <code>true</code> if parameter is defined.
     */
    public boolean isDefined(String name)
    {
        return parameters.isDefined(name);
    }
}
