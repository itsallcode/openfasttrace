package openfasttrack.importer.legacytag;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

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
    public void testGlobDoesntMatches()
    {
        assertMatches("glob:pattern", "wrongpattern", false);
    }

    @Test
    public void testRegexpMatches()
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
    public void testRegexDoesntMatches()
    {
        assertMatches("regex:pattern", "wrongpattern", false);
    }

    @Test
    public void testGetPatternWithoutPrefix()
    {
        assertThat(create("pattern").getPattern(), equalTo("pattern"));
    }

    @Test
    public void testGetPatternWithGlobPrefix()
    {
        assertThat(create("glob:pattern").getPattern(), equalTo("glob:pattern"));
    }

    @Test
    public void testGetPatternWithRegexPrefix()
    {
        assertThat(create("regex:pattern").getPattern(), equalTo("regex:pattern"));
    }

    private void assertMatches(final String pattern, final String path, final boolean expected)
    {
        assertThat(create(pattern).matches(Paths.get(path)), equalTo(expected));
    }

    private PathConfig create(final String pattern)
    {
        return new PathConfig(pattern);
    }
}
