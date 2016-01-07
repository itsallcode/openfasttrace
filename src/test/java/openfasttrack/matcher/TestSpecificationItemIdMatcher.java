package openfasttrack.matcher;

import org.hamcrest.Matcher;
import org.junit.Test;

import openfasttrack.core.SpecificationItemId;

/**
 * Unit test for {@link SpecificationItemIdMatcher}
 */
public class TestSpecificationItemIdMatcher extends MatcherTest<SpecificationItemId>
{
    @Test
    public void testMatches()
    {
        assertMatch(baseBuilder().build());
    }

    @Test
    public void testDifferentArtifactType()
    {
        assertDifferentFromBase(baseBuilder().artifactType("artifactType2"));
    }

    @Test
    public void testDifferentName()
    {
        assertDifferentFromBase(baseBuilder().name("name2"));
    }

    @Test
    public void testDifferentRevision()
    {
        assertDifferentFromBase(baseBuilder().revision(43));
    }

    private openfasttrack.core.SpecificationItemId.Builder baseBuilder()
    {
        return new SpecificationItemId.Builder().artifactType("artifactType").name("name")
                .revision(42);
    }

    private void assertDifferentFromBase(final SpecificationItemId.Builder differentBuilder)
    {
        assertNoMatch(baseBuilder().build(), differentBuilder.build());
    }

    @Override
    protected Matcher<? super SpecificationItemId> createMatcher(final SpecificationItemId object)
    {
        return SpecificationItemIdMatcher.equalTo(object);
    }

    @Override
    protected Class<SpecificationItemId> getType()
    {
        return SpecificationItemId.class;
    }
}
