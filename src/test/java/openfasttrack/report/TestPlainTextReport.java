package openfasttrack.report;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.Trace;

public class TestPlainTextReport
{
    @Mock
    private Trace traceMock;

    private OutputStream outputStream;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
        this.outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void testReportLevel_Minimal_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        assertThat(getReportOutput(ReportVerbosity.MINIMAL), equalTo("OK\n"));
    }

    private String getReportOutput(final ReportVerbosity verbosity)
    {
        final PlainTextReport report = new PlainTextReport(this.traceMock);
        report.renderToStreamWithVerbosityLevel(this.outputStream, verbosity);
        final String output = this.outputStream.toString();
        return output;
    }

    @Test
    public void testReport_LevelMinimal_NotOk()
    {
        when(this.traceMock.isAllCovered()).thenReturn(false);
        assertThat(getReportOutput(ReportVerbosity.MINIMAL), equalTo("Not OK\n"));
    }

    @Test
    public void testReport_LevelSummary_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertThat(getReportOutput(ReportVerbosity.SUMMARY), equalTo("OK - 1 total\n"));
    }

    @Test
    public void testReport_LevelSummary_NotOK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countUncovered()).thenReturn(1);
        assertThat(getReportOutput(ReportVerbosity.SUMMARY),
                equalTo("OK - 2 total, 1 not covered\n"));
    }

    @Test
    public void testReport_LevelFailures_Ok()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertThat(getReportOutput(ReportVerbosity.FAILURES), equalTo(""));
    }

    @Test
    public void testReport_LevelFailures_NotOK()
    {
        final SpecificationItemId idA = SpecificationItemId.parseId("req~foo~1");
        final SpecificationItemId idB = SpecificationItemId.parseId("dsn~bar~1");
        final SpecificationItemId idC = SpecificationItemId.parseId("req~zoo~2");
        final SpecificationItemId idD = SpecificationItemId.parseId("req~zoo~1");
        when(this.traceMock.getUncoveredIds()).thenReturn(Arrays.asList(idA, idB, idC, idD));
        assertThat(getReportOutput(ReportVerbosity.FAILURES),
                equalTo("dsn~bar~1\nreq~foo~1\nreq~zoo~1\nreq~zoo~2\n"));
    }
}
