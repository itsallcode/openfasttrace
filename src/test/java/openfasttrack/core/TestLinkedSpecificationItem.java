package openfasttrack.core;

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
