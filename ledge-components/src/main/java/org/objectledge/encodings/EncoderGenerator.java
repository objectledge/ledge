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

package org.objectledge.encodings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Generator for encoding converter tables.
 *
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: EncoderGenerator.java,v 1.3 2004-03-12 15:49:16 zwierzem Exp $
 */
public class EncoderGenerator
{
    private static int prefixMask = 0xff00;
    private static int suffixMask = 0x00ff;

	private EncoderGenerator()
	{
	}

    /**
     * Generates source code of the CharsetEncoder.
     *
     * @param out writer to which generated source code is written
     * @param encodingName name of encoding for which encoder is generated
     * @param inputTable input table of mappings
     * @throws IOException on any problems with source code writing
     */
    public static void generateTables(Writer out, String encodingName, MappingEntry[] inputTable)
    throws IOException
    {
        // 1. sort the array by unicode values
        Arrays.sort(inputTable,
                new EncoderGenerator.UnicodeComparator());

        // 2. build mappings groups upon most significant bytes of their unicode
        // values
        HashMap groups = new HashMap();
        for(int i = 0; i < inputTable.length; i++)
        {
            short prefixValue = (short)(inputTable[i].getUnicodeCode() & (short)(prefixMask));
            Short prefix = new Short(prefixValue);
            // get or create group array
            HashMap group = (HashMap)(groups.get(prefix));
            if(group == null)
            {
                group = new HashMap(256);
            }
            groups.put(prefix, group);

            // fill group map
            Short suffix = new Short((short)(inputTable[i].getUnicodeCode() & (short)suffixMask));
            group.put(suffix, inputTable[i]);
        }

        // 3. Build indexes
        int[] prefixIndex = new int[256];
        MappingEntry[] suffixIndex = new MappingEntry[256 * (groups.size() + 1)];

        int nullIndex = suffixIndex.length - 256;
        int definedPrefixIndex = 0;

        for(int i = 0; i < 256; i++)
        {
            Short prefix = new Short((short)((i << 8) & prefixMask));

            if(groups.containsKey(prefix))
            {
                prefixIndex[i] = definedPrefixIndex;
                // fill group suffixes
                HashMap group = (HashMap)(groups.get(prefix));

                for(int j = 0; j < 256; j++)
                {
                    Short suffix = new Short((short)j);
                    if(group.containsKey(suffix))
                    {
                        MappingEntry entry = (MappingEntry)(group.get(suffix));
                        suffixIndex[definedPrefixIndex + j] = entry;
                    }
                }
                // increase index
                definedPrefixIndex += 256;
            }
            else
            {
                prefixIndex[i] = nullIndex;
            }
        }

        // 4. Print indexes
		out.write("// \n");
		out.write(
			"//		Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.\n"); 
		out.write("//		All rights reserved.\n"); 
		out.write("// \n");
		out.write("//		Redistribution and use in source and binary forms, with or without " +
			"modification,\n");  
		out.write("//		are permitted provided that the following conditions are met:\n"); 
		out.write("// \n");
		out.write("//		* Redistributions of source code must retain the above copyright " +
			"notice,\n");  
		out.write("//		this list of conditions and the following disclaimer.\n");
		out.write("//		* Redistributions in binary form must reproduce the above copyright " +
			"notice,\n");
		out.write("//		this list of conditions and the following disclaimer in the " +
			"documentation\n");  
		out.write("//		and/or other materials provided with the distribution.\n");
		out.write("//		* Neither the name of the Caltha - Gajda, Krzewski, Mach, " +
			"Potempski Sp.J.\n");
		out.write("//		nor the names of its contributors may be used to endorse or promote " +
			"products\n");  
		out.write("//		derived from this software without specific prior written " +
			"permission.\n"); 
		out.write("// \n");
		out.write("//		THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS " +
			"\"AS IS\"\n");
		out.write("//		AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT " +
			"LIMITED TO, THE IMPLIED\n");
		out.write("//		WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE " +
			"ARE DISCLAIMED.\n");
		out.write("//		IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR " +
			"ANY DIRECT,\n");
		out.write("//		INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES " +
			"(INCLUDING,\n");
		out.write("//		BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; " +
			"LOSS OF USE, DATA,\n");
		out.write("//		OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY " +
			"THEORY OF LIABILITY,\n");
		out.write("//		WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE " +
			"OR OTHERWISE)\n");  
		out.write("//		ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED " +
			"OF THE\n");  
		out.write("//		POSSIBILITY OF SUCH DAMAGE.\n"); 
		out.write("//\n");
        out.write("package pl.caltha.encodings.encoders;\n");
        out.write("\n");
        out.write("/**\n");
        out.write(" * Encoder for "+encodingName+" character set.\n");
        out.write(" *\n");
        out.write(" * @author <a href=\"mailto:dgajda@caltha.pl\">Damian Gajda</a>\n");
        out.write(" * @version " +        	"$Id: EncoderGenerator.java,v 1.3 2004-03-12 15:49:16 zwierzem Exp $\n");
        out.write(" */\n");
        out.write("public class CharEncoder"+encodingName+"\n");
        out.write("         extends CharEncoder\n");
        out.write("{\n");
        out.write("\n");
        out.write("    /** Array indexed by characters most significant byte.\n");
        out.write("     *  It contains indexes for a second table containing values mapped. */\n");
        out.write("    private static final char[][] PREFIX_INDEX = {\n");
        for(int i = 0; i < prefixIndex.length; i++)
        {
            out.write("0x"+Integer.toHexString(prefixIndex[i]));
            if(i != prefixIndex.length - 1)
            {
                out.write(", ");
            }

            if(i>1 && (i+1)%8==0)
            {
                out.write("\n");
            }
        }
        out.write("    };\n\n");

        out.write("    /** Array indexed both by prefixIndex array and characters least " +
        	"significant byte.\n");
        out.write("     *  It contains values mapped onto characters. */\n");
        out.write("    private static final char[][] SUFFIX_INDEX = {\n");
        for(int i = 0; i < suffixIndex.length; i++)
        {
            char[] value = null;
            if(suffixIndex[i] != null)
            {
                value = suffixIndex[i].getValue().toCharArray();
            }

            if(value != null)
            {
                out.write("{ ");
                for(int j = 0; j < value.length; j++)
                {
                    //
                    out.write("0x"+Integer.toHexString(value[j]));
                    //
                    if(j != value.length - 1)
                    {
                        out.write(", ");
                    }
                }
                out.write(" }");
            }
            else
            {
                out.write("null");
            }

            if(i != suffixIndex.length - 1)
            {
                out.write(",");
            }

            if(i>1 && (i+1)%8==0)
            {
                out.write("\n");
            }
        }
        out.write("    };\n\n");

        out.write("    /** Constructor. */\n");
        out.write("    public CharEncoder"+encodingName+"()\n");
        out.write("    {\n");
        out.write("        this.encodingName = \""+encodingName+"\";\n");
        out.write("        ((CharEncoder)this).prefixIndex = PREFIX_INDEX;\n");
        out.write("        ((CharEncoder)this).suffixIndex = SUFFIX_INDEX;\n");
        out.write("    }\n");
        out.write("}\n");
    }


