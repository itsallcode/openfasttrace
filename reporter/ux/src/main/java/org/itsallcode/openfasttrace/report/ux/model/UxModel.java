package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Surrounding model that is used to generate the specitem_data model for OpenFastTrace-UX.
 */
public class UxModel
{
    private final String projectName;
    private final List<String> artifactTypes;
    private final List<String> tags;
    private final List<String> statusNames;

    private final int numberOfSpecItems;
    private final int uncoveredSpecItems;

    private final List<UxSpecItem> items;

    private final List<Integer> typeCount;
    private final List<Integer> uncoveredCount;
    private final List<Integer> statusCount;
    private final List<Integer> tagCount;

    private UxModel(Builder builder)
    {
        projectName = builder.projectName;
        artifactTypes = builder.artifactTypes;
        tags = builder.tags;
        statusNames = builder.statusNames;
        numberOfSpecItems = builder.numberOfSpecItems;
        uncoveredSpecItems = builder.uncoveredSpecItems;
        items = builder.items;
        typeCount = builder.typeCount;
        uncoveredCount = builder.uncoveredCount;
        statusCount = builder.statusCount;
        tagCount = builder.tagCount;
    }

    public static Builder builder(UxModel copy)
    {
        Builder builder = new Builder();
        builder.projectName = copy.getProjectName();
        builder.artifactTypes = copy.getArtifactTypes();
        builder.tags = copy.getTags();
        builder.statusNames = copy.getStatusNames();
        builder.numberOfSpecItems = copy.getNumberOfSpecItems();
        builder.uncoveredSpecItems = copy.getUncoveredSpecItems();
        builder.items = copy.getItems();
        builder.typeCount = copy.getTypeCount();
        builder.uncoveredCount = copy.getUncoveredCount();
        builder.statusCount = copy.getStatusCount();
        builder.tagCount = copy.getTagCount();
        return builder;
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
     * @return The names of the {@link ItemStatus} enum entries.
     */
    public List<String> getStatusNames() {
        return statusNames;
    }

    /**
     * @return items within the model
     */
    public List<UxSpecItem> getItems() {
        return items;
    }

    /**
     * @return number of items by type index
     */
    public List<Integer> getTypeCount()
    {
        return typeCount;
    }

    /**
     * @return covered count per soecObject type
     */
    public List<Integer> getUncoveredCount()
    {
        return uncoveredCount;
    }

    /**
     * @return number of items by status index
     */
    public List<Integer> getStatusCount()
    {
        return statusCount;
    }

    /**
     * @return number of items by status index
     */
    public List<Integer> getTagCount()
    {
        return tagCount;
    }

    /**
     * {@code UxModel} builder static inner class.
     */
    public static final class Builder
    {
        private List<String> artifactTypes;
        private List<String> tags;
        private List<String> statusNames;
        private int numberOfSpecItems;
        private int uncoveredSpecItems;
        private List<UxSpecItem> items;
        private List<Integer> typeCount;
        private List<Integer> uncoveredCount;
        private List<Integer> statusCount;
        private List<Integer> tagCount;
        private String projectName;

        private Builder()
        {
        }

        public static Builder builder()
        {
            return new Builder();
        }

        /**
         * Sets the {@code artifactTypes} and returns a reference to this Builder enabling method chaining.
         *
         * @param artifactTypes
         *         the {@code artifactTypes} to set
         * @return a reference to this Builder
         */
        public Builder withArtifactTypes(List<String> artifactTypes)
        {
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
        public Builder withTags(List<String> tags)
        {
            this.tags = tags;
            return this;
        }

        /**
         * Sets the {@code statusNames} and returns a reference to this Builder enabling method chaining.
         *
         * @param statusNames
         *         the {@code statusNames} to set
         * @return a reference to this Builder
         */
        public Builder withStatusNames(List<String> statusNames)
        {
            this.statusNames = statusNames;
            return this;
        }

        /**
         * Sets the {@code numberOfSpecItems} and returns a reference to this Builder enabling method chaining.
         *
         * @param numberOfSpecItems
         *         the {@code numberOfSpecItems} to set
         * @return a reference to this Builder
         */
        public Builder withNumberOfSpecItems(int numberOfSpecItems)
        {
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
        public Builder withUncoveredSpecItems(int uncoveredSpecItems)
        {
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
        public Builder withItems(List<UxSpecItem> items)
        {
            this.items = items;
            return this;
        }

        /**
         * Sets the {@code typeCount} and returns a reference to this Builder enabling method chaining.
         *
         * @param typeCount
         *         the {@code typeCount} to set
         * @return a reference to this Builder
         */
        public Builder withTypeCount(List<Integer> typeCount)
        {
            this.typeCount = typeCount;
            return this;
        }

        /**
         * Sets the {@code uncoveredCount} and returns a reference to this Builder enabling method chaining.
         *
         * @param uncoveredCount
         *         the {@code uncoveredCount} to set
         * @return a reference to this Builder
         */
        public Builder withUncoveredCount(List<Integer> uncoveredCount)
        {
            this.uncoveredCount = uncoveredCount;
            return this;
        }

        /**
         * Sets the {@code statusCount} and returns a reference to this Builder enabling method chaining.
         *
         * @param statusCount
         *         the {@code statusCount} to set
         * @return a reference to this Builder
         */
        public Builder withStatusCount(List<Integer> statusCount)
        {
            this.statusCount = statusCount;
            return this;
        }

        /**
         * Sets the {@code tagCount} and returns a reference to this Builder enabling method chaining.
         *
         * @param tagCount
         *         the {@code tagCount} to set
         * @return a reference to this Builder
         */
        public Builder withTagCount(List<Integer> tagCount)
        {
            this.tagCount = tagCount;
            return this;
        }

        /**
         * Returns a {@code UxModel} built from the parameters previously set.
         *
         * @return a {@code UxModel} built with parameters of this {@code UxModel.Builder}
         */
        public UxModel build()
        {
            return new UxModel(this);
        }

        /**
         * Sets the {@code projectName} and returns a reference to this Builder enabling method chaining.
         *
         * @param projectName
         *         the {@code projectName} to set
         * @return a reference to this Builder
         */
        public Builder withProjectName(String projectName)
        {
            this.projectName = projectName;
            return this;
        }
    }

} // UxModel
