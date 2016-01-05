package openfasttrack.importer;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import openfasttrack.core.SpecificationItem;;

public class TestSpecificationItemListBuilder
{
    @Test
    public void testBuildBasicItem()
    {
        final SpecificationItemListBuilder itemsBuilder = new SpecificationItemListBuilder();
        itemsBuilder.foundNewSpecificationItem("feat.id");
        itemsBuilder.appendDescription("description");
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo("feat.id"));
        assertThat(items.get(0).getDescription(), equalTo("description"));
    }

}
