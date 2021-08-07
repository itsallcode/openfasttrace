package org.itsallcode.openfasttrace.report.html.view.html;

/*-
 * #%L
 * OpenFastTrace HTML Reporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.openfasttrace.testutil.matcher.MultilineTextMatcher.matchesAllLines;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

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
        this.factory = HtmlViewFactory.create(this.outputStream, HtmlReport.getCssUrl());
    }

    protected void assertOutputLines(final String... lines)
    {
        assertThat(this.outputStream.toString(), matchesAllLines(lines));
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
