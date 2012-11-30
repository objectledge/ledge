package org.objectledge.longops.impl;

import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.objectledge.longops.LongRunningOperation;

public class LongRunningOperationCodeMatcher
    extends TypeSafeMatcher<LongRunningOperation>
{
    private final Pattern pattern;

    public LongRunningOperationCodeMatcher(String codePattern)
    {
        pattern = Pattern.compile(codePattern);
    }

    public static LongRunningOperationCodeMatcher opCodeMatching(String codePattern)
    {
        return new LongRunningOperationCodeMatcher(codePattern);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText("LongRunningOperation with code matching ").appendText(
            pattern.pattern());
    }

    @Override
    protected boolean matchesSafely(LongRunningOperation item)
    {
        return pattern.matcher(item.getCode()).matches();
    }
}
