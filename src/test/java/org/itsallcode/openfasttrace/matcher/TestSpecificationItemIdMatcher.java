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
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.junit.Test;

/**
 * Unit test for {@link SpecificationItemIdMatcher}
 */
public class TestSpecificationItemIdMatcher extends MatcherTestBase<SpecificationItemId>
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

    private org.itsallcode.openfasttrace.core.SpecificationItemId.Builder baseBuilder()
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
}
