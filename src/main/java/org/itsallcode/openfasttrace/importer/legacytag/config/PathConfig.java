package org.itsallcode.openfasttrace.importer.legacytag.config;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.core.SpecificationItem;

/**
 * The configuration of a single path that is imported by
 * {@link LegacyTagImporter}.
 */
public class PathConfig
{
    private final DescribedPathMatcher pathMatcher;
    private final String coveredItemNamePrefix;
    private final String coveredItemArtifactType;
    private final String tagArtifactType;

    /**
     * Create a new pattern based path configuration.
     * 
     * @param pattern
     *            the pattern for the path, see
     *            {@link FileSystem#getPathMatcher(String)} for supported
     *            syntax.
     * @param coveredItemArtifactType
     *            the artifact type covered by the imported
     *            {@link SpecificationItem}s.
     * @param coveredItemNamePrefix
     *            the common name prefix for the item covered by the imported
     *            {@link SpecificationItem}.
     * @param tagArtifactType
     *            the artifact type of the imported {@link SpecificationItem}s.
     */
    public static PathConfig createPatternConfig(final String pattern,
            final String coveredItemArtifactType, final String coveredItemNamePrefix,
            final String tagArtifactType)
    {
        return new PathConfig(DescribedPathMatcher.createPatternMatcher(pattern),
                coveredItemArtifactType, coveredItemNamePrefix, tagArtifactType);
    }

    private PathConfig(final DescribedPathMatcher pathMatcher, final String coveredItemArtifactType,
            final String coveredItemNamePrefix, final String tagArtifactType)
    {
        this.pathMatcher = pathMatcher;
        this.coveredItemNamePrefix = coveredItemNamePrefix;
        this.coveredItemArtifactType = coveredItemArtifactType;
        this.tagArtifactType = tagArtifactType;
    }

    public String getDescription()
    {
        return this.pathMatcher.getDescription();
    }

    public boolean matches(final Path file)
    {
        return this.pathMatcher.matches(file);
    }

    public String getTagArtifactType()
    {
        return this.tagArtifactType;
    }

    public String getCoveredItemArtifactType()
    {
        return this.coveredItemArtifactType;
    }

    public String getCoveredItemNamePrefix()
    {
        return this.coveredItemNamePrefix;
    }

    @Override
    public String toString()
    {
        return "PathConfig [pathMatcher=" + this.pathMatcher.toString() + ", coveredItemNamePrefix="
                + this.coveredItemNamePrefix + ", coveredItemArtifactType="
                + this.coveredItemArtifactType + ", tagArtifactType=" + this.tagArtifactType + "]";
    }
}
