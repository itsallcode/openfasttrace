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
import static openfasttrack.core.SampleArtifactTypes.REQ;
import static openfasttrack.core.SampleArtifactTypes.UMAN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestLinkedSpecificationItem
{
    private LinkedSpecificationItem linkedItem, coveringLinkedItem;
    @Mock
    private SpecificationItem itemMock, coveringItemMock;

    @Before
    public void prepareAllTests()
    {
        MockitoAnnotations.initMocks(this);
        this.linkedItem = new LinkedSpecificationItem(this.itemMock);
        this.coveringLinkedItem = new LinkedSpecificationItem(this.coveringItemMock);
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

    @Test
    public void testIsCoveredDeeply_Ok()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        this.linkedItem.addCoveredArtifactType(DSN);
        when(this.coveringItemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL));
        this.coveringLinkedItem.addCoveredArtifactType(IMPL);
        this.linkedItem.addLinkToItemWithStatus(this.coveringLinkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.isCoveredDeeply(), equalTo(true));
    }

    @Test
    public void testIsCoveredDeeply_NotOk_MissingCoverage()
    {
        when(this.itemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(DSN));
        this.linkedItem.addCoveredArtifactType(DSN);
        when(this.coveringItemMock.getNeedsArtifactTypes()).thenReturn(Arrays.asList(IMPL, UMAN));
        this.coveringLinkedItem.addCoveredArtifactType(IMPL);
        this.linkedItem.addLinkToItemWithStatus(this.coveringLinkedItem, LinkStatus.COVERS);
        assertThat(this.linkedItem.isCoveredDeeply(), equalTo(false));
    }
}
