package org.itsallcode.openfasttrace.core.matcher;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

import com.github.hamstercommunity.matcher.config.ConfigurableMatcher;
import com.github.hamstercommunity.matcher.config.MatcherConfig;

/**
 * {@link Matcher} for {@link SpecificationItemId}
 */
public class SpecificationItemIdMatcher extends ConfigurableMatcher<SpecificationItemId>
{
    private SpecificationItemIdMatcher(final SpecificationItemId expected)
    {
        super(MatcherConfig.builder(expected) //
                .addEqualsProperty("artifactType", SpecificationItemId::getArtifactType) //
                .addEqualsProperty("name", SpecificationItemId::getName) //
                .addEqualsProperty("revision", SpecificationItemId::getRevision) //
                .build());
    }

    /**
     * Factory method for a matcher that matches a given
     * {@link SpecificationItemId}.
     *
     * @param id
     *            the expected id.
     * @return a id matcher.
     */
    @Factory
    public static Matcher<SpecificationItemId> equalTo(final SpecificationItemId id)
    {
        return new SpecificationItemIdMatcher(id);
    }

    /**
     * Factory method for a matcher that matches an {@link Iterable} of
     * {@link SpecificationItemId}s in any order.
     *
     * @param expected
     *            the expected ids.
     * @return a id matcher.
     */
    @Factory
    public static Matcher<Iterable<? extends SpecificationItemId>> equalIds(
            final Collection<SpecificationItemId> expected)
    {
        if (expected.isEmpty())
        {
            return IsEmptyIterable.emptyIterable();
        }
        final List<Matcher<? super SpecificationItemId>> matchers = expected.stream()
                .map(SpecificationItemIdMatcher::equalTo) //
                .collect(Collectors.toList());
        return IsIterableContainingInAnyOrder.containsInAnyOrder(matchers);
    }
}
