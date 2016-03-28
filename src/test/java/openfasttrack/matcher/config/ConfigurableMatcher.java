package openfasttrack.matcher.config;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

public abstract class ConfigurableMatcher<T> extends BaseTypeSafeDiagnosingMatcher<T>
{
    private final MatcherConfig<T> config;

    protected ConfigurableMatcher(final MatcherConfig<T> config)
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
