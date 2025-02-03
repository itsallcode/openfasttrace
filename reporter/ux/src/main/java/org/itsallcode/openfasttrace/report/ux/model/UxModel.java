package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;

import java.util.List;

/**
 * Surrounding model that is used to generate the specitem_data model for OpenFastTrace-UX.
 */
public class UxModel
{
    private final String name;
    private final List<String> artifactTypes;
    private final int numberOfSpecItems;
    private final int uncoveredSpecItems;

    private UxModel(Builder builder)
    {
        name = builder.name;
        artifactTypes = builder.artifactTypes;
        numberOfSpecItems = builder.numberOfSpecItems;
        uncoveredSpecItems = builder.uncoveredSpecItems;
    }

    /**
     * @return Name of the project
     */
    public String getName()
    {
        return name;
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
     * {@code UxModel} builder static inner class.
     */
    public static final class Builder
    {
        private String name;
        private List<String> artifactTypes;
        private int numberOfSpecItems;
        private int uncoveredSpecItems;

        private Builder()
        {
        }

        public static Builder builder()
        {
            return new Builder();
        }

        /**
         * Sets the {@code name} and returns a reference to this Builder enabling method chaining.
         *
         * @param name
         *         the {@code name} to set
         * @return a reference to this Builder
         */
        public Builder withName(String name)
        {
            this.name = name;
            return this;
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
         * Returns a {@code UxModel} built from the parameters previously set.
         *
         * @return a {@code UxModel} built with parameters of this {@code UxModel.Builder}
         */
        public UxModel build()
        {
            return new UxModel(this);
        }
    }
}
