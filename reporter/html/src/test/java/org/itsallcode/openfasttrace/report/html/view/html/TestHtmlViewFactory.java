package org.itsallcode.openfasttrace.report.html.view.html;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.report.html.HtmlReport;
import org.itsallcode.openfasttrace.report.html.view.ViewFactory;
import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestHtmlViewFactory
{
    private LinkedSpecificationItem item;
    private OutputStream outputStream;
    private ViewFactory factory;

    @BeforeEach
    void beforeEach()
    {
        this.outputStream = new ByteArrayOutputStream();
        this.factory = HtmlViewFactory.create(this.outputStream, HtmlReport.getCssUrl());
    }

    @Test
    void testCreateFactoryWithPrintStream() throws UnsupportedEncodingException
    {
        factory = HtmlViewFactory.create(new PrintStream(outputStream, true, StandardCharsets.UTF_8),
                HtmlReport.getCssUrl());
        assertThat(factory, notNullValue());
    }

    @Test
    void testCreateReportDetails()
    {
        final Viewable view = this.factory.createReportDetails();
        assertThat(view, instanceOf(HtmlReportDetails.class));
    }

    @Test
    void testCreateSection()
    {
        final ViewableContainer view = this.factory.createSection("id", "title");
        assertThat(view, instanceOf(HtmlSection.class));
    }

    @Test
    void testCreateSpecificationItem()
    {
        final Viewable view = this.factory.createSpecificationItem(this.item);
        assertThat(view, instanceOf(HtmlSpecificationItem.class));
    }

    @Test
    void testCreateTraceSummary()
    {
        final Trace trace = Trace.builder().build();
        final Viewable view = this.factory.createTraceSummary(trace);
        assertThat(view, instanceOf(HtmlTraceSummary.class));
    }

    @Test
    void testCreateReportSummary()
    {
        final Viewable view = this.factory.createReportSummary();
        assertThat(view, instanceOf(HtmlReportSummary.class));
    }

    @Test
    void testCreateTableOfContents()
    {
        final ViewableContainer parent = this.factory.createView("foo", "bar");
        final Viewable view = this.factory.createTableOfContents(parent);
        assertThat(view, instanceOf(HtmlTableOfContents.class));
    }
}