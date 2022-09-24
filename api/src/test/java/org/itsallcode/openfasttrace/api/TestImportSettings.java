package org.itsallcode.openfasttrace.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterableOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.junit.jupiter.api.Test;

/**
 * This class implements a parameter object to control the settings of OFT's
 * export mode.
 */
class TestImportSettings
{
    @Test
    void testInputListEmptyByDefault()
    {
        assertThat(ImportSettings.createDefault().getInputs(), emptyIterableOf(Path.class));
    }

    @Test
    void testDefaultFilter()
    {
        assertThat(ImportSettings.createDefault().getFilters(),
                equalTo(FilterSettings.createAllowingEverything()));
    }

    @Test
    void testDefaultPathConfigs()
    {
        assertThat(ImportSettings.createDefault().getPathConfigs(),
                emptyIterableOf(PathConfig.class));
    }

    @Test
    void testAddInputList()
    {
        final Path[] expectedInputs = { Paths.get("/a/path"), Paths.get("/another/path") };
        assertThat(ImportSettings.builder().addInputs(Arrays.asList(expectedInputs)).build()
                .getInputs(), containsInAnyOrder(expectedInputs));
    }

    @Test
    void testAddInputArray()
    {
        final Path[] expectedInputs = { Paths.get("/a/path"), Paths.get("/another/path") };
        assertThat(ImportSettings.builder().addInputs(expectedInputs).build().getInputs(),
                containsInAnyOrder(expectedInputs));
    }

    @Test
    void testBuildWithFilter()
    {
        final String[] expectedTags = { "a", "b" };
        final FilterSettings filter = FilterSettings.builder()
                .tags(new HashSet<>(Arrays.asList(expectedTags))).build();
        assertThat(ImportSettings.builder().filter(filter).build().getFilters().getTags(),
                containsInAnyOrder(expectedTags));
    }

    @Test
    void testBuildWithPathConfigs()
    {
        final List<PathConfig> expectedPathConfigs = new ArrayList<>();
        final PathConfig.Builder builder = PathConfig.builder();
        final PathConfig expectedFirstPathConfig = builder.patternPathMatcher("src")
                .coveredItemArtifactType("a1").coveredItemNamePrefix("b1").tagArtifactType("c1")
                .build();
        expectedPathConfigs.add(expectedFirstPathConfig);
        assertThat(ImportSettings.builder().pathConfigs(expectedPathConfigs).build()
                .getPathConfigs().get(0), equalTo(expectedFirstPathConfig));
    }
}