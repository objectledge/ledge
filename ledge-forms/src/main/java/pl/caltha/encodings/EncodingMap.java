package pl.caltha.encodings;

import java.util.HashMap;

/**
 * EncodingMap is a convenience class which handles conversions between IANA encoding names and Java
 * encoding names, and vice versa.
 * Data derived from Xerces-J 2 <code>org.apache.xerces.util.EncodingMap</code> class.
 *
 * @author    <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version   $Id: EncodingMap.java,v 1.2 2005-02-10 17:49:13 rafal Exp $
 */
public class EncodingMap
{
    /** fIANA2JavaMap */
    protected final static HashMap fIANA2JavaMap = new HashMap();

    /** fJava2IANAMap */
    protected final static HashMap fJava2IANAMap = new HashMap();

    static {

        // add IANA to Java encoding mappings.
        fIANA2JavaMap.put("BIG5",            "Big5");
        fIANA2JavaMap.put("CSBIG5",            "Big5");
        fIANA2JavaMap.put("CP037",    "CP037");
        fIANA2JavaMap.put("IBM037",    "CP037");
        fIANA2JavaMap.put("CSIBM037",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-US",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-CA",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-NL",    "CP037");
        fIANA2JavaMap.put("EBCDIC-CP-WT",    "CP037");
        fIANA2JavaMap.put("IBM277",    "CP277");
        fIANA2JavaMap.put("CP277",    "CP277");
        fIANA2JavaMap.put("CSIBM277",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-DK",    "CP277");
        fIANA2JavaMap.put("EBCDIC-CP-NO",    "CP277");
        fIANA2JavaMap.put("IBM278",    "CP278");
        fIANA2JavaMap.put("CP278",    "CP278");
        fIANA2JavaMap.put("CSIBM278",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-FI",    "CP278");
        fIANA2JavaMap.put("EBCDIC-CP-SE",    "CP278");
        fIANA2JavaMap.put("IBM280",    "CP280");
        fIANA2JavaMap.put("CP280",    "CP280");
        fIANA2JavaMap.put("CSIBM280",    "CP280");
        fIANA2JavaMap.put("EBCDIC-CP-IT",    "CP280");
        fIANA2JavaMap.put("IBM284",    "CP284");
        fIANA2JavaMap.put("CP284",    "CP284");
        fIANA2JavaMap.put("CSIBM284",    "CP284");
        fIANA2JavaMap.put("EBCDIC-CP-ES",    "CP284");
        fIANA2JavaMap.put("EBCDIC-CP-GB",    "CP285");
        fIANA2JavaMap.put("IBM285",    "CP285");
        fIANA2JavaMap.put("CP285",    "CP285");
        fIANA2JavaMap.put("CSIBM285",    "CP285");
        fIANA2JavaMap.put("EBCDIC-CP-FR",    "CP297");
        fIANA2JavaMap.put("IBM297",    "CP297");
        fIANA2JavaMap.put("CP297",    "CP297");
        fIANA2JavaMap.put("CSIBM297",    "CP297");
        fIANA2JavaMap.put("EBCDIC-CP-AR1",   "CP420");
        fIANA2JavaMap.put("IBM420",    "CP420");
        fIANA2JavaMap.put("CP420",    "CP420");
        fIANA2JavaMap.put("CSIBM420",    "CP420");
        fIANA2JavaMap.put("EBCDIC-CP-HE",    "CP424");
        fIANA2JavaMap.put("IBM424",    "CP424");
        fIANA2JavaMap.put("CP424",    "CP424");
        fIANA2JavaMap.put("CSIBM424",    "CP424");
        fIANA2JavaMap.put("EBCDIC-CP-CH",    "CP500");
        fIANA2JavaMap.put("IBM500",    "CP500");
        fIANA2JavaMap.put("CP500",    "CP500");
        fIANA2JavaMap.put("CSIBM500",    "CP500");
        fIANA2JavaMap.put("EBCDIC-CP-CH",    "CP500");
        fIANA2JavaMap.put("EBCDIC-CP-BE",    "CP500"); 
        fIANA2JavaMap.put("IBM868",    "CP868");
        fIANA2JavaMap.put("CP868",    "CP868");
        fIANA2JavaMap.put("CSIBM868",    "CP868");
        fIANA2JavaMap.put("CP-AR",        "CP868");
        fIANA2JavaMap.put("IBM869",    "CP869");
        fIANA2JavaMap.put("CP869",    "CP869");
        fIANA2JavaMap.put("CSIBM869",    "CP869");
        fIANA2JavaMap.put("CP-GR",        "CP869");
        fIANA2JavaMap.put("IBM870",    "CP870");
        fIANA2JavaMap.put("CP870",    "CP870");
        fIANA2JavaMap.put("CSIBM870",    "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-ROECE", "CP870");
        fIANA2JavaMap.put("EBCDIC-CP-YU",    "CP870");
        fIANA2JavaMap.put("IBM871",    "CP871");
        fIANA2JavaMap.put("CP871",    "CP871");
        fIANA2JavaMap.put("CSIBM871",    "CP871");
        fIANA2JavaMap.put("EBCDIC-CP-IS",    "CP871");
        fIANA2JavaMap.put("IBM918",    "CP918");
        fIANA2JavaMap.put("CP918",    "CP918");
        fIANA2JavaMap.put("CSIBM918",    "CP918");
        fIANA2JavaMap.put("EBCDIC-CP-AR2",   "CP918");
        fIANA2JavaMap.put("IBM01140",    "Cp1140");
        fIANA2JavaMap.put("CP01140",    "Cp1140");
        fIANA2JavaMap.put("CCSID01140",    "Cp1140");
        fIANA2JavaMap.put("IBM01141",    "Cp1141");
        fIANA2JavaMap.put("CP01141",    "Cp1141");
        fIANA2JavaMap.put("CCSID01141",    "Cp1141");
        fIANA2JavaMap.put("IBM01142",    "Cp1142");
        fIANA2JavaMap.put("CP01142",    "Cp1142");
        fIANA2JavaMap.put("CCSID01142",    "Cp1142");
        fIANA2JavaMap.put("IBM01143",    "Cp1143");
        fIANA2JavaMap.put("CP01143",    "Cp1143");
        fIANA2JavaMap.put("CCSID01143",    "Cp1143");
        fIANA2JavaMap.put("IBM01144",    "Cp1144");
        fIANA2JavaMap.put("CP01144",    "Cp1144");
        fIANA2JavaMap.put("CCSID01144",    "Cp1144");
        fIANA2JavaMap.put("IBM01145",    "Cp1145");
        fIANA2JavaMap.put("CP01145",    "Cp1145");
        fIANA2JavaMap.put("CCSID01145",    "Cp1145");
        fIANA2JavaMap.put("IBM01146",    "Cp1146");
        fIANA2JavaMap.put("CP01146",    "Cp1146");
        fIANA2JavaMap.put("CCSID01146",    "Cp1146");
        fIANA2JavaMap.put("IBM01147",    "Cp1147");
        fIANA2JavaMap.put("CP01147",    "Cp1147");
        fIANA2JavaMap.put("CCSID01147",    "Cp1147");
        fIANA2JavaMap.put("IBM01148",    "Cp1148");
        fIANA2JavaMap.put("CP01148",    "Cp1148");
        fIANA2JavaMap.put("CCSID01148",    "Cp1148");
        fIANA2JavaMap.put("IBM01149",    "Cp1149");
        fIANA2JavaMap.put("CP01149",    "Cp1149");
        fIANA2JavaMap.put("CCSID01149",    "Cp1149");
        fIANA2JavaMap.put("EUC-JP",          "EUCJIS");
        fIANA2JavaMap.put("CSEUCPKDFMTJAPANESE",          "EUCJIS");
        fIANA2JavaMap.put("EXTENDED_UNIX_CODE_PACKED_FORMAT_FOR_JAPANESE",          "EUCJIS");
        fIANA2JavaMap.put("EUC-KR",          "KSC5601");
        fIANA2JavaMap.put("GB2312",          "GB2312");
        fIANA2JavaMap.put("CSGB2312",          "GB2312");
        fIANA2JavaMap.put("ISO-2022-JP",     "JIS");
        fIANA2JavaMap.put("CSISO2022JP",     "JIS");
        fIANA2JavaMap.put("ISO-2022-KR",     "ISO2022KR");
        fIANA2JavaMap.put("CSISO2022KR",     "ISO2022KR");
        fIANA2JavaMap.put("ISO-2022-CN",     "ISO2022CN");

        fIANA2JavaMap.put("X0201",  "JIS0201");
        fIANA2JavaMap.put("CSISO13JISC6220JP", "JIS0201");
        fIANA2JavaMap.put("X0208",  "JIS0208");
        fIANA2JavaMap.put("ISO-IR-87",  "JIS0208");
        fIANA2JavaMap.put("X0208dbiJIS_X0208-1983",  "JIS0208");
        fIANA2JavaMap.put("CSISO87JISX0208",  "JIS0208");
        fIANA2JavaMap.put("X0212",  "JIS0212");
        fIANA2JavaMap.put("ISO-IR-159",  "JIS0212");
        fIANA2JavaMap.put("CSISO159JISX02121990",  "JIS0212");
        fIANA2JavaMap.put("SHIFT_JIS",       "SJIS");
        fIANA2JavaMap.put("CSSHIFTJIS",       "SJIS");
        fIANA2JavaMap.put("MS_KANJI",       "SJIS");
        fIANA2JavaMap.put("WINDOWS-31J",       "MS932");
        fIANA2JavaMap.put("CSWINDOWS31J",       "MS932");

	    // Add support for Cp1252 and its friends
        fIANA2JavaMap.put("WINDOWS-1250",   "Cp1250");
        fIANA2JavaMap.put("WINDOWS-1251",   "Cp1251");
        fIANA2JavaMap.put("WINDOWS-1252",   "Cp1252");
        fIANA2JavaMap.put("WINDOWS-1253",   "Cp1253");
        fIANA2JavaMap.put("WINDOWS-1254",   "Cp1254");
        fIANA2JavaMap.put("WINDOWS-1255",   "Cp1255");
        fIANA2JavaMap.put("WINDOWS-1256",   "Cp1256");
        fIANA2JavaMap.put("WINDOWS-1257",   "Cp1257");
        fIANA2JavaMap.put("WINDOWS-1258",   "Cp1258");
        fIANA2JavaMap.put("TIS-620",   "TIS620");

        fIANA2JavaMap.put("ISO-8859-1",      "ISO8859_1"); 
        fIANA2JavaMap.put("ISO-IR-100",      "ISO8859_1");
        fIANA2JavaMap.put("ISO_8859-1",      "ISO8859_1");
        fIANA2JavaMap.put("LATIN1",      "ISO8859_1");
        fIANA2JavaMap.put("CSISOLATIN1",      "ISO8859_1");
        fIANA2JavaMap.put("L1",      "ISO8859_1");
        fIANA2JavaMap.put("IBM819",      "ISO8859_1");
        fIANA2JavaMap.put("CP819",      "ISO8859_1");

        fIANA2JavaMap.put("ISO-8859-2",      "ISO8859_2"); 
        fIANA2JavaMap.put("ISO-IR-101",      "ISO8859_2");
        fIANA2JavaMap.put("ISO_8859-2",      "ISO8859_2");
        fIANA2JavaMap.put("LATIN2",      "ISO8859_2");
        fIANA2JavaMap.put("CSISOLATIN2",      "ISO8859_2");
        fIANA2JavaMap.put("L2",      "ISO8859_2");

        fIANA2JavaMap.put("ISO-8859-3",      "ISO8859_3"); 
        fIANA2JavaMap.put("ISO-IR-109",      "ISO8859_3");
        fIANA2JavaMap.put("ISO_8859-3",      "ISO8859_3");
        fIANA2JavaMap.put("LATIN3",      "ISO8859_3");
        fIANA2JavaMap.put("CSISOLATIN3",      "ISO8859_3");
        fIANA2JavaMap.put("L3",      "ISO8859_3");

        fIANA2JavaMap.put("ISO-8859-4",      "ISO8859_4"); 
        fIANA2JavaMap.put("ISO-IR-110",      "ISO8859_4");
        fIANA2JavaMap.put("ISO_8859-4",      "ISO8859_4");
        fIANA2JavaMap.put("LATIN4",      "ISO8859_4");
        fIANA2JavaMap.put("CSISOLATIN4",      "ISO8859_4");
        fIANA2JavaMap.put("L4",      "ISO8859_4");

        fIANA2JavaMap.put("ISO-8859-5",      "ISO8859_5"); 
        fIANA2JavaMap.put("ISO-IR-144",      "ISO8859_5");
        fIANA2JavaMap.put("ISO_8859-5",      "ISO8859_5");
        fIANA2JavaMap.put("CYRILLIC",      "ISO8859_5");
        fIANA2JavaMap.put("CSISOLATINCYRILLIC",      "ISO8859_5");

        fIANA2JavaMap.put("ISO-8859-6",      "ISO8859_6"); 
        fIANA2JavaMap.put("ISO-IR-127",      "ISO8859_6");
        fIANA2JavaMap.put("ISO_8859-6",      "ISO8859_6");
        fIANA2JavaMap.put("ECMA-114",      "ISO8859_6");
        fIANA2JavaMap.put("ASMO-708",      "ISO8859_6");
        fIANA2JavaMap.put("ARABIC",      "ISO8859_6");
        fIANA2JavaMap.put("CSISOLATINARABIC",      "ISO8859_6");

        fIANA2JavaMap.put("ISO-8859-7",      "ISO8859_7"); 
        fIANA2JavaMap.put("ISO-IR-126",      "ISO8859_7");
        fIANA2JavaMap.put("ISO_8859-7",      "ISO8859_7");
        fIANA2JavaMap.put("ELOT_928",      "ISO8859_7");
        fIANA2JavaMap.put("ECMA-118",      "ISO8859_7");
        fIANA2JavaMap.put("GREEK",      "ISO8859_7");
        fIANA2JavaMap.put("CSISOLATINGREEK",      "ISO8859_7");
        fIANA2JavaMap.put("GREEK8",      "ISO8859_7");

        fIANA2JavaMap.put("ISO-8859-8",      "ISO8859_8"); 
        fIANA2JavaMap.put("ISO-8859-8-I",      "ISO8859_8"); // added since this encoding only differs w.r.t. presentation 
        fIANA2JavaMap.put("ISO-IR-138",      "ISO8859_8");
        fIANA2JavaMap.put("ISO_8859-8",      "ISO8859_8");
        fIANA2JavaMap.put("HEBREW",      "ISO8859_8");
        fIANA2JavaMap.put("CSISOLATINHEBREW",      "ISO8859_8");

        fIANA2JavaMap.put("ISO-8859-9",      "ISO8859_9"); 
        fIANA2JavaMap.put("ISO-IR-148",      "ISO8859_9");
        fIANA2JavaMap.put("ISO_8859-9",      "ISO8859_9");
        fIANA2JavaMap.put("LATIN5",      "ISO8859_9");
        fIANA2JavaMap.put("CSISOLATIN5",      "ISO8859_9");
        fIANA2JavaMap.put("L5",      "ISO8859_9");

        fIANA2JavaMap.put("KOI8-R",          "KOI8_R");
        fIANA2JavaMap.put("CSKOI8R",          "KOI8_R");
        fIANA2JavaMap.put("US-ASCII",        "ASCII"); 
        fIANA2JavaMap.put("ISO-IR-6",        "ASCII");
        fIANA2JavaMap.put("ANSI_X3.4-1986",        "ASCII");
        fIANA2JavaMap.put("ISO_646.IRV:1991",        "ASCII");
        fIANA2JavaMap.put("ASCII",        "ASCII");
        fIANA2JavaMap.put("CSASCII",        "ASCII");
        fIANA2JavaMap.put("ISO646-US",        "ASCII");
        fIANA2JavaMap.put("US",        "ASCII");
        fIANA2JavaMap.put("IBM367",        "ASCII");
        fIANA2JavaMap.put("CP367",        "ASCII");
        fIANA2JavaMap.put("UTF-8",           "UTF8");
        fIANA2JavaMap.put("UTF-16",           "Unicode");
        fIANA2JavaMap.put("UTF-16BE",           "UnicodeBig");
        fIANA2JavaMap.put("UTF-16LE",           "UnicodeLittle");

        // REVISIT:
        //   j:CNS11643 -> EUC-TW?
        //   ISO-2022-CN? ISO-2022-CN-EXT?
                                                
        // add Java to IANA encoding mappings
        //fJava2IANAMap.put("8859_1",    "US-ASCII"); // ?
        fJava2IANAMap.put("ISO8859_1",    "ISO-8859-1");
        fJava2IANAMap.put("ISO8859_2",    "ISO-8859-2");
        fJava2IANAMap.put("ISO8859_3",    "ISO-8859-3");
        fJava2IANAMap.put("ISO8859_4",    "ISO-8859-4");
        fJava2IANAMap.put("ISO8859_5",    "ISO-8859-5");
        fJava2IANAMap.put("ISO8859_6",    "ISO-8859-6");
        fJava2IANAMap.put("ISO8859_7",    "ISO-8859-7");
        fJava2IANAMap.put("ISO8859_8",    "ISO-8859-8");
        fJava2IANAMap.put("ISO8859_9",    "ISO-8859-9");
        fJava2IANAMap.put("Big5",      "BIG5");
        fJava2IANAMap.put("CP037",     "EBCDIC-CP-US");
        fJava2IANAMap.put("CP278",     "EBCDIC-CP-FI");
        fJava2IANAMap.put("CP280",     "EBCDIC-CP-IT");
        fJava2IANAMap.put("CP284",     "EBCDIC-CP-ES");
        fJava2IANAMap.put("CP285",     "EBCDIC-CP-GB");
        fJava2IANAMap.put("CP297",     "EBCDIC-CP-FR");
        fJava2IANAMap.put("CP420",     "EBCDIC-CP-AR1");
        fJava2IANAMap.put("CP424",     "EBCDIC-CP-HE");
        fJava2IANAMap.put("CP500",     "EBCDIC-CP-CH");
        fJava2IANAMap.put("CP870",     "EBCDIC-CP-ROECE");
        fJava2IANAMap.put("CP871",     "EBCDIC-CP-IS");
        fJava2IANAMap.put("CP918",     "EBCDIC-CP-AR2");
        fJava2IANAMap.put("Cp01140",     "IBM01140");
        fJava2IANAMap.put("Cp01141",     "IBM01141");
        fJava2IANAMap.put("Cp01142",     "IBM01142");
        fJava2IANAMap.put("Cp01143",     "IBM01143");
        fJava2IANAMap.put("Cp01144",     "IBM01144");
        fJava2IANAMap.put("Cp01145",     "IBM01145");
        fJava2IANAMap.put("Cp01146",     "IBM01146");
        fJava2IANAMap.put("Cp01147",     "IBM01147");
        fJava2IANAMap.put("Cp01148",     "IBM01148");
        fJava2IANAMap.put("Cp01149",     "IBM01149");
        fJava2IANAMap.put("EUCJIS",    "EUC-JP");
        fJava2IANAMap.put("GB2312",    "GB2312");
        fJava2IANAMap.put("ISO2022KR", "ISO-2022-KR");
        fJava2IANAMap.put("ISO2022CN", "ISO-2022-CN");
        fJava2IANAMap.put("JIS",       "ISO-2022-JP");
        fJava2IANAMap.put("KOI8_R",    "KOI8-R");
        fJava2IANAMap.put("KSC5601",   "EUC-KR");
        fJava2IANAMap.put("SJIS",      "SHIFT_JIS");
        fJava2IANAMap.put("MS932",      "WINDOWS-31J");
        fJava2IANAMap.put("UTF8",      "UTF-8");
        fJava2IANAMap.put("Unicode",   "UTF-16");
        fJava2IANAMap.put("UnicodeBig",   "UTF-16BE");
        fJava2IANAMap.put("UnicodeLittle",   "UTF-16LE");
        fJava2IANAMap.put("JIS0201",  "X0201");
        fJava2IANAMap.put("JIS0208",  "X0208");
        fJava2IANAMap.put("JIS0212",  "ISO-IR-159");

    } // <clinit>()

    /**
     * Returns the Java encoding name for the specified IANA encoding name.
     *
     * @param ianaEncoding  The IANA encoding name.
     * @return              The IANA2JavaMapping value
     */
    public static String getIANA2JavaMapping( String ianaEncoding )
    {
        return ( String ) fIANA2JavaMap.get( ianaEncoding );
    }


    /**
     * Returns the IANA encoding name for the specified Java encoding name.
     *
     * @param javaEncoding  The Java encoding name.
     * @return              The Java2IANAMapping value
     */
    public static String getJava2IANAMapping( String javaEncoding )
    {
        return ( String ) fJava2IANAMap.get( javaEncoding );
    }
}

