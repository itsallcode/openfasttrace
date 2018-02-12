package org.itsallcode.openfasttrace.core;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.itsallcode.openfasttrace.core.LinkStatus.*;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.IMPL;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.REQ;
import static org.itsallcode.openfasttrace.core.SampleArtifactTypes.UTEST;
import static org.itsallcode.openfasttrace.core.SpecificationItemId.createId;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestLinker
{
    // [utest->dsn~tracing.outgoing-coverage-link-status~3]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, COVERS);
    }

    private List<LinkedSpecificationItem> linkItems(final SpecificationItem... items)
    {
        return new Linker(Arrays.asList(items)).link();
    }

    private void assertItemUnderTestHasExactlyOneLinkWithStatus(
            final SpecificationItem itemUnderTest, final List<LinkedSpecificationItem> linkedItems,
            final LinkStatus expected_status)
    {
        final Optional<LinkedSpecificationItem> linkedItemUnderTest = findLinkedItem(itemUnderTest,
                linkedItems);
        if (linkedItemUnderTest.isPresent())
        {
            assertItemHasExactlyOneLinkWithStatus(linkedItemUnderTest.get(), expected_status);
        }
        else
        {
            fail("Item under test \"" + itemUnderTest.getId() + "\" missing in linked items");
        }
    }

    private Optional<LinkedSpecificationItem> findLinkedItem(final SpecificationItem itemUnderTest,
            final List<LinkedSpecificationItem> linkedItems)
    {
        return linkedItems.stream() //
                .filter(item -> item.getId().equals(itemUnderTest.getId())) //
                .findFirst();
    }

    // [utest->dsn~tracing.outgoing-coverage-link-status~3]
    @Test
    public void testDetectOutgoingLinkStatusOrphaned()
    {
        final SpecificationItem orphan = new SpecificationItem.Builder() //
                .id(IMPL, "orphan", 1) //
                .addCoveredId(createId(REQ, "non-existent", 1)) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(orphan);
        assertItemUnderTestHasExactlyOneLinkWithStatus(orphan, linkedItems, ORPHANED);
    }

    // [utest->dsn~tracing.outgoing-coverage-link-status~3]
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
        final Map<LinkStatus, Integer> expectedStatuses = new HashMap<>();
        expectedStatuses.put(LinkStatus.OUTDATED, 1);
        expectedStatuses.put(LinkStatus.ORPHANED, 1);
        assertItemHasLinksWithStatus(findLinkedItem(covering, linkedItems).get(), expectedStatuses);
    }

    // [utest->dsn~tracing.outgoing-coverage-link-status~3]
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
        final Map<LinkStatus, Integer> expectedStatuses = new HashMap<>();
        expectedStatuses.put(LinkStatus.PREDATED, 1);
        expectedStatuses.put(LinkStatus.ORPHANED, 1);
        assertItemHasLinksWithStatus(findLinkedItem(covering, linkedItems).get(), expectedStatuses);
    }

    // [utest->dsn~tracing.outgoing-coverage-link-status~3]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, AMBIGUOUS);
    }

    // [utest->dsn~tracing.tracing.duplicate-items~1]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(original, linkedItems, DUPLICATE);
        assertItemUnderTestHasExactlyOneLinkWithStatus(duplicate, linkedItems, DUPLICATE);
    }

    // [utest->dsn~tracing.incoming-coverage-link-status~1]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems, COVERED_SHALLOW);
    }

    private void assertItemHasExactlyOneLinkWithStatus(final LinkedSpecificationItem itemUnderTest,
            final LinkStatus expectedStatus)
    {
        final Map<LinkStatus, Integer> expectedStatuses = new HashMap<>();
        expectedStatuses.put(expectedStatus, 1);
        assertItemHasLinksWithStatus(itemUnderTest, expectedStatuses);
    }

    private void assertItemHasLinksWithStatus(final LinkedSpecificationItem itemUnderTest,
            final Map<LinkStatus, Integer> expectedStatuses)
    {
        final Map<LinkStatus, List<LinkedSpecificationItem>> linksWithStatus = itemUnderTest
                .getLinks();
        boolean ok = !linksWithStatus.isEmpty();
        for (final LinkStatus status : linksWithStatus.keySet())
        {
            final int linkCount = linksWithStatus.get(status).size();
            final Integer expectedLinkCount = (expectedStatuses.get(status) == null) ? 0
                    : expectedStatuses.get(status);
            ok = ok && (linkCount == expectedLinkCount);
        }
        if (!ok)
        {
            final String expected = createDebugInfoFromExpectedStatuses(expectedStatuses);
            final StringBuilder actual = createDebugInfoFromLinksWithStatus(linksWithStatus);
            fail("\nExpected: \"" + itemUnderTest.getId() + "\" has links with statuses: "
                    + expected + ".\n" + "     but: was " + actual);
        }
    }

    private String createDebugInfoFromExpectedStatuses(
            final Map<LinkStatus, Integer> expectedStatuses)
    {
        return expectedStatuses.entrySet().stream() //
                .map(entry -> entry.getKey().toString() + ": " + entry.getValue().toString()) //
                .collect(Collectors.joining(", "));
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
                debugInfo.append("; ");
            }
        }
        return debugInfo;
    }

    // [utest->dsn~tracing.incoming-coverage-link-status~1]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems, COVERED_UNWANTED);
        assertThat(findLinkedItem(covered, linkedItems).get().getOverCoveredArtifactTypes(),
                equalTo(new HashSet<>(Arrays.asList(IMPL))));
        assertThat(findLinkedItem(covered, linkedItems).get().getCoveredArtifactTypes(), empty());
        assertThat(findLinkedItem(covered, linkedItems).get().getUncoveredArtifactTypes(), empty());
    }

    // [utest->dsn~tracing.incoming-coverage-link-status~1]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems, COVERED_OUTDATED);
    }

    // [utest->dsn~tracing.incoming-coverage-link-status~1]
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
        assertItemUnderTestHasExactlyOneLinkWithStatus(covered, linkedItems, COVERED_PREDATED);
    }

    // [utest->dsn~tracing.needed-coverage-status~1]
    @Test
    public void testCoverageForDifferentArtifactTypes()
    {
        final SpecificationItem covered = new SpecificationItem.Builder() //
                .id(REQ, "to-be-covered", 42) //
                .addNeedsArtifactType(IMPL) //
                .addNeedsArtifactType(UTEST) //
                .build();
        final SpecificationItem covering = new SpecificationItem.Builder() //
                .id(IMPL, "covering", 1) //
                .addCoveredId(REQ, "to-be-covered", 42) //
                .build();
        final SpecificationItem unwanted = new SpecificationItem.Builder() //
                .id(REQ, "unwanted", 1) //
                .addCoveredId(REQ, "to-be-covered", 42) //
                .build();
        final List<LinkedSpecificationItem> linkedItems = linkItems(covering, covered, unwanted);
        final Optional<LinkedSpecificationItem> linkedCovered = findLinkedItem(covered,
                linkedItems);
        if (linkedCovered.isPresent())
        {
            final Map<LinkStatus, Integer> expectedLinksOnCovered = new HashMap<>();
            expectedLinksOnCovered.put(COVERED_SHALLOW, 1);
            expectedLinksOnCovered.put(COVERED_UNWANTED, 1);
            assertItemHasLinksWithStatus(linkedCovered.get(), expectedLinksOnCovered);
            assertItemUnderTestHasExactlyOneLinkWithStatus(covering, linkedItems, COVERS);
            assertItemUnderTestHasExactlyOneLinkWithStatus(unwanted, linkedItems, UNWANTED);
        }
        else
        {
            fail("Covered item " + covered.getId() + " not found in linked items");
        }
    }

}