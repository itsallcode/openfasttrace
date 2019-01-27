package org.itsallcode.openfasttrace.report.html;

import static org.hamcrest.MatcherAssert.assertThat;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.report.Reportable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestHtmlReport
{
    @Mock
    private Trace traceMock;

    @BeforeEach
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRenderEmptyTrace()
    {
        final String outputAsString = renderToString();
        assertAll(() -> assertThat(outputAsString, startsWith("<!DOCTYPE html>")),
                () -> assertThat(outputAsString, not(containsString("<section"))),
                () -> assertThat(outputAsString, endsWith("</html>")));
    }

    protected String renderToString()
    {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Reportable report = new HtmlReport(this.traceMock);
        report.renderToStream(outputStream);
        final String outputAsString = outputStream.toString();
        return outputAsString;
    }

    @Test
    void testRenderSimpleTrace()
    {
        final LinkedSpecificationItem itemA = new LinkedSpecificationItem(
                SpecificationItem.builder() //
                        .id(SpecificationItemId.createId("a", "a-item", 1)) //
                        .description("Description A") //
                        .build());
        final LinkedSpecificationItem itemB = new LinkedSpecificationItem(
                SpecificationItem.builder() //
                        .id(SpecificationItemId.createId("b", "b-item", 1)) //
                        .description("Description b") //
                        .build());
        when(this.traceMock.getItems()).thenReturn(Arrays.asList(itemA, itemB));
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countDefects()).thenReturn(0);
        final String outputAsString = renderToString();
        assertAll(() -> assertThat(outputAsString, startsWith("<!DOCTYPE html>")),
                () -> assertThat(outputAsString, containsString("<section id=\"a\">")),
                () -> assertThat(outputAsString, containsString("<section id=\"b\">")),
                () -> assertThat(outputAsString, containsString("2 total")),
                () -> assertThat(outputAsString, endsWith("</html>")));
    }
}