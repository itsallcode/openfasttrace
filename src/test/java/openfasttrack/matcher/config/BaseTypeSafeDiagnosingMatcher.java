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
