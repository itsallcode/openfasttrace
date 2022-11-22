package org.itsallcode.openfasttrace.report.plaintext;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.openfasttrace.testutil.core.SampleArtifactTypes.*;
import static org.itsallcode.openfasttrace.testutil.matcher.MultilineTextMatcher.matchesAllLines;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.report.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestPlainTextReport
{
    private static final Newline NEWLINE_SEPARATOR = Newline.UNIX;

    @Mock
    private Trace traceMock;

    @Test
    void testOutputStreamClosed() throws IOException
    {
        final OutputStream outputStreamMock = mock(OutputStream.class);
        final ReportSettings settings = ReportSettings.builder().verbosity(ReportVerbosity.SUMMARY)
                .build();
        new PlainTextReport(this.traceMock, settings).renderToStream(outputStreamMock);
        verify(outputStreamMock).close();
    }

    @Test
    void testReportLevel_Quiet_Ok()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        assertReportOutput(ReportVerbosity.QUIET);
    }

    @Test
    void testReportLevel_Minimal_OK()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        assertReportOutput(ReportVerbosity.MINIMAL, "ok");
    }

    private void assertReportOutput(final ReportVerbosity verbosity,
            final String... expectedReportLines)
    {
        assertReportOutput(verbosity, false, expectedReportLines);
    }

    private void assertReportOutput(final ReportVerbosity verbosity, final boolean showOrigin,
            final String... expectedReportLines)
    {
        final String expectedReportText = getExpectedReportText(expectedReportLines);
        assertThat(getReportOutput(verbosity, showOrigin), matchesAllLines(expectedReportText));
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

    private String getReportOutput(final ReportVerbosity verbosity, final boolean showOrigin)
    {
        return getReportOutputWithNewline(verbosity, NEWLINE_SEPARATOR, showOrigin);
    }

    private String getReportOutputWithNewline(final ReportVerbosity verbosity,
            final Newline newline, final boolean showOrigin)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ReportSettings settings = ReportSettings.builder().verbosity(verbosity)
                .newline(newline).showOrigin(showOrigin).build();
        final PlaintextReporterFactory factory = createFactory(settings);
        final Reportable report = factory.createImporter(this.traceMock);
        report.renderToStream(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private PlaintextReporterFactory createFactory(final ReportSettings settings)
    {
        final PlaintextReporterFactory factory = new PlaintextReporterFactory();
        factory.init(new ReporterContext(settings));
        return factory;
    }

    @Test
    void testReport_LevelMinimal_NotOk()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(false);
        assertReportOutput(ReportVerbosity.MINIMAL, "not ok");
    }

    @Test
    // [utest->dsn~reporting.plain-text.summary~2]
    void testReport_LevelSummary_OK()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertReportOutput(ReportVerbosity.SUMMARY, "ok - 1 total");
    }

    @Test
    // [utest->dsn~reporting.plain-text.summary~2]
    void testReport_LevelSummary_NotOK()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countDefects()).thenReturn(1);
        assertReportOutput(ReportVerbosity.SUMMARY, "ok - 2 total, 1 defect");
    }

    @Test
    void testReport_LevelFailures_Ok()
    {
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.count()).thenReturn(1);
        assertReportOutput(ReportVerbosity.FAILURES);
    }

    @Test
    void testReport_LevelFailures_NotOK()
    {
        final SpecificationItemId idA = SpecificationItemId.parseId("req~foo~1");
        final SpecificationItemId idB = SpecificationItemId.parseId("dsn~bar~1");
        final SpecificationItemId idC = SpecificationItemId.parseId("req~zoo~2");
        final SpecificationItemId idD = SpecificationItemId.parseId("req~zoo~1");
        when(this.traceMock.getDefectIds()).thenReturn(asList(idA, idB, idC, idD));
        assertReportOutput(ReportVerbosity.FAILURES, //
                "dsn~bar~1", "req~foo~1", "req~zoo~1", "req~zoo~2");
    }

    @Test
    // [utest->dsn~reporting.plain-text.specification-item-overview~2]
    void testReport_LevelFailureSummaries_NotOK()
    {
        when(this.traceMock.count()).thenReturn(6);
        when(this.traceMock.countDefects()).thenReturn(4);
        prepareFailedItemDetails();

        assertReportOutput(ReportVerbosity.FAILURE_SUMMARIES, //
                "not ok [ in:  0 /  0   | out:  2 /  4 ✘ ] dsn~bar~1 [proposed] (impl, -uman, utest)", //
                "not ok [ in:  3 /  3 ✔ | out:  2 /  2 ✔ ] req~foo~1 (dsn) [has 1 duplicate]", //
                "not ok [ in:  4 /  7 ✘ | out:  1 /  3 ✘ ] req~zoo~1 [rejected] (-impl, -utest) [has 1 duplicate]", //
                "not ok [ in:  5 /  6 ✘ | out:  0 /  0   ] req~zoo~2 [draft] (dsn, +utest)", //
                "", //
                "not ok - 6 total, 4 defect");
    }

    private void prepareFailedItemDetails()
    {
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("req~foo~1",
                ItemStatus.APPROVED,
                "desc A1" + NEWLINE_SEPARATOR + "desc A2" + NEWLINE_SEPARATOR + "desc A3", 0, 3, 1,
                0, 2);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("dsn~bar~1",
                ItemStatus.PROPOSED, "desc B1", 0, 0, 0, 2, 4);
        final LinkedSpecificationItem itemCMock = createLinkedItemMock("req~zoo~2",
                ItemStatus.DRAFT, "desc C1" + NEWLINE_SEPARATOR + "desc C2", 1, 6, 0, 0, 0);
        final LinkedSpecificationItem itemDMock = createLinkedItemMock("req~zoo~1",
                ItemStatus.REJECTED, "desc D1", 3, 7, 1, 2, 3);

        when(itemAMock.getNeedsArtifactTypes()).thenReturn(List.of(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(DSN)));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(IMPL, UTEST)));
        when(itemBMock.getUncoveredArtifactTypes()).thenReturn(List.of(UMAN));
        when(itemCMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(DSN)));
        when(itemCMock.getOverCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(UTEST)));
        when(itemDMock.getCoveredArtifactTypes()).thenReturn(Collections.emptySet());
        when(itemDMock.getUncoveredArtifactTypes()).thenReturn(List.of(IMPL, UTEST));
        when(this.traceMock.getDefectItems())
                .thenReturn(List.of(itemAMock, itemBMock, itemCMock, itemDMock));
        when(itemAMock.getLocation()).thenReturn(Location.create("/tmp/foo.md", 1));
        when(itemBMock.getLocation()).thenReturn(Location.create("/tmp/bar.md", 2));
        when(itemCMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 13));
        when(itemDMock.getLocation()).thenReturn(Location.create("/tmp/zoo.xml", 17));
    }

    // [utest->dsn~reporting.plain-text.link-details~1]
    @Test
    void testReport_LevelFailureDetails()
    {
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countDefects()).thenReturn(1);
        prepareMixedItemDetails();

        assertReportOutput(ReportVerbosity.FAILURE_DETAILS, //
                "not ok [ in:  1 /  1 ✔ | out:  2 /  4 ✘ ] dsn~failure~0 (impl, uman, -utest) [has 3 duplicates]", //
                "", //
                "  This is a failure.", //
                "", //
                "  [covered shallow  ] ← imp~failure~0", //
                "  [covers           ] → req~bar~1", //
                "  [unwanted         ] → req~baz~1", //
                "  [covers           ] → req~foo~1", //
                "  [outdated         ] → req~zoo~1", //
                "  [orphaned         ] → req~zoo~2", //
                "", //
                "", //
                "not ok - 2 total, 1 defect");
    }

    // [utest->dsn~reporting.plain-text.link-details~1]
    @Test
    void testReport_LevelAll()
    {
        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countDefects()).thenReturn(1);
        prepareMixedItemDetails();

        assertReportOutput(ReportVerbosity.ALL, //
                "not ok [ in:  1 /  1 ✔ | out:  2 /  4 ✘ ] dsn~failure~0 (impl, uman, -utest) [has 3 duplicates]", //
                "", //
                "  This is a failure.", //
                "", //
                "  [covered shallow  ] ← imp~failure~0", //
                "  [covers           ] → req~bar~1", //
                "  [unwanted         ] → req~baz~1", //
                "  [covers           ] → req~foo~1", //
                "  [outdated         ] → req~zoo~1", //
                "  [orphaned         ] → req~zoo~2", //
                "", //
                "ok [ in:  0 /  0   | out:  0 /  0   ] req~success~20170126 (dsn)", //
                "", //
                "  This is a success.", //
                "", //
                "  #: tag, another tag", //
                "", //
                "", //
                "not ok - 2 total, 1 defect");
    }

    private void prepareMixedItemDetails()
    {
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("req~success~20170126", //
                "This is a success." + NEWLINE_SEPARATOR, //
                0, 0, 0, 0, 0);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("dsn~failure~0", //
                "This is a failure.", //
                0, 1, 3, 2, 4);

        when(itemAMock.getNeedsArtifactTypes()).thenReturn(List.of(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(DSN)));
        when(itemAMock.getTags()).thenReturn(List.of("tag", "another tag"));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(IMPL, UMAN)));
        when(itemBMock.getUncoveredArtifactTypes()).thenReturn(List.of(UTEST));
        prepareLinks(itemBMock);
        when(this.traceMock.getItems()).thenReturn(List.of(itemAMock, itemBMock));
        when(this.traceMock.getDefectItems()).thenReturn(List.of(itemBMock));
    }

    private void prepareLinks(final LinkedSpecificationItem itemMock)
    {
        final LinkedSpecificationItem otherA = createOtherItemMock("imp~failure~0");
        final LinkedSpecificationItem otherB = createOtherItemMock("req~bar~1");
        final LinkedSpecificationItem otherC = createOtherItemMock("req~baz~1");
        final LinkedSpecificationItem otherD = createOtherItemMock("req~foo~1");
        final LinkedSpecificationItem otherE = createOtherItemMock("req~zoo~1");
        final LinkedSpecificationItem otherF = createOtherItemMock("req~zoo~2");
        final List<TracedLink> links = new ArrayList<>();
        links.add(new TracedLink(otherA, LinkStatus.COVERED_SHALLOW));
        links.add(new TracedLink(otherB, LinkStatus.COVERS));
        links.add(new TracedLink(otherD, LinkStatus.COVERS));
        links.add(new TracedLink(otherC, LinkStatus.UNWANTED));
        links.add(new TracedLink(otherE, LinkStatus.OUTDATED));
        links.add(new TracedLink(otherF, LinkStatus.ORPHANED));
        when(itemMock.getTracedLinks()).thenReturn(links);
        when(itemMock.hasLinks()).thenReturn(true);
    }

    private LinkedSpecificationItem createOtherItemMock(final String idAsText)
    {
        final LinkedSpecificationItem otherA = mock(LinkedSpecificationItem.class);
        when(otherA.getId()).thenReturn(SpecificationItemId.parseId(idAsText));
        return otherA;
    }

    private LinkedSpecificationItem createLinkedItemMock(final String idAsText,
            final ItemStatus status, final String description, final int incomingBadLinks,
            final int incomingLinks, final int duplicates, final int outgoingBadLinks,
            final int outgoingLinks)
    {
        final SpecificationItemId id = SpecificationItemId.parseId(idAsText);
        final LinkedSpecificationItem linkedItemMock = mock(LinkedSpecificationItem.class);
        when(linkedItemMock.getDescription()).thenReturn(description);
        when(linkedItemMock.getId()).thenReturn(id);
        when(linkedItemMock.getStatus()).thenReturn(status);
        when(linkedItemMock.isDefect())
                .thenReturn(incomingBadLinks + outgoingBadLinks + duplicates > 0);
        when(linkedItemMock.countIncomingBadLinks()).thenReturn(incomingBadLinks);
        when(linkedItemMock.countIncomingLinks()).thenReturn(incomingLinks);
        when(linkedItemMock.countDuplicateLinks()).thenReturn(duplicates);
        when(linkedItemMock.countOutgoingBadLinks()).thenReturn(outgoingBadLinks);
        when(linkedItemMock.countOutgoingLinks()).thenReturn(outgoingLinks);
        return linkedItemMock;
    }

    private LinkedSpecificationItem createLinkedItemMock(final String idAsText,
            final String description, final int incomingBadLinks, final int incomingLinks,
            final int duplicates, final int outgoingBadLinks, final int outgoingLinks)
    {
        return createLinkedItemMock(idAsText, ItemStatus.APPROVED, description, incomingBadLinks,
                incomingLinks, duplicates, outgoingBadLinks, outgoingLinks);
    }

    // [itest->dsn~newline-format~1]
    @Test
    void testReportWithDifferentLineSeparator()
    {
        final Newline separator = Newline.OLDMAC;

        when(this.traceMock.count()).thenReturn(2);
        when(this.traceMock.countDefects()).thenReturn(0);
        final LinkedSpecificationItem itemAMock = createLinkedItemMock("a~a~1", //
                "This is" + separator + "a multiline description", //
                0, 0, 0, 0, 0);
        final LinkedSpecificationItem itemBMock = createLinkedItemMock("b~b~2", //
                "Yet another" + separator + "multiline text", //
                0, 0, 0, 0, 0);
        when(itemAMock.getNeedsArtifactTypes()).thenReturn(List.of(DSN));
        when(itemAMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(DSN)));
        when(itemBMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(IMPL)));
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.getItems()).thenReturn(List.of(itemAMock, itemBMock));

        assertThat(getReportOutputWithNewline(ReportVerbosity.ALL, separator, false), //
                matchesAllLines("ok [ in:  0 /  0   | out:  0 /  0   ] a~a~1 (dsn)" + separator//
                        + "" + separator //
                        + "  This is" + separator //
                        + "  a multiline description" + separator //
                        + separator //
                        + "ok [ in:  0 /  0   | out:  0 /  0   ] b~b~2 (impl)" + separator //
                        + "" + separator //
                        + "  Yet another" + separator //
                        + "  multiline text" + separator //
                        + separator //
                        + separator //
                        + "ok - 2 total" + separator));

    }

    // [utest->dsn~reporting.plain-text.specification-item-origin~1]
    // [utest->dsn~reporting.plain-text.linked-specification-item-origin~1]
    @Test
    void testReportWithOriginDisplayEnabled()
    {
        final LinkedSpecificationItem itemMock = createLinkedItemMock("req~item.with-source~77",
                "Description", 0, 1, 0, 0, 0);
        when(itemMock.getNeedsArtifactTypes()).thenReturn(List.of(DSN));
        when(itemMock.getCoveredArtifactTypes()).thenReturn(new HashSet<>(List.of(DSN)));
        final LinkedSpecificationItem other = createOtherItemMock("dsn~the-other~1");
        when(other.getLocation()).thenReturn(Location.create("baz/zoo", 10));
        final List<TracedLink> links = new ArrayList<>();
        links.add(new TracedLink(other, LinkStatus.COVERED_SHALLOW));
        when(itemMock.getTracedLinks()).thenReturn(links);
        when(itemMock.hasLinks()).thenReturn(true);
        when(itemMock.getLocation()).thenReturn(Location.create("/foo/bar", 42));
        when(this.traceMock.count()).thenReturn(1);
        when(this.traceMock.countDefects()).thenReturn(0);
        when(this.traceMock.hasNoDefects()).thenReturn(true);
        when(this.traceMock.getItems()).thenReturn(List.of(itemMock));
        assertReportOutputWithOrigin(ReportVerbosity.ALL, //
                "ok [ in:  1 /  1 ✔ | out:  0 /  0   ] req~item.with-source~77 (dsn)", //
                "", //
                "  Description", //
                "", //
                "  (/foo/bar:42)", //
                "", //
                "  [covered shallow  ] ← dsn~the-other~1", //
                "         (baz/zoo:10)", //
                "", //
                "", //
                "ok - 1 total");
    }

    private void assertReportOutputWithOrigin(final ReportVerbosity verbosity,
            final String... expectedReportLines)
    {
        assertReportOutput(verbosity, true, expectedReportLines);
    }
}
