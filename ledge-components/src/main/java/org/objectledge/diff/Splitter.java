package org.objectledge.diff;

import java.util.List;

public interface Splitter
{
    public static final Splitter NEWLINE_SPLITTER = new RegexSplitter("\r\n");
    
    public static final Splitter WORD_BOUNDARY_SPLITTER = new RegexSplitter("\\b");
    
    public List<String> split(String string);
}