    /**
     * The main program for the EncodingConverterTableGenerator class
     *
     * @param argv  The command line arguments
     */
    public static void main(String[] argv)
    {
        for(int i = 0; i < argv.length; i++)
        {
            String inputFileName = argv[i];

            String encodingName = 
            	inputFileName.substring(inputFileName.lastIndexOf('/')+1, inputFileName.length());
            encodingName = encodingName.substring(0, encodingName.lastIndexOf('.'));
            if(encodingName.startsWith("8859"))
            {
                encodingName = "ISO" + encodingName;
            }
            encodingName = encodingName.replace('-', '_');

            LineNumberReader inReader = null;
            try
            {
                inReader = new LineNumberReader( 
                	new InputStreamReader( new FileInputStream(inputFileName), "US-ASCII" ) );
            }
            catch(FileNotFoundException e)
            {
                System.err.println("Could not find a file named: "+inputFileName);
                return;
            }
            catch(UnsupportedEncodingException e)
            {
                // WARN: should never happen
                System.err.println("Wrong encoding");
                return;
            }

            ArrayList mappingEntries = new ArrayList(512);

            try
            {
                for(String line = inReader.readLine(); line != null; line = inReader.readLine())
                {
                    // avoid comments and empty lines
                    if(line.length() == 0 || line.startsWith("#"))
                    {
                        continue;
                    }

                    // split the incoming line
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    ArrayList fields = new ArrayList(4);
                    while(tokenizer.hasMoreTokens())
                    {
                        fields.add(tokenizer.nextToken());
                    }

                    // avoid badly constructed lines
                    if(fields.size() < 2)
                    {
                        continue;
                    }

                    // get field values
                    String mapping = (String)( fields.get(0) );
                    String unicodeString = (String)( fields.get(1) );

                    // avoid undefined unicode values
                    if(unicodeString.startsWith("#"))
                    {
                        continue;
                    }

                    int unicodeCode = 0;
                    try
                    {
                        unicodeCode = Integer.decode( unicodeString ).intValue();
                    }
                    catch(NumberFormatException e)
                    {
                        System.err.println("Number format exception in unicode code '"+ 
                        	fields.get(1) +"' in line: "+inReader.getLineNumber());
                        return;
                    }

                    MappingEntry me = null;
                    // check if a line contains a named entity
                    if(Character.isLetter(mapping.charAt(0)))
                    {
                        me = new MappingEntry(unicodeCode, mapping);
                    }
                    // or it is a byte value
                    else
                    {
                        me = new MappingEntry(unicodeCode, Integer.decode(mapping).intValue());
                    }

                    mappingEntries.add(me);
                }

                // close the read file
                inReader.close();
            }
            catch(IOException e)
            {
                System.err.println("Errors reading a file named: "+inputFileName);
                return;
            }

            // preapre table to generate class code
            MappingEntry[] outTable = new MappingEntry[mappingEntries.size()];
            for(int j=0, s=mappingEntries.size(); j<s; j++)
            {
                outTable[j] = (MappingEntry)(mappingEntries.get(j));
            }

            // prepare file to write to
            String outputFileName = "CharEncoder"+encodingName+".java";
            FileWriter outWriter = null;
            try
            {
                outWriter = new FileWriter(outputFileName);
            }
            catch(IOException e)
            {
                System.err.println("Errors creating a file named: "+outputFileName);
                return;
            }


            try
            {
                // run generation for a given table
                generateTables(outWriter, encodingName, outTable);
                // clean up
                outWriter.close();
            }
            catch(IOException e)
            {
                System.err.println("Errors writing a file named: "+outputFileName);
                return;
            }
        }
    }


    /**
     * Description of the Class
     *
     * @author    damian
     * @version
     */
    public static class UnicodeComparator implements Comparator
    {
        /**
         * Description of the Method
         *
         * @param o1  Description of Parameter
         * @param o2  Description of Parameter
         * @return    Description of the Returned Value
         */
        public int compare(Object o1, Object o2)
        {
            MappingEntry m1 = (MappingEntry)o1;
            MappingEntry m2 = (MappingEntry)o2;

            if(m1.getUnicodeCode() < m2.getUnicodeCode())
            {
                return -1;
            }

            if(m1.getUnicodeCode() == m2.getUnicodeCode())
            {
                return 0;
            }

            return 1;
        }
    }
}
