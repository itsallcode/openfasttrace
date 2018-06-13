package org.itsallcode.openfasttrace.importer.legacytag.config;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Test;

public class TestPathConfig
{
    @Test
    public void testGlobMatches()
    {
        assertMatches("glob:pattern", "pattern", true);
    }

    @Test
    public void testGlobUsedByDefault()
    {
        assertMatches("**/pattern", "blah/blubb/pattern", true);
    }

    @Test
    public void testGlobWithStarStarMatches()
    {
        assertMatches("glob:**/pattern", "blah/blubb/pattern", true);
        assertMatches("glob:**/pattern", "/pattern", true);
        assertMatches("glob:**/pattern", "pattern", false);
        assertMatches("glob:**/pattern", "attern", false);
    }

    @Test
    public void testGlobDoesNotMatch()
    {
        assertMatches("glob:pattern", "wrongpattern", false);
    }

    @Test
    public void testLiteralMatches()
    {
        assertMatches("regex:pattern", "pattern", true);
    }

    @Test
    public void testRegexpWithWildCardMatches()
    {
        assertMatches("regex:.*pattern", "pattern", true);
        assertMatches("regex:.*pattern", "blubbpattern", true);
    }

    @Test
    public void testRegexDoesNotMatch()
    {
        assertMatches("regex:pattern", "wrongpattern", false);
    }

    @Test
    public void testGetPatternWithoutPrefix()
    {
        assertThat(create("pattern").getDescription(), equalTo("glob:pattern"));
    }

    @Test
    public void testGetPatternWithGlobPrefix()
    {
        assertThat(create("glob:pattern").getDescription(), equalTo("glob:pattern"));
    }

    @Test
    public void testGetPatternWithRegexPrefix()
    {
        assertThat(create("regex:pattern").getDescription(), equalTo("regex:pattern"));
    }

    private void assertMatches(final String pattern, final String path, final boolean expected)
    {
        final InputFile file = InputFile.forPath(Paths.get(path));
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
