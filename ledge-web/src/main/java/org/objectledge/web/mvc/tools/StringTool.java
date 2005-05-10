// 
// Copyright (c) 2003, 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.web.mvc.tools;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.objectledge.utils.StringUtils;

/**
 * The string manipulation tool.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: StringTool.java,v 1.14 2005-05-10 06:49:21 rafal Exp $
 */
public class StringTool
{
    /**
     * The constructor. 
     */
    public StringTool()
    {
        super();
    }

    /**
     * Trims the string to the specified lengtht.
     * 
     * @param str the string.
     * @param maxLength maximum number of characters preserved.
     * @return the trimmed string.
     */
    public String shorten(String str, int maxLength)
    {
        if(str.length() > maxLength)
        {
            StringBuilder buf = new StringBuilder(str);
            buf.setLength(maxLength-1);
            buf.append('\u2026'); // the ellipsis character - ie. 3 dots ...
            return buf.toString();
        }
        return str;
    }

    /**
     * Trims the string to the specified lengtht.
     * 
     * @param source the string.
     * @param length maximum number of characters preserved.
     * @param suffix suffix to append if the string is acutally trimmed.
     * @return the trimmed string.
     */
    public String shortenString(String source, int length, String suffix)
    {
        if(source == null || length >= source.length())
        {
            return source;
        }
        return source.substring(0, length)+suffix;
    }
    
    /** 
     * See the StringUtils class.
     * 
     * @param input the input string.
     * @return the output.
     */
    public String toOctalUnicode(String input)
    {
        return StringUtils.toOctalUnicode(input);
    }
    
    /**
	 * Convert int to long value.
	 *
	 * @param value the integer value.
	 * @return the long wrapper.
     */
    public Long getLongValue(int value)
    {
    	return new Long(value);
    }

    /**
	 * Convert string to long value.
	 *
	 * @param value the string value.
	 * @return the long wrapper.
     */
    public Long getLongValue(String value)
    {
    	return new Long(value);
    }
    
    /**
	 * Convert int to string value.
	 *
	 * @param value the int value.
	 * @return the string wrapper.
     */
    public String getString(int value)
    {
    	return String.valueOf(value);
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
    public String wrap(String in, int width)
    {
        return StringUtils.wrap(in, width); 
    }
    
    /**
     * Get array size.
     * 
     * @param obj the array.
     * @return the size.
     */
    public int getArraySize(Object obj)
    {
        if(obj == null)
        {
            return 0;
        }
        if(obj.getClass().getComponentType() == null)
        {
            return 0;
        }
        Object[] objs = (Object[])obj;
        return objs.length;
    }
    
    /**
     * Format size value in b, kb, Mb
     *
     * @param value the size in bytes.
     * @return the size as string with a proper unit suffix
     */
    public String bytesSize(int value)
    {
        return bytesSize((long)value);
    }
    
    /**
     * Format size value in b, kb, Mb
     *
     * @param value the size in bytes.
     * @return the size as string with a proper unit suffix
     */
    public String bytesSize(long value)
    {
        StringBuilder b = new StringBuilder();
        if(value < 1024L)
        {
            return b.append(value).append("b").toString();
        }
        double floatValue = (double)value;
        if(value < 1048576L)
        {
            b.append(floatValue/1024);
            int index = b.indexOf(".");
            if(index != -1 && b.length() > index+3)
            {
                b.setLength(index+3);
            }
            return b.append("kb").toString();
        }
        b.append(floatValue/1048576);
        int index = b.indexOf(".");
        if(index != -1 && b.length() > index+3)
        {
            b.setLength(index+3);
        }
        return b.append("Mb").toString();        
    }
    
    /**
     * Convert newlines in the string into <code>&lt;br /&gt;</code> tags.
     *
     * @param s the string to process.
     * @return processed string.
     */
    public String htmlLineBreaks(String s)
    {
        return StringUtils.htmlLineBreaks(s);
    }
 
    /**
     * Converts a string array into a list which is more useful in Velocity. 
     * 
     * @param strings a String array.
     * @return a List of Strings.
     */
    public List<String> arrayToList(String[] strings)
    {
        return Arrays.asList(strings);
    }
    
    /**
     * Returns a copy of the String list sorted according to the given locale.
     * 
     * @param input a list of string.
     * @param locale to use for sorting. 
     * @return a sorted copy
     */
    public List<String> sort(Collection<String> input, Locale locale)
    {
        Collator collator = Collator.getInstance(locale);
        List<String> result = new ArrayList<String>(input);
        Collections.sort(result, collator);
        return result;
    }

    /**
     * Formats interval as a human readable string.
     * 
     * @param interval the interval in seconds.
     * @return a human readable string.
     */
    public String secondsInterval(long interval)
    {
        return StringUtils.formatInterval(interval);
    }

    /**
     * Formats interval as a human readable string.
     * 
     * @param interval the interval in seconds.
     * @return a human readable string.
     */
    public String millisecondsInterval(long interval)
    {
        return StringUtils.formatInterval(interval/1000);
    }

    /**
     * Formats interval as a human readable string.
     * 
     * @param interval the interval in seconds.
     * @return a human readable string.
     */
    public String nanosecondsInterval(long interval)
    {
        return StringUtils.formatInterval(interval/1000000000);
    }
}
