package org.itsallcode.openfasttrace.core;

import java.util.List;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;

/**
 * Traces a given list of {@link LinkedSpecificationItem}s and returns a
 * {@link Trace} as input for reporters.
 */
public class Tracer
{
    /**
     * Traces the given items.
     * 
     * @param items
     *            the items to trace.
     * @return the result {@link Trace}.
     */
    public Trace trace(final List<LinkedSpecificationItem> items)
    {
        final Trace.Builder builder = Trace.builder();
        builder.items(items);
        builder.defectItems(items.stream() //
                .filter(LinkedSpecificationItem::isDefect) //
                .collect(Collectors.toList()));
        return builder.build();
    }
}
