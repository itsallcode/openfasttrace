package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// [utest->dsn~linked-specification-item~1]
public class TestLinkedSpecificationItem
{
    private LinkedSpecificationItem linkedItem;
    private LinkedSpecificationItem coveredLinkedItem;
    private LinkedSpecificationItem otherLinkedItem;

    @Mock
    private SpecificationItem itemMock, coveredItemMock, otherItemMock;

    @Before
    public void prepareAllTests()
    {
        MockitoAnnotations.initMocks(this);
        this.linkedItem = new LinkedSpecificationItem(this.itemMock);
        this.coveredLinkedItem = new LinkedSpecificationItem(this.coveredItemMock);
        this.otherLinkedItem = new LinkedSpecificationItem(this.otherItemMock);
        when(this.itemMock.getStatus()).thenReturn(ItemStatus.APPROVED);
        when(this.itemMock.getId()).thenReturn(SpecificationItemId.parseId("a~item~1"));
        when(this.coveredItemMock.getId()).thenReturn(SpecificationItemId.parseId("a~covered~1"));
        when(this.otherItemMock.getId()).thenReturn(SpecificationItemId.parseId("a~other~1"));
    }

    @Test
    public void testCreateLinkedSpecificationItem()
    {
        assertThat(this.linkedItem.getItem(), equalTo(this.itemMock));
    }

    @Test
    public void testLinkWithStatus()
    {
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.getLinksByStatus(LinkStatus.COVERS),
                containsInAnyOrder(this.linkedItem));
    }

    @Test
    public void testGetCoveredArtifactTypes()
    {
        this.linkedItem.addCoveredArtifactType(UMAN);
        this.linkedItem.addCoveredArtifactType(REQ);
        assertThat(this.linkedItem.getCoveredArtifactTypes(), containsInAnyOrder(UMAN, REQ));
    }

    @Test
    public void testGetUncoveredArtifactTypes()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, REQ));
        this.linkedItem.addCoveredArtifactType(UMAN);
        assertThat(this.linkedItem.getUncoveredArtifactTypes(), containsInAnyOrder(REQ));
    }

    @Test
    public void testGetOverCoveredArtifactTypes()
    {
        this.linkedItem.addOverCoveredArtifactType(REQ);
        assertThat(this.linkedItem.getOverCoveredArtifactTypes(), containsInAnyOrder(REQ));
    }

    @Test
    public void testIsCoveredShallow_Ok()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        this.linkedItem.addCoveredArtifactType(UMAN);
        this.linkedItem.addCoveredArtifactType(IMPL);
        assertThat(this.linkedItem.isCoveredShallow(), equalTo(true));
    }

    @Test
    public void testIsCoveredShallow_NotOk_WrongCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        this.linkedItem.addCoveredArtifactType(UMAN);
        this.linkedItem.addCoveredArtifactType(REQ);
        assertThat(this.linkedItem.isCoveredShallow(), equalTo(false));
    }

    @Test
    public void testIsCoveredShallow_NotOk_MissingCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        this.linkedItem.addCoveredArtifactType(UMAN);
        assertThat(this.linkedItem.isCoveredShallow(), equalTo(false));
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    public void testGetDeepCoverageStatus_Covered()
    {
        prepareCoverThis();
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.COVERED));
    }

    private void prepareCoverThis()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        this.linkedItem.addCoveredArtifactType(DSN);
        when(this.coveredItemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL));
        this.coveredLinkedItem.addCoveredArtifactType(IMPL);
        this.coveredLinkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERED_SHALLOW);
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERS);
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    public void testGetDeepCoverageStatus_MissingCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        this.linkedItem.addCoveredArtifactType(DSN);
        when(this.coveredItemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL, UMAN));
        this.coveredLinkedItem.addCoveredArtifactType(IMPL);
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.UNCOVERED));
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    public void testIsDefect_False()
    {
        prepareCoverThis();
        prepareCoverOther();
        assertThat(this.linkedItem.isDefect(), equalTo(false));
    }

    private void prepareCoverOther()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.COVERS);
        this.coveredLinkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERED_SHALLOW);
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    public void testIsDefect_TrueBecauseOfDuplicates()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.DUPLICATE);
        assertThat(this.linkedItem.isDefect(), equalTo(true));
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    public void testIsDefect_TrueBecauseOfBadLink()
    {
        for (final LinkStatus status : LinkStatus.values())
        {
            final LinkedSpecificationItem item = new LinkedSpecificationItem(this.itemMock);
            item.addLinkToItemWithStatus(this.otherLinkedItem, status);
            final boolean expected = (status != LinkStatus.COVERS)
                    && (status != LinkStatus.COVERED_SHALLOW);
            assertThat("for " + status, item.isDefect(), equalTo(expected));
        }
    }

    @Test
    // [utest->dsn~tracing.defect-items~2]
    public void testIsDefect_FalseBecauseRejected()
    {
        final LinkedSpecificationItem item = new LinkedSpecificationItem(this.itemMock);
        when(this.itemMock.getStatus()).thenReturn(ItemStatus.REJECTED);
        item.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.COVERED_OUTDATED);
        assertThat(item.isDefect(), equalTo(false));
    }

    @Test
    public void testCountOutgoingLinks()
    {
        linkToNewItemsWithStatus(LinkStatus.COVERS, LinkStatus.PREDATED, LinkStatus.OUTDATED,
                LinkStatus.UNWANTED, LinkStatus.ORPHANED, LinkStatus.AMBIGUOUS);

        assertThat(this.linkedItem.countOutgoingLinks(), equalTo(6));
        assertThat(this.linkedItem.countOutgoingBadLinks(), equalTo(5));
    }

    private void linkToNewItemsWithStatus(final LinkStatus... status)
    {
        for (final LinkStatus linkStatus : status)
        {
            linkToNewItemWithStatus(linkStatus);
        }
    }

    private void linkToNewItemWithStatus(final LinkStatus status)
    {
        final SpecificationItem item = mock(SpecificationItem.class);
        this.linkedItem.addLinkToItemWithStatus(new LinkedSpecificationItem(item), status);
    }

    @Test
    public void testCountIncomingLinks()
    {
        linkToNewItemsWithStatus(LinkStatus.COVERED_SHALLOW, LinkStatus.COVERED_PREDATED,
                LinkStatus.COVERED_OUTDATED, LinkStatus.COVERED_UNWANTED);

        assertThat(this.linkedItem.countIncomingLinks(), equalTo(4));
        assertThat(this.linkedItem.countIncomingBadLinks(), equalTo(3));
    }

    @Test
    public void testCountDuplicateLinks()
    {
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERS);
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.DUPLICATE);
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.DUPLICATE);
        assertThat(this.linkedItem.countDuplicateLinks(), equalTo(2));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    public void testGetDeepCoverageStatus_CylceIfSelfLink()
    {
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.CYCLE));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    public void testGetDeepCoverageStatus_DeepCylce()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.COVERS);
        this.otherLinkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.CYCLE));
    }
}
