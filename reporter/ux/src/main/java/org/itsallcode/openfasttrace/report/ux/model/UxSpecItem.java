package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;

import java.util.List;

public class UxSpecItem
{
    private final int index;
    private final int typeIndex;
    private final List<Integer> neededTypeIndexes;
    private final int statusId;
    private final List<UxSpecItem> coveredBy;
    private final List<UxSpecItem> covers;
    private final LinkedSpecificationItem item;

    private UxSpecItem(Builder builder)
    {
        index = builder.index;
        typeIndex = builder.typeIndex;
        neededTypeIndexes = builder.neededTypeIndexes;
        statusId = builder.statusId;
        coveredBy = builder.coveredBy;
        covers = builder.covers;
        item = builder.item;
    }

    public int getIndex()
    {
        return index;
    }

    public int getTypeIndex()
    {
        return typeIndex;
    }

    public List<Integer> getNeededTypeIndexes()
    {
        return neededTypeIndexes;
    }

    public int getStatusId()
    {
        return statusId;
    }

    public List<UxSpecItem> getCoveredBy()
    {
        return coveredBy;
    }

    public List<UxSpecItem> getCovers()
    {
        return covers;
    }

    public LinkedSpecificationItem getItem()
    {
        return item;
    }

    /**
     * {@code UxSpecItem} builder static inner class.
     */
    public static final class Builder
    {
        private int index;
        private int typeIndex;
        private List<Integer> neededTypeIndexes;
        private int statusId;
        private List<UxSpecItem> coveredBy;
        private List<UxSpecItem> covers;
        private LinkedSpecificationItem item;

        private Builder()
        {
        }

        public static Builder builder()
        {
            return new Builder();
        }

        /**
         * Sets the {@code index} and returns a reference to this Builder enabling method chaining.
         *
         * @param index
         *         the {@code index} to set
         * @return a reference to this Builder
         */
        public Builder withIndex(int index)
        {
            this.index = index;
            return this;
        }

        /**
         * Sets the {@code typeIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param typeIndex
         *         the {@code typeIndex} to set
         * @return a reference to this Builder
         */
        public Builder withTypeIndex(int typeIndex)
        {
            this.typeIndex = typeIndex;
            return this;
        }

        /**
         * Sets the {@code neededTypeIndexes} and returns a reference to this Builder enabling method chaining.
         *
         * @param neededTypeIndexes
         *         the {@code neededTypeIndexes} to set
         * @return a reference to this Builder
         */
        public Builder withNeededTypeIndexes(List<Integer> neededTypeIndexes)
        {
            this.neededTypeIndexes = neededTypeIndexes;
            return this;
        }

        /**
         * Sets the {@code statusId} and returns a reference to this Builder enabling method chaining.
         *
         * @param statusId
         *         the {@code statusId} to set
         * @return a reference to this Builder
         */
        public Builder withStatusId(int statusId)
        {
            this.statusId = statusId;
            return this;
        }

        /**
         * Sets the {@code coveredBy} and returns a reference to this Builder enabling method chaining.
         *
         * @param coveredBy
         *         the {@code coveredBy} to set
         * @return a reference to this Builder
         */
        public Builder withCoveredBy(List<UxSpecItem> coveredBy)
        {
            this.coveredBy = coveredBy;
            return this;
        }

        /**
         * Sets the {@code covers} and returns a reference to this Builder enabling method chaining.
         *
         * @param covers
         *         the {@code covers} to set
         * @return a reference to this Builder
         */
        public Builder withCovers(List<UxSpecItem> covers)
        {
            this.covers = covers;
            return this;
        }

        /**
         * Sets the {@code item} and returns a reference to this Builder enabling method chaining.
         *
         * @param item
         *         the {@code item} to set
         * @return a reference to this Builder
         */
        public Builder withItem(LinkedSpecificationItem item)
        {
            this.item = item;
            return this;
        }

        /**
         * Returns a {@code UxSpecItem} built from the parameters previously set.
         *
         * @return a {@code UxSpecItem} built with parameters of this {@code UxSpecItem.Builder}
         */
        public UxSpecItem build()
        {
            return new UxSpecItem(this);
        }
    }
}
