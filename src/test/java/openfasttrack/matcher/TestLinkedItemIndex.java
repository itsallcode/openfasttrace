package openfasttrack.matcher;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.core.LinkedItemIndex;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

public class TestLinkedItemIndex
{
    private final static SpecificationItemId DUPLICATE_ID_1 = SpecificationItemId.createId("type",
            "name", 42);
    private final static SpecificationItemId DUPLICATE_ID_2 = SpecificationItemId.createId("type",
            "name", 42);
    private final static SpecificationItemId DUPLICATE_ID_INGORING_VERSION = SpecificationItemId
            .createId("type", "name", 42 + 1);
    private final static SpecificationItemId UNIQUE_ID = SpecificationItemId.createId("type2",
            "name2", 42 + 1);

    @Mock
    private SpecificationItem duplicateIdItem1Mock, duplicateIdItem2Mock, uniqueIdItemMock,
            duplicateIdIgnoringVersionItemMock;

    @Before
    public void prepareTest()
    {
        MockitoAnnotations.initMocks(this);
        when(this.duplicateIdItem1Mock.getId()).thenReturn(DUPLICATE_ID_1);
        when(this.duplicateIdItem2Mock.getId()).thenReturn(DUPLICATE_ID_2);
        when(this.uniqueIdItemMock.getId()).thenReturn(UNIQUE_ID);
        when(this.duplicateIdIgnoringVersionItemMock.getId())
                .thenReturn(DUPLICATE_ID_INGORING_VERSION);
    }

    @Test
    public void testEmptyIndex()
    {
        final LinkedItemIndex index = createIndex();
        assertThat(index.size(), equalTo(0));
        assertThat(index.getById(DUPLICATE_ID_1), nullValue());
    }

    private LinkedItemIndex createIndex(final SpecificationItem... items)
    {
        final LinkedItemIndex index = LinkedItemIndex.create(asList(items));
        return index;
    }

    @Test
    public void testNonEmptyIndex()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock, this.uniqueIdItemMock);
        assertThat(index.size(), equalTo(2));
        assertThat(index.getById(DUPLICATE_ID_1).getItem(),
                sameInstance(this.duplicateIdItem1Mock));
    }

    @Test
    public void testDuplicateId()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock,
                this.duplicateIdItem2Mock);
        final LinkedSpecificationItem duplicateItem1 = index.getById(DUPLICATE_ID_1);
        assertThat(index.size(), equalTo(1));
        assertThat(duplicateItem1.getItemsDuplicateId(), hasSize(1));
        final LinkedSpecificationItem duplicateItem2 = duplicateItem1.getItemsDuplicateId().get(0);

        assertThat(duplicateItem2.getItemsDuplicateId(), contains(sameInstance(duplicateItem1)));
        assertThat(duplicateItem1.getItemsDuplicateId(), contains(sameInstance(duplicateItem2)));
    }

    @Test
    public void testDuplicateVersionId()
    {
        final LinkedItemIndex index = createIndex(this.duplicateIdItem1Mock,
                this.duplicateIdIgnoringVersionItemMock, this.uniqueIdItemMock);
        assertThat(index.size(), equalTo(3));
        assertThat(index.sizeIgnoringVersion(), equalTo(2));
        assertThat(index.getByIdIgnoringVersion(DUPLICATE_ID_1),
                containsInAnyOrder(asList(
                        LinkedItemInstanceMatcher.sameItemInstance(this.duplicateIdItem1Mock),
                        LinkedItemInstanceMatcher
                                .sameItemInstance(this.duplicateIdIgnoringVersionItemMock))));
    }
}
