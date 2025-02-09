package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;

import java.util.List;

/**
 * Surrounding model that is used to generate the specitem_data model for OpenFastTrace-UX.
 */
public class UxModel
{
    private final String projectName;
    private final List<String> artifactTypes;
    private final List<String> tags;

    private final int numberOfSpecItems;
    private final int uncoveredSpecItems;

    private final List<UxSpecItem> items;

    private UxModel(Builder builder) {
        projectName = builder.projectName;
        artifactTypes = builder.artifactTypes;
        tags = builder.tags;
        numberOfSpecItems = builder.numberOfSpecItems;
        uncoveredSpecItems = builder.uncoveredSpecItems;
        items = builder.items;
    }

    /**
     * @return Name of the project
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * @return types of {@link SpecificationItem}s trace
     */
    public List<String> getArtifactTypes()
    {
        return artifactTypes;
    }

    /**
     * @return Total number of {@link SpecificationItem}s traced
     */
    public int getNumberOfSpecItems()
    {
        return numberOfSpecItems;
    }

    /**
     * @return Number of traced {@link SpecificationItem}s that have deep uncoverered or a staled coverage.
     */
    public int getUncoveredSpecItems()
    {
        return uncoveredSpecItems;
    }

    /**
     * @return all tags of all items in index order used by {@link UxSpecItem}.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @return items within the model
     */
    public List<UxSpecItem> getItems() {
        return items;
    }

    /**
     * {@code UxModel} builder static inner class.
     */
    public static final class Builder {
        private List<String> artifactTypes;
        private List<String> tags;
        private int numberOfSpecItems;
        private int uncoveredSpecItems;
        private List<UxSpecItem> items;
        private String projectName;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Sets the {@code artifactTypes} and returns a reference to this Builder enabling method chaining.
         *
         * @param artifactTypes
         *         the {@code artifactTypes} to set
         * @return a reference to this Builder
         */
        public Builder withArtifactTypes(List<String> artifactTypes) {
            this.artifactTypes = artifactTypes;
            return this;
        }

        /**
         * Sets the {@code tags} and returns a reference to this Builder enabling method chaining.
         *
         * @param tags
         *         the {@code tags} to set
         * @return a reference to this Builder
         */
        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Sets the {@code numberOfSpecItems} and returns a reference to this Builder enabling method chaining.
         *
         * @param numberOfSpecItems
         *         the {@code numberOfSpecItems} to set
         * @return a reference to this Builder
         */
        public Builder withNumberOfSpecItems(int numberOfSpecItems) {
            this.numberOfSpecItems = numberOfSpecItems;
            return this;
        }

        /**
         * Sets the {@code uncoveredSpecItems} and returns a reference to this Builder enabling method chaining.
         *
         * @param uncoveredSpecItems
         *         the {@code uncoveredSpecItems} to set
         * @return a reference to this Builder
         */
        public Builder withUncoveredSpecItems(int uncoveredSpecItems) {
            this.uncoveredSpecItems = uncoveredSpecItems;
            return this;
        }

        /**
         * Sets the {@code items} and returns a reference to this Builder enabling method chaining.
         *
         * @param items
         *         the {@code items} to set
         * @return a reference to this Builder
         */
        public Builder withItems(List<UxSpecItem> items) {
            this.items = items;
            return this;
        }

        /**
         * Returns a {@code UxModel} built from the parameters previously set.
         *
         * @return a {@code UxModel} built with parameters of this {@code UxModel.Builder}
         */
        public UxModel build() {
            return new UxModel(this);
        }

        /**
         * Sets the {@code projectName} and returns a reference to this Builder enabling method chaining.
         *
         * @param projectName
         *         the {@code projectName} to set
         * @return a reference to this Builder
         */
        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }
    }

} // UxModel
