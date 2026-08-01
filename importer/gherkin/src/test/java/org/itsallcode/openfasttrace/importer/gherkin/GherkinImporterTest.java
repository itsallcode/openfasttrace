package org.itsallcode.openfasttrace.importer.gherkin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.ImportAssertions;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;

class GherkinImporterTest
{
    private static final GherkinImporterFactory FACTORY = new GherkinImporterFactory();

    // [utest->dsn~gherkin.streaming-import~1]
    // [utest->dsn~gherkin.id-detection~1]
    // [utest->dsn~gherkin.covers-metadata-validation~1]
    // [utest->dsn~gherkin.needs-metadata-validation~1]
    @Test
    void testImportsScenarioOutlineWithScopedMetadataAndSteps()
    {
        final String source = """
                @smoke
                @unrelated @id:scn~account-login~1 @anotherTag
                # Covers: req~login~1
                # Needs: dsn, itest
                Scenario Outline: Login works
                  Given a registered user
                  When they log in
                  Then access is granted
                Examples:
                  | user |
                  | Ada  |
                """;

        final List<SpecificationItem> items = importText(source);

        final SpecificationItem item = items.get(0);
        assertAll(
                () -> assertThat(items, contains(
                        hasProperty("id", hasToString("scn~account-login~1")))),
                () -> assertThat(item.getTitle(), is("Login works")),
                () -> assertThat(item.getLocation().getLine(), is(2)),
                () -> assertThat(item.getDescription(), is(String.join(System.lineSeparator(),
                        "Given a registered user", "  When they log in", "  Then access is granted"))),
                () -> assertThat(item.getCoveredIds(), contains(hasToString("req~login~1"))),
                () -> assertThat(item.getNeedsArtifactTypes(), containsInAnyOrder("dsn", "itest")));
    }

