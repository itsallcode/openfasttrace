package openfasttrack.matcher.config;

import openfasttrack.matcher.BaseTypeSafeDiagnosingMatcher;
import openfasttrack.matcher.DescriptionBuilder;
import openfasttrack.matcher.MismatchReporter;

public abstract class BaseConfigurableMatcher<T> extends BaseTypeSafeDiagnosingMatcher<T>
{
    private final MatcherConfig<T> config;

    protected BaseConfigurableMatcher(final MatcherConfig<T> config)
    {
        super(config.getExpected());
        this.config = config;
    }

    @Override
    protected final void describeTo(final DescriptionBuilder description)
    {
        for (final PropertyConfig<T, ?> property : this.config.getPropertyConfigs())
        {
            description.append(property.getPropertyName(), property.getMatcher());
        }
    }

    @Override
    protected final void reportMismatches(final T actual, final MismatchReporter mismatchReporter)
    {
        for (final PropertyConfig<T, ?> property : this.config.getPropertyConfigs())
        {
            reportMismatch(actual, mismatchReporter, property);
        }
    }

    private <P> void reportMismatch(final T actual, final MismatchReporter mismatchReporter,
            final PropertyConfig<T, P> property)
    {
        mismatchReporter.checkMismatch(property.getPropertyName(), property.getMatcher(),
                property.getPropertyValue(actual));
    }
}
