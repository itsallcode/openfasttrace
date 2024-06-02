package org.itsallcode.openfasttrace.testutil.core;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

/**
 * The {@link ItemBuilderFactory} class provides convenience methods for creating instances of {@link SpecificationItem}.
 */
public class ItemBuilderFactory {
    private ItemBuilderFactory() {
        // prevent instantiation.
    }

    /**
     * Creates a new instance of {@link SpecificationItem.Builder}.
     *
     * @return a new instance of {@link SpecificationItem.Builder}
     */
    public static final SpecificationItem.Builder item() {
        return SpecificationItem.builder();
    }

    /**
     * Creates a new instance of {@link SpecificationItem.Builder} with the specified ID.
     *
     * @param id ID of the specification item
     *
     * @return a new instance of {@link SpecificationItem.Builder} with the specified ID
     */
    public static final SpecificationItem.Builder itemWithId(SpecificationItemId id) {
        return SpecificationItem.builder().id(id);
    }

    /**
     * Returns a new instance of {@link SpecificationItem.Builder} with the default filename and the specified line.
     *
     * @param line line number of the specification item in the file
     *
     * @return a new instance of {@link SpecificationItem.Builder} with the default filename and the specified line
     */
    public static final SpecificationItem.Builder itemWithDefaultFilenameInLine(final int line) {
        return SpecificationItem.builder().location("file", line);
    }
}
