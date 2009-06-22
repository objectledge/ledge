package org.objectledge.html;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class with different HTML tag and atribute classifications.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLTagTypes.java,v 1.2 2005-02-11 11:30:42 rafal Exp $
 */
public class HTMLTagTypes
{
	/** empty tags. */
	public final static Set EMPTY_TAGS;
	static
	{
		Set emptyTags = new HashSet(); 
		emptyTags.add("AREA"); 		
		emptyTags.add("BASE"); 		
		emptyTags.add("BASEFONT"); 		
		emptyTags.add("BR"); 		
		emptyTags.add("COL"); 		
		emptyTags.add("FRAME"); 		
		emptyTags.add("HR"); 		
		emptyTags.add("IMG"); 		
		emptyTags.add("INPUT"); 		
		emptyTags.add("ISINDEX"); 		
		emptyTags.add("LINK"); 		
		emptyTags.add("META"); 		
		emptyTags.add("PARAM"); 		
		EMPTY_TAGS = Collections.unmodifiableSet(emptyTags); 
	}

    /** block tags. */
    public final static Set BLOCK_TAGS;
    static
    {
    	Set tempBlockTags = new HashSet(); 
        tempBlockTags.add("ADDRESS"); 		
        tempBlockTags.add("BLOCKQUOTE"); 		
        tempBlockTags.add("CENTER");
        tempBlockTags.add("DIV"); 		
        tempBlockTags.add("DL");
        tempBlockTags.add("FIELDSET");
        tempBlockTags.add("FORM");
        tempBlockTags.add("H1");
        tempBlockTags.add("H2");
        tempBlockTags.add("H3");
        tempBlockTags.add("H4");
        tempBlockTags.add("H5");
        tempBlockTags.add("H6");
        tempBlockTags.add("HR");
        tempBlockTags.add("ISINDEX");
        tempBlockTags.add("NOSCRIPT");
        tempBlockTags.add("OL");
        tempBlockTags.add("P");
        tempBlockTags.add("PRE");
        tempBlockTags.add("TABLE"); 		
        tempBlockTags.add("UL");
		BLOCK_TAGS = Collections.unmodifiableSet(tempBlockTags); 
    }

	/** inline tags. */
	public final static Set INLINE_TAGS;
	static
	{
		Set tempInlineTags = new HashSet();
		tempInlineTags.add("TT");
		tempInlineTags.add("I");
		tempInlineTags.add("B");
		tempInlineTags.add("BIG");
		tempInlineTags.add("SMALL");
		tempInlineTags.add("EM");
		tempInlineTags.add("STRONG");
		tempInlineTags.add("DFN");
		tempInlineTags.add("CODE");
		tempInlineTags.add("SAMP");
		tempInlineTags.add("KBD");
		tempInlineTags.add("VAR");
		tempInlineTags.add("CITE");
		tempInlineTags.add("ABBR");
		tempInlineTags.add("ACRONYM");		tempInlineTags.add("A");
		tempInlineTags.add("IMG");
		tempInlineTags.add("OBJECT");
		tempInlineTags.add("BR");
		tempInlineTags.add("SCRIPT");
		tempInlineTags.add("MAP");
		tempInlineTags.add("Q");
		tempInlineTags.add("SUB");
		tempInlineTags.add("SUP");
		tempInlineTags.add("SPAN");
		tempInlineTags.add("BDO");
		tempInlineTags.add("INPUT");
		tempInlineTags.add("SELECT");
		tempInlineTags.add("TEXTAREA");
		tempInlineTags.add("LABEL");
		tempInlineTags.add("BUTTON");
		INLINE_TAGS = Collections.unmodifiableSet(tempInlineTags); 
	}

