package org.itsallcode.openfasttrace.report.view.html;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.itsallcode.openfasttrace.report.view.html.OriginLinkFormatter.format;
import static org.junit.Assert.assertThat;

import org.itsallcode.openfasttrace.core.Location;
import org.junit.Test;

public class TestOriginLinkFormatter
{
    @Test
    public void testFormatMinimalHttpLink()
    {
        assertThat(format(Location.create("http://example.org", 1)),
                equalTo("<a href=\"http://example.org\">http://example.org</a>:1"));
    }

    @Test
    public void testLongerHttpLink()
    {
        assertThat(format(Location.create("http://example.org/foo/bar%20baz?zoo", 1)), equalTo(
                "<a href=\"http://example.org/foo/bar%20baz?zoo\">http://example.org/foo/bar%20baz?zoo</a>:1"));
    }

    @Test
    public void testFormatRegularAbsoluteUnixPath()
    {
        assertThat(format(Location.create("/foo/bar/baz", 1111)),
                equalTo("<a href=\"file:///foo/bar/baz\">/foo/bar/baz</a>:1111"));
    }

    @Test
    public void testFormatAbsoluteUnixPathWithSpecialCharacters()
    {
        assertThat(format(Location.create("/fo o/bär/baz", 12345678)),
                equalTo("<a href=\"file:///fo%20o/b%C3%A4r/baz\">/fo o/bär/baz</a>:12345678"));
    }

    @Test
    public void testFormatRelativeUnixPath()
    {
        assertThat(format(Location.create("foo/bar/baz", 2)), equalTo("foo/bar/baz:2"));
    }

    @Test
    public void testIllegalUri()
    {
        assertThat(format(Location.create("http://example.org/a b", 5)),
                equalTo("http://example.org/a b:5"));
    }
}