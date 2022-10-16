package org.itsallcode.openfasttrace.api;

import java.util.Collections;
import java.util.Set;

/**
 * Settings for import filtering
 */
public final class FilterSettings
{
    private final Set<String> artifactTypes;
    private final Set<String> tags;
    private final boolean withoutTags;

    private FilterSettings(final Builder builder)
    {
        this.artifactTypes = builder.artifactTypes;
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
        return this.artifactTypes;
    }

    /**
     * Get the tags the filter must match.
     * 
     * @return artifact types that must be matched
     */
    public Set<String> getTags()
    {
        return this.tags;
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
        return isArtifactTypeCriteriaSet() || isTagCriteriaSet();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.artifactTypes == null) ? 0 : this.artifactTypes.hashCode());
        result = prime * result + (this.withoutTags ? 1231 : 1237);
        result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof FilterSettings))
        {
            return false;
        }
        final FilterSettings other = (FilterSettings) obj;
        if (this.artifactTypes == null)
        {
            if (other.artifactTypes != null)
            {
                return false;
            }
        }
        else if (!this.artifactTypes.equals(other.artifactTypes))
        {
            return false;
        }
        if (this.withoutTags != other.withoutTags)
        {
            return false;
        }
        if (this.tags == null)
        {
            if (other.tags != null)
            {
                return false;
            }
        }
        else if (!this.tags.equals(other.tags))
        {
            return false;
        }
        return true;
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
    public static class Builder
    {
        private Set<String> artifactTypes = Collections.emptySet();
        private Set<String> tags = Collections.emptySet();
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
            this.artifactTypes = artifactTypes;
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
            this.tags = tags;
            return this;
        }

        /**
         * Configure if filter allows items that have no tags.
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