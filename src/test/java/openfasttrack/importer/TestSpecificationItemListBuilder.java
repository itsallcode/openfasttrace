package openfasttrack.importer;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;;

public class TestSpecificationItemListBuilder
{
    private static final String DESCRIPTION = "description";
    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat.id~1");

    @Test
    public void testBuildBasicItem()
    {
        final SpecificationItemListBuilder itemsBuilder = new SpecificationItemListBuilder();
        itemsBuilder.startSpecificationItem();
        itemsBuilder.setId(ID);
        itemsBuilder.appendDescription(DESCRIPTION);
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(ID));
        assertThat(items.get(0).getDescription(), equalTo(DESCRIPTION));
    }
}
