package org.itsallcode.openfasttrace.api.importer.tag.config;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * A wrapper for {@link PathMatcher} providing a description.
 */
public class DescribedPathMatcher
{
    private static final String REGEX_PREFIX = "regex:";
    private static final String GLOB_PREFIX = "glob:";

    private final String description;
    private final PathMatcher matcher;

    private DescribedPathMatcher(final String description, final PathMatcher matcher)
    {
        this.description = description;
        this.matcher = matcher;
    }

    /**
     * Create a new {@link DescribedPathMatcher} for the given pattern.
     * 
     * @param pattern
     *            the pattern for the new matcher. See
     *            {@link FileSystem#getPathMatcher(String)} for the supported
     *            syntax.
     * @return a new {@link DescribedPathMatcher}.
     */
    public static DescribedPathMatcher createPatternMatcher(final String pattern)
    {
        final String fullPattern = addMissingPatternPrefix(pattern);
        final PathMatcher patternMatcher = FileSystems.getDefault().getPathMatcher(fullPattern);
        return new DescribedPathMatcher(fullPattern, patternMatcher);
    }

    /**
     * Create a new {@link DescribedPathMatcher} for the given list of paths.
     * 
     * @param paths
     *            the paths that the new matcher will match.
     * @return a new {@link DescribedPathMatcher}.
     */
    public static DescribedPathMatcher createPathListMatcher(final List<Path> paths)
    {
        final ListBasedPathMatcher listMatcher = new ListBasedPathMatcher(new HashSet<>(paths));
        return new DescribedPathMatcher(paths.toString(), listMatcher);
    }

    private static String addMissingPatternPrefix(final String pattern)
    {
        if (pattern.startsWith(GLOB_PREFIX) || pattern.startsWith(REGEX_PREFIX))
        {
            return pattern;
        }
        return GLOB_PREFIX + pattern;
    }

    /**
     * @return the description for this {@link DescribedPathMatcher}.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Tells if given {@link InputFile} matches this matcher's pattern.
     * 
     * @param path
     *            the {@link InputFile} to match.
     * @return {@code true} if, and only if, the {@link InputFile} matches this
     *         matcher's pattern.
     * @see PathMatcher#matches(Path)
     */
    public boolean matches(final InputFile path)
    {
        return this.matcher.matches(Paths.get(path.getPath()));
    }

    @Override
    public String toString()
    {
        return getDescription();
    }

    private static class ListBasedPathMatcher implements PathMatcher
    {
        private final Set<Path> paths;

        public ListBasedPathMatcher(final Set<Path> paths)
        {
            this.paths = paths;
        }

        @Override
        public boolean matches(final Path path)
        {
            return this.paths.contains(path);
        }
    }
}
