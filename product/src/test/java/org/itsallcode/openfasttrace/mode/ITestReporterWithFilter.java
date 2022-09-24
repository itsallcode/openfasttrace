package org.itsallcode.openfasttrace.mode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ITestReporterWithFilter extends AbstractFileBasedTest
{
    public static final String SPECIFICATION = String.join(System.lineSeparator(), //
            "`feat~a~1`", //
            "I am a feature", //
            "`req~b~2`", //
            "A user requirement", //
            "Tags: tag1, tag2", //
            "`dsn~c~3`", //
            "Design", //
            "Tags: tag1, tag3", //
            "`impl~d~4`", //
            "An implementation", //
            "Tags: tag2, tag3", //
            "`utest~e~5`", //
            "A unit test", //
            "Tags: tag3");

    private Oft oft;
    private Path tempDir;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) throws IOException
    {
        this.tempDir = tempDir;
        writeTextFile(tempDir.resolve("spec.md").toFile(), SPECIFICATION);
        this.oft = Oft.create();
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTag()
    {
        final FilterSettings filterSettings = FilterSettings.builder() //
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2"))) //
                .withoutTags(false) //
                .build();
        final List<String> filteredIds = getIdsFromTraceWithFilterSettings(filterSettings);
        assertThat(filteredIds, containsInAnyOrder("req~b~2", "dsn~c~3", "impl~d~4"));
    }

    private List<String> getIdsFromTraceWithFilterSettings(final FilterSettings filterSettings)
    {
        final ImportSettings importSettings = ImportSettings.builder() //
                .addInputs(this.tempDir) //
                .filter(filterSettings) //
                .build();
        final List<SpecificationItem> items = this.oft.importItems(importSettings);
        final List<LinkedSpecificationItem> linkedItems = this.oft.link(items);
        final Trace trace = this.oft.trace(linkedItems);
        final List<String> filteredIds = trace.getItems() //
                .stream() //
                .map(item -> item.getId().toString()) //
                .collect(Collectors.toList());
        return filteredIds;
    }

    // [itest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    void testFilterWithAtLeastOneMatchingTagOrNoTags()
    {
        final FilterSettings filterSettings = FilterSettings.builder() //
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2"))) //
                .build();
        final List<String> filteredIds = getIdsFromTraceWithFilterSettings(filterSettings);
        assertThat(filteredIds, containsInAnyOrder("feat~a~1", "req~b~2", "dsn~c~3", "impl~d~4"));
    }
}