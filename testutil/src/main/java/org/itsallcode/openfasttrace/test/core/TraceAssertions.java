package org.itsallcode.openfasttrace.test.core;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.core.Trace;

public final class TraceAssertions
{
    private TraceAssertions()
    {
        // prevent instantiation
    }

    public static LinkedSpecificationItem getItemFromTraceForId(final Trace trace,
            final SpecificationItemId id)
    {
        for (final LinkedSpecificationItem item : trace.getItems())
        {
            if (item.getId().equals(id))
            {
                return item;
            }
        }
        throw new AssertionError(
                "Could not find linked specification item with ID \"" + id.toString() + "\"");
    }

    public static void assertTraceSize(final Trace trace, final int traceSize)
    {
        assertThat("Number of items", trace.getItems().size(), equalTo(traceSize));
    }

    public static void assertTraceContainsNoDefectIds(final Trace trace)
    {
        assertThat("Defect IDs in trace", trace.getDefectIds(), empty());
    }

    public static void assertTraceContainsDefectIds(final Trace trace,
            final SpecificationItemId... ids)
    {
        assertThat("Defect IDs in trace", trace.getDefectIds(), contains(ids));
    }

    public static void assertTraceHasNoDefects(final Trace trace)
    {
        assertThat("Has no defects", trace.hasNoDefects(), equalTo(true));
    }

    public static void assertTraceHasDefects(final Trace trace)
    {
        assertThat("Has no defects", trace.hasNoDefects(), equalTo(false));
    }
}