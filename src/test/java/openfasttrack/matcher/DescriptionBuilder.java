package openfasttrack.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * This class builds a {@link Description} based on {@link Matcher} and a
 * description. It can be used for
 * {@link org.hamcrest.TypeSafeDiagnosingMatcher.TypeSafeDiagnosingMatcher#describeTo(Description)}
 * .
 */
public class DescriptionBuilder
{
    private final Description description;

    private DescriptionBuilder(final Description description)
    {
        this.description = description;
    }

    static DescriptionBuilder start(final Description description)
    {
        description.appendText("{");
        return new DescriptionBuilder(description);
    }

    public DescriptionBuilder append(final String message, final SelfDescribing matcher)
    {
        this.description.appendText(message).appendText(" ").appendDescriptionOf(matcher);
        return this;
    }

    public void close()
    {
        this.description.appendText("}");
    }
}
