package openfasttrack.core;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestLinkedSpecificationItem
{
    private LinkedSpecificationItem linkedItem;
    private LinkedSpecificationItem coveredLinkedItem;
    @Mock
    private SpecificationItem itemMock;
    @Mock
    private SpecificationItem coveredItemMock;

    @Before
    public void prepareAllTests()
    {
        MockitoAnnotations.initMocks(this);
        this.linkedItem = new LinkedSpecificationItem(this.itemMock);
        this.coveredLinkedItem = new LinkedSpecificationItem(this.coveredItemMock);
    }

    @Test
    public void testCreateLinkedSpecificationItem()
    {
        assertThat(this.linkedItem.getItem(), equalTo(this.itemMock));
    }

    @Test
    public void testLinkToCovered()
    {
        this.linkedItem.addCovered(this.coveredLinkedItem);
        assertThat(this.linkedItem.getCoveredLinks(), containsInAnyOrder(this.coveredLinkedItem));
    }
}
