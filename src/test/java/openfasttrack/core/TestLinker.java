package openfasttrack.core;

import static openfasttrack.core.SpecificationItemBuilders.prepare;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class TestLinker
{
    private static final SpecificationItem A;
    private static final SpecificationItem B;
    private static final SpecificationItem C;
    private static final SpecificationItem D;
    private static final SpecificationItem E;
    private static final SpecificationItem F;
    private static final SpecificationItem G;

    static
    {
        A = prepare("req", "A", 2).addNeedsArtifactType("dsn").build();
        B = prepare("dsn", "B", 2).addCoveredId(A.getId()).build();
        C = prepare("dsn", "D", 2).addCoveredId(SpecificationItemId.createId("req", "A", 1))
                .build();
        D = prepare("dsn", "E", 2).addCoveredId(SpecificationItemId.createId("req", "A", 3))
                .build();
        E = prepare("impl", "C", 2).addCoveredId(A.getId()).build();
        F = prepare("req", "F", 2).build();
        G = prepare("dsn", "G", 2).addNeedsArtifactType("uman").build();
    }

    // @formatter:off
    private static final int[][] LINK_MATRIX =
        {
                // CVS | OUT | PRE | UNW | CVS | COU | CPR | CUN
                {    0 ,   0 ,   0 ,   0 ,   1 ,   1 ,   1 ,   1 },
                {    1 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   1 ,   0 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   0 ,   1 ,   0 ,   0 ,   0 ,   0 ,   0 },
                {    0 ,   0 ,   0 ,   1 ,   0 ,   0 ,   0 ,   0 },
        };
    // @formatter:on

    @Test
    public void testLink()
    {
        final List<SpecificationItem> items = Arrays.asList(A, B, C, D, E, F, G);
        final Linker linker = new Linker(items);
        final List<LinkedSpecificationItem> linkedItems = linker.link();
        assertThat(linkedItems, hasSize(items.size()));
        final Iterator<LinkedSpecificationItem> iterator = linkedItems.iterator();
        for (final int[] expectedCounts : LINK_MATRIX)
        {
            final LinkedSpecificationItem item = iterator.next();
            assertItemLinkStatusCount(item, LinkStatus.COVERS, expectedCounts[0]);
            assertItemLinkStatusCount(item, LinkStatus.OUTDATED, expectedCounts[1]);
            assertItemLinkStatusCount(item, LinkStatus.PREDATED, expectedCounts[2]);
            assertItemLinkStatusCount(item, LinkStatus.UNWANTED, expectedCounts[3]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_SHALLOW, expectedCounts[4]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_OUTDATED, expectedCounts[5]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_PREDATED, expectedCounts[6]);
            assertItemLinkStatusCount(item, LinkStatus.COVERED_UNWANTED, expectedCounts[7]);
        }
    }

    private void assertItemLinkStatusCount(final LinkedSpecificationItem item,
            final LinkStatus status, final int count)
    {
        final String message = item.getId().toString() + " must have " + count
                + " links with status " + status.toString();
        assertThat(message, item.getLinksByStatus(status), hasSize(count));
    }
}