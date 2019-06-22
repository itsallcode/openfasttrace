package org.itsallcode.openfasttrace.report.view.html;

import static org.hamcrest.MatcherAssert.assertThat;

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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.itsallcode.openfasttrace.report.view.html.OriginLinkFormatter.formatAsBlock;
import static org.itsallcode.openfasttrace.report.view.html.OriginLinkFormatter.formatAsSpan;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.test.OsDetector;
import org.junit.jupiter.api.Test;

class TestOriginLinkFormatter
{
    @Test
    void testNullOrigin()
    {
        assertThat(formatAsSpan(null), equalTo(""));
    }

    @Test
    void testEmptyOrigin()
    {
        assertThat(formatAsSpan(Location.create("", 1)), equalTo(""));
    }

    @Test
    void testFormatMinimalHttpLink()
    {
        assertPathAndLineRenderedToSpan("http://example.org", 1,
                "<a href=\"http://example.org\">http://example.org</a>:1");
    }

    private void assertPathAndLineRenderedToSpan(final String path, final int line,
            final String expected)
    {
        assertThat(formatAsSpan(Location.create(path, line)),
                equalTo("<span class=\"origin\">" + expected + "</span>"));
    }

    @Test
    void testFormatMinimalHttpLinkAsBlock()
    {
        assertPathAndLineRenderedToBlock("http://example.org", 1,
                "<a href=\"http://example.org\">http://example.org</a>:1");
    }

    private void assertPathAndLineRenderedToBlock(final String path, final int line,
            final String expected)
    {
        assertThat(formatAsBlock(Location.create(path, line)),
                equalTo("<p class=\"origin\">" + expected + "</p>"));
    }

    @Test
    void testLongerHttpLink()
    {
        assertPathAndLineRenderedToSpan("http://example.org/foo/bar%20baz?zoo", 1,
                "<a href=\"http://example.org/foo/bar%20baz?zoo\">http://example.org/foo/bar%20baz?zoo</a>:1");
    }

    @Test
    void testFormatRegularAbsoluteUnixPath()
    {
        OsDetector.assumeRunningOnUnix();
        assertPathAndLineRenderedToSpan("/foo/bar/baz", 1111,
                "<a href=\"file:///foo/bar/baz\">/foo/bar/baz</a>:1111");
    }

    @Test
    void testFormatRegularAbsoluteWindowsPath()
    {
        OsDetector.assumeRunningOnWindows();
        assertPathAndLineRenderedToSpan("C:\\foo\\bar\\baz", 1111,
                "<a href=\"file:///C:/foo/bar/baz\">C:\\foo\\bar\\baz</a>:1111");
    }

    @Test
    void testFormatAbsoluteUnixPathWithSpecialCharacters()
    {
        OsDetector.assumeRunningOnUnix();
        assertPathAndLineRenderedToSpan("/fo o/bär/baz", 12345678,
                "<a href=\"file:///fo%20o/b%C3%A4r/baz\">/fo o/bär/baz</a>:12345678");
    }

    @Test
    void testFormatRelativeUnixPath()
    {
        assertPathAndLineRenderedToSpan("foo/bar/baz", 2, "foo/bar/baz:2");
    }

    @Test
    void testIllegalUri()
    {
        assertPathAndLineRenderedToSpan("http://example.org/a b", 5, "http://example.org/a b:5");
    }
}