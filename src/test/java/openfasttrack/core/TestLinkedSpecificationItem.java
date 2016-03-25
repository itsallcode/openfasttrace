package openfasttrack.core;

import static openfasttrack.core.SampleArtifactTypes.REQ;
import static openfasttrack.core.SampleArtifactTypes.UMAN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestLinkedSpecificationItem
{
    private LinkedSpecificationItem linkedItem;
    @Mock
    private SpecificationItem itemMock;

    @Before
    public void prepareAllTests()
    {
        MockitoAnnotations.initMocks(this);
        this.linkedItem = new LinkedSpecificationItem(this.itemMock);
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
}
