package openfasttrack.matcher.config;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * This is the base class for all matchers that allows using
 * {@link DescriptionBuilder} and {@link MismatchReporter}.
 *
 * @param <T>
 *            the type supported by the matcher.
 */
abstract class BaseTypeSafeDiagnosingMatcher<T> extends TypeSafeDiagnosingMatcher<T>
{
    protected BaseTypeSafeDiagnosingMatcher(final T expected)
    {
    }

    @Override
    public void describeTo(final Description description)
    {
        final DescriptionBuilder builder = DescriptionBuilder.start(description);
        describeTo(builder);
        builder.close();
    }

    /**
     * Describe the expected value using the {@link DescriptionBuilder}.
     *
     * @param description
     *            the {@link DescriptionBuilder}.
     */
    protected abstract void describeTo(DescriptionBuilder description);

    @Override
    protected boolean matchesSafely(final T actual, final Description mismatchDescription)
    {
        final MismatchReporter mismatchReporter = MismatchReporter.start(mismatchDescription);
        reportMismatches(actual, mismatchReporter);
        return mismatchReporter.finishAndCheckMatching();
    }

    /**
     * Report mismatches to the given {@link MismatchReporter}.
     *
     * @param actual
     *            the actual value to compare to the expected value.
     * @param mismatchReporter
     *            the {@link MismatchReporter}.
     */
    protected abstract void reportMismatches(T actual, MismatchReporter mismatchReporter);
}
