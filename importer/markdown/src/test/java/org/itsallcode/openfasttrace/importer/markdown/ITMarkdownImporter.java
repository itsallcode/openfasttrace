package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.openfasttrace.importer.markdown.MarkdownTestConstants.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.matcher.auto.AutoMatcher;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ITMarkdownImporter
{
    private static final String NL = System.lineSeparator();
    private static final String TAG2 = "Tag2";
    private static final String TAG1 = "Tag1";
    private static final String FILENAME = "file name";

    @Test
    void testFindRequirement()
    {
        assertThat(runImporterOnText(createCompleteSpecificationItemInMarkdownFormat()),
                AutoMatcher.contains(SpecificationItem.builder().id(ID1).title("Requirement Title")
                        .comment("Comment" + NL + "More comment")
                        .description("Description" + NL + NL + "More description")
                        .rationale("Rationale" + NL + "More rationale")
                        .addNeedsArtifactType("artA").addNeedsArtifactType("artB")
                        .addCoveredId(SpecificationItemId.parseId(COVERED_ID1))
                        .addCoveredId(SpecificationItemId.parseId(COVERED_ID2))
                        .addDependOnId(SpecificationItemId.parseId(DEPENDS_ON_ID1))
                        .addDependOnId(SpecificationItemId.parseId(DEPENDS_ON_ID2))
                        .location("file name", 2)
                        .build()));

    }

    // [utest->dsn~md.needs-coverage-list-compact~1]
    private String createCompleteSpecificationItemInMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + "`" + ID1 + "` <a id=\"" + ID1 + "\"></a>" //
                + "\n" //
                + DESCRIPTION_LINE1 + "\n" //
                + DESCRIPTION_LINE2 + "\n" //
                + DESCRIPTION_LINE3 + "\n" //
                + "\nRationale:\n" //
                + RATIONALE_LINE1 + "\n" //
                + RATIONALE_LINE2 + "\n" //
                + "\nCovers:\n\n" //
                + "  * " + COVERED_ID1 + "\n" //
                + " + " + "[Link to baz2](#" + COVERED_ID2 + ")\n" //
                + "\nDepends:\n\n" //
                + "  + " + DEPENDS_ON_ID1 + "\n" //
                + "  - " + DEPENDS_ON_ID2 + "\n" //
                + "\nComment:\n\n" //
                + COMMENT_LINE1 + "\n" //
                + COMMENT_LINE2 + "\n" //
                + "\nNeeds: " + NEEDS_ARTIFACT_TYPE1 //
                + " , " + NEEDS_ARTIFACT_TYPE2 + " ";
    }

    private List<SpecificationItem> runImporterOnText(final String text)
    {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = StreamInput.forReader(Paths.get(FILENAME), reader);
        final SpecificationListBuilder specItemBuilder = SpecificationListBuilder.create();
        final Importer importer = new MarkdownImporterFactory().createImporter(file,
                specItemBuilder);
        importer.runImport();
        return specItemBuilder.build();
    }

    @Test
    void testTwoConsecutiveSpecificationItems()
    {
        assertThat(runImporterOnText(createTwoConsecutiveItemsInMarkdownFormat()),
                AutoMatcher
                        .contains(SpecificationItem.builder().id(ID1).title(TITLE).location("file name", 2).build(),
                                SpecificationItem.builder().id(ID2).title("").location("file name", 4).build()));
    }

    private String createTwoConsecutiveItemsInMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + ID1 + "\n" //
                + "\n" + ID2 + "\n" //
                + "# Irrelevant Title";
    }

    @Test
    void testSingleNeeds()
    {
        final String singleNeedsItem = "`foo~bar~1`\n\nNeeds: " + NEEDS_ARTIFACT_TYPE1;
        final List<SpecificationItem> items = runImporterOnText(singleNeedsItem);
        assertThat(items.get(0).getNeedsArtifactTypes(), contains(NEEDS_ARTIFACT_TYPE1));
    }

    @Test
    void testFindLegacyRequirement()
    {
        final String completeItem = createCompleteSpecificationItemInLegacyMarkdownFormat();
        assertThat(runImporterOnText(completeItem),
                AutoMatcher.contains(SpecificationItem.builder().id(SpecificationItemId.parseId(LEGACY_ID))
                        .title("Requirement Title")
                        .status(ItemStatus.PROPOSED)
                        .comment("Comment" + NL + "More comment")
                        .description("Description" + NL + NL + "More description")
                        .rationale("Rationale" + NL + "More rationale")
                        .addNeedsArtifactType("artA").addNeedsArtifactType("artB")
                        .addCoveredId(SpecificationItemId.parseId(LEGACY_COVERED_ID1))
                        .addCoveredId(SpecificationItemId.parseId(LEGACY_COVERED_ID2))
                        .addDependOnId(SpecificationItemId.parseId(LEGACY_DEPENDS_ON_ID1))
                        .addDependOnId(SpecificationItemId.parseId(LEGACY_DEPENDS_ON_ID2))
                        .addTag("Tag1").addTag("Tag2")
                        .location("file name", 2)
                        .build()));
    }

    // [utest->dsn~md.needs-coverage-list~2]
    private String createCompleteSpecificationItemInLegacyMarkdownFormat()
    {
        return "# " + TITLE //
                + "\n" //
                + "`" + LEGACY_ID + "`" //
                + "\n" //
                + "\nStatus: proposed\n" //
                + "\nDescription:\n" + DESCRIPTION_LINE1 + "\n" //
                + DESCRIPTION_LINE2 + "\n" //
                + DESCRIPTION_LINE3 + "\n" //
                + "\nRationale:\n" //
                + RATIONALE_LINE1 + "\n" //
                + RATIONALE_LINE2 + "\n" //
                + "\nDepends:\n\n" //
                + "  + `" + LEGACY_DEPENDS_ON_ID1 + "`\n" //
                + "  - `" + LEGACY_DEPENDS_ON_ID2 + "`\n" //
                + "\nCovers:\n\n" //
                + "  * `" + LEGACY_COVERED_ID1 + "`\n" //
                + " + `" + LEGACY_COVERED_ID2 + "`\n" //
                + "\nComment:\n\n" //
                + COMMENT_LINE1 + "\n" //
                + COMMENT_LINE2 + "\n" //
                + "\nNeeds:\n" //
                + "   * " + NEEDS_ARTIFACT_TYPE1 + "\n"//
                + "+ " + NEEDS_ARTIFACT_TYPE2 + "\n" //
                + "\nTags: " + TAG1 + ", " + TAG2;
    }

    // [utest->dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardRequirement()
    {
        final List<SpecificationItem> items = runImporterOnText("arch-->dsn:req~foobar~2\n" //
                + "   * `dsn --> impl, utest,itest : arch~bar.zoo~123`");
        assertThat(items,
                AutoMatcher.contains(SpecificationItem.builder().id(SpecificationItemId.parseId("arch~foobar~2"))
                        .forwards(true)
                        .addCoveredId(SpecificationItemId.parseId("req~foobar~2"))
                        .addNeedsArtifactType("dsn")
                        .build(),
                        SpecificationItem.builder().id(SpecificationItemId.parseId("dsn~bar.zoo~123"))
                                .addCoveredId(SpecificationItemId.parseId("arch~bar.zoo~123"))
                                .addNeedsArtifactType("impl").addNeedsArtifactType("utest")
                                .addNeedsArtifactType("itest")
                                .forwards(true)
                                .build()));
    }

    // [utest->dsn~md.specification-item-title~1]
    @Test
    void testFindTitleAfterTitle()
    {
        assertThat(runImporterOnText("## This title should be ignored\n\n" //
                + "### Title\n" //
                + "`a~b~1`"),
                AutoMatcher.contains(SpecificationItem.builder().id(SpecificationItemId.parseId("a~b~1"))
                        .title("Title").location("file name", 4)
                        .build()));
    }

    @ParameterizedTest
    @MethodSource("needsCoverage")
    void testNeedsCoverage(final String mdContent, final List<String> expected)
    {
        final List<SpecificationItem> items = runImporterOnText("`a~b~1`\n" + mdContent);
        assertThat(items.get(0).getNeedsArtifactTypes(), equalTo(expected));
    }

    static Stream<Arguments> needsCoverage()
    {
        return Stream.of(
                Arguments.of("Needs: req , dsn ", List.of("req", "dsn")),
                Arguments.of("Needs: req ", List.of("req")),
                Arguments.of("Needs: req,dsn ", List.of("req", "dsn")),
                Arguments.of("Needs: req ,dsn", List.of("req", "dsn")),
                Arguments.of("Needs: req,dsn", List.of("req", "dsn")),
                Arguments.of("Needs:  req,\tdsn\n", List.of("req", "dsn")),
                Arguments.of("Needs:req,dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n* req\n* dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n  * req\n  * dsn", List.of("req", "dsn")),
                Arguments.of("Needs:\n*  req \n\t*  dsn ", List.of("req", "dsn")),
                Arguments.of("Needs:\n* req\n* dsn", List.of("req", "dsn")));
    }

    @ParameterizedTest
    @MethodSource("tags")
    void testTags(final String mdContent, final List<String> expected)
    {
        final List<SpecificationItem> items = runImporterOnText("`a~b~1`\n" + mdContent);
        assertThat(items.get(0).getTags(), equalTo(expected));
    }

    static Stream<Arguments> tags()
    {
        return Stream.of(
                Arguments.of("Tags: req , dsn ", List.of("req", "dsn")),
                Arguments.of("Tags: req ", List.of("req")),
                Arguments.of("Tags: req,dsn ", List.of("req", "dsn")),
                Arguments.of("Tags: req ,dsn", List.of("req", "dsn")),
                Arguments.of("Tags: req,dsn", List.of("req", "dsn")),
                Arguments.of("Tags: req,\tdsn\n", List.of("req", "dsn")),
                Arguments.of("Tags:req,dsn", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req\n* dsn", List.of("req", "dsn")),
                Arguments.of("Tags:\n * req\n * dsn\n", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req \n\t* dsn ", List.of("req", "dsn")),
                Arguments.of("Tags:\n* req\n* dsn", List.of("req", "dsn")));
    }

    @Test
    void testItemIdSupportsUmlauts()
    {
        final List<SpecificationItem> items = runImporterOnText(
                "`### Die Implementierung muss den Zustand einzelner Zellen ändern.\n"
                        + "`req~zellzustandsänderung~1`\n"
                        + "Diese Anforderung ermöglicht die Aktualisierung des Zustands von lebenden und toten Zellen"
                        + " in jeder Generation.\n"
                        + "Needs: arch");
        assertThat(items, AutoMatcher.contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("req", "zellzustandsänderung", 1))
                .description(
                        "Diese Anforderung ermöglicht die Aktualisierung des Zustands von lebenden und toten Zellen"
                                + " in jeder Generation.")
                .location("file name", 2).addNeedsArtifactType("arch")
                .build()));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "---------------------------------", "---", "===", "======", "--------------------------------- ", "--- ", "=== ",
            "====== " })
    void testRecognizeItemTitleWithUnderlines(final String underline)
    {
        final List<SpecificationItem> items = runImporterOnText(
                "This is a title with an underline\n" //
                        + underline + "\n" //
                        + "`extra~support-underlined-headers~1`\n" //
                        + "Body text.\n");
        assertThat(items, AutoMatcher.contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("extra", "support-underlined-headers", 1))
                .title("This is a title with an underline")
                .description("Body text.")
                .location("file name", 3)
                .build()));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "---------------------------------", "---", "===", "======", "================================================",
            "--- ", "=== " })
    void testRecognizeItemTitleWithUnderlinesAfterAnotherTitle(final String underline)
    {
        final List<SpecificationItem> items = runImporterOnText(
                "# This must be ignored.\n" //
                        + "This is a title with an underline\n" //
                        + underline + "\n" //
                        + "`extra~support-underlined-headers~1`\n" //
                        + "Body text.\n");
        assertThat(items, AutoMatcher.contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("extra", "support-underlined-headers", 1))
                .title("This is a title with an underline")
                .description("Body text.")
                .location("file name", 4)
                .build()));
    }

    @Test
    void testLessThenTwoUnderliningCharactersAreNotDetectedAsTitleUnderlines()
    {
        final List<SpecificationItem> items = runImporterOnText(
                "This is not a title since the underline is too short\n"
                        + "--\n"
                        + "req~too-short~111");
        assertThat(items, AutoMatcher.contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("req", "too-short", 111))
                .location("file name", 3)
                .build()));
    }

    @Test
    void testBrokenNeedsCoverage()
    {
        final List<SpecificationItem> items = runImporterOnText(
                "Test case for assigning a specific number of SPIs to allocate for dom0less domain\n"
                        + "=================================================================================\n"
                        + "\n"
                        + "`XenValTestCase~arm64_assign_domain_SPIs~1`\n"
                        + "\n"
                        + "Covers:\n"
                        + "- `XenSSR~arm64_assign_domain_SPIs~1`\n"
                        + "\n"
                        + "Needs:\n"
                        + "- XenValTestCode\n"
                        + "\n"
                        + "Objectives\n"
                        + "----------\n"
                        + "\n"
                        + "The script is responsible for:\n"
                        + "- preparing kernel image (AArch64 linux image)");
        assertThat(items, AutoMatcher.contains(
                SpecificationItem.builder().id("XenValTestCase", "arm64_assign_domain_SPIs", 1)
                        .title("Test case for assigning a specific number of SPIs to allocate for dom0less domain")
                        .addNeedsArtifactType("XenValTestCode")
                        .addCoveredId("XenSSR", "arm64_assign_domain_SPIs", 1).location(FILENAME, 4)
                        .description(
                                "Objectives\n----------\n\nThe script is responsible for:\n- preparing kernel image (AArch64 linux image)")
                        .build()));
    }

    @Test
    void testBrokenNeedsCoverageMinimal()
    {
        final List<SpecificationItem> items = runImporterOnText(
                "## Test case for assigning a specific number of SPIs to allocate for dom0less domain\n"
                        + "`XenValTestCase~arm64_assign_domain_SPIs~1`\n"
                        + "Covers:\n"
                        + "- `XenSSR~arm64_assign_domain_SPIs~1`\n"
                        + "\n"
                        + "Needs:\n"
                        + "- XenValTestCode\n"
                        + "\n"
                        + "Objectives\n"
                        + "The script is responsible for:\n"
                        + "- preparing kernel image (AArch64 linux image)");
        assertThat(items, AutoMatcher.contains(
                SpecificationItem.builder().id("XenValTestCase", "arm64_assign_domain_SPIs", 1)
                        .title("Test case for assigning a specific number of SPIs to allocate for dom0less domain")
                        .addNeedsArtifactType("XenValTestCode")
                        .addCoveredId("XenSSR", "arm64_assign_domain_SPIs", 1).location(FILENAME, 2)
                        .description(
                                "Objectives\nThe script is responsible for:\n- preparing kernel image (AArch64 linux image)")
                        .build()));
    }
}
