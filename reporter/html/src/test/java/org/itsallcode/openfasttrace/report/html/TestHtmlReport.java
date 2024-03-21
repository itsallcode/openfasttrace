package org.itsallcode.openfasttrace.report.html;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.itemWithId;

@ExtendWith(MockitoExtension.class)
class TestHtmlReport
{
    @Mock
    private Trace traceMock;

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
        final HtmlReporterFactory htmlReporterFactory = new HtmlReporterFactory();
        htmlReporterFactory.init(new ReporterContext(ReportSettings.createDefault()));
        final Reportable report = htmlReporterFactory.createImporter(traceMock);
        report.renderToStream(outputStream);
        final String outputAsString = outputStream.toString();
        return outputAsString;
    }

    @Test
    void testRenderSimpleTrace()
    {
        final LinkedSpecificationItem itemA = new LinkedSpecificationItem(
                itemWithId(SpecificationItemId.createId("a", "a-item", 1)) //
                        .description("Description A") //
                        .build());
        final LinkedSpecificationItem itemB = new LinkedSpecificationItem(
                itemWithId(SpecificationItemId.createId("b", "b-item", 1)) //
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