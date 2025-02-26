package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;

import java.util.ArrayList;
import java.util.List;

public class UxSpecItem
{
    private final int index;
    private final int typeIndex;
    private final String title;
    private final String name;
    private final String id;
    private final List<Integer> tagIndex;
    private final List<Integer> providesIndex;
    private final List<Integer> neededTypeIndex;
    private final List<Integer> coveredIndex;
    private final List<Integer> uncoveredIndex;
    private final List<Integer> coveringIndex;
    private final List<Integer> coveredByIndex;
    private final List<Integer> dependsIndex;
    private final int statusId;
    private final List<String> path;
    private final LinkedSpecificationItem item;

    private UxSpecItem(Builder builder) {
        index = builder.index;
        typeIndex = builder.typeIndex;
        title = builder.title;
        name = builder.name;
        id = builder.id;
        tagIndex = builder.tagIndex;
        providesIndex = builder.providesIndex;
        neededTypeIndex = builder.neededTypeIndex;
        coveredIndex = builder.coveredIndex;
        uncoveredIndex = builder.uncoveredIndex;
        coveringIndex = builder.coveringIndex;
        coveredByIndex = builder.coveredByIndex;
        dependsIndex = builder.dependsIndex;
        statusId = builder.statusId;
        path = builder.path;
        item = builder.item;
    }

    public int getIndex() {
        return index;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Integer> getTagIndex() {
        return tagIndex;
    }

    public List<Integer> getProvidesIndex() {
        return providesIndex;
    }

    public List<Integer> getNeededTypeIndex() {
        return neededTypeIndex;
    }

    public List<Integer> getCoveredIndex() {
        return coveredIndex;
    }

    public List<Integer> getUncoveredIndex() {
        return uncoveredIndex;
    }

    public List<Integer> getCoveringIndex() {
        return coveringIndex;
    }

    public List<Integer> getCoveredByIndex() {
        return coveredByIndex;
    }

    public List<Integer> getDependsIndex() {
        return dependsIndex;
    }

    public int getStatusId() {
        return statusId;
    }

    public List<String> getPath() {
        return path;
    }

    public LinkedSpecificationItem getItem() {
        return item;
    }

    /**
     * {@code UxSpecItem} builder static inner class.
     */
    public static final class Builder {
        private int index;
        private int typeIndex;
        private String title;
        private String name;
        private String id;
        private List<Integer> tagIndex = new ArrayList<>();
        private List<Integer> providesIndex = new ArrayList<>();
        private List<Integer> neededTypeIndex;
        private List<Integer> coveredIndex;
        private List<Integer> uncoveredIndex = new ArrayList<>();
        private List<Integer> coveringIndex;
        private List<Integer> coveredByIndex;
        private List<Integer> dependsIndex = new ArrayList<>();
        private int statusId;
        private List<String> path = new ArrayList<>();
        private LinkedSpecificationItem item;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Sets the {@code index} and returns a reference to this Builder enabling method chaining.
         *
         * @param index
         *         the {@code index} to set
         * @return a reference to this Builder
         */
        public Builder withIndex(int index) {
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
        public Builder withTypeIndex(int typeIndex) {
            this.typeIndex = typeIndex;
            return this;
        }

        /**
         * Sets the {@code title} and returns a reference to this Builder enabling method chaining.
         *
         * @param title
         *         the {@code title} to set
         * @return a reference to this Builder
         */
        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the {@code name} and returns a reference to this Builder enabling method chaining.
         *
         * @param name
         *         the {@code name} to set
         * @return a reference to this Builder
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the {@code id} and returns a reference to this Builder enabling method chaining.
         *
         * @param id
         *         the {@code id} to set
         * @return a reference to this Builder
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the {@code tagIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param tagIndex
         *         the {@code tagIndex} to set
         * @return a reference to this Builder
         */
        public Builder withTagIndex(List<Integer> tagIndex) {
            this.tagIndex = tagIndex;
            return this;
        }

        /**
         * Sets the {@code providesIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param providesIndex
         *         the {@code providesIndex} to set
         * @return a reference to this Builder
         */
        public Builder withProvidesIndex(List<Integer> providesIndex) {
            this.providesIndex = providesIndex;
            return this;
        }

        /**
         * Sets the {@code neededTypeIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param neededTypeIndex
         *         the {@code neededTypeIndex} to set
         * @return a reference to this Builder
         */
        public Builder withNeededTypeIndex(List<Integer> neededTypeIndex) {
            this.neededTypeIndex = neededTypeIndex;
            return this;
        }

        /**
         * Sets the {@code coveredIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param coveredIndex
         *         the {@code coveredIndex} to set
         * @return a reference to this Builder
         */
        public Builder withCoveredIndex(List<Integer> coveredIndex) {
            this.coveredIndex = coveredIndex;
            return this;
        }

        /**
         * Sets the {@code uncoveredIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param uncoveredIndex
         *         the {@code uncoveredIndex} to set
         * @return a reference to this Builder
         */
        public Builder withUncoveredIndex(List<Integer> uncoveredIndex) {
            this.uncoveredIndex = uncoveredIndex;
            return this;
        }

        /**
         * Sets the {@code coveringIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param coveringIndex
         *         the {@code coveringIndex} to set
         * @return a reference to this Builder
         */
        public Builder withCoveringIndex(List<Integer> coveringIndex) {
            this.coveringIndex = coveringIndex;
            return this;
        }

        /**
         * Sets the {@code coveredByIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param coveredByIndex
         *         the {@code coveredByIndex} to set
         * @return a reference to this Builder
         */
        public Builder withCoveredByIndex(List<Integer> coveredByIndex) {
            this.coveredByIndex = coveredByIndex;
            return this;
        }

        /**
         * Sets the {@code dependsIndex} and returns a reference to this Builder enabling method chaining.
         *
         * @param dependsIndex
         *         the {@code dependsIndex} to set
         * @return a reference to this Builder
         */
        public Builder withDependsIndex(List<Integer> dependsIndex) {
            this.dependsIndex = dependsIndex;
            return this;
        }

        /**
         * Sets the {@code statusId} and returns a reference to this Builder enabling method chaining.
         *
         * @param statusId
         *         the {@code statusId} to set
         * @return a reference to this Builder
         */
        public Builder withStatusId(int statusId) {
            this.statusId = statusId;
            return this;
        }

        /**
         * Sets the {@code path} and returns a reference to this Builder enabling method chaining.
         *
         * @param path
         *         the {@code path} to set
         * @return a reference to this Builder
         */
        public Builder withPath(List<String> path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the {@code item} and returns a reference to this Builder enabling method chaining.
         *
         * @param item
         *         the {@code item} to set
         * @return a reference to this Builder
         */
        public Builder withItem(LinkedSpecificationItem item) {
            this.item = item;
            return this;
        }

        /**
         * Returns a {@code UxSpecItem} built from the parameters previously set.
         *
         * @return a {@code UxSpecItem} built with parameters of this {@code UxSpecItem.Builder}
         */
        public UxSpecItem build() {
            return new UxSpecItem(this);
        }
    }
}
