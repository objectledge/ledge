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
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class contains various functions for manipulating Java Strings.
 *
 * @author <a href="mailto:damian@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 * @version $Id: StringUtils.java,v 1.42 2006-06-30 12:07:29 zwierzem Exp $
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
                if (Character.isWhitespace(c) || c == '=' || c == ';' || c == ',' || c == '$' 
                    || !(( c >= 'a' && c <= 'z') || ( c >= 'A' && c <= 'Z') || ( c >= '0' && c <= '9')))
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
        if(in == null)
        {
            return null;
        }
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
			if(chars[i] < 128)
			{
				buff.append(chars[i]);
			}
			else
			{
				buff.append("\\u");
				String ucode = Integer.toString(chars[i], 16);
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
                            throw new IllegalArgumentException("invalid unicode character code " +
                                    "in an unicode escape");
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        throw new IllegalArgumentException("invalid hexadecimal number in an " +
                                "unicode escape");
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
        String[] t = name.split("_");
        switch(t.length)
        {
        case 1:
            return new Locale(t[0]);
        case 2:
            return new Locale(t[0], t[1]);
        case 3:
            return new Locale(t[0], t[1], t[2]);
        default:
            StringBuilder v = new StringBuilder();
            for(int i = 2 ; i < t.length; i++)
            {
                v.append(t[i]);
                if(i < t.length - 1)
                {
                    v.append('_');
                }
            }
            return new Locale(t[0], t[1], v.toString());
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
            int value = input.charAt(i);
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
    
    /**
     * Justifies a list of strings.
     * 
     * @param strings the strings.
     * @param w the width of the text column.
     */
    public static String justify(List<String> strings, int w)
    {
        StringBuilder buff = new StringBuilder(w);
        int t = 0;
        for(int i=0; i<strings.size(); i++)
        {
            t += strings.get(i).length();
        }
        int s = w - t;
        int g = strings.size() - 1;
        if(g < 0)
        {
            // do nothing
        }
        else if(g == 0)
        {
            buff.append(strings.get(0));
        }
        else if(s <= g)
        {
            for(int i=0; i<strings.size(); i++)
            {
                buff.append(strings.get(i).trim());
                buff.append(' ');
            }
        }
        else
        {
            int gw = s / g;
            int d = s % g > 0 ? g / (s % g) : 0; 
            for(int i=0; i<strings.size(); i++)
            {
                buff.append(strings.get(i).trim());
                for(int j=0; j<gw; j++)
                {
                    buff.append(' ');
                }
                if(d > 0 && i % d == 0)
                {
                    buff.append(' ');
                }
            }
        }
        return buff.toString();
    }

    /**
     * Shorten the string to the specifed lenght.
     * 
     * <p>If the string is shorter than the maxLength limit it is returned intact. If the string is 
     * longer,it will be truncated and the suffix will be added. If truncation is necessary, the 
     * preferred truncation point is the last punctuation character before the limit, unless it
     * occurs later than minLength in the string. In this case the prefferred truncation point
     * is the last whitespace before the maxLength limit unless it occurs later than minLength.
     * At last resort, the string is truncated at maxLenght limit.</p>
     * 
     * @param source the string to process.
     * @param minLength minimum length of the shortened string.
     * @param maxLength maximum length limit.
     * @param suffix suffix to add if the string is actually shortened. Should be " ..." or
     * " \u2026" (using Unicode horizontal ellipsis glyph).
     * @return shortened string.
     */
    public static String shortenString(String source, int minLength, int maxLength, String suffix)
    {
        if(source == null || maxLength >= source.length())
        {
            return source;
        }
        // try to find a punctuation character before length limit
        int i;
        for(i = maxLength - 1; i >=0 ; i--)
        {
            int type = Character.getType(source.charAt(i));
            if(type >= Character.DASH_PUNCTUATION && type <= Character.OTHER_PUNCTUATION)
            {
                break;
            }
        }
        if(i < minLength)
        {
            // try to find a whitespace chracter before length limit
            for(i = maxLength - 1; i >=0 ; i--)
            {
                if(Character.isWhitespace(source.charAt(i)))
                {
                    break;
                }
            }
        }
        if(i < minLength)
        {
            i = maxLength;
        }
        return source.substring(0, i) + suffix;
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
        if(path.length()==0 || path.equals("/"))
        {
            return "/";
        }
        StringTokenizer st = new StringTokenizer(path, "/");
        ArrayList<String> temp = new ArrayList<String>(st.countTokens());
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
            sb.append('/').append(temp.get(i));
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
        if(s == null)
        {
            return "";
        }
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
    
    /**
     * Returns human readable representation of interval value in days, hours etc.
     * 
     * @param interval in seconds.
     * @return human readable interval specification.
     */
    public static String formatInterval(long interval)
    {
        long days = interval / (24 * 60 * 60);
        interval -= days * 24 * 60 * 60;
        long hours = interval / (60 * 60);
        interval -= hours * 60 * 60;
        long minutes = interval / 60;
        interval -= minutes * 60;
        long seconds = interval;
        StringBuffer buff = new StringBuffer();
        if(days > 0)
        {
            buff.append(days).append(" days, ");
        }
        if(days > 0 || hours > 0)
        {
            buff.append(hours).append(" hours, ");
        }
        if(days > 0 || hours > 0 || minutes > 0)
        {
            buff.append(minutes).append(" minutes, ");
        }
        buff.append(seconds).append(" seconds");
        return buff.toString();
    }

    /**
     * Format a millisecond interval as number of seconds (with fracitonal part).
     * 
     * @param interval interval in milliseconds. 
     * @return interval as number of seconds (with fracitonal part).
     */
    public static String formatMilliIntervalAsSeconds(long interval)
    {
        long seconds = interval / 1000;
        long millis = interval - seconds * 1000;
        
        StringBuilder buff = new StringBuilder();
        buff.append(seconds).append(".");
        buff.append(millis).append("s");
        return buff.toString();
    }
    
    /**
     * Renders a human readable event rate esitmation.
     * 
     * @param events number of events.
     * @param time timespan in seconds.
     * @param event event name.
     * @return a human readable event rate esitmation.
     */
    public static String formatRate(double events, double time, String event)
    {
        StringBuffer buff = new StringBuffer();
        NumberFormat format = new DecimalFormat("#.##");
        if(events > time)
        {
            buff.append(format.format(events/time)+" "+event+"s / 1s on average");
        }
        else
        {
            double interval = time/events;
            int d = (int)(interval / (24 * 3600));
            interval -= d * 24 * 3600;
            int h = (int)(interval / 3600);
            interval -= h * 3600;
            int m = (int)(interval / 60);
            interval -= m * 60;
            buff.append("1 "+event+" / ");
            if(d > 0)
            {
                buff.append(d+"d ");
            }
            if(h > 0 || d > 0)
            {
                buff.append(h+"h ");
            }
            if(m > 0 || h > 0 || d > 0)
            {
                buff.append(m+"m ");
            }
            buff.append(format.format(interval)+" s on average");
        }                
        return buff.toString();
    }    
    
    /**
     * Checks if a given string is <code>null</code> or empty.
     * 
     * @param str string to be checked.
     * @return true if the string is <code>null</code> or empty.
     */
    public static boolean isEmpty(String str)
    {
        if( str == null)
        { 
            return true;
        }
        if( str.length()==0)
        {
            return true;
        }
        return false;
    }

    /**
     * Capitalize given string by transforming the first character to upper case.
     * 
     * @param str the string.
     * @return the capitalized string.
     */
    public static String capitalize(String str)
    {
        StringBuilder buff = new StringBuilder(str.length());
        buff.append(Character.toUpperCase(str.charAt(0)));
        buff.append(str.substring(1));
        return buff.toString();
    }
    
    public static final String UTF_8_ENCODING = "UTF-8";

    /**
     * Convert from UTF-8 bytes to a String.
     * 
     * @param bytes UTF-8 bytes.
     * @return a String.
     */
    public static String fromUTF8(byte[] bytes)
    {
        if(bytes == null)
        {
            return null;
        }
        try
        {
            return new String(bytes, UTF_8_ENCODING);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new IllegalStateException("UTF-8 not supported?", e);
        }
    }

    /**
     * Convert from a String to UTF-8 bytes.
     * 
     * @param string String.
     * @return UTF-8 bytes.
     */
    public static byte[] toUTF8(String string)
    {
        if(string == null)
        {
            return null;
        }        
        try
        {
            return string.getBytes(UTF_8_ENCODING);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new IllegalStateException("UTF-8 not supported?", e);
        }
    }    
    
    /**
     * Append strings to the end of a string array.
     * 
     * @param values original array.
     * @param additional strings to be added.
     * @return new array containg combined values.
     */
    public static String[] push(String[] values, String ... additional)
    {
        String[] result = new String[values.length + additional.length];
        System.arraycopy(values, 0, result, 0, values.length);
        System.arraycopy(additional, 0, result, values.length, additional.length);
        return result;
    }
    
    /**
     * Remove strings from the end of a string array.
     * 
     * @param values original array.
     * @param n the number of strings to be removed.
     * @return new array containt smallr number of values.
     */
    public static String[] pop(String[] values, int n)
    {
        String[] result = new String[values.length - n];
        System.arraycopy(values, 0, result, 0, values.length - n);
        return result;
    }

    /**
     * Splits the string by the given separator and returns results as a set of strings.
     * The set is ordered (LinkedHashSet). 
     * @param in the input string
     * @param separator the delimiter
     * @return the set of strings.
     */
    public static Set<String> split(String in, String separator)
    {
        if(in == null || in.equals(""))
        {
            return new LinkedHashSet<String>();
        }
        Set<String> set = new LinkedHashSet<String>(in.length() / 2);
        set.addAll(Arrays.asList(in.split(separator)));
        return set;
    }
    
    /**
     * Joins a set of strings comobinig them usign a given separator.
     *  
     * @param strings the input set of strings.
     * @param separator the joining string.
     * @return the resulting string.
     */
    public static String join(Set<String> strings, String separator)
    {
        StringBuilder b = new StringBuilder(256);
        int i = 0;
        for (String str : strings)
        {
            if(str != null && str.length() > 0)
            {
                if(i > 0)
                {
                    b.append(separator);
                }
                b.append(str);
                i++;
            }
        }
        return b.toString();
    }

    
    /**
     * Format size value in <code>B</code>, <code>kB</code>, <code>MB</code>,
     * for example <code>15kB</code> or <code>23.5MB</code>.
     *
     * @param value the size in bytes.
     * @param precision number of digits in decimal fraction.
     * @return the size as string with a proper unit suffix.
     */
    public static String bytesSize(long value, int precision)
    {        
        StringBuilder b = new StringBuilder();
        if(value < 1024L)
        {
            return b.append(value).append("B").toString();
        }
        double floatValue = value;
        if(value < 1048576L)
        {
            b.append(floatValue/1024.0);
            cutDigits(precision, b);
            return b.append("kB").toString();
        }
        b.append(floatValue/1048576.0);
        cutDigits(precision, b);
        return b.append("MB").toString();        
    }

    private static void cutDigits(int precision, StringBuilder b)
    {
        int index = b.indexOf(".");
        if(index != -1 && b.length() > index+precision)
        {
            b.setLength(index+precision);
        }
    }

    private static enum ByteSizeState
    {
        START, BYTE, PREFRACTION, FRACTION, NUMBER, ERROR
    }
    
    /**
     * Parse a size value given as a number with <code>B</code>, <code>kB</code>, <code>MB</code>
     * suffix, for example <code>15kB</code> or <code>23.5MB</code>.
     *
     * @param value the size as string with a proper unit suffix.
     * @return the size in bytes.
     */
    public static long parseBytesSize(String value)
    {
        if(isEmpty(value))
        {
            return -1L; // error
        }
        
        value = value.toLowerCase().trim();
        long multiplier = 1L;
        long order = 1L;
        long size = 0L;
        long sizeFraction = 0L;
        ByteSizeState state = ByteSizeState.START;
        for (int i = value.length() - 1; i >= 0 && state != ByteSizeState.ERROR; i--)
        {
            char c = value.charAt(i);
            switch(state)
            {
            case START:
                if(c == 'b')
                {
                    state = ByteSizeState.BYTE;
                }
                else if(c >= '0' && c <= '9')
                {
                    state = ByteSizeState.FRACTION;
                    size = (c - '0');
                    order = 10L;
                }
                else
                {
                    state = ByteSizeState.ERROR;
                }
                break;
            case BYTE:
                state = ByteSizeState.PREFRACTION;
                if(c == 'k') multiplier = 1024L;
                else if(c == 'm') multiplier = 1048576L;
                else if(c == 'g') multiplier = 1073741824L;
                else if(c == ' ' || c == '\t')
                {
                    // PREFRACTION
                }
                else if(c >= '0' && c <= '9')
                {
                    size = (c - '0');
                    order = 10L;
                    state = ByteSizeState.FRACTION;
                }
                else
                {
                    state = ByteSizeState.ERROR;
                }
                break;
            case PREFRACTION:
                if(c >= '0' && c <= '9')
                {
                    state = ByteSizeState.FRACTION;
                    size = (c - '0');
                    order = 10L;
                }
                else if(c == ' ' || c == '\t')
                {
                    // keep the state
                }
                else
                {
                    state = ByteSizeState.ERROR;
                }
                break;
            case FRACTION:
                if(c >= '0' && c <= '9')
                {
                    size += (c - '0') * order;
                    order *= 10L;
                }
                else if(c == ',' || c =='.')
                {
                    state = ByteSizeState.NUMBER;
                    sizeFraction = Math.round((double)(size * multiplier) / (double)order); 
                                    // yes, very big sizes will be wrong
                    size = 0L;
                    order = 1L;
                }
                else
                {
                    state = ByteSizeState.ERROR;
                }
                break;
            case NUMBER:
                if(c >= '0' && c <= '9')
                {
                    size += (c - '0') * order;
                    order *= 10;
                }
                else
                {
                    state = ByteSizeState.ERROR;
                }
                break;
            default:
                state = ByteSizeState.ERROR;
                
            }
        }
        
        if(state == ByteSizeState.FRACTION || state == ByteSizeState.NUMBER)
        {
            return size * multiplier + sizeFraction;
        }
        else
        {
            return -1L; // error
        }
    }
    
}

