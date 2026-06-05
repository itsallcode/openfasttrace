package org.itsallcode.openfasttrace.api.core;

import java.util.*;

/**
 * A specification item that requires coverage from other items and provides
 * coverage for other items.
 */
// [impl->dsn~specification-item~3]
public final class SpecificationItem
{
    private final SpecificationItemId id;
    private final String title;
    private final String description;
    private final String rationale;
    private final String comment;
    private final Location location;
    private final ItemStatus status;
    private final List<SpecificationItemId> coveredIds;
    private final List<SpecificationItemId> dependOnIds;
    private final List<String> needsArtifactTypes;
    private final List<String> tags;
    private final boolean forwards;

    private SpecificationItem(final Builder builder)
    {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.rationale = builder.rationale;
        this.comment = builder.comment;
        this.location = builder.location;
        this.status = builder.status;
        this.coveredIds = Collections.unmodifiableList(builder.coveredIds);
        this.dependOnIds = Collections.unmodifiableList(builder.dependOnIds);
        this.needsArtifactTypes = Collections.unmodifiableList(builder.neededArtifactTypes);
        this.tags = Collections.unmodifiableList(builder.tags);
        this.forwards = builder.forwards;
    }

    /**
     * Get the ID of the specification item
     *
     * @return ID
     */
    public SpecificationItemId getId()
    {
        return this.id;
    }

    /**
     * Get the artifact type of the specification item
     * 
     * @return artifact type
     */
    public String getArtifactType()
    {
        return this.id.getArtifactType();
    }

    /**
     * Get the name part of the specification item ID
     * 
     * <p>
     * Not to be mixed up with the
     * {@link org.itsallcode.openfasttrace.api.core.SpecificationItem#getTitle()}
     * of the specification item
     * </p>
     * 
     * @return name part of the specification item ID
     */
    public String getName()
    {
        return this.id.getName();
    }

    /**
     * Get the revision of the specification item
     * 
     * @return revision
     */
    public int getRevision()
    {
        return this.id.getRevision();
    }

    /**
     * Get the title of the specification item
     *
     * @return title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Get the description of the specification item
     *
     * @return description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Get the rationale of the specification item
     *
     * @return rationale
     */
    public String getRationale()
    {
        return this.rationale;
    }

    /**
     * Get the comment of the specification item
     *
     * @return comment
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * Get the list of covered {@link SpecificationItemId}s
     *
     * @return list of covered IDs
     */
    public List<SpecificationItemId> getCoveredIds()
    {
        return this.coveredIds;
    }

    /**
     * Get the list of {@link SpecificationItemId}s this item depends on
     *
     * @return list of IDs this item depends on
     */
    public List<SpecificationItemId> getDependOnIds()
    {
        return this.dependOnIds;
    }

    /**
     * Get the list of artifact types this specification item need to be covered
     * in
     *
     * @return list of artifact types
     */
    public List<String> getNeedsArtifactTypes()
    {
        return this.needsArtifactTypes;
    }

    /**
     * Check if this specification item needs to be covered by the given
     * artifact type.
     *
     * @param artifactType
     *            the artifact type for which needed coverage is evaluated.
     * @return {@code true} if this item needs to be covered by the given
     *         artifact type.
     */
    public boolean needsCoverageByArtifactType(final String artifactType)
    {
        return this.needsArtifactTypes.contains(artifactType);
    }

    /**
     * Check if the item needs any coverage
     *
     * @return {@code true} if the item needs coverage
     */
    public boolean needsCoverage()
    {
        return !this.needsArtifactTypes.isEmpty();
    }

    /**
     * Get the location where this specification item was defined.
     * 
     * @return location of this item.
     */
    public Location getLocation()
    {
        return this.location;
    }

    /**
     * Get the maturity status of the specification item
     * 
     * @return maturity status
     */
    public ItemStatus getStatus()
    {
        return this.status;
    }

