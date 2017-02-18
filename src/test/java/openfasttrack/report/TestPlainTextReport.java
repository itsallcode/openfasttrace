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

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Location;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.Trace;
import openfasttrack.matcher.MultilineTextMatcher;

public class TestPlainTextReport
{
    private static final String DSN = "dsn";
    private static final String UMAN = "uman";
    private static final String UTEST = "utest";
    private static final String IMPL = "impl";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Mock
    private Trace traceMock;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOutputStreamClosed() throws IOException
    {
        final OutputStream outputStreamMock = mock(OutputStream.class);
        new PlainTextReport(this.traceMock).renderToStreamWithVerbosityLevel(outputStreamMock,
                ReportVerbosity.SUMMARY);
        verify(outputStreamMock).close();
    }

    @Test
    public void testReportLevel_Quiet_Ok()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        assertReportOutput(ReportVerbosity.QUIET);
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
        assertThat(getReportOutput(verbosity),
                MultilineTextMatcher.matchesAllLines(expectedReportText));
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
                "not ok - 0/0>0>2/4 - dsn~bar~1 (impl, -uman, utest)", //
                "not ok - 0/3>1>0/2 - req~foo~1 (dsn)", //
                "not ok - 3/7>1>2/3 - req~zoo~1 (-impl, -utest)", //
                "not ok - 1/6>0>0/0 - req~zoo~2 (dsn, +utest)", //
                "", //
                "not ok - 6 total, 4 not covered");
    }

    private void prepareFailedItemDetails()
    {
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("req~foo~1", //
                "desc A1" + System.lineSeparator() + "desc A2" + System.lineSeparator() + "desc A3", //
                0, 3, 1, 0, 2);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("dsn~bar~1", //
                "desc B1", //
                0, 0, 0, 2, 4);
        final LinkedSpecificationItem itemCMock = createLinkedItemMock("req~zoo~2", //
                "desc C1" + System.lineSeparator() + "desc C2", //
                1, 6, 0, 0, 0);
        final LinkedSpecificationItem itemDMock = createLinkedItemMock("req~zoo~1", //
                "desc D1", //
                3, 7, 1, 2, 3);

        when(itemAMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(Arrays.asList(DSN)));
        when(itemBMock.getCoveredArtifactTypes())
                .thenReturn(new HashSet<>(Arrays.asList(IMPL, UTEST)));
        when(itemBMock.getUncoveredArtifactTypes()).thenReturn(Arrays.asList(UMAN));
        when(itemCMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(Arrays.asList(DSN)));
        when(itemCMock.getOverCoveredArtifactTypes()).thenReturn(Arrays.asList(UTEST));
        when(itemDMock.getCoveredArtifactTypes()).thenReturn(Collections.emptySet());
        when(itemDMock.getUncoveredArtifactTypes()).thenReturn(Arrays.asList(IMPL, UTEST));
        when(this.traceMock.getUncoveredItems())
                .thenReturn(Arrays.asList(itemAMock, itemBMock, itemCMock, itemDMock));
        when(itemAMock.getLocation()).thenReturn(Location.create("/tmp/foo.md", 1));
        when(itemBMock.getLocation()).thenReturn(Location.create("/tmp/bar.md", 2));
        when(itemCMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 13));
        when(itemDMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 17));
    }

    private LinkedSpecificationItem createLinkedItemMock(final String idAsText,
            final String description, final int incomingBadLinks, final int incomingLinks,
            final int duplicates, final int outgoingBadLinks, final int outgoingLinks)
    {
        final SpecificationItemId id = SpecificationItemId.parseId(idAsText);
        final LinkedSpecificationItem itemAMock = mock(LinkedSpecificationItem.class);
        when(itemAMock.getDescription()).thenReturn(description);
        when(itemAMock.getId()).thenReturn(id);
        when(itemAMock.isDefect()).thenReturn(incomingBadLinks + outgoingBadLinks + duplicates > 0);
        when(itemAMock.countIncomingBadLinks()).thenReturn(incomingBadLinks);
        when(itemAMock.countIncomingLinks()).thenReturn(incomingLinks);
        when(itemAMock.countDuplicateLinks()).thenReturn(duplicates);
        when(itemAMock.countOutgoingBadLinks()).thenReturn(outgoingBadLinks);
        when(itemAMock.countOutgoingLinks()).thenReturn(outgoingLinks);
        return itemAMock;
    }
}
