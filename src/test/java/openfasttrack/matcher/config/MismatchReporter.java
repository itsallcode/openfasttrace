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
