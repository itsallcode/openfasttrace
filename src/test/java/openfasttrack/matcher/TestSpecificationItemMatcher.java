package openfasttrack.matcher;

import org.hamcrest.Matcher;
import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItem.Builder;
import openfasttrack.core.SpecificationItemId;

/**
 * Unit test for {@link SpecificationItemMatcher}
 */
public class TestSpecificationItemMatcher extends MatcherTest<SpecificationItem>
{
    private final static SpecificationItemId ID1 = new SpecificationItemId.Builder()
            .artifactType("artifactType").name("name").revision(42).build();
    private final static SpecificationItemId ID2 = new SpecificationItemId.Builder()
            .artifactType("artifactType2").name("name2").revision(42 + 2).build();
    private final static SpecificationItemId ID3 = new SpecificationItemId.Builder()
            .artifactType("artifactType3").name("name3").revision(42 + 3).build();
    private final static SpecificationItemId ID4 = new SpecificationItemId.Builder()
            .artifactType("artifactType4").name("name4").revision(42 + 4).build();

    @Test
    public void testEmptyObjectMatches()
    {
        assertMatch(new SpecificationItem.Builder().id(ID1).build());
    }

    @Test
    public void testFilledObjectMatches()
    {
        assertMatch(baseBuilder().build());
    }

    @Test
    public void testFilledObjectMultipleIdsMatches()
    {
        assertMatch(baseBuilder().addCoveredId(ID4).addDependOnId(ID4).addNeedsArtifactType("ID4")
                .build());
    }

    @Test
    public void testDifferentEmptyIdLists()
    {
        assertDifferentFromBase(new SpecificationItem.Builder().id(ID1));
    }

    @Test
    public void testDifferentCoveredId()
    {
        assertDifferentFromBase(baseBuilder().addCoveredId(ID4));
    }

    @Test
    public void testDifferentDependOnId()
    {
        assertDifferentFromBase(baseBuilder().addDependOnId(ID4));
    }

    @Test
    public void testDifferentNeededArtifactType()
    {
        assertDifferentFromBase(baseBuilder().addNeedsArtifactType("ID4"));
    }

    @Test
    public void testDifferentId()
    {
        assertDifferentFromBase(baseBuilder().id(ID2));
    }

    @Test
    public void testDifferentComment()
    {
        assertDifferentFromBase(baseBuilder().comment("comment2"));
    }

    @Test
    public void testDifferentRationale()
    {
        assertDifferentFromBase(baseBuilder().rationale("rationale2"));
    }

    @Test
    public void testDifferentDescription()
    {
        assertDifferentFromBase(baseBuilder().description("description2"));
    }

    private Builder baseBuilder()
    {
        return new SpecificationItem.Builder().id(ID1).addCoveredId(ID2).addDependOnId(ID3)
                .addNeedsArtifactType("neededType").comment("comment").description("description")
                .rationale("rationale");
    }

    private void assertDifferentFromBase(final SpecificationItem.Builder differentBuilder)
    {
        assertNoMatch(baseBuilder().build(), differentBuilder.build());
    }

    @Override
    protected Matcher<? super SpecificationItem> createMatcher(final SpecificationItem object)
    {
        return SpecificationItemMatcher.equalTo(object);
    }

    @Override
    protected Class<SpecificationItem> getType()
    {
        return SpecificationItem.class;
    }
}
