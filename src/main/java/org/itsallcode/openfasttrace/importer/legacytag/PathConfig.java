package org.itsallcode.openfasttrace.importer.legacytag;

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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class PathConfig
{
    private static final String REGEX_PREFIX = "regex:";
    private static final String GLOB_PREFIX = "glob:";

    private final String pattern;
    private final PathMatcher pathMatcher;
    private final String coveredItemNamePrefix;
    private final String coveredItemArtifactType;
    private final String tagArtifactType;

    public PathConfig(final String pattern, final String coveredItemArtifactType,
            final String coveredItemNamePrefix, final String tagArtifactType)
    {
        this(pattern, createMatcher(pattern), coveredItemArtifactType, coveredItemNamePrefix,
                tagArtifactType);
    }

    private PathConfig(final String pattern, final PathMatcher pathMatcher,
            final String coveredItemArtifactType, final String coveredItemNamePrefix,
            final String tagArtifactType)
    {
        this.pattern = pattern;
        this.pathMatcher = pathMatcher;
        this.coveredItemNamePrefix = coveredItemNamePrefix;
        this.coveredItemArtifactType = coveredItemArtifactType;
        this.tagArtifactType = tagArtifactType;
    }

    private static PathMatcher createMatcher(final String pattern)
    {
        return FileSystems.getDefault().getPathMatcher(addMissingPatternPrefix(pattern));
    }

    private static String addMissingPatternPrefix(final String pattern)
    {
        if (pattern.startsWith(GLOB_PREFIX) || pattern.startsWith(REGEX_PREFIX))
        {
            return pattern;
        }
        return GLOB_PREFIX + pattern;
    }

    public String getPattern()
    {
        return this.pattern;
    }

    boolean matches(final Path file)
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
        return "PathConfig [pattern=" + this.pattern + ", pathMatcher=" + this.pathMatcher
                + ", coveredItemNamePrefix=" + this.coveredItemNamePrefix
                + ", coveredItemArtifactType="
                + this.coveredItemArtifactType + ", tagArtifactType=" + this.tagArtifactType + "]";
    }
}
