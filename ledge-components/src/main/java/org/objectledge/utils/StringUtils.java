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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class contains various functions for manipulating Java Strings.
 *
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * @version $Id: StringUtils.java,v 1.26 2005-02-21 16:27:24 zwierzem Exp $
 */
public class StringUtils
{
    /**
     * A private constructor to prevent instantiation of this static method only class.
     */
    private StringUtils()
    {
        // static access only
    }
    
    /**
     *  Prepares a given String to be used as a HTTP cookie name.
   	 * 
  	 *
  	 * @see StringUtils#cookieNameSafeString(String, char)
   	 * @param input Cookie name unsafe string.
   	 * @return a modified string.
   	 */
    public static String cookieNameSafeString(String input)
    {
        return cookieNameSafeString(input, '.');
    }

    /** 
     * Prepares a given String to be used as a HTTP cookie name.
     *
     * <p>It replaces characters used in cookies (exactly <code>;</code>
     * semicolon, <code>,</code> comma, <code>=</code> equals, <code>$</code>
     * and whitespace) with a given character.  If this character is equal to
     * any of the unsafe characters <code>.</code> dot is used.</p>
     *
     * @param input Cookie name unsafe string.
     * @param replaceChar Character to be used as a replacement for unsafe
     *        characters.
     * @return a modified string.
     */
    public static String cookieNameSafeString(String input, char replaceChar)
    {
        // check for unsafe replacement character
        if (replaceChar == '=' || replaceChar == ',' || replaceChar == ';' || 
            replaceChar == '$' || Character.isWhitespace(replaceChar))
        {
            replaceChar = '.';
        }

        if (input != null)
        {
            StringBuilder sb = new StringBuilder(input);
            int length = sb.length();

            for (int i = 0; i < length; i++)
            {
                char c = sb.charAt(i);
                // replace unwanted chars
                if (Character.isWhitespace(c) || c == '=' || c == ';' || c == ',' || c == '$')
                {
                    sb.setCharAt(i, replaceChar);
                }
            }
            input = sb.toString();
        }
        return input;
    }
    
	/**
	 * Backslash escape reserved characters in a string.
	 *
	 * @param in the string to process.
	 * @param reserved the reserved characters.
	 * @return the string reserved characters escaped.
	 */
	public static String backslashEscape(String in, String reserved)
	{
		StringBuilder out = new StringBuilder();
		StringTokenizer st = new StringTokenizer(in,reserved,true);
		while(st.hasMoreTokens())
		{
			String t = st.nextToken();
			if(t.length() == 1 && reserved.indexOf(t) >= 0)
			{
				out.append('\\');
			}
			out.append(t);
		}               
		return out.toString();
	}

