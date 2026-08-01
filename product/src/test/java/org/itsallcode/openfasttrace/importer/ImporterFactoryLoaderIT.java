package org.itsallcode.openfasttrace.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ImporterFactoryLoaderIT
{
    @Test
    void testFallBackToTagImporterWhenXmlIsNotSpecObjectFile(@TempDir final Path tempDir) throws IOException
    {
        final Oft oft = Oft.create();
        final String specObjectContent = """
                <specdocument>
                    <specobjects doctype="dsn">
                        <specobject>
                            <id>foobar</id>
                                 <version>1</version>
                            </specobject>
                    </specobjects>
                </specdocument>""";
        Files.write(tempDir.resolve("specobject.xml"), specObjectContent.getBytes());
        final String xmlContent = "<!-- [" + "impl -> dns~foobar~1" + "] -->";
        Files.write(tempDir.resolve("non_specobject.xml"), xmlContent.getBytes());
        final ImportSettings settings = ImportSettings.builder()
                .addInputs(tempDir)
                .filter(FilterSettings.builder().build())
                .build();
        final List<SpecificationItem> items = oft.importItems(settings);
        assertThat(items, containsInAnyOrder(
                hasProperty("id", hasToString("dsn~foobar~1")),
                hasProperty("id", hasToString(startsWith("impl~foobar")))));
    }

    // [itest->dsn~gherkin.importer-selection~1]
    @Test
    void testGherkinImporterAlsoImportsBasicCoverageTags(@TempDir final Path tempDir) throws IOException
    {
        final Oft oft = Oft.create();
        Files.writeString(tempDir.resolve("login.feature"), """
                @id:scn~login~1
                Scenario: Login
                  # [%s]
                  Given [%s]
                """.formatted("impl~login~1 -> dsn~login~1",
                "impl~must-not-be-imported~1 -> dsn~login~1"));
        final ImportSettings settings = ImportSettings.builder()
                .addInputs(tempDir)
                .filter(FilterSettings.builder().build())
                .build();
        final List<SpecificationItem> items = oft.importItems(settings);
        assertThat(items, containsInAnyOrder(
                hasProperty("id", hasToString("scn~login~1")),
                hasProperty("id", hasToString("impl~login~1"))));
    }

    // [itest->dsn~markdown.comment-coverage-tags~1]
    // [itest->dsn~rst.comment-coverage-tags~1]
    @ParameterizedTest
    @ValueSource(strings =
    { "markdown.md", "markdown.markdown", "restructuredtext.rst" })
    void testSelectsLightWeightMarkupImportersBeforeTagImporter(final String fileName, @TempDir final Path tempDir)
            throws IOException
    {
        final Oft oft = Oft.create();
        final String nonCommentTag = "[impl~must-not-be-imported~1"
                + "->req~covered~1]";
        Files.writeString(tempDir.resolve(fileName), """
                %s
                %s
                """.formatted(coverageComment(fileName), nonCommentTag));
        final ImportSettings settings = ImportSettings.builder()
                .addInputs(tempDir)
                .build();

        final List<SpecificationItem> items = oft.importItems(settings);

        assertThat(items, contains(
                allOf(
                        hasProperty("id", hasToString("impl~markdown-comment~1")),
                        hasProperty("coveredIds", contains(hasToString("req~covered~1"))))));
    }

    private static String coverageComment(final String fileName)
    {
        final String coverageTag = "[impl~markdown-comment~1" + "->req~covered~1]";
        return fileName.endsWith(".rst") ? ".. " + coverageTag : "<!-- " + coverageTag + " -->";
    }
}
