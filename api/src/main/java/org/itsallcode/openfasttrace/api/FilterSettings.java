package org.itsallcode.openfasttrace.api;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.itsallcode.openfasttrace.api.core.ItemStatus;

/**
 * Settings for import filtering
 */
public final class FilterSettings
{
    private final Set<String> artifactTypes;
    private final Set<ItemStatus> wantedStatuses;
    private final Set<String> tags;
    private final boolean withoutTags;

    private FilterSettings(final Builder builder)
    {
        this.artifactTypes = builder.artifactTypes;
        this.wantedStatuses = builder.wantedStatuses;
        this.tags = builder.tags;
        this.withoutTags = builder.withoutTags;
    }

    /**
     * Get the artifact types the filter must match.
     * 
     * @return artifact types that must be matched
     */
    public Set<String> getArtifactTypes()
    {
        return Set.copyOf(this.artifactTypes);
    }

    /**
     * Get the statuses the filter must match.
     * 
     * @return statuses that must be matched
     */
    public Set<ItemStatus> getWantedStatuses()
    {
        return Set.copyOf(this.wantedStatuses);
    }

    /**
     * Get the tags the filter must match.
     * 
     * @return artifact types that must be matched
     */

    public Set<String> getTags()
    {
        return Set.copyOf(this.tags);
    }

    /**
     * Check if the filter allows items with no tags.
     * 
     * @return {@code true} if the filter allows items with no tags
     */
    public boolean withoutTags()
    {
        return this.withoutTags;
    }

    /**
     * Check if the artifact type filter is set.
     * 
     * @return {@code true} if the artifact type filter is set
     */
    public boolean isArtifactTypeCriteriaSet()
    {
        return this.artifactTypes != null && !this.artifactTypes.isEmpty();
    }

    /**
     * Check if the status filter is set.
     * 
     * @return {@code true} if the status filter is set
     */
    public boolean isStatusCriteriaSet()
    {
        return this.wantedStatuses != null && !this.wantedStatuses.isEmpty();
    }

    /**
     * Check if the tag filter is set.
     * 
     * @return {@code true} if the tag filter is set
     */
    public boolean isTagCriteriaSet()
    {
        return !this.withoutTags() || (this.tags != null && !this.tags.isEmpty());
    }

    /**
     * Check if any kind of filter criteria is set.
     * 
     * @return {@code true} if any filter criteria is set
     */
    public boolean isAnyCriteriaSet()
    {
        return isArtifactTypeCriteriaSet() || isStatusCriteriaSet() || isTagCriteriaSet();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.artifactTypes, this.wantedStatuses, this.tags, this.withoutTags);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof final FilterSettings that)) {
            return false;
        }
        return withoutTags == that.withoutTags && Objects.equals(artifactTypes, that.artifactTypes)
                && Objects.equals(wantedStatuses, that.wantedStatuses) && Objects.equals(tags, that.tags);
    }

    /**
     * Create filter settings that allow everything to pass unfiltered
     * 
     * @return <code>FilterSettings</code> that allow everything to pass
     *         unfiltered
     */
    public static FilterSettings createAllowingEverything()
    {
        return FilterSettings.builder().build();
    }

    /**
     * Create a new {@link Builder} for creating {@link FilterSettings}.
     * 
     * @return a new {@link Builder}.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for {@link FilterSettings}
     */
    public static final class Builder
    {
        private Set<String> artifactTypes = Set.of();
        private Set<ItemStatus> wantedStatuses = EnumSet.noneOf(ItemStatus.class);
        private Set<String> tags = Set.of();
        private boolean withoutTags = true;

        private Builder()
        {
            // empty by intention
        }

        /**
         * Set the list of artifact types that the filter matches.
         * 
         * @param artifactTypes
         *            artifact types that must be matched
         * @return <code>this</code> for fluent programming
         */
        public Builder artifactTypes(final Set<String> artifactTypes)
        {
            this.artifactTypes = Set.copyOf(artifactTypes);
            return this;
        }

        /**
         * Set the list of statuses that the filter matches.
         * 
         * @param statuses
         *            statuses that must be matched
         * @return <code>this</code> for fluent programming
         */
        public Builder wantedStatuses(final Set<ItemStatus> statuses)
        {
            this.wantedStatuses = Set.copyOf(statuses);
            return this;
        }

        /**
         * Set the list of tags that the filter matches.
         * 
         * @param tags
         *            tags that must be matched
         * @return <code>this</code> for fluent programming
         */
        public Builder tags(final Set<String> tags)
        {
            this.tags = Set.copyOf(tags);
            return this;
        }

        /**
         * Configure if the filter allows items that have no tags.
         * 
         * @param noTags
         *            {@code true} to match items without any tags
         * @return <code>this</code> for fluent programming
         */
        public Builder withoutTags(final boolean noTags)
        {
            this.withoutTags = noTags;
            return this;
        }

        /**
         * Build an instance of type {@link FilterSettings}.
         * 
         * @return the new instance.
         */
        public FilterSettings build()
        {
            return new FilterSettings(this);
        }
    }
}
