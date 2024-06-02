package org.itsallcode.openfasttrace.importer.markdown;

import static org.itsallcode.matcher.auto.AutoMatcher.contains;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImporterFactory;
import org.itsallcode.openfasttrace.testutil.importer.lightweightmarkup.AbstractLightWeightMarkupImporterTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestMarkdownMarkupImporter extends AbstractLightWeightMarkupImporterTest
{
    private final static ImporterFactory importerFactory = new MarkdownImporterFactory();

    TestMarkdownMarkupImporter()
    {
        super(0);
    }

    @Override
    protected ImporterFactory getImporterFactory()
    {
        return importerFactory;
    }

    protected String formatTitle(final String title, final int level)
    {
        return "#".repeat(level) + " " + title;
    }

    // [utest -> dsn~md.specification-item-title~1]
    @Test
    void testMarkdownTitleBeforeRequirementIdIsRequirementTitle()
    {
        assertImport("titles.md",
                """
                        # The title
                        the~id~1
                        """,
                contains(item()
                        .id("the", "id", 1)
                        .title("The title")
                        .location("titles.md", 2)
                        .build()));
    }

    // [utest -> dsn~md.specification-item-title~1]
    @Test
    void testMarkdownTitleDetectedAfterAnotherTitle()
    {
        assertImport("more_titles.md",
                """
                        # 1st level title

                        # 2nd level title

                        the~id~1
                        """,
                contains(item()
                        .title("2nd level title")
                        .id("the", "id", 1)
                        .location("more_titles.md", 5)
                        .build()));
    }

    // [utest->dsn~md.specification-item-title~1]
    @Test
    void testFindTitleAfterTitle()
    {
        assertImport("x", """
                ## This title should be ignored

                ### Title
                `a~b~1
                """,
                contains(item()
                        .id(SpecificationItemId.parseId("a~b~1"))
                        .title("Title").location("x", 4)
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
}
