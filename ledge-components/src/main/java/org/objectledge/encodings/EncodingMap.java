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

import java.util.HashMap;

/**
 * EncodingMap is a convenience class which handles conversions between IANA encoding names and Java
 * encoding names, and vice versa.
 * <p>Data derived from Xerces-J 2 <code>org.apache.xerces.util.EncodingMap</code> class.
 * As required, license attached below:
 * </p>
 * <pre>
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * </pre>
 * 
 * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: EncodingMap.java,v 1.3 2005-02-10 17:46:55 rafal Exp $
 */
public class EncodingMap
{
    /** IANA to Java map */
    private static final HashMap IANA_2_JAVA_MAP = new HashMap();
    /** Java to IANA map */
    private static final HashMap JAVA_2_IANA_MAP = new HashMap();

    static {
        // add IANA to Java encoding mappings.
        IANA_2_JAVA_MAP.put("BIG5",            "Big5");
        IANA_2_JAVA_MAP.put("CSBIG5",            "Big5");
        IANA_2_JAVA_MAP.put("CP037",    "CP037");
        IANA_2_JAVA_MAP.put("IBM037",    "CP037");
        IANA_2_JAVA_MAP.put("CSIBM037",    "CP037");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-US",    "CP037");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-CA",    "CP037");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-NL",    "CP037");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-WT",    "CP037");
        IANA_2_JAVA_MAP.put("IBM277",    "CP277");
        IANA_2_JAVA_MAP.put("CP277",    "CP277");
        IANA_2_JAVA_MAP.put("CSIBM277",    "CP277");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-DK",    "CP277");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-NO",    "CP277");
        IANA_2_JAVA_MAP.put("IBM278",    "CP278");
        IANA_2_JAVA_MAP.put("CP278",    "CP278");
        IANA_2_JAVA_MAP.put("CSIBM278",    "CP278");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-FI",    "CP278");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-SE",    "CP278");
        IANA_2_JAVA_MAP.put("IBM280",    "CP280");
        IANA_2_JAVA_MAP.put("CP280",    "CP280");
        IANA_2_JAVA_MAP.put("CSIBM280",    "CP280");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-IT",    "CP280");
        IANA_2_JAVA_MAP.put("IBM284",    "CP284");
        IANA_2_JAVA_MAP.put("CP284",    "CP284");
        IANA_2_JAVA_MAP.put("CSIBM284",    "CP284");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-ES",    "CP284");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-GB",    "CP285");
        IANA_2_JAVA_MAP.put("IBM285",    "CP285");
        IANA_2_JAVA_MAP.put("CP285",    "CP285");
        IANA_2_JAVA_MAP.put("CSIBM285",    "CP285");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-FR",    "CP297");
        IANA_2_JAVA_MAP.put("IBM297",    "CP297");
        IANA_2_JAVA_MAP.put("CP297",    "CP297");
        IANA_2_JAVA_MAP.put("CSIBM297",    "CP297");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-AR1",   "CP420");
        IANA_2_JAVA_MAP.put("IBM420",    "CP420");
        IANA_2_JAVA_MAP.put("CP420",    "CP420");
        IANA_2_JAVA_MAP.put("CSIBM420",    "CP420");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-HE",    "CP424");
        IANA_2_JAVA_MAP.put("IBM424",    "CP424");
        IANA_2_JAVA_MAP.put("CP424",    "CP424");
        IANA_2_JAVA_MAP.put("CSIBM424",    "CP424");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-CH",    "CP500");
        IANA_2_JAVA_MAP.put("IBM500",    "CP500");
        IANA_2_JAVA_MAP.put("CP500",    "CP500");
        IANA_2_JAVA_MAP.put("CSIBM500",    "CP500");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-CH",    "CP500");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-BE",    "CP500");
        IANA_2_JAVA_MAP.put("IBM868",    "CP868");
        IANA_2_JAVA_MAP.put("CP868",    "CP868");
        IANA_2_JAVA_MAP.put("CSIBM868",    "CP868");
        IANA_2_JAVA_MAP.put("CP-AR",        "CP868");
        IANA_2_JAVA_MAP.put("IBM869",    "CP869");
        IANA_2_JAVA_MAP.put("CP869",    "CP869");
        IANA_2_JAVA_MAP.put("CSIBM869",    "CP869");
        IANA_2_JAVA_MAP.put("CP-GR",        "CP869");
        IANA_2_JAVA_MAP.put("IBM870",    "CP870");
        IANA_2_JAVA_MAP.put("CP870",    "CP870");
        IANA_2_JAVA_MAP.put("CSIBM870",    "CP870");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-ROECE", "CP870");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-YU",    "CP870");
        IANA_2_JAVA_MAP.put("IBM871",    "CP871");
        IANA_2_JAVA_MAP.put("CP871",    "CP871");
        IANA_2_JAVA_MAP.put("CSIBM871",    "CP871");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-IS",    "CP871");
        IANA_2_JAVA_MAP.put("IBM918",    "CP918");
        IANA_2_JAVA_MAP.put("CP918",    "CP918");
        IANA_2_JAVA_MAP.put("CSIBM918",    "CP918");
        IANA_2_JAVA_MAP.put("EBCDIC-CP-AR2",   "CP918");
        IANA_2_JAVA_MAP.put("IBM01140",    "Cp1140");
        IANA_2_JAVA_MAP.put("CP01140",    "Cp1140");
        IANA_2_JAVA_MAP.put("CCSID01140",    "Cp1140");
        IANA_2_JAVA_MAP.put("IBM01141",    "Cp1141");
        IANA_2_JAVA_MAP.put("CP01141",    "Cp1141");
        IANA_2_JAVA_MAP.put("CCSID01141",    "Cp1141");
        IANA_2_JAVA_MAP.put("IBM01142",    "Cp1142");
        IANA_2_JAVA_MAP.put("CP01142",    "Cp1142");
        IANA_2_JAVA_MAP.put("CCSID01142",    "Cp1142");
        IANA_2_JAVA_MAP.put("IBM01143",    "Cp1143");
        IANA_2_JAVA_MAP.put("CP01143",    "Cp1143");
        IANA_2_JAVA_MAP.put("CCSID01143",    "Cp1143");
        IANA_2_JAVA_MAP.put("IBM01144",    "Cp1144");
        IANA_2_JAVA_MAP.put("CP01144",    "Cp1144");
        IANA_2_JAVA_MAP.put("CCSID01144",    "Cp1144");
        IANA_2_JAVA_MAP.put("IBM01145",    "Cp1145");
        IANA_2_JAVA_MAP.put("CP01145",    "Cp1145");
        IANA_2_JAVA_MAP.put("CCSID01145",    "Cp1145");
        IANA_2_JAVA_MAP.put("IBM01146",    "Cp1146");
        IANA_2_JAVA_MAP.put("CP01146",    "Cp1146");
        IANA_2_JAVA_MAP.put("CCSID01146",    "Cp1146");
        IANA_2_JAVA_MAP.put("IBM01147",    "Cp1147");
        IANA_2_JAVA_MAP.put("CP01147",    "Cp1147");
        IANA_2_JAVA_MAP.put("CCSID01147",    "Cp1147");
        IANA_2_JAVA_MAP.put("IBM01148",    "Cp1148");
        IANA_2_JAVA_MAP.put("CP01148",    "Cp1148");
        IANA_2_JAVA_MAP.put("CCSID01148",    "Cp1148");
        IANA_2_JAVA_MAP.put("IBM01149",    "Cp1149");
        IANA_2_JAVA_MAP.put("CP01149",    "Cp1149");
        IANA_2_JAVA_MAP.put("CCSID01149",    "Cp1149");
        IANA_2_JAVA_MAP.put("EUC-JP",          "EUCJIS");
        IANA_2_JAVA_MAP.put("CSEUCPKDFMTJAPANESE",          "EUCJIS");
        IANA_2_JAVA_MAP.put("EXTENDED_UNIX_CODE_PACKED_FORMAT_FOR_JAPANESE",          "EUCJIS");
        IANA_2_JAVA_MAP.put("EUC-KR",          "KSC5601");
        IANA_2_JAVA_MAP.put("GB2312",          "GB2312");
        IANA_2_JAVA_MAP.put("CSGB2312",          "GB2312");
        IANA_2_JAVA_MAP.put("ISO-2022-JP",     "JIS");
        IANA_2_JAVA_MAP.put("CSISO2022JP",     "JIS");
        IANA_2_JAVA_MAP.put("ISO-2022-KR",     "ISO2022KR");
        IANA_2_JAVA_MAP.put("CSISO2022KR",     "ISO2022KR");
        IANA_2_JAVA_MAP.put("ISO-2022-CN",     "ISO2022CN");

        IANA_2_JAVA_MAP.put("X0201",  "JIS0201");
        IANA_2_JAVA_MAP.put("CSISO13JISC6220JP", "JIS0201");
        IANA_2_JAVA_MAP.put("X0208",  "JIS0208");
        IANA_2_JAVA_MAP.put("ISO-IR-87",  "JIS0208");
        IANA_2_JAVA_MAP.put("X0208dbiJIS_X0208-1983",  "JIS0208");
        IANA_2_JAVA_MAP.put("CSISO87JISX0208",  "JIS0208");
        IANA_2_JAVA_MAP.put("X0212",  "JIS0212");
        IANA_2_JAVA_MAP.put("ISO-IR-159",  "JIS0212");
        IANA_2_JAVA_MAP.put("CSISO159JISX02121990",  "JIS0212");
        IANA_2_JAVA_MAP.put("SHIFT_JIS",       "SJIS");
        IANA_2_JAVA_MAP.put("CSSHIFTJIS",       "SJIS");
        IANA_2_JAVA_MAP.put("MS_KANJI",       "SJIS");
        IANA_2_JAVA_MAP.put("WINDOWS-31J",       "MS932");
        IANA_2_JAVA_MAP.put("CSWINDOWS31J",       "MS932");

        // Add support for Cp1252 and its friends
        IANA_2_JAVA_MAP.put("WINDOWS-1250",   "Cp1250");
        IANA_2_JAVA_MAP.put("WINDOWS-1251",   "Cp1251");
        IANA_2_JAVA_MAP.put("WINDOWS-1252",   "Cp1252");
        IANA_2_JAVA_MAP.put("WINDOWS-1253",   "Cp1253");
        IANA_2_JAVA_MAP.put("WINDOWS-1254",   "Cp1254");
        IANA_2_JAVA_MAP.put("WINDOWS-1255",   "Cp1255");
        IANA_2_JAVA_MAP.put("WINDOWS-1256",   "Cp1256");
        IANA_2_JAVA_MAP.put("WINDOWS-1257",   "Cp1257");
        IANA_2_JAVA_MAP.put("WINDOWS-1258",   "Cp1258");
        IANA_2_JAVA_MAP.put("TIS-620",   "TIS620");

        IANA_2_JAVA_MAP.put("ISO-8859-1",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("ISO-IR-100",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("ISO_8859-1",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("LATIN1",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("CSISOLATIN1",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("L1",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("IBM819",      "ISO8859_1");
        IANA_2_JAVA_MAP.put("CP819",      "ISO8859_1");

        IANA_2_JAVA_MAP.put("ISO-8859-2",      "ISO8859_2");
        IANA_2_JAVA_MAP.put("ISO-IR-101",      "ISO8859_2");
        IANA_2_JAVA_MAP.put("ISO_8859-2",      "ISO8859_2");
        IANA_2_JAVA_MAP.put("LATIN2",      "ISO8859_2");
        IANA_2_JAVA_MAP.put("CSISOLATIN2",      "ISO8859_2");
        IANA_2_JAVA_MAP.put("L2",      "ISO8859_2");

        IANA_2_JAVA_MAP.put("ISO-8859-3",      "ISO8859_3");
        IANA_2_JAVA_MAP.put("ISO-IR-109",      "ISO8859_3");
        IANA_2_JAVA_MAP.put("ISO_8859-3",      "ISO8859_3");
        IANA_2_JAVA_MAP.put("LATIN3",      "ISO8859_3");
        IANA_2_JAVA_MAP.put("CSISOLATIN3",      "ISO8859_3");
        IANA_2_JAVA_MAP.put("L3",      "ISO8859_3");

        IANA_2_JAVA_MAP.put("ISO-8859-4",      "ISO8859_4");
        IANA_2_JAVA_MAP.put("ISO-IR-110",      "ISO8859_4");
        IANA_2_JAVA_MAP.put("ISO_8859-4",      "ISO8859_4");
        IANA_2_JAVA_MAP.put("LATIN4",      "ISO8859_4");
        IANA_2_JAVA_MAP.put("CSISOLATIN4",      "ISO8859_4");
        IANA_2_JAVA_MAP.put("L4",      "ISO8859_4");

        IANA_2_JAVA_MAP.put("ISO-8859-5",      "ISO8859_5");
        IANA_2_JAVA_MAP.put("ISO-IR-144",      "ISO8859_5");
        IANA_2_JAVA_MAP.put("ISO_8859-5",      "ISO8859_5");
        IANA_2_JAVA_MAP.put("CYRILLIC",      "ISO8859_5");
        IANA_2_JAVA_MAP.put("CSISOLATINCYRILLIC",      "ISO8859_5");

        IANA_2_JAVA_MAP.put("ISO-8859-6",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("ISO-IR-127",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("ISO_8859-6",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("ECMA-114",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("ASMO-708",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("ARABIC",      "ISO8859_6");
        IANA_2_JAVA_MAP.put("CSISOLATINARABIC",      "ISO8859_6");

        IANA_2_JAVA_MAP.put("ISO-8859-7",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("ISO-IR-126",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("ISO_8859-7",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("ELOT_928",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("ECMA-118",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("GREEK",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("CSISOLATINGREEK",      "ISO8859_7");
        IANA_2_JAVA_MAP.put("GREEK8",      "ISO8859_7");

        IANA_2_JAVA_MAP.put("ISO-8859-8",      "ISO8859_8");
        IANA_2_JAVA_MAP.put("ISO-8859-8-I",      "ISO8859_8");
        	// added since this encoding only differs w.r.t. presentation
        IANA_2_JAVA_MAP.put("ISO-IR-138",      "ISO8859_8");
        IANA_2_JAVA_MAP.put("ISO_8859-8",      "ISO8859_8");
        IANA_2_JAVA_MAP.put("HEBREW",      "ISO8859_8");
        IANA_2_JAVA_MAP.put("CSISOLATINHEBREW",      "ISO8859_8");

        IANA_2_JAVA_MAP.put("ISO-8859-9",      "ISO8859_9");
        IANA_2_JAVA_MAP.put("ISO-IR-148",      "ISO8859_9");
        IANA_2_JAVA_MAP.put("ISO_8859-9",      "ISO8859_9");
        IANA_2_JAVA_MAP.put("LATIN5",      "ISO8859_9");
        IANA_2_JAVA_MAP.put("CSISOLATIN5",      "ISO8859_9");
        IANA_2_JAVA_MAP.put("L5",      "ISO8859_9");

        IANA_2_JAVA_MAP.put("KOI8-R",          "KOI8_R");
        IANA_2_JAVA_MAP.put("CSKOI8R",          "KOI8_R");
        IANA_2_JAVA_MAP.put("US-ASCII",        "ASCII");
        IANA_2_JAVA_MAP.put("ISO-IR-6",        "ASCII");
        IANA_2_JAVA_MAP.put("ANSI_X3.4-1986",        "ASCII");
        IANA_2_JAVA_MAP.put("ISO_646.IRV:1991",        "ASCII");
        IANA_2_JAVA_MAP.put("ASCII",        "ASCII");
        IANA_2_JAVA_MAP.put("CSASCII",        "ASCII");
        IANA_2_JAVA_MAP.put("ISO646-US",        "ASCII");
        IANA_2_JAVA_MAP.put("US",        "ASCII");
        IANA_2_JAVA_MAP.put("IBM367",        "ASCII");
        IANA_2_JAVA_MAP.put("CP367",        "ASCII");
        IANA_2_JAVA_MAP.put("UTF-8",           "UTF8");
        IANA_2_JAVA_MAP.put("UTF-16",           "Unicode");
        IANA_2_JAVA_MAP.put("UTF-16BE",           "UnicodeBig");
        IANA_2_JAVA_MAP.put("UTF-16LE",           "UnicodeLittle");

        // REVISIT:
        //   j:CNS11643 -> EUC-TW?
        //   ISO-2022-CN? ISO-2022-CN-EXT?

        // add Java to IANA encoding mappings
        //fJava2IANAMap.put("8859_1",    "US-ASCII"); // ?
        JAVA_2_IANA_MAP.put("ISO8859_1",    "ISO-8859-1");
        JAVA_2_IANA_MAP.put("ISO8859_2",    "ISO-8859-2");
        JAVA_2_IANA_MAP.put("ISO8859_3",    "ISO-8859-3");
        JAVA_2_IANA_MAP.put("ISO8859_4",    "ISO-8859-4");
        JAVA_2_IANA_MAP.put("ISO8859_5",    "ISO-8859-5");
        JAVA_2_IANA_MAP.put("ISO8859_6",    "ISO-8859-6");
        JAVA_2_IANA_MAP.put("ISO8859_7",    "ISO-8859-7");
        JAVA_2_IANA_MAP.put("ISO8859_8",    "ISO-8859-8");
        JAVA_2_IANA_MAP.put("ISO8859_9",    "ISO-8859-9");
        JAVA_2_IANA_MAP.put("Big5",      "BIG5");
        JAVA_2_IANA_MAP.put("CP037",     "EBCDIC-CP-US");
        JAVA_2_IANA_MAP.put("CP278",     "EBCDIC-CP-FI");
        JAVA_2_IANA_MAP.put("CP280",     "EBCDIC-CP-IT");
        JAVA_2_IANA_MAP.put("CP284",     "EBCDIC-CP-ES");
        JAVA_2_IANA_MAP.put("CP285",     "EBCDIC-CP-GB");
        JAVA_2_IANA_MAP.put("CP297",     "EBCDIC-CP-FR");
        JAVA_2_IANA_MAP.put("CP420",     "EBCDIC-CP-AR1");
        JAVA_2_IANA_MAP.put("CP424",     "EBCDIC-CP-HE");
        JAVA_2_IANA_MAP.put("CP500",     "EBCDIC-CP-CH");
        JAVA_2_IANA_MAP.put("CP870",     "EBCDIC-CP-ROECE");
        JAVA_2_IANA_MAP.put("CP871",     "EBCDIC-CP-IS");
        JAVA_2_IANA_MAP.put("CP918",     "EBCDIC-CP-AR2");
        JAVA_2_IANA_MAP.put("Cp01140",     "IBM01140");
        JAVA_2_IANA_MAP.put("Cp01141",     "IBM01141");
        JAVA_2_IANA_MAP.put("Cp01142",     "IBM01142");
        JAVA_2_IANA_MAP.put("Cp01143",     "IBM01143");
        JAVA_2_IANA_MAP.put("Cp01144",     "IBM01144");
        JAVA_2_IANA_MAP.put("Cp01145",     "IBM01145");
        JAVA_2_IANA_MAP.put("Cp01146",     "IBM01146");
        JAVA_2_IANA_MAP.put("Cp01147",     "IBM01147");
        JAVA_2_IANA_MAP.put("Cp01148",     "IBM01148");
        JAVA_2_IANA_MAP.put("Cp01149",     "IBM01149");
        JAVA_2_IANA_MAP.put("EUCJIS",    "EUC-JP");
        JAVA_2_IANA_MAP.put("GB2312",    "GB2312");
        JAVA_2_IANA_MAP.put("ISO2022KR", "ISO-2022-KR");
        JAVA_2_IANA_MAP.put("ISO2022CN", "ISO-2022-CN");
        JAVA_2_IANA_MAP.put("JIS",       "ISO-2022-JP");
        JAVA_2_IANA_MAP.put("KOI8_R",    "KOI8-R");
        JAVA_2_IANA_MAP.put("KSC5601",   "EUC-KR");
        JAVA_2_IANA_MAP.put("SJIS",      "SHIFT_JIS");
        JAVA_2_IANA_MAP.put("MS932",      "WINDOWS-31J");
        JAVA_2_IANA_MAP.put("UTF8",      "UTF-8");
        JAVA_2_IANA_MAP.put("Unicode",   "UTF-16");
        JAVA_2_IANA_MAP.put("UnicodeBig",   "UTF-16BE");
        JAVA_2_IANA_MAP.put("UnicodeLittle",   "UTF-16LE");
        JAVA_2_IANA_MAP.put("JIS0201",  "X0201");
        JAVA_2_IANA_MAP.put("JIS0208",  "X0208");
        JAVA_2_IANA_MAP.put("JIS0212",  "ISO-IR-159");

    } // <clinit>()

    /** Default constructor. */
    private EncodingMap()
    {
        // static access only
    }

    /**
     * Returns the Java encoding name for the specified IANA encoding name.
     *
     * @param ianaEncoding  The IANA encoding name.
     * @return              The IANA2JavaMapping value
     */
    public static String getIANA2JavaMapping( String ianaEncoding )
    {
        return ( String ) IANA_2_JAVA_MAP.get( ianaEncoding );
    }

    /**
     * Returns the IANA encoding name for the specified Java encoding name.
     *
     * @param javaEncoding  The Java encoding name.
     * @return              The Java2IANAMapping value
     */
    public static String getJava2IANAMapping( String javaEncoding )
    {
        return ( String ) JAVA_2_IANA_MAP.get( javaEncoding );
    }
}

