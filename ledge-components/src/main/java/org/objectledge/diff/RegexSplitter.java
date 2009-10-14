package org.objectledge.diff;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegexSplitter
    implements Splitter
{
    private final Pattern pattern;
    
    public RegexSplitter(String pattern)
    {
        this.pattern = Pattern.compile(pattern);
    }    
    
    @Override
    public List<String> split(String string)
    {
        return Arrays.asList(pattern.split(string));
    }
}
