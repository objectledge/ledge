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

/**
 * This class contains various functions for manipulating Java Strings.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * @version $Id: StringUtils.java,v 1.2 2003-12-03 14:39:43 mover Exp $
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
	public static String expandUnicodeEscapes(String s)
		throws IllegalArgumentException
	{
		StringBuffer buffer = new StringBuffer();
		int last = 0;
		int cur = s.indexOf("\\u", last);
		if(cur >= 0)
		{
			if(cur <= s.length() - 6)
			{
				buffer.setLength(0);
				while(cur >= 0)
				{
					buffer.append(s.substring(last,cur));
					String ucodeStr = s.substring(cur+2, cur+6);
					try
					{
						int ucode = Integer.parseInt(ucodeStr, 16);
						if(Character.isDefined((char)ucode))
						{
							buffer.append((char)ucode);
						}
						else
						{
							throw new IllegalArgumentException("invalid unicode character code "+
													 " in an unicode escape");
						}
					}
					catch(NumberFormatException e)
					{
						throw new IllegalArgumentException("invalid hexadecimal number "+
												 "in an unicode escape");
					}
					last = cur+6;
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
}
