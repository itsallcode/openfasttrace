package org.itsallcode.openfasttrace.mode;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.Reporter;
import org.itsallcode.openfasttrace.core.Trace;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ITestReporterWithFilter
{
    public static final String SPECIFICATION = String.join(System.lineSeparator(), //
            "`feat~a~1`", //
            "", //
            "I am a feature", //
            "", //
            "`req~b~2`", //
            "", //
            "A user requirement", //
            "", //
            "Tags: tag1, tag2", //
            "", //
            "`dsn~c~3`", //
            "", //
            "Design", //
            "", //
            "Tags: tag1, tag3", //
            "", //
            "`impl~d~4`", //
            "", //
            "An implementation", //
            "", //
            "Tags: tag2, tag3", //
            "", //
            "`utest~e~5`", //
            "", //
            "A unit test", //
            "", //
            "Tags: tag3");

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Reporter reporter;

    @Before
    public void before() throws IOException
    {
        final File specification = this.tempFolder.newFile("spec.md");
        final PrintWriter writer = new PrintWriter(specification);
        writer.print(SPECIFICATION);
        writer.flush();
        writer.close();
        this.reporter = new ReportMode();
        this.reporter.addInputs(this.tempFolder.getRoot().toPath());
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testFilterWithAtLeastOneMatchingTag()
    {
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2"))) //
                .withoutTags(false) //
                .build();
        final List<String> filteredIds = getIdsFromTraceWithFilterSettings(filterSettings);
        assertThat(filteredIds, containsInAnyOrder("req~b~2", "dsn~c~3", "impl~d~4"));
    }

    private List<String> getIdsFromTraceWithFilterSettings(final FilterSettings filterSettings)
    {
        final Trace trace = this.reporter //
                .setFilters(filterSettings) //
                .trace();
        final List<String> filteredIds = trace.getItems() //
                .stream() //
                .map(item -> item.getId().toString()) //
                .collect(Collectors.toList());
        return filteredIds;
    }

    // [itest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    public void testFilterWithAtLeastOneMatchingTagOrNoTags()
    {
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2"))) //
                .build();
        final List<String> filteredIds = getIdsFromTraceWithFilterSettings(filterSettings);
        assertThat(filteredIds, containsInAnyOrder("feat~a~1", "req~b~2", "dsn~c~3", "impl~d~4"));

    }
}