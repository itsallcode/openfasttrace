package openfasttrack.importer;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItemId;

public class TestSpecificationListBuilder
{
    private static final SpecificationItemId ITEM_ID = SpecificationItemId.parseId("foo~bar~1");
    private SpecificationListBuilder builder;

    @Before
    public void setUp()
    {
        this.builder = new SpecificationListBuilder();
    }

    @Test
    public void testDuplicateIdNotIgnored()
    {
        this.builder.beginSpecificationItem();
        this.builder.setId(ITEM_ID);
        this.builder.endSpecificationItem();
        this.builder.beginSpecificationItem();
        this.builder.setId(ITEM_ID);
        this.builder.endSpecificationItem();

        assertThat(this.builder.getItemCount(), equalTo(2));
    }

}
