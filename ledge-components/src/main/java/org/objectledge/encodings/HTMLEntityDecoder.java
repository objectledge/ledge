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
package org.objectledge.encodings;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.objectledge.encodings.encoders.CharEncoderHTMLEntity;

/**
 * Tool for parsing text with characters encoded as HTML entities. All recognized entities
 * (including numeric ones) are converted to single unicode characters.
 * 
 * <p>This tool is completely thread safe and does not keep any internal state.</p> 
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLEntityDecoder.java,v 1.3 2005-02-08 00:35:34 rafal Exp $
 */
public class HTMLEntityDecoder
{
	/** Entity encoder is used to reverse encode detected named and numeric entities. */
	private static final CharEncoderHTMLEntity HTML_ENTITY_ENCODER = new CharEncoderHTMLEntity();

	/** Default automata state - characters pass through. */ 
	private static final int DEFAULT = 0;
	/** Maybe entity automata state - depending on a character we may start a named or numeric
	 * entity or get back to default state. */ 
	private static final int MAYBE_ENTITY = 1;
	/** Maybe start numeric entity automata state - depending on a character we may start a decimal
	 * or hexadecimal numeric entity or get back to default state. */ 
	private static final int MAYBE_START_NUMERIC_ENTITY = 2;
	/** Maybe in a hexadecimal numeric entity automata state - depending on a character we may
	 * collect characters of a hexadecimal numeric entity or get back to default state. */ 
	private static final int MAYBE_IN_NUMHEX_ENTITY = 3;
	/** Maybe in a decimal numeric entity automata state - depending on a character we may collect
	 * characters of a decimal numeric entity or get back to default state. */ 
	private static final int MAYBE_IN_NUMDEC_ENTITY = 4;
	/** Maybe in a named entity automata state - depending on a character we may collect
	 * characters of a named entity or get back to default state. */ 
	private static final int MAYBE_IN_NAMED_ENTITY = 5;

	/** 
	 * Decodes a given string and returns a string with entities converted to unicode characters.
	 * This operation compresses strings. Example:
	 * <pre>
	 * input  = " &amp;amp; &amp;quot; &amp;euml; &amp;Oacute; &amp;#211; "+
	 * 		"&amp;sdsd&amp;#1233&amp;amp; &amp;euml &amp;notsgmlentity; "
	 * output = " &amp; " � � � &amp;sdsd&amp;#1233&amp; &amp;euml &amp;notsgmlentity; "
	 * </pre>
	 * 
	 * @param input input string with characters encoded as named or numeric entities.
	 * @return string with entities converted to single unicode characters.
	 */
	public String decode(String input)
	{
		return decode(input, false);
	}

	/** 
	 * Decodes a string and returns a string with entities converted to unicode characters,
	 * avoids converting core XML entities.
	 * This operation compresses strings. Example:
	 * <pre>
	 * input  = " &amp;amp; &amp;quot; &amp;lt; &amp;euml; &amp;Oacute;"+ 
	 * 		"&amp;#211; &amp;sdsd&#1233&amp;amp; &amp;euml &amp;notsgmlentity; "
	 * output = " &amp;amp; &amp;quot; &amp;lt; � � � &amp;sdsd&amp;#1233&amp;amp; "+
	 * 		"&amp;euml &amp;notsgmlentity; "
	 * </pre>
	 * 
	 * @param input input string with characters encoded as named or numeric entities.
	 * @return string with entities converted to single unicode characters.
	 */
	public String decodeXML(String input)
	{
		return decode(input, true);
	}

	private String decode(String input, boolean keepXmlEntities)
	{
		try
		{
			StringWriter writer = new StringWriter(input.length());
			decode(new StringReader(input), writer, keepXmlEntities);
			return writer.toString();
		}
		catch (IOException e)
		{
			// should not happen
			return null;
		}
	}

	/** 
	 * Decodes a given character stream and writes a string with entities converted to unicode
	 * characters into the given writer.
	 * 
	 * @param inputReader input character stream with characters encoded as named or numeric
	 *  entities, this method CLOSES the given reader.
	 * @param outputWriter output character stream with entities converted to single unicode
	 *  characters.
	 * @throws IOException on errors in reading or writing characters
	 */
	public void decode(Reader inputReader, Writer outputWriter)
		throws IOException
	{
		decode(inputReader, outputWriter, false);
	}

