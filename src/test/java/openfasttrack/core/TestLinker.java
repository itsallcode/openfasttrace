package openfasttrack.core;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import static openfasttrack.core.SampleArtifactTypes.DSN;
import static openfasttrack.core.SampleArtifactTypes.IMPL;
import static openfasttrack.core.SampleArtifactTypes.OMAN;
import static openfasttrack.core.SampleArtifactTypes.REQ;
import static openfasttrack.core.SampleArtifactTypes.UMAN;
import static openfasttrack.core.SpecificationItemBuilders.prepare;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestLinker
{
    private static final SpecificationItem A;
    private static final SpecificationItem B;
    private static final SpecificationItem C;
    private static final SpecificationItem D;
    private static final SpecificationItem E;
    private static final SpecificationItem F;
    private static final SpecificationItem G;

    static
    {
        A = prepare(REQ, "A", 2).addNeedsArtifactType(DSN).addNeedsArtifactType(OMAN).build();
        B = prepare(DSN, "B", 2).addCoveredId(A.getId()).build();
        C = prepare(DSN, "D", 2).addCoveredId(SpecificationItemId.createId(REQ, "A", 1)).build();
        D = prepare(DSN, "E", 2).addCoveredId(SpecificationItemId.createId(REQ, "A", 3)).build();
        E = prepare(IMPL, "C", 2).addCoveredId(A.getId()).build();
        F = prepare(REQ, "F", 2).build();
        G = prepare(DSN, "G", 2).addNeedsArtifactType(UMAN).build();
    }

    // @formatter:off
    private static final int[][] LINK_MATRIX =
        {
                // CVS | OUT | PRE | UNW | CVS | COU | CPR | CUN
                {    0 ,   0 ,   0 ,   0 ,   1 ,   1 ,   1 ,   1 },
                {    1 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   1 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   0 ,   1 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   0 ,   0 ,   1 ,   0 ,   0 ,   0 ,   0 },
        };
    // @formatter:on

    private Linker linker;
    private List<SpecificationItem> items;
    private List<LinkedSpecificationItem> linkedItems;

    @Before
    public void prepareTest()
    {
        this.items = Arrays.asList(A, B, C, D, E, F, G);
        this.linker = new Linker(this.items);
        this.linkedItems = this.linker.link();
    }

    @Test
    public void testItemInCountEqualsItemOutCount()
    {
        assertThat(this.linkedItems, hasSize(this.items.size()));
    }

    // [utest->dsn~tracing.needed-coverage-status~1]
    // [utest->dsn~tracing.outgoing-coverage-link-status~1]
    // [utest->dsn~tracing.incoming-coverage-link-status~1]]
    @Test
    public void testLinkStatus()
    {
        final Iterator<LinkedSpecificationItem> iterator = this.linkedItems.iterator();
        for (final int[] expectedCounts : LINK_MATRIX)
        {
            final LinkedSpecificationItem item = iterator.next();
            assertItemLinkStatusCount(item, LinkStatus.COVERS, expectedCounts[0]);
            assertItemLinkStatusCount(item, LinkStatus.OUTDATED, expectedCounts[1]);
            assertItemLinkStatusCount(item, LinkStatus.PREDATED, expectedCounts[2]);
            assertItemLinkStatusCount(item, LinkStatus.UNWANTED, expectedCounts[3]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_SHALLOW, expectedCounts[4]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_OUTDATED, expectedCounts[5]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_PREDATED, expectedCounts[6]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_UNWANTED, expectedCounts[7]);
        }
    }

    private void assertItemLinkStatusCount(final LinkedSpecificationItem item,
            final LinkStatus status, final int count)
    {
        final String message = item.getId().toString() + " must have " + count
                + " links with status " + status.toString();
        assertThat(message, item.getLinksByStatus(status), hasSize(count));
    }

    @Test
    public void testGetCoveredArtifactTypes()
    {
        assertThat(this.linkedItems.get(0).getCoveredArtifactTypes(), containsInAnyOrder(DSN));
        assertThat(this.linkedItems.get(1).getCoveredArtifactTypes(), empty());
    }
}