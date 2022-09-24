package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.itsallcode.openfasttrace.report.html.view.html.OriginLinkFormatter.formatAsBlock;
import static org.itsallcode.openfasttrace.report.html.view.html.OriginLinkFormatter.formatAsSpan;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.testutil.OsDetector;
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

    @Test
    void testFormatHttpLinkWithSpecialCharacters()
    {
        assertPathAndLineRenderedToBlock("http://äöü.org", 1,
                "<a href=\"http://äöü.org\">http://äöü.org</a>:1");
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
    void testFormatAbsoluteUnixPathWithSpecialCharactersLinux()
    {
        OsDetector.assumeRunningOnLinux();
        assertPathAndLineRenderedToSpan("/fo o/bär/baz", 12345678,
                "<a href=\"file:///fo%20o/b%C3%A4r/baz\">/fo o/bär/baz</a>:12345678");
    }

    @Test
    void testFormatAbsoluteUnixPathWithSpecialCharactersMacOs()
    {
        OsDetector.assumeRunningOnMacOs();
        assertPathAndLineRenderedToSpan("/fo o/bär/baz", 12345678,
                "<a href=\"file:///fo%20o/ba%CC%88r/baz\">/fo o/bär/baz</a>:12345678");
    }

    @Test
    void testFormatRelativeUnixPath()
    {
        assertPathAndLineRenderedToSpan("foo/bar/baz", 2, "foo/bar/baz:2");
    }

    @Test
    void testFormatRelativeUnixPathWithSpecialCharacters()
    {
        assertPathAndLineRenderedToSpan("foo/bär/baz", 2, "foo/bär/baz:2");
    }

    @Test
    void testIllegalUri()
    {
        assertPathAndLineRenderedToSpan("http://example.org/a b", 5, "http://example.org/a b:5");
    }
}