package org.itsallcode.openfasttrace.importer.gherkin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.ImportAssertions;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GherkinImporterTest {
    private static final GherkinImporterFactory FACTORY = new GherkinImporterFactory();

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testImportsScenarioOutlineWithScopedMetadataAndSteps() {
        final String source = """
                @smoke
                @id:scn~account-login~1
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

        assertThat(items, contains(
                hasProperty("id", hasToString("scn~account-login~1"))));
        final SpecificationItem item = items.get(0);
        assertThat(item.getTitle(), is("Login works"));
        assertThat(item.getLocation().getLine(), is(5));
        assertThat(item.getDescription(), is(String.join(System.lineSeparator(),
                "Given a registered user", "  When they log in", "  Then access is granted")));
        assertThat(item.getCoveredIds(), contains(hasToString("req~login~1")));
        assertThat(item.getNeedsArtifactTypes(), containsInAnyOrder("dsn", "itest"));
    }

    // [utest->dsn~gherkin.importer-selection~1]
    @Test
    void testFactorySupportsFeatureFilesWithHigherPrecedenceThanTagImporter() {
        final InputFile file = StreamInput.forReader(Path.of("specification.feature"),
                new java.io.BufferedReader(new java.io.StringReader("")));

        assertThat(FACTORY.supportsFile(file), is(true));
        assertThat(FACTORY.getPriority(), is(9000));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testIgnoresScenarioWithoutOftMetadata() {
        final List<SpecificationItem> items = importText("""
                Feature: login
                Scenario: ordinary scenario
                  Given nothing
                """);

        assertThat(items, is(empty()));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testImportsMultipleCoversDirectives() {
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
    void testRejectsDirectiveWithoutId() {
        final String source = """
                @ordinary
                # Needs: dsn
                Scenario: Login
                """;

        final ImporterException exception = assertThrows(ImporterException.class, () -> importText(source));

        assertThat(exception.getMessage(), hasToString(org.hamcrest.Matchers.containsString("requires exactly one")));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testIgnoresDirectivesOutsideAnIdMetadataRegion() {
        final List<SpecificationItem> items = importText("""
                # Covers: req~login~1
                Scenario: Login
                """);

        assertThat(items, is(empty()));
    }

    // [utest->dsn~gherkin.streaming-import~1]
    @Test
    void testKeepsMetadataWhenAnUnrelatedCommentPrecedesTheScenario() {
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
    void testRejectsInvalidMetadata(final String source, final String reason) {
        final ImporterException exception = assertThrows(ImporterException.class, () -> importText(source));

        assertThat(exception.getMessage(), containsString(reason));
    }

    private static Stream<Arguments> invalidMetadata() {
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
    void testImportsCommentCoverageTagsButIgnoresExecutableCoverageTags() {
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

    @Test
    void testEventBufferReplaysBeginItem() { assertEvent(EventBuffer::beginSpecificationItem, ImportEventListener::beginSpecificationItem); }
    @Test void testEventBufferReplaysId() { final SpecificationItemId id = SpecificationItemId.parseId("req~login~1"); assertEvent(b -> b.setId(id), l -> verify(l).setId(id)); }
    @Test void testEventBufferReplaysTitle() { assertEvent(b -> b.setTitle("title"), l -> verify(l).setTitle("title")); }
    @Test void testEventBufferReplaysStatus() { assertEvent(b -> b.setStatus(ItemStatus.DRAFT), l -> verify(l).setStatus(ItemStatus.DRAFT)); }
    @Test void testEventBufferReplaysDescription() { assertEvent(b -> b.appendDescription("description"), l -> verify(l).appendDescription("description")); }
    @Test void testEventBufferReplaysRationale() { assertEvent(b -> b.appendRationale("rationale"), l -> verify(l).appendRationale("rationale")); }
    @Test void testEventBufferReplaysComment() { assertEvent(b -> b.appendComment("comment"), l -> verify(l).appendComment("comment")); }
    @Test void testEventBufferReplaysCoveredId() { final SpecificationItemId id = SpecificationItemId.parseId("req~login~1"); assertEvent(b -> b.addCoveredId(id), l -> verify(l).addCoveredId(id)); }
    @Test void testEventBufferReplaysDependencyId() { final SpecificationItemId id = SpecificationItemId.parseId("req~login~1"); assertEvent(b -> b.addDependsOnId(id), l -> verify(l).addDependsOnId(id)); }
    @Test void testEventBufferReplaysNeededArtifactType() { assertEvent(b -> b.addNeededArtifactType("dsn"), l -> verify(l).addNeededArtifactType("dsn")); }
    @Test void testEventBufferReplaysTag() { assertEvent(b -> b.addTag("tag"), l -> verify(l).addTag("tag")); }
    @Test void testEventBufferReplaysPathLocation() { assertEvent(b -> b.setLocation("file.feature", 1), l -> verify(l).setLocation("file.feature", 1)); }
    @Test void testEventBufferReplaysLocation() { final Location location = Location.create("file.feature", 2); assertEvent(b -> b.setLocation(location), l -> verify(l).setLocation(location)); }
    @Test void testEventBufferReplaysForwards() { assertEvent(b -> b.setForwards(true), l -> verify(l).setForwards(true)); }
    @Test void testEventBufferReplaysEndItem() { assertEvent(EventBuffer::endSpecificationItem, ImportEventListener::endSpecificationItem); }

    private static void assertEvent(final Consumer<EventBuffer> addEvent,
            final Consumer<ImportEventListener> verifyEvent) {
        final EventBuffer buffer = new EventBuffer();
        final ImportEventListener listener = mock(ImportEventListener.class);
        addEvent.accept(buffer);
        buffer.replay(listener);
        verifyEvent.accept(listener);
    }

    private static List<SpecificationItem> importText(final String source) {
        return ImportAssertions.runImporterOnText(Path.of("specification.feature"), source, FACTORY);
    }
}
