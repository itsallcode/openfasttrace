package openfasttrack.matcher.config;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * This is a helper class used by {@link TypeSafeDiagnosingMatcher}s in
 * {@link TypeSafeDiagnosingMatcher#matchesSafely(Object,Description)}.
 */
class MismatchReporter
{
    private final Description mismatchDescription;
    private boolean firstMismatch = true;
    private boolean matches = true;

    private MismatchReporter(final Description mismatchDescription)
    {
        this.mismatchDescription = mismatchDescription;
    }

    static MismatchReporter start(final Description mismatchDescription)
    {
        mismatchDescription.appendText("{");
        return new MismatchReporter(mismatchDescription);
    }

    boolean finishAndCheckMatching()
    {
        this.mismatchDescription.appendText("}");
        return this.matches;
    }

    public <T> MismatchReporter checkMismatch(final String message, final Matcher<T> matcher,
            final T actual)
    {
        if (!matcher.matches(actual))
        {
            reportMismatch(message, matcher, actual);
            this.matches = false;
        }
        return this;
    }

    private void reportMismatch(final String name, final Matcher<?> matcher, final Object item)
    {
        appendComma();
        this.mismatchDescription.appendText(name).appendText(" ");
        matcher.describeMismatch(item, this.mismatchDescription);
    }

    private void appendComma()
    {
        if (!this.firstMismatch)
        {
            this.mismatchDescription.appendText(", ");
        }
        this.firstMismatch = false;
    }
}
