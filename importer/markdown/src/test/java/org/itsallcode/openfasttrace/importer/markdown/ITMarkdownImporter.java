package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.matcher.auto.AutoMatcher.contains;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.itsallcode.matcher.auto.AutoMatcher;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ITMarkdownImporter
{
    private static final String NL = System.lineSeparator();
    private static final String TAG2 = "Tag2";
    private static final String TAG1 = "Tag1";
    private static final String FILENAME = "file name";

    // [utest -> dsn~md.specification-item-id-format~3]
    @CsvSource({
            "at~name~67, at, name, 67",
            "longartifacttype~name.with.dots~123456789, longartifacttype, name.with.dots, 123456789",
            "a~b1.c1.d~0, a, b1.c1.d, 0"
    })
    @ParameterizedTest
    void testRequirementIdDetected(final String markdownId, final String expectedArtifactType,
                                   final String expectedName, final int expectedRevision)
    {
        assertThat(runImporterOnText("/doc/spec.md/", markdownId),
                contains(SpecificationItem.builder()
                        .id(expectedArtifactType, expectedName, expectedRevision)
                        .location("/doc/spec.md", 1)
                        .build()));
    }

    // [utest -> dsn~md.specification-item-title~1]
    @Test
    void testMarkdownTitleBeforeRequirementIdIsRequirementTitle()
    {
        assertThat(runImporterOnText("titles.md", """
                # The title
                the~id~1
                """),
                contains(SpecificationItem.builder()
                        .title("The title")
                        .id("the", "id", 1)
                        .location("titles.md", 2)
                        .build()));
    }

    @Test
    void testFindRequirement()
    {
        assertThat(runImporterOnText("""
                        # Requirement Title
                        `type~id~1` <a id="type~id~1"></a>
                        Description
                        
                        More description
                        
                        Rationale:
                        Rationale
                        More rationale
                        
                        Covers:
                        
                          * impl~foo1~1
                         + [Link to baz2](#impl~baz2~2)
                        
                        Depends:
                        
                          + configuration~blubb.blah.blah~4711
                          - db~blah.blubb~42
                        
                        Comment:
                        
                        Comment
                        More comment
                        
                        Needs: artA , artB
                        """),
                contains(SpecificationItem.builder().id(SpecificationItemId.parseId("type~id~1")).title("Requirement Title")
                        .comment("Comment" + NL + "More comment")
                        .description("Description" + NL + NL + "More description")
                        .rationale("Rationale" + NL + "More rationale")
                        .addNeedsArtifactType("artA").addNeedsArtifactType("artB")
                        .addCoveredId(SpecificationItemId.parseId("impl~foo1~1"))
                        .addCoveredId(SpecificationItemId.parseId("impl~baz2~2"))
                        .addDependOnId(SpecificationItemId.parseId("configuration~blubb.blah.blah~4711"))
                        .addDependOnId(SpecificationItemId.parseId("db~blah.blubb~42"))
                        .location("file name", 2)
                        .build()));
    }

    // [utest->dsn~md.needs-coverage-list-compact~1]

    private List<SpecificationItem> runImporterOnText(final String text) {
        return runImporterOnText(FILENAME, text);
    }

    private List<SpecificationItem> runImporterOnText( final String filename, final String text)
    {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final InputFile file = StreamInput.forReader(Paths.get(filename), reader);
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
                contains(SpecificationItem.builder().id(SpecificationItemId.parseId("type~id~1")).title("Requirement Title").location("file name", 2).build(),
                                SpecificationItem.builder().id(MarkdownTestConstants.ID2).title("").location("file name", 4).build()));
    }

    private String createTwoConsecutiveItemsInMarkdownFormat()
    {
        return "# " + "Requirement Title" //
                + "\n" //
                + SpecificationItemId.parseId("type~id~1") + "\n" //
                + "\n" + MarkdownTestConstants.ID2 + "\n" //
                + "# Irrelevant Title";
    }

    @Test
    void testSingleNeeds()
    {
        final String singleNeedsItem = "`foo~bar~1`\n\nNeeds: " + "artA";
        final List<SpecificationItem> items = runImporterOnText(singleNeedsItem);
        MatcherAssert.assertThat(items.get(0).getNeedsArtifactTypes(), Matchers.contains("artA"));
    }

    @Test
    void testFindLegacyRequirement()
    {
        final String completeItem = createCompleteSpecificationItemInLegacyMarkdownFormat();
        assertThat(runImporterOnText(completeItem),
                contains(SpecificationItem.builder().id(SpecificationItemId.parseId(MarkdownTestConstants.LEGACY_ID))
                        .title("Requirement Title")
                        .status(ItemStatus.PROPOSED)
                        .comment("Comment" + NL + "More comment")
                        .description("Description" + NL + NL + "More description")
                        .rationale("Rationale" + NL + "More rationale")
                        .addNeedsArtifactType("artA").addNeedsArtifactType("artB")
                        .addCoveredId(SpecificationItemId.parseId(MarkdownTestConstants.LEGACY_COVERED_ID1))
                        .addCoveredId(SpecificationItemId.parseId(MarkdownTestConstants.LEGACY_COVERED_ID2))
                        .addDependOnId(SpecificationItemId.parseId(MarkdownTestConstants.LEGACY_DEPENDS_ON_ID1))
                        .addDependOnId(SpecificationItemId.parseId(MarkdownTestConstants.LEGACY_DEPENDS_ON_ID2))
                        .addTag("Tag1").addTag("Tag2")
                        .location("file name", 2)
                        .build()));
    }

    // [utest->dsn~md.needs-coverage-list~2]
    private String createCompleteSpecificationItemInLegacyMarkdownFormat()
    {
        return "# " + "Requirement Title" //
                + "\n" //
                + "`" + MarkdownTestConstants.LEGACY_ID + "`" //
                + "\n" //
                + "\nStatus: proposed\n" //
                + "\nDescription:\n" + "Description" + "\n" //
                + "" + "\n" //
                + "More description" + "\n" //
                + "\nRationale:\n" //
                + "Rationale" + "\n" //
                + "More rationale" + "\n" //
                + "\nDepends:\n\n" //
                + "  + `" + MarkdownTestConstants.LEGACY_DEPENDS_ON_ID1 + "`\n" //
                + "  - `" + MarkdownTestConstants.LEGACY_DEPENDS_ON_ID2 + "`\n" //
                + "\nCovers:\n\n" //
                + "  * `" + MarkdownTestConstants.LEGACY_COVERED_ID1 + "`\n" //
                + " + `" + MarkdownTestConstants.LEGACY_COVERED_ID2 + "`\n" //
                + "\nComment:\n\n" //
                + "Comment" + "\n" //
                + "More comment" + "\n" //
                + "\nNeeds:\n" //
                + "   * " + "artA" + "\n"//
                + "+ " + "artB" + "\n" //
                + "\nTags: " + TAG1 + ", " + TAG2;
    }

    // [utest->dsn~md.artifact-forwarding-notation~1]
    @Test
    void testForwardRequirement()
    {
        final List<SpecificationItem> items = runImporterOnText("arch-->dsn:req~foobar~2\n" //
                + "   * `dsn --> impl, utest,itest : arch~bar.zoo~123`");
        assertThat(items,
                contains(SpecificationItem.builder().id(SpecificationItemId.parseId("arch~foobar~2"))
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
                contains(SpecificationItem.builder().id(SpecificationItemId.parseId("a~b~1"))
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
        assertThat(items, contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("req", "zellzustandsänderung", 1))
                .description(
                        "Diese Anforderung ermöglicht die Aktualisierung des Zustands von lebenden und toten Zellen"
                                + " in jeder Generation.")
                .location("file name", 2).addNeedsArtifactType("arch")
                .build()));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "---------------------------------", "---", "===", "======", "--- ", "===   ", "---\t" })
    void testRecognizeItemTitleWithUnderlines(final String underline)
    {
        final List<SpecificationItem> items = runImporterOnText(
                "This is a title with an underline\n" //
                        + underline + "\n" //
                        + "`extra~support-underlined-headers~1`\n" //
                        + "Body text.\n");
        assertThat(items, contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("extra", "support-underlined-headers", 1))
                .title("This is a title with an underline")
                .description("Body text.")
                .location("file name", 3)
                .build()));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "---------------------------------", "---", "===", "======", "================================================",
            "--- ", "===   ", "---\t"
    })
    void testRecognizeItemTitleWithUnderlinesAfterAnotherTitle(final String underline)
    {
        final List<SpecificationItem> items = runImporterOnText(
                "# This must be ignored.\n" //
                        + "This is a title with an underline\n" //
                        + underline + "\n" //
                        + "`extra~support-underlined-headers~1`\n" //
                        + "Body text.\n");
        assertThat(items, contains(SpecificationItem.builder()
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
        assertThat(items, contains(SpecificationItem.builder()
                .id(SpecificationItemId.createId("req", "too-short", 111))
                .location("file name", 3)
                .build()));
    }
}
