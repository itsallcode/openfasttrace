package openfasttrack.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TestTraceEntry
{
    private static final SpecificationItem ITEM = new SpecificationItem.Builder()
            .id("req", "foo", 1).build();
    private static final SpecificationItem DUPLICATE = new SpecificationItem.Builder()
            .id("req", "foo", 2).build();
    private static final SpecificationItem COVERED_ITEM = new SpecificationItem.Builder()
            .id("feat", "foo", 1).build();
    private static final String NEEDED_ARTIFACT_TYPE = "req";
    private static final SpecificationItemId IRRELEVANT_ID = SpecificationItemId.createId("foo",
            "bar");
    private TraceEntry traceEntry;

    @Before
    public void preparEachTest()
    {
        this.traceEntry = new TraceEntry.Builder(ITEM) //
                .addNeedsCoverageLink(NEEDED_ARTIFACT_TYPE, NeedsCoverageLinkStatus.OK) //
                .addBackwardLink(COVERED_ITEM.getId(), BackwardLinkStatus.OK) //
                .addDuplicate(DUPLICATE) //
                .build();
    }

    @Test
    public void testCreation()
    {
        assertThat(this.traceEntry.getNeedsCoverageLinks().size(), equalTo(1));
        assertThat(this.traceEntry.getBackwardLinks().size(), equalTo(1));
        assertThat(this.traceEntry.getDuplicates().size(), equalTo(1));
    }

    @Test
    public void testBackwardLinksClean_withoutLinks()
    {
        assertThat(new TraceEntry.Builder(ITEM).build().isBackwardLinksClean(), equalTo(true));
    }

    @Test
    public void testBackwardLinksClean_withCleanLink()
    {
        final TraceEntry entry = new TraceEntry.Builder(ITEM)
                .addBackwardLink(IRRELEVANT_ID, BackwardLinkStatus.OK).build();
        assertThat(entry.isBackwardLinksClean(), equalTo(true));
    }

    @Test
    public void testBackwardLinksClean_withUncleanLink()
    {
        final TraceEntry entry = new TraceEntry.Builder(ITEM)
                .addBackwardLink(IRRELEVANT_ID, BackwardLinkStatus.AMBIGUOUS).build();
        assertThat(entry.isBackwardLinksClean(), equalTo(false));
    }

    @Test
    public void testNeedsCoverageClean_withoutNeedsCoverage()
    {
        assertThat(new TraceEntry.Builder(ITEM).build().isNeedsCoverageClean(), equalTo(true));
    }

    @Test
    public void testNeedsCoverageClean_withCleanNeedsCoverage()
    {
        final TraceEntry entry = new TraceEntry.Builder(ITEM)
                .addNeedsCoverageLink("FOO", NeedsCoverageLinkStatus.OK).build();
        assertThat(entry.isNeedsCoverageClean(), equalTo(true));
    }

    @Test
    public void testNeedsCoverageClean_withUncleanNeedsCoverage()
    {
        final TraceEntry entry = new TraceEntry.Builder(ITEM)
                .addNeedsCoverageLink("FOO", NeedsCoverageLinkStatus.OUTDATED).build();
        assertThat(entry.isNeedsCoverageClean(), equalTo(false));
    }
}
