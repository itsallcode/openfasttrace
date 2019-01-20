package org.itsallcode.openfasttrace.mode;

import static org.hamcrest.MatcherAssert.assertThat;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.importer.ImportSettings;
import org.itsallcode.openfasttrace.testutil.AbstractFileBasedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

@ExtendWith(TempDirectory.class)
public class ITestReporterWithFilter extends AbstractFileBasedTest
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
        final FilterSettings filterSettings = new FilterSettings.Builder() //
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
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .tags(new HashSet<>(Arrays.asList("tag1", "tag2"))) //
                .build();
        final List<String> filteredIds = getIdsFromTraceWithFilterSettings(filterSettings);
        assertThat(filteredIds, containsInAnyOrder("feat~a~1", "req~b~2", "dsn~c~3", "impl~d~4"));
    }
}