	/** 
	 * Deodes a given character stream and writes a string with entities converted to unicode
	 * characters into the given writer, avoids converting core XML entities.
	 * 
	 * @param inputReader input character stream with characters encoded as named or numeric
	 *  entities, this method CLOSES the given reader.
	 * @param outputWriter output character stream with entities converted to single unicode
	 *  characters.
	 * @throws IOException on errors in reading or writing characters
	 */
	public void decodeXML(Reader inputReader, Writer outputWriter)
		throws IOException
	{
		decode(inputReader, outputWriter, true);
	}

	private void decode(Reader inputReader, Writer outputWriter, boolean keepXmlEntities)
		throws IOException 
	{
		StringBuffer entity = new StringBuffer(16); // sufficient for all entities
        
		PushbackReader reader = new PushbackReader(inputReader);
		int state = DEFAULT;
		int chr;
		while((chr = reader.read()) != -1)
		{
			char c = (char)chr;
			switch(state)
			{
				case DEFAULT:
					if(c == '&')
					{
						entity.append(c);
						state = MAYBE_ENTITY;
					}
					else
					{
						outputWriter.write(c);
					}
				break;
				case MAYBE_ENTITY:
					entity.append(c);
					if(c == '#')
					{
						state = MAYBE_START_NUMERIC_ENTITY;
					}
					else if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
					{
						state = MAYBE_IN_NAMED_ENTITY;
					}
					else
					{
						state = notAnEntity(reader, c, entity, outputWriter);
					}
				break;
				case MAYBE_START_NUMERIC_ENTITY:
					entity.append(c);
					if(c >= '0' && c <= '9')
					{
						state = MAYBE_IN_NUMDEC_ENTITY;
					}
					else if(c == 'x')
					{
						state = MAYBE_IN_NUMHEX_ENTITY;
					}
					else
					{
						state = notAnEntity(reader, c, entity, outputWriter);
					}
				break;
				case MAYBE_IN_NUMDEC_ENTITY:
					entity.append(c);
					if(c == ';')
					{
						state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
					}
					else if(entity.length() > 8 || !(c >= '0' && c <= '9'))
					{
						state = notAnEntity(reader, c, entity, outputWriter);
					}
				break;
				case MAYBE_IN_NUMHEX_ENTITY:
					entity.append(c);
					if(c == ';')
					{
						state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
					}
					else if(entity.length() > 8 || 
						!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F'))
					{
						state = notAnEntity(reader, c, entity, outputWriter);
					}
				break;
				case MAYBE_IN_NAMED_ENTITY:
					entity.append(c);
					if(c == ';')
					{
						state = dumpEntity(reader, c, entity, outputWriter, keepXmlEntities);
					}
					else if(entity.length() > 12 || // should be 10
						!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'))
					{
						state = notAnEntity(reader, c, entity, outputWriter);
					}
				break;
				default:
					throw new IllegalStateException("the automata should not enter this state");
			}
		}
        
		if(entity.length() > 0)
		{
			outputWriter.write(entity.toString());
		}
        
		inputReader.close(); // TODO should we do it?
	}

	private int notAnEntity(PushbackReader reader, char c, StringBuffer entity, Writer writer)
		throws IOException
	{
		reader.unread(c);
		entity.setLength(entity.length()-1);
		writer.write(entity.toString());
		entity.setLength(0);  // clear out entity buffer
		return DEFAULT;
	}

	private int dumpEntity(PushbackReader reader, char c, StringBuffer entity, Writer writer,
		boolean keepXmlEntities)
		throws IOException
	{
		int eC = HTML_ENTITY_ENCODER.entityCode(entity.toString());
		if(eC != -1)
		{
			if(keepXmlEntities && (eC == '"' || eC == '\'' || eC == '&' || eC == '<' || eC == '>'))
			{
				return notAnEntity(reader, c, entity, writer);
			}
			writer.write((char)eC);
			entity.setLength(0); // clear out entity buffer
			return DEFAULT;                        
		}
		else
		{
			return notAnEntity(reader, c, entity, writer);
		}
	}
}
