package openfasttrack.core;

import static openfasttrack.core.SpecificationItemId.createId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestLink
{

    private static final SpecificationItemId ID1 = createId("req", "foo", 1);
    private static final SpecificationItemId ID2 = createId("impl", "bar", 2);

    @Test
    public void test()
    {
        final Link link = new Link(ID1, ID2, LinkStatus.OK);
        assertThat(link.getFrom(), equalTo(ID1));
        assertThat(link.getTo(), equalTo(ID2));
        assertThat(link.getStatus(), equalTo(LinkStatus.OK));
    }
}
