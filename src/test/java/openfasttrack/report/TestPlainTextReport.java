package openfasttrack.report;

import static java.util.stream.Collectors.joining;
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

import static org.junit.Assert.assertEquals;
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
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Mock
    private Trace traceMock;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReportLevel_Minimal_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        assertReportOutput(ReportVerbosity.MINIMAL, "ok");
    }

    private void assertReportOutput(final ReportVerbosity verbosity,
            final String... expectedReportLines)
    {
        final String expectedReportText = getExpectedReportText(expectedReportLines);
        assertEquals(expectedReportText, getReportOutput(verbosity));
    }

    private String getExpectedReportText(final String... expectedReportLines)
    {
        if (expectedReportLines.length == 0)
        {
            return "";
        }
        return Arrays.stream(expectedReportLines) //
                .collect(joining(LINE_SEPARATOR)) //
                + LINE_SEPARATOR;
    }

    private String getReportOutput(final ReportVerbosity verbosity)
    {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Reportable report = new PlainTextReport(this.traceMock);
        report.renderToStreamWithVerbosityLevel(outputStream, verbosity);
        return outputStream.toString();
    }

    @Test
    public void testReport_LevelMinimal_NotOk()
    {
        when(this.traceMock.isAllCovered()).thenReturn(false);
        assertReportOutput(ReportVerbosity.MINIMAL, "not ok");
    }

    @Test
    public void testReport_LevelSummary_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertReportOutput(ReportVerbosity.SUMMARY, "ok - 1 total");
    }

    @Test
    public void testReport_LevelSummary_NotOK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countUncovered()).thenReturn(1);
        assertReportOutput(ReportVerbosity.SUMMARY, "ok - 2 total, 1 not covered");
    }

    @Test
    public void testReport_LevelFailures_Ok()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertReportOutput(ReportVerbosity.FAILURES);
    }

    @Test
    public void testReport_LevelFailures_NotOK()
    {
        final SpecificationItemId idA = SpecificationItemId.parseId("req~foo~1");
        final SpecificationItemId idB = SpecificationItemId.parseId("dsn~bar~1");
        final SpecificationItemId idC = SpecificationItemId.parseId("req~zoo~2");
        final SpecificationItemId idD = SpecificationItemId.parseId("req~zoo~1");
        when(this.traceMock.getUncoveredIds()).thenReturn(Arrays.asList(idA, idB, idC, idD));
        assertReportOutput(ReportVerbosity.FAILURES, //
                "dsn~bar~1", "req~foo~1", "req~zoo~1", "req~zoo~2");
    }

    @Test
    public void testReport_LevelFailureDetails_NotOK()
    {
        when(this.traceMock.count()).thenReturn(6);
        when(this.traceMock.countUncovered()).thenReturn(4);
        prepareFailedItemDetails();

        assertReportOutput(ReportVerbosity.FAILURE_DETAILS, //
                "not ok - 0/0>0>2/4 - dsn~bar~1", //
                "#", //
                "# desc B1", //
                "#", //
                "not ok - 0/3>1>0/2 - req~foo~1", //
                "#", //
                "# desc A1", //
                "# desc A2", //
                "# desc A3", //
                "#", //
                "not ok - 3/7>1>2/3 - req~zoo~1", //
                "#", //
                "# desc D1", //
                "#", //
                "not ok - 1/6>0>0/0 - req~zoo~2", //
                "#", //
                "# desc C1", //
                "# desc C2", //
                "#", //
                "", //
                "not ok - 6 total, 4 not covered");
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
        when(itemAMock.getDescription())
                .thenReturn("desc A1" + LINE_SEPARATOR + "desc A2" + LINE_SEPARATOR + "desc A3");
        when(itemBMock.getDescription()).thenReturn("desc B1");
        when(itemCMock.getDescription()).thenReturn("desc C1" + LINE_SEPARATOR + "desc C2");
        when(itemDMock.getDescription()).thenReturn("desc D1");
        when(itemAMock.isDefect()).thenReturn(true);
        when(itemBMock.isDefect()).thenReturn(true);
        when(itemCMock.isDefect()).thenReturn(true);
        when(itemDMock.isDefect()).thenReturn(true);
        when(this.traceMock.getUncoveredItems())
                .thenReturn(Arrays.asList(itemAMock, itemBMock, itemCMock, itemDMock));
        when(itemAMock.countIncomingBadLinks()).thenReturn(0);
        when(itemAMock.countIncomingLinks()).thenReturn(3);
        when(itemAMock.countDuplicateLinks()).thenReturn(1);
        when(itemAMock.countOutgoingBadLinks()).thenReturn(0);
        when(itemAMock.countOutgoingLinks()).thenReturn(2);
        when(itemBMock.countIncomingBadLinks()).thenReturn(0);
        when(itemBMock.countIncomingLinks()).thenReturn(0);
        when(itemBMock.countDuplicateLinks()).thenReturn(0);
        when(itemBMock.countOutgoingBadLinks()).thenReturn(2);
        when(itemBMock.countOutgoingLinks()).thenReturn(4);
        when(itemCMock.countIncomingBadLinks()).thenReturn(1);
        when(itemCMock.countIncomingLinks()).thenReturn(6);
        when(itemCMock.countDuplicateLinks()).thenReturn(0);
        when(itemCMock.countOutgoingBadLinks()).thenReturn(0);
        when(itemCMock.countOutgoingLinks()).thenReturn(0);
        when(itemDMock.countIncomingBadLinks()).thenReturn(3);
        when(itemDMock.countIncomingLinks()).thenReturn(7);
        when(itemDMock.countDuplicateLinks()).thenReturn(1);
        when(itemDMock.countOutgoingBadLinks()).thenReturn(2);
        when(itemDMock.countOutgoingLinks()).thenReturn(3);
    }
}
