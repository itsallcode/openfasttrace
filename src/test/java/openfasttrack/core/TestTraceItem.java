package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestTraceItem
{
    private static final SpecificationItem ITEM = new SpecificationItem.Builder()
            .id("req", "foo", 1).build();
    private static final SpecificationItem DUPLICATE = new SpecificationItem.Builder()
            .id("req", "foo", 2).build();
    private static final SpecificationItem COVERED_ITEM = new SpecificationItem.Builder()
            .id("feat", "foo", 1).build();
    private static final String NEEDED_ARTIFACT_TYPE = "req";

    @Test
    public void test()
    {
        final TraceItem traceItem = new TraceItem.Builder(ITEM) //
                .addForwardLink(NEEDED_ARTIFACT_TYPE, ForwardLinkStatus.OK) //
                .addBackwardLink(COVERED_ITEM.getId(), BackwardLinkStatus.OK) //
                .addDuplicate(DUPLICATE) //
                .build();
        assertThat(traceItem.getForwardLinks().size(), equalTo(1));
        assertThat(traceItem.getBackwardLinks().size(), equalTo(1));
        assertThat(traceItem.getDuplicates().size(), equalTo(1));
    }
}
