//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.
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

package org.objectledge.utils;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * This class contains various functions for manipulating Java Strings.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * @version $Id: StringUtils.java,v 1.3 2003-12-30 14:35:10 pablo Exp $
 */
public class StringUtils
{
    /** private constructor */
    private StringUtils()
    {
    }

    /**
     * Expand unicode escapes.
     * 
     * @param s the string to process.
     * @return processed string.
     * @throws IllegalArgumentException if the string contains invalid unicode
     *   escapes.
     */
    public static String expandUnicodeEscapes(String s) throws IllegalArgumentException
    {
        StringBuffer buffer = new StringBuffer();
        int last = 0;
        int cur = s.indexOf("\\u", last);
        if (cur >= 0)
        {
            if (cur <= s.length() - 6)
            {
                buffer.setLength(0);
                while (cur >= 0)
                {
                    buffer.append(s.substring(last, cur));
                    String ucodeStr = s.substring(cur + 2, cur + 6);
                    try
                    {
                        int ucode = Integer.parseInt(ucodeStr, 16);
                        if (Character.isDefined((char)ucode))
                        {
                            buffer.append((char)ucode);
                        }
                        else
                        {
                            throw new IllegalArgumentException("invalid unicode character code "+
                            									" in an unicode escape");
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        throw new IllegalArgumentException("invalid hexadecimal number " +
                        									"in an unicode escape");
                    }
                    last = cur + 6;
                    cur = s.indexOf("\\u", last);
                }
            }
            else
            {
                throw new IllegalArgumentException("truncated unicode escape");
            }
        }
        buffer.append(s.substring(last));
        return buffer.toString();
    }

    /**
     * Performs variable substitution on a template string.
     *
     * <p>The variables are formed of the dollar sign followed by a digit, ranging
     * from 1 to 9. Special variable formed of the dollar singn followed by an
     * asterisk is substitued with the remaining values separated by
     * commas. Dollar sign followed by another dollar sign expands to single
     * dollar sign. If there are not enough values provided, undefined variables
     * will be substitutes with empty strings. Examples:
     * <table>
     * <tr><td>template</td><td>values</td><td>result</td></tr>
     * <tr><td>$1 rules</td><td>{"ziu"}</td><td>ziu rules</td></tr>
     * <tr><td>$1 said "$2"</td><td>{"Fred","Foo!"}</td><td>Fred said
     * "Foo!"</td></tr>
     * <tr><td>"$2" said $1</td><td>{"Fred","Foo!"}</td><td>"Foo!" said
     * Fred</td></tr>
     * <tr><td>$1 likes the following colors:
     * $*</td><td>{"Mike","blue","grey","cyan"}</td>
     * <td>Mike likes the following colors: blue, grey, cyan</td></tr>
     * <tr><td>$1 likes the following $2:
     * $*</td><td>{"Mike","food","pizza","french fries"}</td><td>Mike likes
     * the following food: pizza, french fries</td></tr>
     * </table></p>
     *
     * @param template the template string.
     * @param values the values of the variables.
     * @return the output string.
     */
    public static String substitute(String template, String[] values)
    {
        StringBuffer buff = new StringBuffer();
        int maxUsed = 0;
        char[] t = template.toCharArray();
        for (int i = 0; i < t.length; i++)
        {
            if (t[i] == '$' && i < t.length - 1)
            {
                if (t[i + 1] == '$')
                {
                    buff.append('$');
                    i++;
                }
                else if (t[i + 1] > '0' && t[i + 1] < '9')
                {
                    int v = t[i + 1] - '0';
                    if (v - 1 < values.length)
                    {
                        buff.append(values[v - 1]);
                    }
                    if (v > maxUsed)
                    {
                        maxUsed = v;
                    }
                    i++;
                }
                else if (t[i + 1] == '*')
                {
                    if (maxUsed < values.length)
                    {
                        for (int v = maxUsed; v < values.length; v++)
                        {
                            buff.append(values[v]);
                            buff.append(", ");
                        }
                        buff.setLength(buff.length() - 2);
                    }
                    i++;
                }
                else
                {
                    buff.append('$');
                }
            }
            else
            {
                buff.append(t[i]);
            }
        }
        return buff.toString();
    }
    
	/**
	 * Build a locale from string.
	 *
	 * @param name a string representation of a locale
	 * @return a <code>Locale</code> object
	 */
	public static Locale getLocale(String name)
	{
		try
		{
			Locale locale;
			StringTokenizer st = new StringTokenizer(name, "_");
			String language = st.nextToken();
			String country = st.nextToken();
			if(st.hasMoreTokens())
			{
				String variant = st.nextToken();
				locale = new Locale(language, country, variant);
			}
			else
			{
				locale = new Locale(language, country);
			}
			return locale;
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Locale parsing exception - "
												+"invalid string representation '"+name+"'");
		}
	}
}
