package org.itsallcode.openfasttrace.report.aspec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.itsallcode.openfasttrace.testutil.core.SampleArtifactTypes.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.testutil.matcher.StringRegexpMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/*-
 * #%L
 * OpenFastTrace augmented specobject Reporter
 * %%
 * Copyright (C) 2016 - 2021 itsallcode.org
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
@ExtendWith(MockitoExtension.class)
class TestASpecReport
{

    private List<LinkedSpecificationItem> items;

    private ReporterContext reportContext;

    @BeforeEach
    void prepareEachTest()
    {
        final ReportSettings reportSettings = ReportSettings.builder()
                .newline(Newline.UNIX)
                .build();
        reportContext = new ReporterContext(reportSettings);
        items = new ArrayList<>();
    }

    /**
     * Tests an empty APSpecReport.
     */
    @Test
    void testEmptyReport()
    {
        final String reportString = renderToString();
        assertAll(() -> assertThat(reportString, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")),
                () -> assertThat(reportString, containsString("<specdocument>")),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements for a single requirement.
     */
    @Test
    void testReportWithItem()
    {
        createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-requirement", 1))
                .description("Sample arch requirement")
                .addNeedsArtifactType(ARCH)
                .addDependOnId(SpecificationItemId.createId("fea", "fea-feature", 1)));

        final String reportString = renderToString();
        assertAll(() -> assertThat(reportString, containsRegexp(item(
                new Field(Field.Type.ID, "arch-requirement"),
                new Field(Field.Type.VERSION, 1),
                new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                new Field(Field.Type.DESCRIPTION, "Sample arch requirement"),
                new Field(Field.Type.NEEDS_COVERAGE, ARCH),
                new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                new Field(Field.Type.DEPENDENCIES, "fea-feature", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements arch->dsn->impl,utest where all
     * requirements are correct.
     */
    @Test
    void testReportFullTransientCoverageWithTwoLayersOfItems()
    {
        final LinkedSpecificationItem archItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-covered", 1))
                .description("Valid Arch Requirement.")
                .addNeedsArtifactType(DSN));
        final LinkedSpecificationItem dsnItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("dsn", "dsn-covered", 1))
                .description("This design is successfully covered.")
                .addNeedsArtifactType(UTEST)
                .addNeedsArtifactType(IMPL));
        archItem.addLinkToItemWithStatus(dsnItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem utestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("utest", "utest-valid", 1))
                .description("A valid utest requirement covering all dsn requirements."));
        dsnItem.addLinkToItemWithStatus(utestItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem implItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("impl", "impl-accepted", 1))
                .description("This impl is valid."));
        dsnItem.addLinkToItemWithStatus(implItem, LinkStatus.COVERED_SHALLOW);

        final String reportString = renderToString();
        assertAll(
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "arch-covered"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "Valid Arch Requirement."),
                        new Field(Field.Type.NEEDS_COVERAGE, DSN),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERED_TYPES, DSN),
                        new Field(Field.Type.COVERED_ITEMS, "dsn-covered", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "dsn-covered"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "This design is successfully covered."),
                        new Field(Field.Type.NEEDS_COVERAGE, UTEST, IMPL),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERED_TYPES, UTEST, IMPL),
                        new Field(Field.Type.COVERED_ITEMS, "utest-valid", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERED_ITEMS, "impl-accepted", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERING_ITEMS, "arch-covered", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "utest-valid"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "A valid utest requirement covering all dsn requirements."),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-covered", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "impl-accepted"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "This impl is valid."),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-covered", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements arch->dsn->impl,utest where impl is not
     * approved.
     */
    @Test
    void testReportUncoveredTransientCoverageWithTwoLayersNonApprovedOfItems()
    {
        final LinkedSpecificationItem archItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-impl-only-proposed", 1))
                .description("Valid Arch Requirement.")
                .addNeedsArtifactType(DSN));
        final LinkedSpecificationItem dsnItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("dsn", "dsn-uncovered-impl-only-proposed", 1))
                .description("This design is partly covered. The impl is only status proposed.")
                .addNeedsArtifactType(UTEST)
                .addNeedsArtifactType(IMPL));
        archItem.addLinkToItemWithStatus(dsnItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem utestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("utest", "utest-valid", 1))
                .description("A valid utest requirement covering all dsn requirements."));
        dsnItem.addLinkToItemWithStatus(utestItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem implItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("impl", "impl-wrong-status", 1))
                .description("This impl has a non approved status.")
                .status(ItemStatus.PROPOSED));
        dsnItem.addLinkToItemWithStatus(implItem, LinkStatus.COVERED_SHALLOW);

        final String reportString = renderToString();
        assertAll(
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "arch-impl-only-proposed"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "Valid Arch Requirement."),
                        new Field(Field.Type.NEEDS_COVERAGE, DSN),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, DSN),
                        new Field(Field.Type.COVERED_ITEMS, "dsn-uncovered-impl-only-proposed", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.UNCOVERED, DeepCoverageStatus.UNCOVERED,
                                ASpecReport.CoveringStatus.UNCOVERED)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "dsn-uncovered-impl-only-proposed"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION,
                                "This design is partly covered. The impl is only status proposed."),
                        new Field(Field.Type.NEEDS_COVERAGE, UTEST, IMPL),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, UTEST),
                        new Field(Field.Type.UNCOVERED_TYPES, IMPL),
                        new Field(Field.Type.COVERED_ITEMS, "utest-valid", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERED_ITEMS, "impl-wrong-status", 1, ItemStatus.PROPOSED,
                                DeepCoverageStatus.UNCOVERED, DeepCoverageStatus.UNCOVERED,
                                ASpecReport.CoveringStatus.UNCOVERED),
                        new Field(Field.Type.COVERING_ITEMS, "arch-impl-only-proposed", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "utest-valid"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.DESCRIPTION, "A valid utest requirement covering all dsn requirements."),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-only-proposed", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "impl-wrong-status"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.PROPOSED),
                        new Field(Field.Type.DESCRIPTION, "This impl has a non approved status."),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-only-proposed", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements arch->dsn->impl,utest where impl refers
     * wrong dsn version.
     */
    @Test
    void testReportUncoveredTransientCoverageWithTwoLayersWrongVersionInDependencyOfItems()
    {
        final LinkedSpecificationItem archItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-impl-wrong-version", 1))
                .description("Valid Arch Requirement.")
                .addNeedsArtifactType(DSN));
        final LinkedSpecificationItem dsnItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("dsn", "dsn-uncovered-impl-wrong-version", 1))
                .description("This design is uncovered as impl uses a wrong dsn version.")
                .addNeedsArtifactType(UTEST)
                .addNeedsArtifactType(IMPL));
        archItem.addLinkToItemWithStatus(dsnItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem utestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("utest", "utest-valid", 1))
                .description("A valid utest requirement covering all dsn requirements."));
        dsnItem.addLinkToItemWithStatus(utestItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem implItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("impl", "impl-wrong-dsn-version", 1))
                .description("This impl refers to a wrong dsn version."));
        dsnItem.addLinkToItemWithStatus(implItem, LinkStatus.COVERED_PREDATED);

        final String reportString = renderToString();
        assertAll(
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "arch-impl-wrong-version"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, DSN),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, DSN),
                        new Field(Field.Type.COVERED_ITEMS, "dsn-uncovered-impl-wrong-version", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.UNCOVERED, DeepCoverageStatus.UNCOVERED,
                                ASpecReport.CoveringStatus.UNCOVERED)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "dsn-uncovered-impl-wrong-version"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, UTEST, IMPL),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, UTEST),
                        new Field(Field.Type.UNCOVERED_TYPES, IMPL),
                        new Field(Field.Type.COVERED_ITEMS, "utest-valid", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERED_ITEMS, "impl-wrong-dsn-version", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.OUTDATED),
                        new Field(Field.Type.COVERING_ITEMS, "arch-impl-wrong-version", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "utest-valid"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-wrong-version", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "impl-wrong-dsn-version"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-wrong-version", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements arch->dsn->impl,utest where impl is
     * missing.
     */
    @Test
    void testReportUncoveredTransientCoverageWithTwoLayersMissingImplOfItems()
    {
        final LinkedSpecificationItem archItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-impl-missing-impl", 1))
                .description("Valid Arch Requirement.")
                .addNeedsArtifactType(DSN));
        final LinkedSpecificationItem dsnItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("dsn", "dsn-uncovered-missing-impl", 1))
                .description("This design is uncovered as impl is missing.")
                .addNeedsArtifactType(UTEST)
                .addNeedsArtifactType(IMPL));
        archItem.addLinkToItemWithStatus(dsnItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem utestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("utest", "utest-valid", 1))
                .description("A valid utest requirement covering all dsn requirements."));
        dsnItem.addLinkToItemWithStatus(utestItem, LinkStatus.COVERED_SHALLOW);

        final String reportString = renderToString();
        assertAll(
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "arch-impl-missing-impl"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, DSN),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, DSN),
                        new Field(Field.Type.COVERED_ITEMS, "dsn-uncovered-missing-impl", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.UNCOVERED, DeepCoverageStatus.UNCOVERED,
                                ASpecReport.CoveringStatus.UNCOVERED)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "dsn-uncovered-missing-impl"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, UTEST, IMPL),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, UTEST),
                        new Field(Field.Type.UNCOVERED_TYPES, IMPL),
                        new Field(Field.Type.COVERED_ITEMS, "utest-valid", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERING_ITEMS, "arch-impl-missing-impl", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "utest-valid"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-missing-impl", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    /**
     * Writes report for requirements arch->dsn->impl,utest where impl has wrong
     * type.
     */
    @Test
    void testReportUncoveredTransientCoverageWithTwoLayersWrongTypeOfItems()
    {
        final LinkedSpecificationItem archItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("arch", "arch-impl-wrong-type", 1))
                .description("Valid Arch Requirement.")
                .addNeedsArtifactType(DSN));
        final LinkedSpecificationItem dsnItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("dsn", "dsn-uncovered-impl-wrong-type", 1))
                .description("This design is covered with unwanted type item.")
                .addNeedsArtifactType(UTEST)
                .addNeedsArtifactType(IMPL));
        archItem.addLinkToItemWithStatus(dsnItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem utestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("utest", "utest-valid", 1))
                .description("A valid utest requirement covering all dsn requirements."));
        dsnItem.addLinkToItemWithStatus(utestItem, LinkStatus.COVERED_SHALLOW);
        final LinkedSpecificationItem itestItem = createItem(createItemBuilder()
                .id(SpecificationItemId.createId("itest", "itest-wrong-type", 1))
                .description("This impl has wrong type (itest instead of impl)."));
        dsnItem.addLinkToItemWithStatus(itestItem, LinkStatus.COVERED_UNWANTED);

        final String reportString = renderToString();
        assertAll(
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "arch-impl-wrong-type"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, DSN),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, DSN),
                        new Field(Field.Type.COVERED_ITEMS, "dsn-uncovered-impl-wrong-type", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.UNCOVERED, DeepCoverageStatus.UNCOVERED,
                                ASpecReport.CoveringStatus.UNCOVERED)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "dsn-uncovered-impl-wrong-type"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.NEEDS_COVERAGE, UTEST, IMPL),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.UNCOVERED),
                        new Field(Field.Type.COVERED_TYPES, UTEST),
                        new Field(Field.Type.UNCOVERED_TYPES, IMPL),
                        new Field(Field.Type.COVERED_ITEMS, "utest-valid", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.COVERING),
                        new Field(Field.Type.COVERED_ITEMS, "itest-wrong-type", 1, ItemStatus.APPROVED,
                                DeepCoverageStatus.COVERED, DeepCoverageStatus.COVERED,
                                ASpecReport.CoveringStatus.UNEXPECTED),
                        new Field(Field.Type.COVERING_ITEMS, "arch-impl-wrong-type", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "utest-valid"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-wrong-type", 1)))),
                () -> assertThat(reportString, containsRegexp(item(
                        new Field(Field.Type.ID, "itest-wrong-type"),
                        new Field(Field.Type.VERSION, 1),
                        new Field(Field.Type.STATUS, ItemStatus.APPROVED),
                        new Field(Field.Type.SHALLOW_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.DEEP_COVERAGE, DeepCoverageStatus.COVERED),
                        new Field(Field.Type.COVERING_ITEMS, "dsn-uncovered-impl-wrong-type", 1)))),
                () -> assertThat(reportString, endsWith("</specdocument>")));
    }

    private SpecificationItem.Builder createItemBuilder()
    {
        return SpecificationItem.builder();
    }

    private LinkedSpecificationItem createItem(final SpecificationItem.Builder builder)
    {
        final LinkedSpecificationItem item = new LinkedSpecificationItem(builder.build());
        items.add(item);
        return item;
    }

    private String renderToString()
    {
        final OutputStream outputStream = new ByteArrayOutputStream();
        final Reportable report = new ASpecReport(getTrace(), reportContext);
        report.renderToStream(outputStream);
        return outputStream.toString();
    }

    private Trace getTrace()
    {
        final Trace.Builder builder = Trace.builder();
        builder.items(items);
        builder.defectItems(items.stream() //
                .filter(LinkedSpecificationItem::isDefect) //
                .collect(Collectors.toList()));
        return builder.build();
    }

    private String item(final Field... fields)
    {
        final StringBuilder result = new StringBuilder();
        final Map<Field.Type, List<Field>> fieldMap = new HashMap<>();
        for (final Field field : fields)
        {
            final List<Field> value = fieldMap.containsKey(field.type) ? fieldMap.get(field.type) : new ArrayList<>();
            value.add(field);
            fieldMap.put(field.type, value);
        }

        result.append(".*<specobject>");
        addField(fieldMap, result, Field.Type.ID, "id");
        addField(fieldMap, result, Field.Type.VERSION, "version");
        addField(fieldMap, result, Field.Type.STATUS, "status");
        addField(fieldMap, result, Field.Type.DESCRIPTION, "description");
        if (fieldMap.containsKey(Field.Type.SHALLOW_COVERAGE)
                || fieldMap.containsKey(Field.Type.DEEP_COVERAGE)
                || fieldMap.containsKey(Field.Type.NEEDS_COVERAGE))
        {

            result.append(".*<coverage>");
            addField(fieldMap, result, Field.Type.NEEDS_COVERAGE, "needsobj", "needscoverage");
            addField(fieldMap, result, Field.Type.SHALLOW_COVERAGE, "shallowCoverageStatus");
            addField(fieldMap, result, Field.Type.DEEP_COVERAGE, "deepCoverageStatus");
            addItemList(fieldMap, result, Field.Type.COVERED_ITEMS, "coveringSpecObject", "coveringSpecObjects");
            addField(fieldMap, result, Field.Type.COVERED_TYPES, "coveredType", "coveredTypes");
            addField(fieldMap, result, Field.Type.UNCOVERED_TYPES, "uncoveredType", "uncoveredTypes");
            result.append(".*</coverage>");
        }
        if (fieldMap.containsKey(Field.Type.COVERING_ITEMS))
        {
            addItemList(fieldMap, result, Field.Type.COVERING_ITEMS, "coveredType", "covering");
        }
        if (fieldMap.containsKey(Field.Type.DEPENDENCIES))
        {
            addItemList(fieldMap, result, Field.Type.DEPENDENCIES, "dependsOnSpecObject", "dependencies");
        }
        result.append(".*</specobject>.*");

        return result.toString();
    }

    private void addField(final Map<Field.Type, List<Field>> fields, final StringBuilder b,
            final Field.Type field, final String fieldName)
    {
        addField(fields, b, field, fieldName, null);
    }

    private void addField(final Map<Field.Type, List<Field>> fields, final StringBuilder b, final Field.Type field,
            final String fieldName, final String fieldWrapper)
    {
        if (fields.containsKey(field))
        {
            if (fieldWrapper != null && !fieldWrapper.isEmpty())
            {
                b.append(".*<").append(fieldWrapper).append(">");
            }
            for (final String value : fields.get(field).get(0).getValues())
            {
                b.append(".*<")
                        .append(fieldName)
                        .append(">")
                        .append(value)
                        .append("</")
                        .append(fieldName)
                        .append(">");
            }
            if (fieldWrapper != null && !fieldWrapper.isEmpty())
            {
                b.append(".*</").append(fieldWrapper).append(">");
            }
        }
    }

    void addItemList(final Map<Field.Type, List<Field>> fields, final StringBuilder b, final Field.Type field,
            final String fieldName, final String fieldWrapper)
    {
        if (fields.containsKey(field))
        {
            b.append(".*<").append(fieldWrapper).append(">");
            for (final Field item : fields.get(field))
            {
                b.append(".*<").append(fieldName).append(">");
                final List<String> values = item.getValues();
                b.append(".*<id>").append(values.get(0)).append("</id>");
                b.append(".*<version>").append(values.get(1)).append("</version>");
                if (values.size() > 2)
                {
                    b.append(".*<status>").append(values.get(2)).append("</status>");
                }
                if (values.size() > 3)
                {
                    b.append(".*<ownCoverageStatus>").append(values.get(3)).append("</ownCoverageStatus>");
                }
                if (values.size() > 4)
                {
                    b.append(".*<deepCoverageStatus>").append(values.get(4))
                            .append("</deepCoverageStatus>");
                }
                if (values.size() > 5)
                {
                    b.append(".*<coveringStatus>").append(values.get(5)).append("</coveringStatus>");
                }
                b.append(".*</").append(fieldName).append(">");
            }
            b.append(".*</").append(fieldWrapper).append(">");
        }
    }

    private static class Field
    {
        enum Type
        {
            ID, VERSION, STATUS, DESCRIPTION, NEEDS_COVERAGE, SHALLOW_COVERAGE, DEEP_COVERAGE, COVERED_ITEMS, COVERED_TYPES, UNCOVERED_TYPES, COVERING_ITEMS, DEPENDENCIES
        }

        private final Type type;
        private final List<String> values = new ArrayList<>();

        Field(final Type type, final String... value)
        {
            this.type = type;
            Collections.addAll(values, value);
        }

        Field(final Type type, final int value)
        {
            this.type = type;
            values.add(Integer.toString(value));
        }

        Field(final Type type, final ItemStatus value)
        {
            this.type = type;
            values.add(value.toString());
        }

        Field(final Type type, final DeepCoverageStatus value)
        {
            this.type = type;
            values.add(value == DeepCoverageStatus.COVERED ? "COVERED" : "UNCOVERED");
        }

        Field(final Type type, final String id, final int version, ItemStatus status,
                final DeepCoverageStatus ownCoverage,
                final DeepCoverageStatus deepCoverage,
                final ASpecReport.CoveringStatus coveringStatus)
        {
            this.type = type;
            values.add(id);
            values.add(Integer.toString(version));
            values.add(status.toString());
            values.add(ownCoverage == DeepCoverageStatus.COVERED ? "COVERED" : "UNCOVERED");
            values.add(deepCoverage == DeepCoverageStatus.COVERED ? "COVERED" : "UNCOVERED");
            values.add(coveringStatus.getLabel());
        }

        public Field(final Type type, final String id, final int version)
        {
            this.type = type;
            values.add(id);
            values.add(Integer.toString(version));
        }

        public List<String> getValues()
        {
            return values;
        }
    }

    @Factory
    static Matcher<String> containsRegexp(final String regexp)
    {
        return new StringRegexpMatcher(regexp);
    }
}
