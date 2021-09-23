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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.StreamSupport;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;

import com.github.hamstercommunity.matcher.config.ConfigurableMatcher;
import com.github.hamstercommunity.matcher.config.MatcherConfig;

/**
 * {@link Matcher} for {@link SpecificationItem}
 */
class SpecificationItemMatcher extends ConfigurableMatcher<SpecificationItem>
{
    private SpecificationItemMatcher(final SpecificationItem expected)
    {
        super(MatcherConfig.builder(expected) //
                .addProperty("id", SpecificationItem::getId, SpecificationItemIdMatcher::equalTo) //
                .addEqualsProperty("comment", SpecificationItem::getComment) //
                .addEqualsProperty("description", SpecificationItem::getDescription) //
                .addEqualsProperty("rationale", SpecificationItem::getRationale) //
                .addIterableProperty("coveredIds", SpecificationItem::getCoveredIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("dependsOnIds", SpecificationItem::getDependOnIds,
                        SpecificationItemIdMatcher::equalTo) //
                .addIterableProperty("neededArtifactTypes",
                        SpecificationItem::getNeedsArtifactTypes, Matchers::equalTo) //
                .build());
    }

    @Factory
    public static Matcher<SpecificationItem> equalTo(final SpecificationItem item)
    {
        return new SpecificationItemMatcher(item);
    }

    @Factory
    public static Collection<Matcher<SpecificationItem>> equalTo(
            final Iterable<SpecificationItem> items)
    {
        return StreamSupport.stream(items.spliterator(), false) //
                .map(SpecificationItemMatcher::equalTo) //
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    @Factory
    public static Matcher<Iterable<? extends SpecificationItem>> equalToAnyOrder(
            final Collection<SpecificationItem> items)
    {
        if (items.isEmpty())
        {
            return IsEmptyIterable.emptyIterable();
        }
        return IsIterableContainingInAnyOrder
                .containsInAnyOrder(equalTo(items).toArray(new Matcher[0]));
    }
}
