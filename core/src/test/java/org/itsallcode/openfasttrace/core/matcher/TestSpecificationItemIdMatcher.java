package org.itsallcode.openfasttrace.core.matcher;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.testutil.matcher.MatcherTestBase;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link SpecificationItemIdMatcher}
 */
class TestSpecificationItemIdMatcher extends MatcherTestBase<SpecificationItemId>
{
    @Test
    void testMatches()
    {
        assertMatch(baseBuilder().build());
    }

    @Test
    void testDifferentArtifactType()
    {
        assertDifferentFromBase(baseBuilder().artifactType("artifactType2"));
    }

    @Test
    void testDifferentName()
    {
        assertDifferentFromBase(baseBuilder().name("name2"));
    }

    @Test
    void testDifferentRevision()
    {
        assertDifferentFromBase(baseBuilder().revision(43));
    }

    private org.itsallcode.openfasttrace.api.core.SpecificationItemId.Builder baseBuilder()
    {
        return new SpecificationItemId.Builder().artifactType("artifactType").name("name")
                .revision(42);
    }

    private void assertDifferentFromBase(final SpecificationItemId.Builder differentBuilder)
    {
        assertNoMatch(baseBuilder().build(), differentBuilder.build());
    }

    @Override
    protected Matcher<SpecificationItemId> createMatcher(final SpecificationItemId object)
    {
        return SpecificationItemIdMatcher.equalTo(object);
    }
}
