package org.itsallcode.openfasttrace.testutil.core;

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