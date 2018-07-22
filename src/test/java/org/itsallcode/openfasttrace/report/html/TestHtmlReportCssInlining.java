package org.itsallcode.openfasttrace.report.html;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.itsallcode.openfasttrace.report.view.ViewableContainer;
import org.itsallcode.openfasttrace.report.view.html.HtmlViewFactory;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TemporaryFolder;

public class TestHtmlReportCssInlining extends AbstractFileBasedTest
{
    private static final String CSS = "* { font-family: helvetica; }";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    // [itest->dsn~reporting.html.inline_css~1]
    @Test
    public void testHtmlReportContainsInlineCSS() throws IOException
    {
        final File cssFile = this.tempFolder.newFile("test.css");
        writeTextFile(cssFile, CSS);
        final HtmlViewFactory factory = HtmlViewFactory.create(System.out, cssFile.toURI().toURL());
        final ViewableContainer view = factory.createView("foo", "bar");
        view.render();
        assertThat(this.systemOutRule.getLog(), containsString(CSS));
    }

    @Test(expected = org.itsallcode.openfasttrace.report.ReportException.class)
    public void testInliningNonExistentCssThrowsException() throws MalformedURLException
    {
        final HtmlViewFactory factory = HtmlViewFactory.create(System.out,
                new URL("file:///this_file_does_not_exist.css"));
        final ViewableContainer view = factory.createView("foo", "bar");
        view.render();
    }

    @Test(expected = org.itsallcode.openfasttrace.report.ReportException.class)
    public void testInliningUnreadableCssThrowsException() throws IOException
    {
        final File cssFile = this.tempFolder.newFile("test.css");
        cssFile.setReadable(false);
        final HtmlViewFactory factory = HtmlViewFactory.create(System.out, cssFile.toURI().toURL());
        final ViewableContainer view = factory.createView("foo", "bar");
        view.render();
    }
}