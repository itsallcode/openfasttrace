package org.itsallcode.openfasttrace.importer.gherkin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
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
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.ImportAssertions;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

class GherkinImporterTest
{
    private static final GherkinImporterFactory FACTORY = new GherkinImporterFactory();

    // [utest->dsn~gherkin.streaming-import~1]
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
                () -> assertThat(item.getLocation().getLine(), is(5)),
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
    @Test
    void testIgnoresScenarioWithoutOftMetadata()
    {
        final List<SpecificationItem> items = importText("""
                Feature: login
                Scenario: ordinary scenario
                  Given nothing
                """);

        assertThat(items, is(empty()));
    }

    // [utest->dsn~gherkin.streaming-import~1]
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
    @Test
    void testRejectsDirectiveWithoutId()
    {
        final String source = """
                @ordinary
                # Needs: dsn
                Scenario: Login
                """;

        final ImporterException exception = assertThrows(ImporterException.class, () -> importText(source));

        assertThat(exception.getMessage(), equalTo(
                "Error processing line specification.feature:2 '# Needs: dsn': specification.feature:2: Needs directive requires exactly one preceding @id tag"));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testIgnoresDirectivesOutsideAnIdMetadataRegion()
    {
        final List<SpecificationItem> items = importText("""
                # Covers: req~login~1
                Scenario: Login
                """);

        assertThat(items, is(empty()));
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
    @ParameterizedTest
    @MethodSource("invalidMetadata")
    void testRejectsInvalidMetadata(final String source, final String reason)
    {
        final ImporterException exception = assertThrows(ImporterException.class, () -> importText(source));

        assertThat(exception.getMessage(), containsString(reason));
    }

    private static Stream<Arguments> invalidMetadata()
    {
        return Stream.of(
                Arguments.of("""
                        @id:invalid
                        Scenario: Login
                        """, "invalid specification item ID"),
                Arguments.of("""
                        @id:scn~login~1
                        @id:scn~another-login~1
                        Scenario: Login
                        """, "multiple @id tags"),
                Arguments.of("""
                        @id:scn~login~1
                        # Needs: dsn
                        # Needs: itest
                        Scenario: Login
                        """, "repeated Needs directive"),
                Arguments.of("""
                        @id:scn~login~1
                        # Covers:
                        Scenario: Login
                        """, "requires a non-empty list"),
                Arguments.of("""
                        @id:scn~login~1
                        # Covers: req~login~1,
                        Scenario: Login
                        """, "contains an empty value"),
                Arguments.of("""
                        @id:scn~login~1
                        # Covers: req~login~1, req~login~1
                        Scenario: Login
                        """, "contains duplicate value"),
                Arguments.of("""
                        @id:scn~login~1
                        # Needs: invalid-type
                        Scenario: Login
                        """, "invalid artifact type"),
                Arguments.of("""
                        @id:scn~login~1
                        # Needs: dsn, dsn
                        Scenario: Login
                        """, "contains duplicate value"),
                Arguments.of("""
                        @id:scn~login~1
                        Scenario: Login
                        Feature: Another feature
                        @id:scn~login~1
                        Scenario: Login again
                        """, "duplicate Gherkin ID"));
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
