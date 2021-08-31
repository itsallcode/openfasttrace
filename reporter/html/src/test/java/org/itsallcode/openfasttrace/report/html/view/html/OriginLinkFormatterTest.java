package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.itsallcode.openfasttrace.api.core.Location;
import org.junit.jupiter.api.Test;

class OriginLinkFormatterTest
{
    @Test
    void formatNormalPath()
    {
        assertFormatter(Location.create("path", 4), "path:4");
        assertFormatter(Location.create("path/file.ext", 4), "path/file.ext:4");
        assertFormatter(Location.create("path\\file.ext", 4), "path\\file.ext:4");
        assertFormatter(Location.create("protocol//server/path", 4), "protocol//server/path:4");
        assertFormatter(Location.create("file//server/path", 4), "file//server/path:4");
    }

    @Test
    void formatUrls()
    {
        assertLinkFormatter(Location.create("http://server:80/path", 4));
        assertLinkFormatter(Location.create("https://server:443/path", 4));
        assertLinkFormatter(Location.create("ftp://server:21/path", 4));
        assertLinkFormatter(Location.create("file://server/path", 4));
        assertLinkFormatter(Location.create("mailto:user@example.com", 4));
    }

    private void assertLinkFormatter(Location location)
    {
        assertThat(OriginLinkFormatter.formatAsBlock(location),
                equalTo("<p class=\"origin\"><a href=\"" + location.getPath() + "\">" + location.getPath() + "</a>:"
                        + location.getLine() + "</p>"));
        assertThat(OriginLinkFormatter.formatAsSpan(location),
                equalTo("<span class=\"origin\"><a href=\"" + location.getPath() + "\">" + location.getPath() + "</a>:"
                        + location.getLine() + "</span>"));
    }

    private void assertFormatter(Location location, String expected)
    {
        assertThat(OriginLinkFormatter.formatAsBlock(location),
                equalTo("<p class=\"origin\">" + expected + "</p>"));
        assertThat(OriginLinkFormatter.formatAsSpan(location),
                equalTo("<span class=\"origin\">" + expected + "</span>"));
    }
}
