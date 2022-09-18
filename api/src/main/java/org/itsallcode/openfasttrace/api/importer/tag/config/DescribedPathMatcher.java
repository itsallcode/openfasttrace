package org.itsallcode.openfasttrace.api.importer.tag.config;
import java.nio.file.*;
import java.util.*;

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
     * Get the description for this {@link DescribedPathMatcher}.
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
