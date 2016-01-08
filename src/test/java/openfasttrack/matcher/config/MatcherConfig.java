package openfasttrack.matcher.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;

public class MatcherConfig<T>
{
    private final T expected;
    private final List<PropertyConfig<T, ?>> propertyConfigs;

    private MatcherConfig(final T expected, final List<PropertyConfig<T, ?>> propertyConfigs)
    {
        this.expected = expected;
        this.propertyConfigs = propertyConfigs;
    }

    T getExpected()
    {
        return this.expected;
    }

    List<PropertyConfig<T, ?>> getPropertyConfigs()
    {
        return this.propertyConfigs;
    }

    public static <B> Builder<B> builder(final B expected)
    {
        return new Builder<B>(expected);
    }

    public static class Builder<B>
    {
        private final B expected;
        private final List<PropertyConfig<B, ?>> properties = new ArrayList<>();

        private Builder(final B expected)
        {
            this.expected = expected;
        }

        public Builder<B> addStringProperty(final String propertyName,
                final Function<B, String> propertyAccessor)
        {
            return addPropertyInternal(propertyName, createEqualsMatcher(propertyAccessor),
                    propertyAccessor);
        }

        public Builder<B> addIntProperty(final String propertyName,
                final Function<B, Integer> propertyAccessor)
        {
            return addPropertyInternal(propertyName, createEqualsMatcher(propertyAccessor),
                    propertyAccessor);
        }

        public <P> Builder<B> addIterableProperty(final String propertyName,
                final Function<B, Iterable<? extends P>> propertyAccessor,
                final Function<P, Matcher<P>> matcherBuilder)
        {
            final Matcher<Iterable<? extends P>> listMatcher = createIterableMatcher(
                    propertyAccessor.apply(this.expected), matcherBuilder);
            return addPropertyInternal(propertyName, listMatcher, propertyAccessor);
        }

        public <P> Builder<B> addGenericProperty(final String propertyName,
                final Function<B, P> propertyAccessor, final Function<P, Matcher<P>> matcherBuilder)
        {
            return addPropertyInternal(propertyName,
                    matcherBuilder.apply(propertyAccessor.apply(this.expected)), propertyAccessor);
        }

        private <P> Matcher<Iterable<? extends P>> createIterableMatcher(
                final Iterable<? extends P> expected, final Function<P, Matcher<P>> matcherBuilder)
        {
            if (!expected.iterator().hasNext())
            {
                return IsEmptyIterable.<P> emptyIterable();
            }

            final List<Matcher<? super P>> matchers = StreamSupport
                    .stream(expected.spliterator(), false) //
                    .map(matcherBuilder) //
                    .collect(Collectors.toList());
            return IsIterableContainingInAnyOrder.containsInAnyOrder(matchers);
        }

        private <P> Matcher<P> createEqualsMatcher(final Function<B, P> propertyAccessor)
        {
            return Matchers.equalTo(propertyAccessor.apply(this.expected));
        }

        private <P> Builder<B> addPropertyInternal(final String propertyName,
                final Matcher<P> matcher, final Function<B, P> propertyAccessor)
        {
            this.properties.add(new PropertyConfig<>(propertyName, matcher, propertyAccessor));
            return this;
        }

        public MatcherConfig<B> build()
        {
            return new MatcherConfig<B>(this.expected, this.properties);
        }
    }
}
