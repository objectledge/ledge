package org.objectledge.utils;

/**
 * This class contains various functions for manipulating Java Strings.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * @version $Id: StringUtils.java,v 1.1 2003-11-28 12:28:11 pablo Exp $
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
