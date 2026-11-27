package org.itsallcode.openfasttrace.testutil.core;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

/**
 * The {@link ItemBuilderFactory} class provides convenience methods for creating instances of
 * {@link SpecificationItem}.
 */
public final class ItemBuilderFactory
{
    private ItemBuilderFactory()
    {
        // prevent instantiation.
    }

    /**
     * Creates a new instance of {@link Builder}.
     *
     * @return a new instance of {@link Builder}
     */
    public static Builder item()
    {
        return SpecificationItem.builder();
    }

    /**
     * Creates a new instance of {@link Builder} with the specified ID.
     *
     * @param id
     *            ID of the specification item
     *
     * @return a new instance of {@link Builder} with the specified ID
     */
    public static Builder itemWithId(final SpecificationItemId id)
    {
        return SpecificationItem.builder().id(id);
    }

    /**
     * Returns a new instance of {@link Builder} with the default filename and the specified line.
     *
     * @param line
     *            line number of the specification item in the file
     *
     * @return a new instance of {@link Builder} with the default filename and the specified line
     */
    public static Builder itemWithDefaultFilenameInLine(final int line)
    {
        return SpecificationItem.builder().location("file", line);
    }

    /**
     * Creates a copy of an item without source locations on its IDs.
     *
     * @param item
     *            item to copy
     * @return copy without source locations on its IDs
     */
    public static SpecificationItem withoutIdLocations(final SpecificationItem item)
    {
        return item.toBuilder()
                .id(item.getId())
                .coveredIds(item.getCoveredIds())
                .dependOnIds(item.getDependOnIds())
                .build();
    }
}
