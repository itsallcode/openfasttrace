package openfasttrack.importer;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;;

public class TestSpecificationItemListBuilder
{
    private static final String DESCRIPTION = "description";
    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    @Test
    public void testBuildBasicItem()
    {
        final SpecificationMapListBuilder itemsBuilder = new SpecificationMapListBuilder();
        itemsBuilder.beginSpecificationItem();
        itemsBuilder.setId(ID);
        itemsBuilder.appendDescription(DESCRIPTION);
        final Map<SpecificationItemId, SpecificationItem> items = itemsBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(ID).getId(), equalTo(ID));
        assertThat(items.get(ID).getDescription(), equalTo(DESCRIPTION));
    }
}
