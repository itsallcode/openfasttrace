package openfasttrack.matcher.config;

import java.util.function.Function;

import org.hamcrest.Matcher;

/**
 * @param <T>
 *            base type of the {@link Matcher}
 * @param
 *            <P>
 *            type of the property
 */
class PropertyConfig<T, P>
{
    private final String propertyName;
    private final Matcher<P> matcher;
    private final Function<T, P> propertyAccessor;

    PropertyConfig(final String propertyName, final Matcher<P> matcher,
            final Function<T, P> propertyAccessor)
    {
        this.propertyName = propertyName;
        this.matcher = matcher;
        this.propertyAccessor = propertyAccessor;
    }

    public String getPropertyName()
    {
        return this.propertyName;
    }

    public Matcher<P> getMatcher()
    {
        return this.matcher;
    }

    public P getPropertyValue(final T object)
    {
        return this.propertyAccessor.apply(object);
    }
}