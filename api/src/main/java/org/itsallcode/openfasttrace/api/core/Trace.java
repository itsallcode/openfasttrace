package org.itsallcode.openfasttrace.api.core;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of tracing requirements.
 */
public class Trace
{
    private final List<LinkedSpecificationItem> items;
    private final List<LinkedSpecificationItem> defectItems;

    private Trace(final List<LinkedSpecificationItem> items,
            final List<LinkedSpecificationItem> uncleanItems)
    {
        this.items = items;
        this.defectItems = uncleanItems;
    }

    /**
     * Check if this result has no defects.
     * 
     * @return {@code true} if and only if no defects where found during
     *         tracing.
     */
    public boolean hasNoDefects()
    {
        return this.defectItems.isEmpty();
    }

    /**
     * Get all defect items.
     * 
     * @return all defect items found during tracing.
     */
    public List<LinkedSpecificationItem> getDefectItems()
    {
        return this.defectItems;
    }

    /**
     * Get all items.
     * 
     * @return all items found during tracing.
     */
    public List<LinkedSpecificationItem> getItems()
    {
        return this.items;
    }

    /**
     * Get IDs of the defect items.
     * 
     * @return the IDs of the defect items.
     */
    public List<SpecificationItemId> getDefectIds()
    {
        return this.defectItems.stream()
                .map(LinkedSpecificationItem::getId)
                .collect(Collectors.toList());
    }

    /**
     * Get the number of defect items.
     * 
     * @return the number of defect items found.
     */
    public int countDefects()
    {
        return this.defectItems.size();
    }

    /**
     * Get the total number of items.
     * 
     * @return the total number of items found.
     */
    public int count()
    {
        return this.items.size();
    }

    /**
     * Create a new instance of a {@link Builder}
     * 
     * @return builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * A builder for {@link Trace}. Use {@link Trace#builder()} to create a new
     * builder and call {@link #build()} to build a {@link Trace}.
     */
    public static class Builder
    {
        private List<LinkedSpecificationItem> items;
        private List<LinkedSpecificationItem> defectItems;

        private Builder()
        {
        }

        /**
         * Set the items.
         * 
         * @param items
         *            the items.
         * @return this instance for method chaining.
         */
        public Builder items(final List<LinkedSpecificationItem> items)
        {
            this.items = items;
            return this;
        }

        /**
         * Set the defect items.
         * 
         * @param defectItems
         *            the defect items.
         * @return this instance for method chaining.
         */
        public Builder defectItems(final List<LinkedSpecificationItem> defectItems)
        {
            this.defectItems = defectItems;
            return this;
        }

        /**
         * Builds a new {@link Trace}.
         * 
         * @return a new {@link Trace}.
         */
        public Trace build()
        {
            return new Trace(this.items, this.defectItems);
        }
    }
}