package org.itsallcode.openfasttrace.testutil.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.core.Trace;

/**
 * Provides assertion methods for testing {@link Trace} objects.
 */
public final class TraceAssertions
{
    private TraceAssertions()
    {
        // prevent instantiation
    }

    /**
     * Get a linked specification item from a trace by its ID.
     * 
     * @param trace
     *            the trace to search in
     * @param id
     *            the ID of the item to find
     * @return the linked specification item with the given ID
     * @throws AssertionError
     *             if no item with the given ID is found in the trace
     */
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

    /**
     * Assert that a trace contains the expected number of items.
     * 
     * @param trace
     *            the trace to check
     * @param traceSize
     *            the expected number of items
     */
    public static void assertTraceSize(final Trace trace, final int traceSize)
    {
        assertThat("Number of items", trace.getItems().size(), equalTo(traceSize));
    }

    /**
     * Assert that a trace contains no defect IDs.
     * 
     * @param trace
     *            the trace to check
     */
    public static void assertTraceContainsNoDefectIds(final Trace trace)
    {
        assertThat("Defect IDs in trace", trace.getDefectIds(), empty());
    }

    /**
     * Assert that a trace contains the expected defect IDs.
     * 
     * @param trace
     *            the trace to check
     * @param ids
     *            the expected defect IDs
     */
    public static void assertTraceContainsDefectIds(final Trace trace,
            final SpecificationItemId... ids)
    {
        assertThat("Defect IDs in trace", trace.getDefectIds(), contains(ids));
    }

    /**
     * Assert that a trace has no defects.
     * 
     * @param trace
     *            the trace to check
     */
    public static void assertTraceHasNoDefects(final Trace trace)
    {
        assertThat("Has no defects", trace.hasNoDefects(), equalTo(true));
    }

    /**
     * Assert that a trace has defects.
     * 
     * @param trace
     *            the trace to check
     */
    public static void assertTraceHasDefects(final Trace trace)
    {
        assertThat("Has no defects", trace.hasNoDefects(), equalTo(false));
    }
}
