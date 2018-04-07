package org.itsallcode.openfasttrace.importer.legacytag.config;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DescribedPathMatcherTest
{
    private DescribedPathMatcher matcher;

    @Test
    public void testListBasedMatcherWithEmptyListMatchesNothing()
    {
        createListMatcher();
        assertMatches("path", false);
    }

    @Test
    public void testListBasedMatcherMatchesRelativePath()
    {
        createListMatcher("rel/path");
        assertMatches("rel/path", true);
        assertMatches("/rel/path", false);
        assertMatches("rel/path/", true);
        assertMatches("rel/path1", false);
        assertMatches("REL/path", true);
        assertMatches("rel/PATH", true);
        assertMatches("rel/path.txt", false);
        assertMatches("path", false);
        assertMatches("rel", false);
    }

    @Test
    public void testListBasedMatcherMatchesAbsolutePath()
    {
        createListMatcher("/abs/path");
        assertMatches("/abs/path", true);
        assertMatches("abs/path", false);
        assertMatches("/abs/path/", true);
    }

    @Test
    public void testListBasedMatcherMatchesMultiplePaths()
    {
        createListMatcher("path1", "path2");
        assertMatches("path1", true);
        assertMatches("path2", true);
        assertMatches("path2/", true);
        assertMatches("/path2", false);
        assertMatches("path", false);
    }

    @Test
    public void testListBasedMatcherDescription()
    {
        createListMatcher("path1", "path2");
        assertDescription("[path1, path2]");
    }

    @Test
    public void testPatternMatcherDescription()
    {
        createPatternMatcher("path");
        assertDescription("glob:path");
    }

    @Test
    public void testPatternMatcherMatchesGlob()
    {
        createPatternMatcher("glob:path/**");
        assertMatches("path", false);
        assertMatches("path/", false);
        assertMatches("path/file", true);
        assertMatches("path/dir/", true);
        assertMatches("path/dir/file", true);
    }

    private void createListMatcher(final String... paths)
    {
        final List<Path> pathList = Arrays.stream(paths).map(Paths::get).collect(toList());
        this.matcher = DescribedPathMatcher.createPathListMatcher(pathList);
    }

    private void createPatternMatcher(final String pattern)
    {
        this.matcher = DescribedPathMatcher.createPatternMatcher(pattern);
    }

    private void assertMatches(final String path, final boolean expected)
    {
        assertThat("path " + path, this.matcher.matches(Paths.get(path)), equalTo(expected));
    }

    private void assertDescription(final String expected)
    {
        assertThat(this.matcher.getDescription(), equalTo(expected));
    }
}
