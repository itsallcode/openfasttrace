package openfasttrack.core;

import static openfasttrack.core.SampleArtifactTypes.IMPL;
import static openfasttrack.core.SampleArtifactTypes.REQ;
import static openfasttrack.core.SpecificationItemId.createId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void testDetectOutgoingLinkStatusCovered()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "covered", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, LinkStatus.COVERS);
    }

    private List<LinkedSpecificationItem> linkItems(final SpecificationItem... items)
    {
        return new Linker(Arrays.asList(items)).link();
    }

    private void assertItemUnderTestHasExactlyOneLinkWithStatus(
            final SpecificationItem itemUnderTest, final List<LinkedSpecificationItem> linkedItems,
            final LinkStatus expected_status)
    {
        boolean found = false;
        for (final LinkedSpecificationItem linkedItem : linkedItems)
        {
            if (linkedItem.getId().equals(itemUnderTest.getId()))
            {
                assertItemHasExactlyOneLinkWithStatus(linkedItem, expected_status);
                found = true;
            }
        }
        assertThat("Item under test \"" + itemUnderTest.getId() + "\" found in linked items", found,
                equalTo(true));
    }

    @Test
    public void testDetectOutgoingLinkStatusOrphaned()
    {
        final SpecificationItem orphan = new SpecificationItem.Builder() //
                .id(IMPL, "orphan", 1) //
                .addCoveredId(createId(REQ, "non-existent", 1)) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(orphan);
        assertItemUnderTestHasExactlyOneLinkWithStatus(orphan, linkedItems, LinkStatus.ORPHANED);
    }

    @Test
    public void testDetectOutgoingLinkStatusOutdated()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "this-is-newer-than-link", 2) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "this-is-newer-than-link", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, LinkStatus.OUTDATED);
    }

    @Test
    public void testDetectOutgoingLinkStatusPredated()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "this-is-older-than-link", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "this-is-older-than-link", 2) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, LinkStatus.PREDATED);
    }

    @Test
    public void testDetectOutgoingLinkStatusAmbiguous()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem duplicate = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "covered", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered, duplicate);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, LinkStatus.AMBIGUOUS);
    }

    @Test
    public void testDetectOutgoingLinkStatusDuplicate()
    {
        final SpecificationItem original = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .build();
        final SpecificationItem duplicate = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(original, duplicate);
        assertItemUnderTestHasExactlyOneLinkWithStatus(original, linkedItems, LinkStatus.DUPLICATE);
        assertItemUnderTestHasExactlyOneLinkWithStatus(duplicate, linkedItems,
                LinkStatus.DUPLICATE);
    }

    // In
    // 1. Covered shallow: coverage provider for a required coverage exists
    // 2. Covered unwanted: coverage provider covers an artifact type the requester
    // does not want
    // 3. Covered predated: coverage provider covers a higher revision number than
    // the requester has
    // 4. Covered outdated: coverage provider covers a lower revision number than
    // the requester has

    @Test
    public void testDetectIncomingLinkStatusCoveredShallow()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "covered", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems,
                LinkStatus.COVERED_SHALLOW);
    }

    private void assertItemHasExactlyOneLinkWithStatus(final LinkedSpecificationItem itemUnderTest,
            final LinkStatus expectedStatus)
    {
        final Map<LinkStatus, List<LinkedSpecificationItem>> linksWithStatus = itemUnderTest
                .getLinks();
        boolean ok = !linksWithStatus.isEmpty();
        for (final LinkStatus status : linksWithStatus.keySet())
        {
            final int linkCount = linksWithStatus.get(status).size();
            final boolean hasExpectedStatus = status.equals(expectedStatus);
            ok = ok && //
                    (hasExpectedStatus ? (linkCount == 1) : (linkCount == 0));
        }
        if (!ok)
        {
            final StringBuilder debugInfo = createDebugInfoFromLinksWithStatus(linksWithStatus);
            fail("\nExpected: \"" + itemUnderTest.getId()
                    + "\" has exaclty one incoming link and it is of type " + expectedStatus + ".\n"
                    + "     but: was " + debugInfo);
        }
    }

    private StringBuilder createDebugInfoFromLinksWithStatus(
            final Map<LinkStatus, List<LinkedSpecificationItem>> linksWithStatus)
    {
        final StringBuilder debugInfo = new StringBuilder();
        final Set<LinkStatus> keySet = linksWithStatus.keySet();
        if (keySet.isEmpty())
        {
            debugInfo.append("no links");
        }
        else
        {
            for (final LinkStatus status : keySet)
            {
                final List<LinkedSpecificationItem> items = linksWithStatus.get(status);
                debugInfo.append(status);
                debugInfo.append(": ");
                debugInfo.append(items.stream().map(item -> item.getId().toString())
                        .collect(Collectors.joining(", ")));
                debugInfo.append("\n");
            }
        }
        return debugInfo;
    }

    @Test
    public void testDetectIncomingLinkStatusUnwanted()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "covered", 1) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "covered", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems,
                LinkStatus.COVERED_UNWANTED);
    }

    @Test
    public void testDetectIncomingLinkStatusCoveredOutdated()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "newer", 2) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "newer", 1) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems,
                LinkStatus.COVERED_OUTDATED);
    }

    @Test
    public void testDetectIncomingLinkStatusCoveredPredated()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "older", 1) //
                .addNeedsArtifactType(IMPL) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "older", 2) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered);
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems,
                LinkStatus.COVERED_PREDATED);
    }
}