    /**
     * Get the list of associated tags
     * 
     * @return tags
     */
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * Check if this item forwards coverage.
     * 
     * @return {@code true} if this specification item forwards needed coverage
     */
    public boolean isForwarding()
    {
        return this.forwards;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof final SpecificationItem that)) {
            return false;
        }
        return forwards == that.forwards && Objects.equals(id, that.id) && Objects.equals(title, that.title)
                && Objects.equals(description, that.description) && Objects.equals(rationale, that.rationale)
                && Objects.equals(comment, that.comment) && Objects.equals(location, that.location)
                && status == that.status && Objects.equals(coveredIds, that.coveredIds)
                && Objects.equals(dependOnIds, that.dependOnIds)
                && Objects.equals(needsArtifactTypes, that.needsArtifactTypes) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, rationale, comment, location, status, coveredIds, dependOnIds,
                needsArtifactTypes, tags, forwards);
    }

    /**
     * Create a builder for specification items
     * 
     * @return new {@link Builder} instance
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for objects of type {@link SpecificationItem}
     */
    public static final class Builder
    {
        private SpecificationItemId id;
        private String title;
        private String description;
        private String rationale;
        private String comment;
        private ItemStatus status;
        private Location location;
        private final List<SpecificationItemId> coveredIds;
        private final List<SpecificationItemId> dependOnIds;
        private final List<String> neededArtifactTypes;
        private final List<String> tags;
        private boolean forwards;

        /**
         * Create a new instance of type {@link SpecificationItem.Builder}
         */
        private Builder()
        {
            this.id = null;
            this.title = "";
            this.description = "";
            this.rationale = "";
            this.comment = "";
            this.location = null;
            this.status = ItemStatus.APPROVED;
            this.coveredIds = new ArrayList<>();
            this.dependOnIds = new ArrayList<>();
            this.neededArtifactTypes = new ArrayList<>();
            this.tags = new ArrayList<>();
        }

        /**
         * Set the specification item ID
         *
         * @param id
         *            the ID
         * @return this builder instance
         */
        public Builder id(final SpecificationItemId id)
        {
            this.id = id;
            return this;
        }

        /**
         * Set the specification item ID by its parts
         *
         * @param artifactType
         *            the artifact type
         * @param name
         *            the artifact name
         * @param revision
         *            the revision number
         * @return this builder instance
         */
        public Builder id(final String artifactType, final String name, final int revision)
        {
            this.id = new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build();
            return this;
        }

        /**
         * Set the title
         *
         * @param title
         *            the title
         * @return this builder instance
         */
        public Builder title(final String title)
        {
            this.title = title;
            return this;
        }

        /**
         * Set the description
         *
         * @param description
         *            the description
         * @return this builder instance
         */
        public Builder description(final String description)
        {
            this.description = description;
            return this;
        }

        /**
         * Set the rationale
         *
         * @param rationale
         *            the rationale
         * @return this builder instance
         */
        public Builder rationale(final String rationale)
        {
            this.rationale = rationale;
            return this;
        }

        /**
         * Set the comment
         *
         * @param comment
         *            the comment
         * @return this builder instance
         */
        public Builder comment(final String comment)
        {
            this.comment = comment;
            return this;
        }

        /**
         * Set the status
         * 
         * @param status
         *            the status
         * @return this builder instance
         */
        public Builder status(final ItemStatus status)
        {
            this.status = status;
            return this;
        }

        /**
         * Add the ID of a specification item covered by the item to build
         *
         * @param coveredId
         *            the covered ID
         * @return this builder instance
         */
        public Builder addCoveredId(final SpecificationItemId coveredId)
        {
            this.coveredIds.add(coveredId);
            return this;
        }

        /**
         * Add the ID of a specification item covered by the item to build
         *
         * @param artifactType
         *            the artifact type of the covered item
         * @param name
         *            the artifact name of the covered item
         * @param revision
         *            the revision number of the covered item
         * @return this builder instance
         */
        public Builder addCoveredId(final String artifactType, final String name,
                final int revision)
        {
            this.coveredIds.add(new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build());
            return this;
        }

        /**
         * Add the ID of a specification item the item to be build depends on
         *
         * @param dependOnId
         *            the ID the item to be build depends on
         * @return this builder instance
         */
        public Builder addDependOnId(final SpecificationItemId dependOnId)
        {
            this.dependOnIds.add(dependOnId);
            return this;
        }

        /**
         * Add the ID of a specification item the item to be build depends on
         *
         * @param artifactType
         *            the artifact type of item to be build depends on
         * @param name
         *            the artifact name of item to be build depends on
         * @param revision
         *            the revision number of item to be build depends on
         * @return this builder instance
         */
        public Builder addDependOnId(final String artifactType, final String name,
                final int revision)
        {
            this.dependOnIds.add(new SpecificationItemId.Builder() //
                    .artifactType(artifactType).name(name).revision(revision) //
                    .build());
            return this;
        }

        /**
         * Add an artifact type where the specification item to be build
         * requires to be covered
         *
         * @param neededArtifactType
         *            the artifact type
         * @return this builder instance
         */
        public Builder addNeedsArtifactType(final String neededArtifactType)
        {
            this.neededArtifactTypes.add(neededArtifactType);
            return this;
        }

        /**
         * Add a tag
         *
         * @param tag
         *            the tag
         * @return this builder instance
         */
        public Builder addTag(final String tag)
        {
            this.tags.add(tag);
            return this;
        }

        /**
         * Set the location
         *
         * @param location
         *            the location
         * @return this builder instance
         */
        public Builder location(final Location location)
        {
            this.location = location;
            return this;
        }

        /**
         * Set the location
         *
         * @param path
         *            the path of the location
         * @param line
         *            the line of the location
         * @return this builder instance
         */
        public Builder location(final String path, final Integer line)
        {
            Objects.requireNonNull(path, "path");
            Objects.requireNonNull(line, "line");
            return this.location(Location.create(path, line));
        }

        /**
         * Build a new instance of type {@link SpecificationItem}
         *
         * @return the specification item
         */
        public SpecificationItem build()
        {
            if (this.id == null)
            {
                throw new IllegalStateException("No id given");
            }
            return new SpecificationItem(this);
        }

        /**
         * Set to {@code true} if this specification item forwards needed
         * coverage
         *
         * @param forwards
         *            {@code true} if the specification item forwards needed
         *            coverage
         * @return this builder instance
         */
        public Builder forwards(final boolean forwards)
        {
            this.forwards = forwards;
            return this;
        }
    }
}
