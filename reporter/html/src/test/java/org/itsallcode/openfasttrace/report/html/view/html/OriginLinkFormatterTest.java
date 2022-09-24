package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.itsallcode.openfasttrace.api.core.Location;
import org.junit.jupiter.api.Test;

class OriginLinkFormatterTest
{
    @Test
    void formatNormalPath()
    {
        assertAll(

                () -> assertFormatter(Location.create("path", 4), "path:4"),
                () -> assertFormatter(Location.create("path/file.ext", 4), "path/file.ext:4"),
                () -> assertFormatter(Location.create("path\\file.ext", 4), "path\\file.ext:4"),
                () -> assertFormatter(Location.create("protocol//server/path", 4), "protocol//server/path:4"),
                () -> assertFormatter(Location.create("file//server/path", 4), "file//server/path:4"));
    }

    private void assertFormatter(Location location, String expected)
    {
        assertAll(
                () -> assertThat(OriginLinkFormatter.formatAsBlock(location),
                        equalTo("<p class=\"origin\">" + expected + "</p>")),
                () -> assertThat(OriginLinkFormatter.formatAsSpan(location),
                        equalTo("<span class=\"origin\">" + expected + "</span>")));
    }

    @Test
    void formatUrlsAsBlock()
    {
        assertAll(
                () -> assertLinkFormatterAsBlock(Location.create("http://server:80/path", 4),
                        "<span class=\"origin\"><a href=\"http://server:80/path\">http://server:80/path</a>:4</span>"),
                () -> assertLinkFormatterAsBlock(Location.create("https://server:443/path", 4),
                        "<span class=\"origin\"><a href=\"https://server:443/path\">https://server:443/path</a>:4</span>"),
                () -> assertLinkFormatterAsBlock(Location.create("ftp://server:21/path", 4),
                        "<span class=\"origin\"><a href=\"ftp://server:21/path\">ftp://server:21/path</a>:4</span>"),
                () -> assertLinkFormatterAsBlock(Location.create("file://server/path", 4),
                        "<span class=\"origin\"><a href=\"file://server/path\">file://server/path</a>:4</span>"),
                () -> assertLinkFormatterAsBlock(Location.create("mailto:user@example.com", 4),
                        "<span class=\"origin\"><a href=\"mailto:user@example.com\">mailto:user@example.com</a>:4</span>"));
    }

    private void assertLinkFormatterAsBlock(Location location, String expected)
    {
        assertThat(OriginLinkFormatter.formatAsSpan(location), equalTo(expected));
    }

    @Test
    void formatUrlsAsSpan()
    {
        assertAll(
                () -> assertLinkFormatterAsSpan(Location.create("http://server:80/path", 4),
                        "<p class=\"origin\"><a href=\"http://server:80/path\">http://server:80/path</a>:4</p>"),
                () -> assertLinkFormatterAsSpan(Location.create("https://server:443/path", 4),
                        "<p class=\"origin\"><a href=\"https://server:443/path\">https://server:443/path</a>:4</p>"),
                () -> assertLinkFormatterAsSpan(Location.create("ftp://server:21/path", 4),
                        "<p class=\"origin\"><a href=\"ftp://server:21/path\">ftp://server:21/path</a>:4</p>"),
                () -> assertLinkFormatterAsSpan(Location.create("file://server/path", 4),
                        "<p class=\"origin\"><a href=\"file://server/path\">file://server/path</a>:4</p>"),
                () -> assertLinkFormatterAsSpan(Location.create("mailto:user@example.com", 4),
                        "<p class=\"origin\"><a href=\"mailto:user@example.com\">mailto:user@example.com</a>:4</p>"));
    }

    private void assertLinkFormatterAsSpan(Location location, String expected)
    {
        assertThat(OriginLinkFormatter.formatAsBlock(location), equalTo(expected));
    }

}
