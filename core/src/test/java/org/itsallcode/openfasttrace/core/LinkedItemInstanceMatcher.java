package org.itsallcode.openfasttrace.core;

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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;

class LinkedItemInstanceMatcher extends TypeSafeMatcher<LinkedSpecificationItem>
{
    private final SpecificationItem expectedItem;

    private LinkedItemInstanceMatcher(final SpecificationItem expectedItem)
    {
        this.expectedItem = expectedItem;
    }

    @Override
    public void describeTo(final Description description)
    {
        description.appendValue(this.expectedItem);
    }

    @Override
    protected boolean matchesSafely(final LinkedSpecificationItem actualItem)
    {
        return actualItem.getItem() == this.expectedItem;
    }

    public static Matcher<LinkedSpecificationItem> sameItemInstance(
            final SpecificationItem expectedItem)
    {
        return new LinkedItemInstanceMatcher(expectedItem);
    }
}
