package org.itsallcode.openfasttrace.importer.legacytag.config;

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

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.OsDetector;
import org.junit.Before;
import org.junit.Test;

public class DescribedPathMatcherTest
{
    private DescribedPathMatcher matcher;

    @Before
    public void setup()
    {
        this.matcher = null;
    }

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
        assertMatches("rel/path.txt", false);
        assertMatches("path", false);
        assertMatches("rel", false);
    }

    @Test
    public void testListBasedMatcherIsCaseInsensitiveUnderWindows()
    {
        OsDetector.assumeRunningOnWindows();
        createListMatcher("rel/path");
        assertMatches("REL/path", true);
        assertMatches("rel/PATH", true);
    }

    @Test
    public void testListBasedMatcherIsCaseSensitiveUnderUnix()
    {
        OsDetector.assumeRunningOnUnix();
        createListMatcher("rel/path");
        assertMatches("REL/path", false);
        assertMatches("rel/PATH", false);
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
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithForwardSlash()
    {
        createListMatcher("C:/abs/path");
        assertMatches("C:/abs/path", true);
        assertMatches("abs/path", false);
        assertMatches("C:/abs/path/", true);
    }

    @Test
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithForwardSlashWindows()
    {
        OsDetector.assumeRunningOnWindows();
        createListMatcher("C:/abs/path");
        assertMatches("C:\\abs\\path", true);
    }

    @Test
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithForwardSlashUnix()
    {
        OsDetector.assumeRunningOnUnix();
        createListMatcher("C:/abs/path");
        assertMatches("C:\\abs\\path", false);
    }

    @Test
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithBackslash()
    {
        createListMatcher("C:\\abs\\path");
        assertMatches("C:\\abs\\path", true);
        assertMatches("abs/path", false);
    }

    @Test
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithBackslashUnix()
    {
        OsDetector.assumeRunningOnUnix();
        createListMatcher("C:\\abs\\path");
        assertMatches("C:/abs/path", false);
        assertMatches("C:\\abs\\path\\", false);
    }

    @Test
    public void testListBasedMatcherMatchesAbsoluteWindowsStylePathWithBackslashWindows()
    {
        OsDetector.assumeRunningOnWindows();
        createListMatcher("C:\\abs\\path");
        assertMatches("C:/abs/path", true);
        assertMatches("C:\\abs\\path\\", true);
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
        assertMatches("path/FILE", true);
        assertMatches("path/dir/", true);
        assertMatches("path/dir/file", true);
    }

    private void createListMatcher(final String... paths)
    {
        final List<Path> pathList = Arrays.stream(paths) //
                .map(Paths::get) //
                .collect(toList());
        this.matcher = DescribedPathMatcher.createPathListMatcher(pathList);
    }

    private void createPatternMatcher(final String pattern)
    {
        this.matcher = DescribedPathMatcher.createPatternMatcher(pattern);
    }

    private void assertMatches(final String path, final boolean expected)
    {
        assertThat("Initialize matcher before assertions", this.matcher, notNullValue());
        final InputFile file = InputFile.forPath(Paths.get(path));
        assertThat("path " + path, this.matcher.matches(file), equalTo(expected));
    }

    private void assertDescription(final String expected)
    {
        assertThat("Initialize matcher before assertions", this.matcher, notNullValue());
        assertThat(this.matcher.getDescription(), equalTo(expected));
    }
}
