package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestSpecificationItemCollection
{
    private static final int REVISIONS_PER_ITEM = 10;
    private static final int ITEM_COUNT = 100;
    private final List<SpecificationItem> inputItems = new ArrayList<>();
    private SpecificationItemCollection items;

    @Before
    public void prepareTest()
    {
        for (int i = 0; i < ITEM_COUNT; ++i)
        {
            final SpecificationItem item = new SpecificationItem.Builder() //
                    .id(createItemForNumber(i)) //
                    .title(getTitleForNumber(i)) //
                    .build();
            this.inputItems.add(item);
        }
        this.items = new SpecificationItemCollection(this.inputItems);
    }

    private SpecificationItemId createItemForNumber(final int i)
    {
        return SpecificationItemId.createId("foo", "name" + i, i % REVISIONS_PER_ITEM + 1);
    }

    private String getTitleForNumber(final int i)
    {
        return "title_" + i;
    }

    @Test
    public void testSize()
    {
        assertThat(this.items.size(), equalTo(ITEM_COUNT));
    }

    @Test
    public void testGet_ForId()
    {
        final SpecificationItem item = this.items.getFirst(createItemForNumber(42));
        assertThat(item.getTitle(), equalTo(getTitleForNumber(42)));
    }
}
