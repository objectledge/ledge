// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.parameters;

/**
 * An interface defining read methods for parameter containter.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ParametersRead.java,v 1.1 2005-02-22 20:05:42 zwierzem Exp $
 */
public interface ParametersRead
{
    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name, String defaultValue);

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the string values of the parameter.
     */
    public String[] getStrings(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name, boolean defaultValue);

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the boolean values of the parameter.
     */
    public boolean[] getBooleans(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the float value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public float getFloat(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the float value of the parameter.
     */
    public float getFloat(String name, float defaultValue);

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the float values of the parameter.
     * @throws NumberFormatException if anyone of the values is not a number. 
     */
    public float[] getFloats(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public int getInt(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public int getInt(String name, int defaultValue);

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the integer values of the parameter.
     * @throws NumberFormatException if anyone of the values is not a number.
     */
    public int[] getInts(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public long getLong(String name) throws NumberFormatException;

    /**
     * Return the parameter as array of long values. 
     * 
     * @param name the name of the parameter.
     * @return the array of parameter values.
     * @throws NumberFormatException if parameter is not a number.
     */
    public long[] getLongs(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public long getLong(String name, long defaultValue);

    /**
     * Return the names of all parameters.
     * 
     * @return the parameter names.
     */
    public String[] getParameterNames();

    /**
     * Checks whether parameter is defined.
     * 
     * @param name the name of the parameter.
     * @return <code>true</code> if parameter is defined.
     */
    public boolean isDefined(String name);

}