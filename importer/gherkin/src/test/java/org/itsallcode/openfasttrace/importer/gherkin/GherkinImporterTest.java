package org.itsallcode.openfasttrace.importer.gherkin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.ImportAssertions;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

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
        assertThat(item.getDescription(), is("Given a registered user\n  When they log in\n  Then access is granted"));
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

    // [utest->dsn~gherkin.comment-coverage-tags~1]
    @Test
    void testImportsCommentCoverageTagsButIgnoresExecutableCoverageTags() {
        final String source = """
                # [""" + "impl~gherkin-comment~1 -> dsn~gherkin~1]\n" + """
                Scenario: ordinary
                  Given [""" + "impl~gherkin-executable~1 -> dsn~gherkin~1]\n";
        final List<SpecificationItem> items = importText(source);

        assertThat(items, contains(hasProperty("id", hasToString("impl~gherkin-comment~1"))));
    }

    private static List<SpecificationItem> importText(final String source) {
        return ImportAssertions.runImporterOnText(Path.of("specification.feature"), source, FACTORY);
    }
}
