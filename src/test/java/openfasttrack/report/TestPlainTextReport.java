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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.equalTo;
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
import openfasttrack.core.Newline;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.Trace;
import openfasttrack.matcher.MultilineTextMatcher;

public class TestPlainTextReport
{
    private static final Newline NEWLINE_SEPARATOR = Newline.UNIX;
    private static final String DSN = "dsn";
    private static final String UMAN = "uman";
    private static final String UTEST = "utest";
    private static final String IMPL = "impl";

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
        new PlainTextReport(this.traceMock, NEWLINE_SEPARATOR)
                .renderToStreamWithVerbosityLevel(outputStreamMock, ReportVerbosity.SUMMARY);
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
                .collect(joining(NEWLINE_SEPARATOR.toString())) //
                + NEWLINE_SEPARATOR;
    }

    private String getReportOutput(final ReportVerbosity verbosity)
    {
        final Newline newline = NEWLINE_SEPARATOR;
        return getReportOutputWithNewline(verbosity, newline);
    }

    private String getReportOutputWithNewline(final ReportVerbosity verbosity,
            final Newline newline)
    {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Reportable report = new PlainTextReport(this.traceMock, newline);
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
    // [utest->dsn~reporting.plain-text.summary~1]
    public void testReport_LevelSummary_OK()
    {
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertReportOutput(ReportVerbosity.SUMMARY, "ok - 1 total");
    }

    @Test
    // [utest->dsn~reporting.plain-text.summary~1]
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
        when(this.traceMock.getUncoveredIds()).thenReturn(asList(idA, idB, idC, idD));
        assertReportOutput(ReportVerbosity.FAILURES, //
                "dsn~bar~1", "req~foo~1", "req~zoo~1", "req~zoo~2");
    }

    @Test
    // [utest->dsn~reporting.plain-text.specification-item-overview~1]
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
                "desc A1" + NEWLINE_SEPARATOR + "desc A2" + NEWLINE_SEPARATOR + "desc A3", //
                0, 3, 1, 0, 2);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("dsn~bar~1", //
                "desc B1", //
                0, 0, 0, 2, 4);
        final LinkedSpecificationItem itemCMock = createLinkedItemMock("req~zoo~2", //
                "desc C1" + NEWLINE_SEPARATOR + "desc C2", //
                1, 6, 0, 0, 0);
        final LinkedSpecificationItem itemDMock = createLinkedItemMock("req~zoo~1", //
                "desc D1", //
                3, 7, 1, 2, 3);

        when(itemAMock.getNeedsArtifactTypes()).thenReturn(asList(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(DSN)));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(IMPL, UTEST)));
        when(itemBMock.getUncoveredArtifactTypes()).thenReturn(asList(UMAN));
        when(itemCMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(DSN)));
        when(itemCMock.getOverCoveredArtifactTypes()).thenReturn(asList(UTEST));
        when(itemDMock.getCoveredArtifactTypes()).thenReturn(Collections.emptySet());
        when(itemDMock.getUncoveredArtifactTypes()).thenReturn(asList(IMPL, UTEST));
        when(this.traceMock.getUncoveredItems())
                .thenReturn(asList(itemAMock, itemBMock, itemCMock, itemDMock));
        when(itemAMock.getLocation()).thenReturn(Location.create("/tmp/foo.md", 1));
        when(itemBMock.getLocation()).thenReturn(Location.create("/tmp/bar.md", 2));
        when(itemCMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 13));
        when(itemDMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 17));
    }

    @Test
    public void testReport_LevelAll()
    {
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countUncovered()).thenReturn(1);
        prepareMixedItemDetails();

        assertReportOutput(ReportVerbosity.ALL, //
                "not ok - 0/0>3>1/4 - dsn~failure~0 (impl, uman, -utest)", //
                "#", //
                "# This is a failure.", //
                "#", //
                "ok - 0/0>0>0/0 - req~success~20170126 (dsn)", //
                "#", //
                "# This is a success.", //
                "#", //
                "", //
                "not ok - 2 total, 1 not covered");
    }

    private void prepareMixedItemDetails()
    {
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("req~success~20170126", //
                "This is a success." + NEWLINE_SEPARATOR, //
                0, 0, 0, 0, 0);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("dsn~failure~0", //
                "This is a failure.", //
                0, 0, 3, 1, 4);

        when(itemAMock.getNeedsArtifactTypes()).thenReturn(asList(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(DSN)));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(IMPL, UMAN)));
        when(itemBMock.getUncoveredArtifactTypes()).thenReturn(asList(UTEST));
        when(this.traceMock.getItems()).thenReturn(asList(itemAMock, itemBMock));
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

    // [itest->dsn~newline-format~1]
    @Test
    public void testReportWithDifferentLineSeparator()
    {
        final Newline separator = Newline.OLDMAC;

        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countUncovered()).thenReturn(0);
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("a~a~1", //
                "This is" + separator + "a multiline description", //
                0, 0, 0, 0, 0);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("b~b~2", //
                "Yet another" + separator + "multiline text", //
                0, 0, 0, 0, 0);
        when(itemAMock.getNeedsArtifactTypes()).thenReturn(asList(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(DSN)));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(asList(IMPL)));
        when(this.traceMock.isAllCovered()).thenReturn(true);
        when(this.traceMock.getItems()).thenReturn(asList(itemAMock, itemBMock));

        assertThat(getReportOutputWithNewline(ReportVerbosity.ALL, separator), //
                equalTo("ok - 0/0>0>0/0 - a~a~1 (dsn)" + separator//
                        + "#" + separator //
                        + "# This is" + separator //
                        + "# a multiline description" + separator //
                        + "#" + separator //
                        + "ok - 0/0>0>0/0 - b~b~2 (impl)" + separator //
                        + "#" + separator //
                        + "# Yet another" + separator //
                        + "# multiline text" + separator //
                        + "#" + separator //
                        + "" + separator //
                        + "ok - 2 total" + separator));

    }
}