    // [utest->dsn~gherkin.importer-selection~1]
    @Test
    void testFactorySupportsFeatureFilesWithHigherPrecedenceThanTagImporter()
    {
        final InputFile file = StreamInput.forReader(Path.of("specification.feature"),
                new java.io.BufferedReader(new java.io.StringReader("")));

        assertAll(
                () -> assertThat(FACTORY.supportsFile(file), is(true)),
                () -> assertThat(FACTORY.getPriority(), is(9000)));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    // [utest->dsn~gherkin.covers-metadata-validation~1]
    @Test
    void testImportsMultipleCoversDirectives()
    {
        final String source = """
                @id:scn~account-login~1
                # Covers: req~login~1
                # Covers: req~security~1
                Scenario: Login
                """;

        final List<SpecificationItem> items = importText(source);

        assertThat(items.get(0).getCoveredIds(), contains(
                hasToString("req~login~1"), hasToString("req~security~1")));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    // [utest->dsn~gherkin.id-detection~1]
    // [utest->dsn~gherkin.needs-metadata-validation~1]
    @Test
    void testSkipsScenarioWithDirectiveWithoutId()
    {
        final String source = """
                @ordinary
                # Needs: dsn
                Scenario: Login
                """;

        assertThat(importText(source), is(empty()));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testKeepsMetadataWhenAnUnrelatedCommentPrecedesTheScenario()
    {
        final List<SpecificationItem> items = importText("""
                @id:scn~login~1
                # A human-readable comment
                Scenario: Login
                """);

        assertThat(items, contains(hasProperty("id", hasToString("scn~login~1"))));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    // [utest->dsn~gherkin.id-detection~1]
    // [utest->dsn~gherkin.covers-metadata-validation~1]
    // [utest->dsn~gherkin.needs-metadata-validation~1]
    @ParameterizedTest
    @MethodSource("invalidMetadata")
    void testSkipsScenarioWithInvalidMetadata(final String source)
    {
        assertThat(importText(source), is(empty()));
    }

    private static Stream<String> invalidMetadata()
    {
        return Stream.of(
                """
                        @id:invalid
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        @id:scn~another-login~1
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Needs: dsn
                        # Needs: itest
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Covers:
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Covers: req~login~1,
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Covers: req~login~1, req~login~1
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Needs: invalid-type
                        Scenario: Login
                        """,
                """
                        @id:scn~login~1
                        # Needs: dsn, dsn
                        Scenario: Login
                        """);
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testOmitsTrailingEmptyScenarioLines()
    {
        final List<SpecificationItem> items = importText("""
                @id:scn~login~1
                Scenario: Login
                  Given a registered user

                """);

        assertThat(items.get(0).getDescription(), is("Given a registered user"));
    }

    // [utest->dsn~gherkin.id-detection~1]
    @ParameterizedTest
    @ValueSource(strings =
    {
            "@someTag@id:scn~login~1",
            "@id:scn~login~1 @anotherTag",
            "@someTag @id:scn~login~1",
            "@id:scn~login~1@anotherTag" })
    void testRecognizesIdTagInTagRegion(final String tagLine)
    {
        final List<SpecificationItem> items = importText(tagLine + "\nScenario: Login\n");

        assertThat(items.get(0).getId(), hasToString("scn~login~1"));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    // [utest->dsn~gherkin.id-detection~1]
    // [utest->dsn~gherkin.covers-metadata-validation~1]
    @ParameterizedTest
    @MethodSource("scenariosWithoutUsableIdMetadata")
    void testSkipsScenariosWithoutUsableIdMetadata(final String source)
    {
        assertThat(importText(source), is(empty()));
    }

    private static Stream<String> scenariosWithoutUsableIdMetadata()
    {
        return Stream.of(
                """
                        Feature: login
                        Scenario: ordinary scenario
                          Given nothing
                        """,
                """
                        # Covers: req~login~1
                        Scenario: Login
                        """,
                """
                        # Needs: impl
                        Scenario: Login
                        """,
                """
                        @unrelatedTag
                        Scenario: Login
                        """,
                """
                        @id:invalidId
                        Scenario: Login
                        """);
    }

    // [utest->dsn~gherkin.id-detection~1]
    // [utest->dsn~gherkin.covers-metadata-validation~1]
    // [utest->dsn~gherkin.needs-metadata-validation~1]
    @Test
    void testContinuesAfterInvalidScenarioMetadata()
    {
        final List<SpecificationItem> items = importText("""
                @id:scn~invalid~1
                # Needs: dsn
                # Needs: itest
                Scenario: Invalid
                @id:scn~valid~1
                # Covers: req~login~1
                # Needs: dsn
                Scenario: Valid
                """);

        assertThat(items, contains(hasProperty("id", hasToString("scn~valid~1"))));
    }

    // [utest->dsn~gherkin.id-detection~1]
    @Test
    void testImportsScenariosWithDuplicateIds()
    {
        final List<SpecificationItem> items = importText("""
                @id:scn~login~1
                Scenario: First login
                Feature: Another feature
                @id:scn~login~1
                Scenario: Second login
                """);

        assertThat(items, contains(
                hasProperty("title", is("First login")),
                hasProperty("title", is("Second login"))));
    }

    // [utest->dsn~gherkin.comment-coverage-tags~1]
    @Test
    void testImportsCommentCoverageTagsButIgnoresExecutableCoverageTags()
    {
        final String source = """
                @id:scn~ordinary~1
                Scenario: ordinary
                  # [%s]
                  Given [%s]
                """.formatted("impl~gherkin-comment~1 -> dsn~gherkin~1",
                "impl~gherkin-executable~1 -> dsn~gherkin~1");
        final List<SpecificationItem> items = importText(source);

        assertThat(items, containsInAnyOrder(
                hasProperty("id", hasToString("scn~ordinary~1")),
                hasProperty("id", hasToString("impl~gherkin-comment~1"))));
    }

    // [utest->dsn~gherkin.comment-coverage-tags~1]
    @Test
    void testImportsCommentCoverageTagsWhileScenarioIsOpen()
    {
        final ImportEventListener listener = mock(ImportEventListener.class);
        final InputFile file = StreamInput.forReader(Path.of("specification.feature"),
                new BufferedReader(new StringReader("""
                        @id:scn~ordinary~1
                        Scenario: ordinary
                          # [%s]
                          Given a step
                        """.formatted("impl~gherkin-comment~1 -> dsn~gherkin~1"))));

        new GherkinImporter(file, listener).runImport();

        final InOrder events = inOrder(listener);
        events.verify(listener).beginSpecificationItem();
        events.verify(listener).setId(SpecificationItemId.parseId("scn~ordinary~1"));
        events.verify(listener).addSpecificationItem(any(SpecificationItem.class));
        events.verify(listener).endSpecificationItem();
    }

    private static List<SpecificationItem> importText(final String source)
    {
        return ImportAssertions.runImporterOnText(Path.of("specification.feature"), source, FACTORY);
    }
}
