package org.itsallcode.openfasttrace.importer.restructuredtext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.matcher.auto.AutoMatcher.contains;
import static org.itsallcode.openfasttrace.api.core.SpecificationItemId.createId;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.testutil.importer.ImportAssertions;
import org.itsallcode.openfasttrace.testutil.importer.lightweightmarkup.AbstractLightWeightMarkupImporterTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestRestructuredTextImporter extends AbstractLightWeightMarkupImporterTest
{
    private static final String FULL_COVERAGE_TAG = "[doc" + "->req~guide~1]";
    private static final ImporterFactory importerFactory = createImporterFactory();

    TestRestructuredTextImporter()
    {
        super(1);
    }

    @Override
    protected ImporterFactory getImporterFactory()
    {
        return importerFactory;
    }

    private static ImporterFactory createImporterFactory()
    {
        final RestructuredTextImporterFactory factory = new RestructuredTextImporterFactory();
        factory.init(new ImporterContext(ImportSettings.createDefault()));
        return factory;
    }

    // [utest->dsn~rst.comment-coverage-tags~1]
    @Test
    void testImportsCoverageTagFromStandaloneComment()
    {
        assertImport("guide.rst", """
                Guide
                =====
                .. %s
                req~ordinary~1
                """.formatted(FULL_COVERAGE_TAG), contains(
                        item().id("doc", "guide-1442787702", 0)
                                .addCoveredId("req", "guide", 1)
                                .location("guide.rst", 3).build(),
                        item().id("req", "ordinary", 1).location("guide.rst", 4).build()));
    }

    // [utest->dsn~rst.comment-coverage-tags~1]
    @ParameterizedTest
    @ValueSource(strings = {
            FULL_COVERAGE_TAG,
            ".. directive:: " + FULL_COVERAGE_TAG,
            "..\n   " + FULL_COVERAGE_TAG,
            "   " + FULL_COVERAGE_TAG })
    void testIgnoresCoverageTagsOutsideComments(final String line)
    {
        assertImport("guide.rst", line, emptyIterable());
    }

    // [utest->dsn~rst.comment-coverage-tags~1]
    @Test
    void testIgnoresCoverageTagsInMultilineComments()
    {
        assertImport("guide.rst", """
                .. %s
                   continuation
                """.formatted(FULL_COVERAGE_TAG), emptyIterable());
    }

    // [utest->dsn~rst.comment-coverage-tags~1]
    @Test
    void testUsesFirstMatchingPathConfigurationForShortCoverageTags()
    {
        final PathConfig first = pathConfig("first.", "first");
        final PathConfig second = pathConfig("second.", "second");
        final List<SpecificationItem> items = importWithSettings(".. [[covered:3]]", first, second);

        assertAll(
                () -> assertThat(items.get(0).getId().getArtifactType(), equalTo("first")),
                () -> assertThat(items.get(0).getCoveredIds(),
                        equalTo(List.of(createId("req", "first.covered", 3)))));
    }

    // [utest->dsn~rst.comment-coverage-tags~1]
    @Test
    void testDoesNotImportShortCoverageTagWithoutMatchingPathConfiguration()
    {
        assertThat(importWithSettings(".. [[covered:3]]",
                PathConfig.builder().patternPathMatcher("glob:**.md").coveredItemArtifactType("req")
                        .tagArtifactType("doc").build()), emptyIterable());
    }

    private static List<SpecificationItem> importWithSettings(final String input,
            final PathConfig... pathConfigs)
    {
        final RestructuredTextImporterFactory factory = new RestructuredTextImporterFactory();
        factory.init(new ImporterContext(ImportSettings.builder().pathConfigs(List.of(pathConfigs)).build()));
        return ImportAssertions.runImporterOnText(Path.of("guide.rst"), input, factory);
    }

    private static PathConfig pathConfig(final String prefix, final String tagArtifactType)
    {
        return PathConfig.builder().patternPathMatcher("glob:**.rst").coveredItemArtifactType("req")
                .coveredItemNamePrefix(prefix).tagArtifactType(tagArtifactType).build();
    }

    protected String formatTitle(final String title, final int level)
    {
        return title + "\n" + "=".repeat(title.length());
    }

    // [utest -> dsn~md.specification-item-title~1]
    @Test
    void testMarkdownTitleBeforeRequirementIdIsRequirementTitle()
    {
        assertImport("titles.md",
                """
                        The title
                        =========
                        the~id~1
                        """,
                contains(item()
                        .id("the", "id", 1)
                        .title("The title")
                        .location("titles.md", 3)
                        .build()));
    }

    // [utest -> dsn~md.specification-item-title~1]
    @Test
    void testMarkdownTitleDetectedAfterAnotherTitle()
    {
        assertImport("more_titles.md",
                """
                        1st level title
                        ===============

                        2nd level title
                        ---------------

                        the~id~1
                        """,
                contains(item()
                        .title("2nd level title")
                        .id("the", "id", 1)
                        .location("more_titles.md", 7)
                        .build()));
    }

    // [utest->dsn~md.specification-item-title~1]
    @Test
    void testFindTitleAfterTitle()
    {
        assertImport("x", """
                This title should be ignored
                ============================

                Title
                -----
                `a~b~1
                """,
                contains(item()
                        .id(SpecificationItemId.parseId("a~b~1"))
                        .title("Title").location("x", 6)
                        .build()));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "---------------------------------", "---", "===", "======", "--- ",
            "=== ", "---\t" })
    void testRecognizeItemTitleWithUnderlines(final String underline)
    {
        assertImport("file name", """
                This is a title with an underline
                %s
                `extra~support-underlined-headers~1`
                Body text.
                """.formatted(underline),
                contains(item()
                        .id(SpecificationItemId.createId("extra", "support-underlined-headers",
                                1))
                        .title("This is a title with an underline")
                        .description("Body text.")
                        .location("file name", 3)
                        .build()));
    }

    @ValueSource(strings = { "---------------------------------", "---", "===", "======",
            "================================================",
            "--- ", "=== ", "---\t"
    })
    @ParameterizedTest
    void testRecognizeItemTitleWithUnderlinesAfterAnotherTitle(final String underline)
    {
        assertImport("y", """
                # This must be ignored.
                This is a title with an underline
                %s
                `extra~support-underlined-headers~1`
                Body text.
                """.formatted(underline),
                contains(item()
                        .id(SpecificationItemId.createId("extra", "support-underlined-headers",
                                1))
                        .title("This is a title with an underline")
                        .description("Body text.")
                        .location("y", 4)
                        .build()));
    }

    @Test
    void testLessThenThreeUnderliningCharactersAreNotDetectedAsTitleUnderlines()
    {
        assertImport("z", """
                This is not a title since the underline is too short
                --
                req~too-short~111
                """,
                contains(item()
                        .id(SpecificationItemId.createId("req", "too-short", 111))
                        .location("z", 3)
                        .build()));
    }

    // [utest -> dsn~disabling-oft-parsing-for-parts-of-a-markup-file~1]
    @Test
    void testDisablingRstParsingForATextBlock() {
        assertImport("disable_parsing.rst", """
                `req~stop-parsing~1`
                
                The next part must not be parsed:
                
                .. oft:off
                `req~do-not-parse-me~2`
                
                Invisible.
                
                Needs: utest
                .. oft:on
                
                Needs: impl
                """,
                contains(item()
                        .id(createId("req", "stop-parsing", 1))
                        .description("The next part must not be parsed:")
                        .addNeedsArtifactType("impl")
                        .location("disable_parsing.rst", 1)
                        .build()));
    }
}
