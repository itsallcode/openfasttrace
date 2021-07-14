package org.itsallcode.openfasttrace.api.core;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.itsallcode.openfasttrace.api.core.SampleArtifactTypes.*;
import static org.itsallcode.openfasttrace.api.core.SpecificationItemAssertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

// [utest->dsn~linked-specification-item~1]
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestLinkedSpecificationItem
{
    private LinkedSpecificationItem linkedItem;
    private LinkedSpecificationItem coveredLinkedItem;
    private LinkedSpecificationItem otherLinkedItem;

    @Mock
    private SpecificationItem itemMock, coveredItemMock, otherItemMock;

    @BeforeEach
    public void prepareAllTests()
    {
        this.linkedItem = new LinkedSpecificationItem(this.itemMock);
        this.coveredLinkedItem = new LinkedSpecificationItem(this.coveredItemMock);
        this.otherLinkedItem = new LinkedSpecificationItem(this.otherItemMock);
        when(this.itemMock.getStatus()).thenReturn(ItemStatus.APPROVED);
        when(this.itemMock.getId()).thenReturn(SpecificationItemId.parseId("a~item~1"));
        when(this.coveredItemMock.getId()).thenReturn(SpecificationItemId.parseId("a~covered~1"));
        when(this.otherItemMock.getId()).thenReturn(SpecificationItemId.parseId("a~other~1"));
    }

    @Test
    void testCreateLinkedSpecificationItem()
    {
        assertThat(this.linkedItem.getItem(), equalTo(this.itemMock));
    }

    @Test
    void testLinkWithStatus()
    {
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.getLinksByStatus(LinkStatus.COVERS),
                containsInAnyOrder(this.linkedItem));
    }

    @Test
    void testGetCoveredArtifactTypes()
    {
        when(this.coveredItemMock.getArtifactType()).thenReturn(UMAN);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        when(this.coveredItemMock.getArtifactType()).thenReturn(REQ);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemHasCoveredArtifactTypes(this.linkedItem, UMAN, REQ);
    }

    @Test
    void testGetUncoveredArtifactTypes()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, REQ));
        when(this.coveredItemMock.getArtifactType()).thenReturn(UMAN);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemHasUncoveredArtifactTypes(this.linkedItem, REQ);
    }

    @Test
    void testGetOverCoveredArtifactTypes()
    {
        when(this.coveredLinkedItem.getArtifactType()).thenReturn(REQ);
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERED_UNWANTED);
        assertItemHasOvercoveredArtifactTypes(this.linkedItem, REQ);
    }

    @Test
    void testIsCoveredShallow_Ok()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        when(this.coveredItemMock.getArtifactType()).thenReturn(UMAN);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        when(this.coveredItemMock.getArtifactType()).thenReturn(IMPL);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemCoveredShallow(this.linkedItem, true);
    }

    @Test
    void testIsCoveredShallow_NotOk_WrongCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        when(this.coveredItemMock.getArtifactType()).thenReturn(UMAN);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        when(this.coveredItemMock.getArtifactType()).thenReturn(REQ);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemCoveredShallow(this.linkedItem, false);
    }

    @Test
    void testIsCoveredShallow_NotOk_MissingCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(UMAN, IMPL));
        when(this.coveredItemMock.getArtifactType()).thenReturn(UMAN);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemCoveredShallow(this.linkedItem, false);
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    void testGetDeepCoverageStatus_Covered()
    {
        prepareCoverThis();
        assertItemDeepCoverageStatus(this.linkedItem, DeepCoverageStatus.COVERED);
    }

    private void prepareCoverThis()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL));
        when(this.coveredItemMock.getArtifactType()).thenReturn(IMPL);
        this.linkedItem.addLinkToItemWithStatus(coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    void testGetDeepCoverageStatus_MissingCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        when(this.coveredItemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL, UMAN));
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERED_SHALLOW);
        assertItemDeepCoverageStatus(this.linkedItem, DeepCoverageStatus.UNCOVERED);
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    void testIsDefect_False()
    {
        prepareCoverThis();
        assertItemDefect(this.linkedItem, false);
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    void testIsDefect_TrueBecauseOfDuplicates()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.DUPLICATE);
        assertItemDefect(this.linkedItem, true);
    }

    // [utest->dsn~tracing.defect-items~2]
    @Test
    void testIsDefect_TrueBecauseOfBadLink()
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
    void testIsDefect_FalseBecauseRejected()
    {
        final LinkedSpecificationItem item = new LinkedSpecificationItem(this.itemMock);
        when(this.itemMock.getStatus()).thenReturn(ItemStatus.REJECTED);
        item.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.COVERED_OUTDATED);
        assertItemDefect(item, false);
    }

    @Test
    void testCountOutgoingLinks()
    {
        linkToNewItemsWithStatus(LinkStatus.COVERS, LinkStatus.PREDATED, LinkStatus.OUTDATED,
                LinkStatus.UNWANTED, LinkStatus.ORPHANED, LinkStatus.AMBIGUOUS);

        assertAll(() -> assertItemOutgoingLinkCount(this.linkedItem, 6),
                () -> assertItemOutgoingBadLinkCount(this.linkedItem, 5));
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
    void testCountIncomingLinks()
    {
        linkToNewItemsWithStatus(LinkStatus.COVERED_SHALLOW, LinkStatus.COVERED_PREDATED,
                LinkStatus.COVERED_OUTDATED, LinkStatus.COVERED_UNWANTED);

        assertAll(() -> assertItemIncomingLinkCount(this.linkedItem, 4),
                () -> assertItemIncomingBadLinkCount(this.linkedItem, 3));
    }

    @Test
    void testCountDuplicateLinks()
    {
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERS);
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.DUPLICATE);
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.DUPLICATE);
        assertThat(this.linkedItem.countDuplicateLinks(), equalTo(2));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testGetDeepCoverageStatus_CylceIfSelfLink()
    {
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERED_SHALLOW);
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.CYCLE));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testGetDeepCoverageStatus_DeepCycle()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.COVERED_SHALLOW);
        this.otherLinkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.COVERED_SHALLOW);
        assertThat(this.linkedItem.getDeepCoverageStatus(), equalTo(DeepCoverageStatus.CYCLE));
    }

    @Test
    void testGetTitleWithFallback_HasTitle()
    {
        final String expectedReturn = "title";
        when(this.itemMock.getTitle()).thenReturn(expectedReturn);
        when(this.itemMock.getId()).thenReturn(SpecificationItemId.parseId("foo~wrong-return~1"));
        assertThat(this.linkedItem.getTitleWithFallback(), equalTo(expectedReturn));
    }

    @Test
    void testGetTitle()
    {
        when(this.itemMock.getTitle()).thenReturn("title");
        assertThat(this.linkedItem.getTitle(), equalTo("title"));
    }

    @Test
    void testGetTitleWithFallback_HasNoTitle()
    {
        when(this.itemMock.getTitle()).thenReturn(null);
        when(this.itemMock.getId()).thenReturn(SpecificationItemId.parseId("foo~id-name~1"));
        assertThat(this.linkedItem.getTitleWithFallback(), equalTo("id-name"));
    }

    @Test
    void testGetTitleWithFallback_HasEmptyTitle()
    {
        when(this.itemMock.getTitle()).thenReturn("");
        when(this.itemMock.getId()).thenReturn(SpecificationItemId.parseId("foo~id-name~1"));
        assertThat(this.linkedItem.getTitleWithFallback(), equalTo("id-name"));
    }

    @Test
    void testGetTitleWithFallback_HasNothing()
    {
        when(this.itemMock.getTitle()).thenReturn("");
        when(this.itemMock.getId()).thenReturn(null);
        assertThat(this.linkedItem.getTitleWithFallback(), equalTo("???"));
    }

    @Test
    void testGetTracedLinks()
    {
        this.linkedItem.addLinkToItemWithStatus(this.coveredLinkedItem, LinkStatus.COVERS);
        this.linkedItem.addLinkToItemWithStatus(this.linkedItem, LinkStatus.ORPHANED);
        final List<TracedLink> links = this.linkedItem.getTracedLinks();
        final TracedLink expectedLinkA = new TracedLink(this.linkedItem, LinkStatus.ORPHANED);
        final TracedLink expectedLinkB = new TracedLink(this.coveredLinkedItem, LinkStatus.COVERS);
        assertThat(links, containsInAnyOrder(expectedLinkA, expectedLinkB));
    }

    @Test
    void testHasLinks_InitiallyFalse()
    {
        assertThat(this.linkedItem.hasLinks(), equalTo(false));
    }

    @Test
    void testGetLinksForStatus_InitiallyEmpty()
    {
        assertThat(this.linkedItem.getLinksByStatus(LinkStatus.AMBIGUOUS), empty());
    }

    @Test
    void testHasLinks()
    {
        this.linkedItem.addLinkToItemWithStatus(this.otherLinkedItem, LinkStatus.AMBIGUOUS);
        assertThat(this.linkedItem.hasLinks(), equalTo(true));
    }

    @Test
    void testGetArtifactType()
    {
        final String expectedArtifactType = "atype";
        when(this.itemMock.getArtifactType()).thenReturn(expectedArtifactType);
        assertThat(this.linkedItem.getArtifactType(), equalTo(expectedArtifactType));
    }

    @Test
    void testGetName()
    {
        final String expectedName = "name";
        when(this.itemMock.getName()).thenReturn(expectedName);
        assertThat(this.linkedItem.getName(), equalTo(expectedName));
    }

    @Test
    void testGetRevision()
    {
        final int expectedRevision = 1024;
        when(this.itemMock.getRevision()).thenReturn(expectedRevision);
        assertThat(this.linkedItem.getRevision(), equalTo(expectedRevision));
    }
}