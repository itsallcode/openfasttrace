package org.itsallcode.openfasttrace.testutil.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.*;
import org.junit.jupiter.api.Test;

/**
 * This is the base class for all tests of {@link TypeSafeDiagnosingMatcher}.
 *
 * @param <T>
 *            the type compared by the {@link TypeSafeDiagnosingMatcher} under
 *            test.
 */
public abstract class MatcherTestBase<T>
{
    protected MatcherTestBase()
    {
        // Default constructor to fix compiler warning "missing-explicit-ctor"
    }

    @Test
    void testNullObject()
    {
        assertThrows(NullPointerException.class, () -> createMatcher(null));
    }

    protected void assertMatch(final T object)
    {
        assertThat(object, createMatcher(object));
        assertThat(getDescription(object), equalTo(getDescription(object)));
    }

    protected void assertNoMatch(final T objectA, final T objectB)
    {
        assertMatch(objectA);
        assertMatch(objectB);
        assertThat(objectA, not(createMatcher(objectB)));
        assertThat(objectB, not(createMatcher(objectA)));

        assertThat(getDescription(objectA), not(equalTo(getDescription(objectB))));
        assertThat(getDescription(objectA), equalTo(getDescription(objectA)));
        assertThat(getDescription(objectB), equalTo(getDescription(objectB)));
    }

    protected abstract Matcher<T> createMatcher(final T object);

    private String getDescription(final T object)
    {
        final StringDescription description = new StringDescription();
        createMatcher(object).describeTo(description);
        return description.toString();
    }
}
