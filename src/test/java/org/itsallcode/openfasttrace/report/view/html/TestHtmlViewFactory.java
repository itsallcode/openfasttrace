package org.itsallcode.openfasttrace.report.view.html;

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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.html.HtmlReport;
import org.itsallcode.openfasttrace.report.view.ViewFactory;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.itsallcode.openfasttrace.report.view.ViewableContainer;
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