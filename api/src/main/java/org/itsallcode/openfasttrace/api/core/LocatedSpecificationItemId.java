package org.itsallcode.openfasttrace.api.core;

import java.util.Objects;
import java.util.Optional;

/**
 * One occurrence of a specification item ID in an imported source artifact. Component ranges may be {@code null} if
 * unknown.
 */
public final class LocatedSpecificationItemId
{
    private final SpecificationItemId id;
    private final SourceRange range;
    private final SourceRange artifactTypeRange;
    private final SourceRange nameRange;
    private final SourceRange revisionRange;

    private LocatedSpecificationItemId(final Builder builder)
    {
        this.id = Objects.requireNonNull(builder.id, "specification item ID must not be null");
        this.range = builder.range;
        this.artifactTypeRange = builder.artifactTypeRange;
        this.nameRange = builder.nameRange;
        this.revisionRange = builder.revisionRange;
    }

    /**
     * Create a new builder for {@link LocatedSpecificationItemId}.
     *
     * @return a new builder instance for {@link LocatedSpecificationItemId}.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Get the ID of this located specification item.
     *
     * @return the item ID
     */
    public SpecificationItemId getId()
    {
        return this.id;
    }

    /**
     * Get the source range of the complete source construct.
     *
     * @return range of the complete source construct
     */
    public SourceRange getRange()
    {
        return this.range;
    }

    /**
     * Get the source range of the artifact type, if represented in source.
     *
     * @return source range of the artifact type, if represented in source
     */
    public Optional<SourceRange> getArtifactTypeRange()
    {
        return Optional.ofNullable(this.artifactTypeRange);
    }

    /**
     * Get the source range of the name, if represented in source.
     *
     * @return source range of the name, if represented in source
     */
    public Optional<SourceRange> getNameRange()
    {
        return Optional.ofNullable(this.nameRange);
    }

    /**
     * Get the source range of the revision, if represented in source.
     *
     * @return source range of the revision, if represented in source
     */
    public Optional<SourceRange> getRevisionRange()
    {
        return Optional.ofNullable(this.revisionRange);
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof final LocatedSpecificationItemId that))
        {
            return false;
        }
        return Objects.equals(this.id, that.id) && Objects.equals(this.range, that.range)
                && Objects.equals(this.artifactTypeRange, that.artifactTypeRange)
                && Objects.equals(this.nameRange, that.nameRange)
                && Objects.equals(this.revisionRange, that.revisionRange);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id, this.range, this.artifactTypeRange, this.nameRange, this.revisionRange);
    }

    /**
     * Builder for {@link LocatedSpecificationItemId}.
     */
    public static final class Builder
    {
        private SpecificationItemId id;
        private SourceRange range;
        private SourceRange artifactTypeRange;
        private SourceRange nameRange;
        private SourceRange revisionRange;

        private Builder()
        {
        }

        /**
         * Set the item ID
         *
         * @param id
         *            the item ID
         * @return this builder instance
         */
        public Builder id(final SpecificationItemId id)
        {
            this.id = id;
            return this;
        }

        /**
         * Set the source range of the complete source construct
         *
         * @param range
         *            the source range
         * @return this builder instance
         */
        public Builder range(final SourceRange range)
        {
            this.range = range;
            return this;
        }

        /**
         * Set the source range of the artifact type, if represented in source
         *
         * @param artifactTypeRange
         *            the source range of the artifact type
         * @return this builder instance
         */
        public Builder artifactTypeRange(final SourceRange artifactTypeRange)
        {
            this.artifactTypeRange = artifactTypeRange;
            return this;
        }

        /**
         * Set the source range of the name, if represented in source
         *
         * @param nameRange
         *            the source range of the name
         * @return this builder instance
         */
        public Builder nameRange(final SourceRange nameRange)
        {
            this.nameRange = nameRange;
            return this;
        }

        /**
         * Set the source range of the revision, if represented in source
         *
         * @param revisionRange
         *            the source range of the revision
         * @return this builder instance
         */
        public Builder revisionRange(final SourceRange revisionRange)
        {
            this.revisionRange = revisionRange;
            return this;
        }

        /**
         * Build a new instance of {@link LocatedSpecificationItemId}.
         *
         * @return a new instance of {@link LocatedSpecificationItemId}
         */
        public LocatedSpecificationItemId build()
        {
            return new LocatedSpecificationItemId(this);
        }
    }
}