	/** tags which need space addition to avoid word concatenation. */
	public final static Set SPACE_ADD_TAGS;
	static
	{
		Set tempSpaceAddTags = new HashSet();
		tempSpaceAddTags.add("ADDRESS"); 		
		tempSpaceAddTags.add("APPLET");
		tempSpaceAddTags.add("BDO"); 		
		tempSpaceAddTags.add("BLOCKQUOTE"); 		
		tempSpaceAddTags.add("BR");
		tempSpaceAddTags.add("BUTTON");
		tempSpaceAddTags.add("CAPTION");
		tempSpaceAddTags.add("CENTER");
		tempSpaceAddTags.add("DD");
		tempSpaceAddTags.add("DFN"); 		
		tempSpaceAddTags.add("DIR");
		tempSpaceAddTags.add("DIV"); 		
		tempSpaceAddTags.add("DL");
		tempSpaceAddTags.add("DT");
		tempSpaceAddTags.add("FIELDSET");
		tempSpaceAddTags.add("FORM");
		tempSpaceAddTags.add("H1");
		tempSpaceAddTags.add("H2");
		tempSpaceAddTags.add("H3");
		tempSpaceAddTags.add("H4");
		tempSpaceAddTags.add("H5");
		tempSpaceAddTags.add("H6");
		tempSpaceAddTags.add("HR");
		tempSpaceAddTags.add("IFRAME");
		tempSpaceAddTags.add("IMG");
		tempSpaceAddTags.add("INPUT");
		tempSpaceAddTags.add("ISINDEX");
		tempSpaceAddTags.add("LABEL");	
		tempSpaceAddTags.add("LEGEND");
		tempSpaceAddTags.add("LI");
		tempSpaceAddTags.add("MENU");
		tempSpaceAddTags.add("META");
		tempSpaceAddTags.add("NOFRAMES");
		tempSpaceAddTags.add("NOSCRIPT");
		tempSpaceAddTags.add("OBJECT");
		tempSpaceAddTags.add("OL");
		tempSpaceAddTags.add("OPTGROUP");
		tempSpaceAddTags.add("OPTION");
		tempSpaceAddTags.add("P");
		tempSpaceAddTags.add("PARAM");
		tempSpaceAddTags.add("PRE");
		tempSpaceAddTags.add("SCRIPT"); 		
		tempSpaceAddTags.add("SELECT"); 		
		tempSpaceAddTags.add("STYLE"); 		
		tempSpaceAddTags.add("TABLE"); 		
		tempSpaceAddTags.add("TD");
		tempSpaceAddTags.add("TEXTAREA");
		tempSpaceAddTags.add("TH");
		tempSpaceAddTags.add("TITLE"); 		
		tempSpaceAddTags.add("UL");
		SPACE_ADD_TAGS = Collections.unmodifiableSet(tempSpaceAddTags); 
	}

    /** attributes which contain meaningful text. */
    public final static Set TEXT_ATTRIBUTES;
    static
    {
        Set tempTextAttributes = new HashSet();
        tempTextAttributes.add("ABBR");
        tempTextAttributes.add("ALT");
        tempTextAttributes.add("CONTENT");
        tempTextAttributes.add("LABEL");
        tempTextAttributes.add("SUMMARY");
        tempTextAttributes.add("TITLE");
        tempTextAttributes.add("HREF");
        tempTextAttributes.add("DATETIME");
        tempTextAttributes.add("VALUE");
		TEXT_ATTRIBUTES = Collections.unmodifiableSet(tempTextAttributes);
    }
    
	public static boolean isEmptyTag(String name)
	{
		return EMPTY_TAGS.contains(name.toUpperCase());
	}

    public static boolean isBlockTag(String name)
    {
		return BLOCK_TAGS.contains(name.toUpperCase());
    }

	public static boolean isInlineTag(String name)
	{
		return INLINE_TAGS.contains(name.toUpperCase());
	}

	public static boolean isSpaceAddTag(String name)
	{
		return SPACE_ADD_TAGS.contains(name.toUpperCase());
	}

	public static boolean isTextAttribute(String name)
	{
		return TEXT_ATTRIBUTES.contains(name.toUpperCase());
	}
}
