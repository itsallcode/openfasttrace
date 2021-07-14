package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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

import static org.hamcrest.Matchers.equalTo;

import java.util.regex.Matcher;

class MarkdownAsserts
{
    static void assertMatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, true);
    }

    static void assertMismatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, false);
    }

    static void assertMatching(final String[] samples, final MdPattern mdPattern,
            final boolean mustMatch)
    {
        for (final String text : samples)
        {
            final Matcher matcher = mdPattern.getPattern().matcher(text);
            assertThat(mdPattern.toString() + " must " + (mustMatch ? "" : "not ") + "match " + "\""
                    + text + "\"", matcher.matches(), equalTo(mustMatch));
        }
    }
}
