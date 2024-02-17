package org.itsallcode.openfasttrace.report.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.*;
import java.net.*;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.api.DetailsSectionDisplay;
import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;
import org.itsallcode.openfasttrace.report.html.view.html.HtmlViewFactory;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.itsallcode.openfasttrace.testutil.OsDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestHtmlReportCssInlining extends AbstractFileBasedTest
{
    private static final String CSS = "* { font-family: helvetica; }";

    // [itest->dsn~reporting.html.inline_css~1]
    @Test
    void testHtmlReportContainsInlineCSS(@TempDir final Path tempDir) throws IOException
    {
        final File cssFile = tempDir.resolve("test.css").toFile();
        writeTextFile(cssFile, CSS);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final HtmlViewFactory factory = HtmlViewFactory.create(stream, cssFile.toURI().toURL(),
                DetailsSectionDisplay.COLLAPSE);
        final ViewableContainer view = factory.createView("foo", "bar");
        view.render();
        assertThat(stream.toString(), containsString(CSS));
    }

    @Test
    void testInliningNonExistentCssThrowsException() throws URISyntaxException, MalformedURLException
    {
        final HtmlViewFactory factory = HtmlViewFactory.create(new ByteArrayOutputStream(),
                new URI("file:///this_file_does_not_exist.css").toURL(), DetailsSectionDisplay.COLLAPSE);
        final ViewableContainer view = factory.createView("foo", "bar");
        final ReportException exception = assertThrows(ReportException.class, view::render);
        assertThat(exception.getMessage(), equalTo(
                "Unable to copy CSS content \"file:/this_file_does_not_exist.css\" trying to generate HTML view."));
    }

    @Test
    void testInliningUnreadableCssThrowsException(@TempDir final Path tempDir) throws IOException
    {
        OsDetector.assumeRunningOnUnix();
        final File cssFile = tempDir.resolve("test.css").toFile();
        cssFile.setReadable(false);
        final HtmlViewFactory factory = HtmlViewFactory.create(new ByteArrayOutputStream(), cssFile.toURI().toURL(),
                DetailsSectionDisplay.COLLAPSE);
        final ViewableContainer view = factory.createView("foo", "bar");
        final ReportException exception = assertThrows(ReportException.class, view::render);
        assertThat(exception.getMessage(), equalTo(
                "Unable to copy CSS content \"file:" + cssFile + "\" trying to generate HTML view."));
    }
}
