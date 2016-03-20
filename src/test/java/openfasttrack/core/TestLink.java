package openfasttrack.core;

import static openfasttrack.core.SpecificationItemId.createId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestLink
{

    private static final SpecificationItemId ID1 = createId("req", "foo", 1);
    private static final SpecificationItemId ID2 = createId("impl", "bar", 2);
    private static final SpecificationItem ITEM = new SpecificationItem.Builder().id(ID1).build();

    @Test
    public void test()
    {
        final BackwardLink link = new BackwardLink(ITEM, ID2, BackwardLinkStatus.OK);
        assertThat(link.getFrom(), equalTo(ITEM));
        assertThat(link.getToId(), equalTo(ID2));
        assertThat(link.getStatus(), equalTo(BackwardLinkStatus.OK));
    }
}
