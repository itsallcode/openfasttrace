package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.openfasttrace.testutil.matcher.MultilineTextMatcher.matchesAllLines;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.DetailsSectionDisplay;
import org.itsallcode.openfasttrace.report.html.HtmlReport;
import org.itsallcode.openfasttrace.report.html.view.ViewFactory;
import org.junit.jupiter.api.BeforeEach;

class AbstractTestHtmlRenderer
{
    protected OutputStream outputStream;
    protected ViewFactory factory;

    @BeforeEach
    void prepareEachTest()
    {
        this.outputStream = new ByteArrayOutputStream();
        this.factory = HtmlViewFactory.create(this.outputStream, HtmlReport.getCssUrl(),
                DetailsSectionDisplay.COLLAPSE);
    }

    protected void assertOutputLines(final String... lines)
    {
        final Matcher<String> matcher = matchesAllLines(lines);
        assertOutput(matcher);
    }

    protected void assertOutput(final Matcher<String> matcher)
    {
        assertThat(this.outputStream.toString(), matcher);
    }

    protected void assertOutputLinesWithoutCSS(final String... lines)
    {
        final String html = this.outputStream.toString();
        final String htmlWithoutCSS = Pattern //
                .compile("<style>.*</style>", Pattern.DOTALL) //
                .matcher(html) //
                .replaceAll("<style></style>");
        assertThat(htmlWithoutCSS, matchesAllLines(lines));
    }
}
