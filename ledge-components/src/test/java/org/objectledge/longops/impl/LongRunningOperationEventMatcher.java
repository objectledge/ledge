package org.objectledge.longops.impl;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.longops.LongRunningOperationEvent;
import org.objectledge.longops.LongRunningOperationEvent.Type;

public class LongRunningOperationEventMatcher
    extends TypeSafeMatcher<LongRunningOperationEvent>
{
    private final Type type;

    private final Matcher<LongRunningOperation> opMatcher;

    public LongRunningOperationEventMatcher(LongRunningOperationEvent.Type type,
        Matcher<LongRunningOperation> opMatcher)
    {
        this.type = type;
        this.opMatcher = opMatcher;
    }

    public static LongRunningOperationEventMatcher event(
        LongRunningOperationEvent.Type type, Matcher<LongRunningOperation> opMatcher)
    {
        return new LongRunningOperationEventMatcher(type, opMatcher);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText("LongRunningOperationEvent of type " + type.name() + " for ")
            .appendDescriptionOf(opMatcher);
    }

    @Override
    protected boolean matchesSafely(LongRunningOperationEvent item)
    {
        return item.getType().equals(type) && opMatcher.matches(item.getOperation());
    }
}
