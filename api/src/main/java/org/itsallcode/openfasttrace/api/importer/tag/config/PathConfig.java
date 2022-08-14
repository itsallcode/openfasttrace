package org.itsallcode.openfasttrace.api.importer.tag.config;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * The configuration of a single path that is imported by the tag importer Use
 * {@link #builder()} to create a new instance.
 */
public class PathConfig
{
    private static final Logger LOG = Logger.getLogger(PathConfig.class.getName());

    private final DescribedPathMatcher pathMatcher;
    private final String coveredItemNamePrefix;
    private final String coveredItemArtifactType;
    private final String tagArtifactType;

    private PathConfig(final Builder builder)
    {
        this.pathMatcher = Objects.requireNonNull(builder.pathMatcher, "pathMatcher");
        this.coveredItemNamePrefix = builder.coveredItemNamePrefix;
        this.coveredItemArtifactType = Objects.requireNonNull(builder.coveredItemArtifactType,
                "coveredItemArtifactType");
        this.tagArtifactType = Objects.requireNonNull(builder.tagArtifactType, "tagArtifactType");
    }

    /**
     * @return the description of this path configuration.
     */
    public String getDescription()
    {
        return this.pathMatcher.getDescription();
    }

    /**
     * Tells if given {@link InputFile} matches this path configuration.
     * 
     * @param file
     *            the {@link InputFile} to match.
     * @return {@code true} if, and only if, the {@link InputFile} matches this
     *         matcher's pattern.
     */
    public boolean matches(final InputFile file)
    {
        final boolean matches = this.pathMatcher.matches(file);
        LOG.finest(() -> "File " + file + " matches " + this.pathMatcher + " = " + matches);
        return matches;
    }

    /**
     * @return the artifact type.
     */
    public String getTagArtifactType()
    {
        return this.tagArtifactType;
    }

    /**
     * @return the artifact type used for covered items.
     */
    public String getCoveredItemArtifactType()
    {
        return this.coveredItemArtifactType;
    }

    /**
     * @return the name prefix used for covered items.
     */
    public String getCoveredItemNamePrefix()
    {
        return this.coveredItemNamePrefix;
    }

    @Override
    public String toString()
    {
        return "PathConfig [pathMatcher='" + this.pathMatcher.getDescription()
                + "', coveredItemNamePrefix=" + this.coveredItemNamePrefix
                + ", coveredItemArtifactType=" + this.coveredItemArtifactType + ", tagArtifactType="
                + this.tagArtifactType + "]";
    }

    /**
     * @return a new {@link Builder} that allows creating a {@link PathConfig}
     *         object.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for {@link PathConfig} objects.
     */
    public static class Builder
    {
        private DescribedPathMatcher pathMatcher;
        private String coveredItemNamePrefix;
        private String coveredItemArtifactType;
        private String tagArtifactType;

        private Builder()
        {
        }

        /**
         * @param pattern
         *            the pattern for the path, see
         *            {@link FileSystem#getPathMatcher(String)} for supported
         *            syntax.
         * @return this builder instance.
         */
        public Builder patternPathMatcher(final String pattern)
        {
            this.pathMatcher = DescribedPathMatcher.createPatternMatcher(pattern);
            return this;
        }

        /**
         * @param paths
         *            a {@link List} of {@link Path}s that match should be
         *            matched for the {@link PathConfig}.
         * @return this builder instance.
         */
        public Builder pathListMatcher(final List<Path> paths)
        {
            this.pathMatcher = DescribedPathMatcher.createPathListMatcher(paths);
            return this;
        }

        /**
         * @param coveredItemNamePrefix
         *            the common name prefix for the item covered by the
         *            imported {@link SpecificationItem}.
         * @return this builder instance.
         */
        public Builder coveredItemNamePrefix(final String coveredItemNamePrefix)
        {
            this.coveredItemNamePrefix = coveredItemNamePrefix;
            return this;
        }

        /**
         * @param coveredItemArtifactType
         *            the artifact type covered by the imported
         *            {@link SpecificationItem}s.
         * @return this builder instance.
         */
        public Builder coveredItemArtifactType(final String coveredItemArtifactType)
        {
            this.coveredItemArtifactType = coveredItemArtifactType;
            return this;
        }

        /**
         * @param tagArtifactType
         *            artifact type of the imported {@link SpecificationItem}s.
         * 
         * @return this builder instance.
         */
        public Builder tagArtifactType(final String tagArtifactType)
        {
            this.tagArtifactType = tagArtifactType;
            return this;
        }

        /**
         * @return the new {@link PathConfig}.
         */
        public PathConfig build()
        {
            return new PathConfig(this);
        }
    }
}
