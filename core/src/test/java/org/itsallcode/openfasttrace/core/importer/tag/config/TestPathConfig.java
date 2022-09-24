package org.itsallcode.openfasttrace.core.importer.tag.config;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.junit.jupiter.api.Test;

class TestPathConfig
{
    @Test
    void testGlobMatches()
    {
        assertMatches("glob:pattern", "pattern", true);
    }

    @Test
    void testGlobUsedByDefault()
    {
        assertMatches("**/pattern", "blah/blubb/pattern", true);
    }

    @Test
    void testGlobWithStarStarMatches()
    {
        assertMatches("glob:**/pattern", "blah/blubb/pattern", true);
        assertMatches("glob:**/pattern", "/pattern", true);
        assertMatches("glob:**/pattern", "pattern", false);
        assertMatches("glob:**/pattern", "attern", false);
    }

    @Test
    void testGlobDoesNotMatch()
    {
        assertMatches("glob:pattern", "wrongpattern", false);
    }

    @Test
    void testLiteralMatches()
    {
        assertMatches("regex:pattern", "pattern", true);
    }

    @Test
    void testRegexpWithWildCardMatches()
    {
        assertMatches("regex:.*pattern", "pattern", true);
        assertMatches("regex:.*pattern", "blubbpattern", true);
    }

    @Test
    void testRegexDoesNotMatch()
    {
        assertMatches("regex:pattern", "wrongpattern", false);
    }

    @Test
    void testGetPatternWithoutPrefix()
    {
        assertThat(create("pattern").getDescription(), equalTo("glob:pattern"));
    }

    @Test
    void testGetPatternWithGlobPrefix()
    {
        assertThat(create("glob:pattern").getDescription(), equalTo("glob:pattern"));
    }

    @Test
    void testGetPatternWithRegexPrefix()
    {
        assertThat(create("regex:pattern").getDescription(), equalTo("regex:pattern"));
    }

    @Test
    void testBuilder()
    {
        final List<Path> paths = new ArrayList<>();
        final String pattern = "pattern";
        final String coveredItemArtifactType = "coveredItemArtifactType";
        final String coveredItemNamePrefix = "coveredItemNamePrefix";
        final String tagArtifactType = "tagArtifactType";
        final PathConfig config = PathConfig.builder().pathListMatcher(paths)
                .patternPathMatcher(pattern).coveredItemArtifactType(coveredItemArtifactType)
                .coveredItemNamePrefix(coveredItemNamePrefix).tagArtifactType(tagArtifactType)
                .build();
        assertThat(config.getCoveredItemArtifactType(), equalTo(coveredItemArtifactType));
        assertThat(config.getCoveredItemNamePrefix(), equalTo(coveredItemNamePrefix));
        assertThat(config.getDescription(), equalTo("glob:" + pattern));
        assertThat(config.getTagArtifactType(), equalTo(tagArtifactType));
    }

    private void assertMatches(final String pattern, final String path, final boolean expected)
    {
        final InputFile file = RealFileInput.forPath(Paths.get(path));
        assertThat(create(pattern).matches(file), equalTo(expected));
    }

    private PathConfig create(final String pattern)
    {
        return PathConfig.builder() //
                .patternPathMatcher(pattern) //
                .coveredItemArtifactType("") //
                .tagArtifactType("") //
                .build();
    }
}
