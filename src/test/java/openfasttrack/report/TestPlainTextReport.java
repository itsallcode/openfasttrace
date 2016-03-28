package openfasttrack.report;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.LinkedSpecificationItem;
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
        assertThat(getReportOutput(ReportVerbosity.MINIMAL), equalTo("ok\n"));
    }

    private String getReportOutput(final ReportVerbosity verbosity)
    {
        final Reportable report = new PlainTextReport(this.traceMock);
        report.renderToStreamWithVerbosityLevel(this.outputStream, verbosity);
        final String output = this.outputStream.toString();
        return output;
    }

    @Test
    public void testReport_LevelMinimal_NotOk()
    {
        when(this.traceMock.isAllCovered()).thenReturn(false);
        assertThat(getReportOutput(ReportVerbosity.MINIMAL), equalTo("not ok\n"));
    }

    @Test
    public void testReport_LevelSummary_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertThat(getReportOutput(ReportVerbosity.SUMMARY), equalTo("ok - 1 total\n"));
    }

    @Test
    public void testReport_LevelSummary_NotOK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countUncovered()).thenReturn(1);
        assertThat(getReportOutput(ReportVerbosity.SUMMARY),
                equalTo("ok - 2 total, 1 not covered\n"));
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

    @Test
    public void testReport_LevelFailureDetails_NotOK()
    {
        when(this.traceMock.count()).thenReturn(6);
        when(this.traceMock.countUncovered()).thenReturn(4);
        prepareFailedItemDetails();
        final String expected = expectFailureDetails();
        assertThat(getReportOutput(ReportVerbosity.FAILURE_DETAILS), equalTo(expected));
    }

    private String expectFailureDetails()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("not ok - dsn~bar~1\n") //
                .append("# desc B1\n") //
                .append("not ok - req~foo~1\n") //
                .append("# desc A1\n# desc A2\n# desc A3\n") //
                .append("not ok - req~zoo~1\n") //
                .append("# desc D1\n") //
                .append("not ok - req~zoo~2\n") //
                .append("# desc C1\n# desc C2\n") //
                .append("\nnot ok - 6 total, 4 not covered\n");

        return builder.toString();
    }

    private void prepareFailedItemDetails()
    {
        final LinkedSpecificationItem itemAMock = mock(LinkedSpecificationItem.class);
        final LinkedSpecificationItem itemBMock = mock(LinkedSpecificationItem.class);
        final LinkedSpecificationItem itemCMock = mock(LinkedSpecificationItem.class);
        final LinkedSpecificationItem itemDMock = mock(LinkedSpecificationItem.class);
        final SpecificationItemId idA = SpecificationItemId.parseId("req~foo~1");
        final SpecificationItemId idB = SpecificationItemId.parseId("dsn~bar~1");
        final SpecificationItemId idC = SpecificationItemId.parseId("req~zoo~2");
        final SpecificationItemId idD = SpecificationItemId.parseId("req~zoo~1");
        when(itemAMock.getId()).thenReturn(idA);
        when(itemBMock.getId()).thenReturn(idB);
        when(itemCMock.getId()).thenReturn(idC);
        when(itemDMock.getId()).thenReturn(idD);
        when(itemAMock.getDescription()).thenReturn("desc A1\ndesc A2\ndesc A3");
        when(itemBMock.getDescription()).thenReturn("desc B1");
        when(itemCMock.getDescription()).thenReturn("desc C1\ndesc C2");
        when(itemDMock.getDescription()).thenReturn("desc D1");
        when(this.traceMock.getUncoveredItems())
                .thenReturn(Arrays.asList(itemAMock, itemBMock, itemCMock, itemDMock));
    }
}
