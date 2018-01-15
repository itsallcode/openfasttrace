package openfasttrack.importer.legacytag;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class PathConfig
{
    private static final String REGEX_PREFIX = "regex:";
    private static final String GLOB_PREFIX = "glob:";
    private final String pattern;
    private final PathMatcher pathMatcher;

    public PathConfig(final String pattern)
    {
        this(pattern, createMatcher(pattern));
    }

    private PathConfig(final String pattern, final PathMatcher pathMatcher)
    {
        this.pattern = pattern;
        this.pathMatcher = pathMatcher;
    }

    private static PathMatcher createMatcher(final String pattern)
    {
        return FileSystems.getDefault().getPathMatcher(normalizePattern(pattern));
    }

    private static String normalizePattern(final String pattern)
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
}