	/**
	 * Escapes characters outside the US-ASCII range as Java unicode scapes
	 * (&#2F;uxxxx where x is a hexadecimal digit).
	 *
	 * @param s the string to process.
	 * @return processed string.
	 */
	public static String escapeNonASCIICharacters(String s)
	{
		StringBuilder buff = new StringBuilder();
		char[] chars = s.toCharArray();
		for(int i=0; i<chars.length; i++)
		{
			if((int)chars[i] < 128)
			{
				buff.append(chars[i]);
			}
			else
			{
				buff.append("\\u");
				String ucode = Integer.toString((int)chars[i], 16);
				for(int j=4-ucode.length(); j>0; j--)
				{
					buff.append('0');
				}
				buff.append(ucode);
			}
		}
		return buff.toString();
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
        StringBuilder buffer = new StringBuilder();
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
        StringBuilder buff = new StringBuilder();
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
	
	/**
	 * Determines the number of bytes the string will ocuppy in a specifc 
	 * character encoding.
	 * 
	 * @param string the string.
	 * @param encoding the requested encoding.
	 * @return the size of the string.
	 * @throws IOException if happens.
	 */
	public static int getByteCount(String string, String encoding) 
		throws IOException
	{
		if(encoding.startsWith("ISO-8859"))
		{
			return string.length();
		}
		if(encoding.equals("UTF-16"))
		{
			return string.length() * 2;
		}
		if(string.length() < 65536)
		{
			byte[] bytes = string.getBytes(encoding);
			return bytes.length;
		}
		else
		{
			CountOutputStream counter = new CountOutputStream();
            OutputStreamWriter writer = null; 
            try
            {
                writer = new OutputStreamWriter(counter, encoding);
                writer.write(string);
            }
            finally
            {
                ///CLOVER:OFF
                if(writer != null)
                {
                    writer.close();
                }
                ///CLOVER:ON
            }
			return counter.getCount();
		}
	}
	
	/**
	 * Helper class to count the string length. 
	 */
	private static class CountOutputStream
		extends OutputStream
	{
		private int count;

        ///CLOVER:OFF
		/* overriden */
		public void write(int b) throws IOException
		{
			count++;
		}
        
		/* overiden */
		public void write(byte[] b) throws IOException
		{
			count += b.length;
		}
        ///CLOVER:ON

		/* overiden */
		public void write(byte[] b, int offset, int length) throws IOException
		{
			count += length;
		}
        
		public int getCount()
		{
			return count;
		}
	}  
    
    /**
     * Appends the specified number of space charcter to a string buffer and returns it.
     * 
     * @param buffer the buffer.
     * @param d number of spaces.
     * @return the buffer.
     */
    public static StringBuilder indent(StringBuilder buffer, int d)
    {
        for(int i=0; i<d; i++)
        {
            buffer.append(' ');
        }
        return buffer;
    }

    /**
     * Fill the string to the expected length with the specified filling character.
     * Filling charaters will be added at the beginning of this input string.
     * 
     * @param input the input string.
     * @param total the expected length of result string.
     * @param filling the filling character.
     * @return the result string.
     */
    public static String fillString(String input,int total,char filling)
    {
        StringBuilder sb = new StringBuilder();
        int missing = total-input.length();
        for(int i=0; i<missing; i++)
        {
            sb.append(filling);
        }
        sb.append(input);
        return sb.toString();
    }
    
    /** 
     * Creates ascii based unicode representation of the string.
     * Each unicode character of the input will be tranformed to 8 ascii 
     * characters in the following format:
     * "\"&lt;octal lower byte&gt;"\"&lt;octal higher byte&gt;
     * i.e. the output string looks as follows:
     * "\124\000\102\001\165\000\155\000".
     *
     * @param input the input string.
     * @return the output.
     */
    public static String toOctalUnicode(String input)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < input.length(); i++)
        {
            int value = (int)input.charAt(i);
            String upper = fillString(Integer.toOctalString(value/256), 3, '0');
            String lower = fillString(Integer.toOctalString(value%256), 3, '0');            
            sb.append("\\");
            sb.append(lower);
            sb.append("\\");
            sb.append(upper);
        }
        return sb.toString();
    }    

    /**
     * Wrap the text to the specified number of columns.
     *
     * <p>The input string is expected to be a series of lines of text
     * delimeted by \n characters. The output string contains the text
     * reformatted in such way that each line is at most <code>width</code>
     * characters wide. For each line of input text that is longer than the
     * limit, last whitespace character before the limit is searched, and is
     * replaced by a newline. Any whitespace characters immediately following
     * that character are discarded. If the input text contains a sequence of 
     * non-whitespace characters longer than the specified limit, the sequence
     * will be broken by newlines to fit in the limit.</p>
     * 
     * @param in the text to format.
     * @param width the width of the output text.
     * @return wrapped text.
     */
    public static String wrap(String in, int width)
    {
        if(in.length() <= width)
        {
            return in;
        }
        StringTokenizer st = new StringTokenizer(in, "\n");
        StringBuilder out = new StringBuilder();
        StringBuilder lineOut = new StringBuilder();
        String line;
        int a,b;
        while(st.hasMoreTokens())
        {
            line = st.nextToken();
            if(line.length() <= width)
            {
                out.append(line).append('\n');
                continue;
            }
            lineOut.setLength(0);
            a = 0;
            b = width;
            while(b < line.length())
            {
                while(b > a && !Character.isWhitespace(line.charAt(b)))
                {
                    b--;
                }
                if(b == a)
                {
                    b = a + width;
                }
                lineOut.append(line.substring(a,b)).append('\n') ;
                a = b;
                while(a < line.length() && Character.isWhitespace(line.charAt(a)))
                {
                    a++;
                }
                b = a + width;
            }
            lineOut.append(line.substring(a));
            out.append(lineOut);
        }
        return out.toString();
    }
    
    // pathnames ////////////////////////////////////////////////////////////
    
    /**
     * Normalizes a pathname.
     *
     * <p>This method removes redundant / characters, removes . and .. path elements,
     * taking care that the paths dont reach outside filesystem root, removes trailing /
     * from directories and adding leading / as neccessary.</p>
     * 
     * @param path the path.
     * @return normalized path.
     * @throws IllegalArgumentException if the path reaches outside the filesystem root.
     */
    public static String normalizedPath(String path)
        throws IllegalArgumentException
    {
        StringTokenizer st = new StringTokenizer(path, "/");
        ArrayList temp = new ArrayList(st.countTokens());
        while(st.hasMoreTokens())
        {
            String t = st.nextToken();
            if(t.equals("."))
            {
                continue;
            }
            else if(t.equals(".."))
            {
                if(temp.isEmpty())
                {
                    throw new IllegalArgumentException("path outside filesystem root: "+path);  
                }
                else
                {
                    temp.remove(temp.size()-1);
                }
            }
            else
            {
                temp.add(t);
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<temp.size(); i++)
        {
            sb.append('/').append((String)temp.get(i));
        }
        return sb.toString();
    }
    
    /**
     * Returns the base name of a file.
     * 
     * <p>This method returns the contents of the pathname after the last '/' 
     * character. </p>
     *
     * @param path the pathname of the file.
     * @return the basename of the file.     
     */
    public static String basePath(String path)
    {
        int pos = path.lastIndexOf('/');
        if(pos < 0)
        {
            return path;
        }
        else
        {
            return path.substring(pos+1);
        }
    }
    
    /**
     * Returns hte directory name of a file.
     * 
     * <p>This method returns the normalized path before the last '/' character
     * in the path.</p>
     * 
     * @param path the pathname of the file.
     * @return the directory name of the file.   
     */
    public static String directoryPath(String path)
    {
        path = normalizedPath(path);
        return path.substring(0, path.lastIndexOf('/'));        
    }
    
    /**
     * Returns the relative pathname of a file with respect to given
     * base directory.
     *
     * @param path the pathname of a file.
     * @param base the base pathname.
     * @return the relative pathname.
     * @throws IllegalArgumentException if the file is contained
     *         outside of base.
     */
    public static String relativePath(String path, String base)
        throws IllegalArgumentException
    {
        base = normalizedPath(base);
        path = normalizedPath(path);
        if(!path.startsWith(base))
        {
            throw new IllegalArgumentException(path+" is not contained in "+base);
        }
        return path.substring(base.length());
    }    
    
    /**
     * Expand macros in a string.
     *  
     * @param s the String to process.
     * @param t the macros (token -&gt; value)
     * @return an expanded String
     */
    public static String expand(String s, Map t)
    {
        if(t==null || t.size()==0)
        {
            return s;
        }
        StringBuilder buff = new StringBuilder(s.length());
        Iterator keys = t.keySet().iterator();
        int pos, lastpos;
        String k, v;
        while(keys.hasNext())
        {
            k = (String)keys.next();
            pos = s.indexOf(k);
            if(pos < 0)
            {
                continue;
            }
            lastpos = 0;
            v = (String)t.get(k);
            buff.setLength(0);
            while(pos >= 0)
            {
                buff.append(s.substring(lastpos, pos));
                buff.append(v);
                lastpos = pos+k.length();
                pos = s.indexOf(k, lastpos);
            }
            buff.append(s.substring(lastpos));
            s = buff.toString();
        }
        return s;
    }
    
    /**
     * Escapes xml characters.
     *
     * @param string a string to escape
     * @return the processed string.
     */
    public static String escapeXMLCharacters(String string)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            char c = string.charAt(i);
            switch (c)
            {
                case '<' :
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Convert newlines in the string into &lt;br/&gt; tags.
     *
     * @param s the string to process.
     * @return processed string.
     */
    public static String htmlLineBreaks(String s)
    {
        StringBuilder out = new StringBuilder();
        char[] chars = s.toCharArray();
        for(int i=0; i<chars.length; i++)
        {
            if(chars[i] == '\n')
            {
                out.append("<br />");
            }
            else if(chars[i] == '\r')
            {
                if(i<chars.length-1 && chars[i+1] == '\n')
                {
                    i++;
                }
                out.append("<br />");
            }
            else
            {
                out.append(chars[i]);
            }
        }
        return out.toString();
    }
}
