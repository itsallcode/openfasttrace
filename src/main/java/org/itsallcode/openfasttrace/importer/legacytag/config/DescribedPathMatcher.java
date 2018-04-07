package org.itsallcode.openfasttrace.importer.legacytag.config;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class DescribedPathMatcher
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

    static DescribedPathMatcher createPatternMatcher(final String pattern)
    {
        final String fullPattern = addMissingPatternPrefix(pattern);
        final PathMatcher patternMatcher = FileSystems.getDefault().getPathMatcher(fullPattern);
        return new DescribedPathMatcher(fullPattern, patternMatcher);
    }

    static DescribedPathMatcher createPathListMatcher(final List<Path> paths)
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

    String getDescription()
    {
        return this.description;
    }

    boolean matches(final Path path)
    {
        return this.matcher.matches(path);
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
