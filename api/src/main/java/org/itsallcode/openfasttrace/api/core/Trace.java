package org.itsallcode.openfasttrace.api.core;

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
     * @return {@code true} if and only if no defects where found during
     *         tracing.
     */
    public boolean hasNoDefects()
    {
        return this.defectItems.isEmpty();
    }

    /** @return all defect items found during tracing. */
    public List<LinkedSpecificationItem> getDefectItems()
    {
        return this.defectItems;
    }

    /**
     * 
     * @return all items found during tracing.
     */
    public List<LinkedSpecificationItem> getItems()
    {
        return this.items;
    }

    /**
     * @return the IDs of the defect items.
     */
    public List<SpecificationItemId> getDefectIds()
    {
        return this.defectItems.stream()
                .map(LinkedSpecificationItem::getId)
                .collect(Collectors.toList());
    }

    /**
     * @return the number of defect items found.
     */
    public int countDefects()
    {
        return this.defectItems.size();
    }

    /**
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