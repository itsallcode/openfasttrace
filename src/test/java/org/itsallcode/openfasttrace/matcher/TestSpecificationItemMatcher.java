package org.itsallcode.openfasttrace.matcher;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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


import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.SpecificationItem.Builder;
import org.junit.Test;

/**
 * Unit test for {@link SpecificationItemMatcher}
 */
public class TestSpecificationItemMatcher extends MatcherTestBase<SpecificationItem>
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
}
