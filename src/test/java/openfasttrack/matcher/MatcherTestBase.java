package openfasttrack.matcher;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

/**
 * This is the base class for all tests of {@link TypeSafeDiagnosingMatcher}.
 *
 * @param <T>
 *            the type compared by the {@link TypeSafeDiagnosingMatcher} under
 *            test.
 */
public abstract class MatcherTestBase<T>
{
    @Test(expected = NullPointerException.class)
    public void testNullObject()
    {
        createMatcher(null);
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

    protected abstract Matcher<? super T> createMatcher(final T object);

    protected abstract Class<T> getType();

    private String getDescription(final T object)
    {
        final StringDescription description = new StringDescription();
        createMatcher(object).describeTo(description);
        return description.toString();
    }
}
