package openfasttrack.core;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class LinkedItemInstanceMatcher extends TypeSafeMatcher<LinkedSpecificationItem>
{
    private final SpecificationItem expectedItem;

    private LinkedItemInstanceMatcher(final SpecificationItem expectedItem)
    {
        this.expectedItem = expectedItem;
    }

    @Override
    public void describeTo(final Description description)
    {
        description.appendValue(this.expectedItem);
    }

    @Override
    protected boolean matchesSafely(final LinkedSpecificationItem actualItem)
    {
        return actualItem.getItem() == this.expectedItem;
    }

    public static Matcher<LinkedSpecificationItem> sameItemInstance(
            final SpecificationItem expectedItem)
    {
        return new LinkedItemInstanceMatcher(expectedItem);
    }
}