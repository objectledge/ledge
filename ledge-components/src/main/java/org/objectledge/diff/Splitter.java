package org.objectledge.diff;

import java.util.List;

public interface Splitter
{
    public static final Splitter NEWLINE_SPLITTER = new RegexSplitter("\r\n");
    
    public static final Splitter WORD_BOUNDARY_SPLITTER = new RegexSplitter("\\b");
    
    public static final Splitter SP_SPLITTER = new RegexSplitter("\\s+");

    public static final Splitter CHARACTER_SPLITER = new RegexSplitter("");

    public List<String> split(String string);
}
