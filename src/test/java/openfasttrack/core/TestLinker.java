package openfasttrack.core;

import static openfasttrack.core.SampleArtifactTypes.IMPL;
import static openfasttrack.core.SampleArtifactTypes.REQ;
import static openfasttrack.core.SpecificationItemId.createId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestLinker
{
    // States to test for:
    //
    // Out
    // 1. Covers: link points to a specification item which wants this coverage
    // 2. Outdated: link points to a specification item which has a higher revision
    // number
    // 3. Predated: link points to a specification item which has a lower revision
    // number
    // 4. Ambiguous: link points to a specification item that has duplicates
    // 5. Orphaned: link is broken - there is no matching coverage requester
    //
    // In
    // 1. Covered shallow: coverage provider for a required coverage exists
    // 2. Covered unwanted: coverage provider covers an artifact type the requester
    // does not want
    // 3. Covered predated: coverage provider covers a higher revision number than
    // the requester has
    // 4. Covered outdated: coverage provider covers a lower revision number than
    // the requester has

    @Test
    public void testLinkerDetectsOrphan()
    {
        final SpecificationItem orphan = new SpecificationItem.Builder() //
                .id(IMPL, "orphan", 1) //
                .addCoveredId(createId(REQ, "non-existent", 1)) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItemUnderTestTo(orphan);
        final LinkStatus EXPECTED_LINK_STATUS = LinkStatus.ORPHANED;
        final LinkedSpecificationItem itemUnderTest = linkedItems.get(0);
        for (final LinkStatus linkStatus : LinkStatus.values())
        {
            assertThat("Link Status " + linkStatus,
                    itemUnderTest.getLinksByStatus(linkStatus).size(),
                    equalTo(linkStatus == EXPECTED_LINK_STATUS ? 1 : 0));
        }
    }

    private List<LinkedSpecificationItem> linkItemUnderTestTo(final SpecificationItem itemUnderTest,
            final SpecificationItem... otherItems)
    {
        final List<SpecificationItem> allItems = new ArrayList<>();
        allItems.add(itemUnderTest);
        allItems.addAll(Arrays.asList(otherItems));
        return new Linker(allItems).link();
    }
}
