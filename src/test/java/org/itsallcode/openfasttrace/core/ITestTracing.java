package org.itsallcode.openfasttrace.core;

import static org.itsallcode.openfasttrace.core.SpecificationItemAssertions.*;
import static org.itsallcode.openfasttrace.core.TraceAssertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.core.SpecificationItem.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ITestTracing
{
    private static final SpecificationItemId grandParentId = SpecificationItemId.parseId("a~a~1");
    private static final SpecificationItemId parentId = SpecificationItemId.parseId("b~b~1");
    private static final SpecificationItemId childAId = SpecificationItemId.parseId("ca~ca~1");
    private Builder grandParentBuilder;
    private Builder parentBuilder;
    private Builder childABuilder;

    @BeforeEach
    void beforeEach()
    {
        this.grandParentBuilder = SpecificationItem.builder().id(grandParentId);
        this.parentBuilder = SpecificationItem.builder().id(parentId);
        this.childABuilder = SpecificationItem.builder().id(childAId);
    }

    @Test
    void testTwoLevelsOk()
    {

        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType("ca"), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasNoDefects(trace), //
                () -> assertTraceContainsNoDefectIds(trace), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(parent), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.COVERED), //
                () -> assertItemCoveredShallow(child), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemCoversArtifactTypes(parent, "ca"), //
                () -> assertItemHasNoUncoveredArtifactTypes(parent));
    }

    private Trace traceItems(final SpecificationItem.Builder... builders)
    {
        final List<SpecificationItem> items = buildTestItems(builders);
        final Oft oft = Oft.create();
        final List<LinkedSpecificationItem> linkedItems = oft.link(items);
        final Trace trace = oft.trace(linkedItems);
        return trace;
    }

    private List<SpecificationItem> buildTestItems(final SpecificationItem.Builder... builders)
    {
        final List<SpecificationItem> items = new ArrayList<>();
        for (final SpecificationItem.Builder builder : builders)
        {
            items.add(builder.build());
        }
        return items;
    }

    private LinkedSpecificationItem getItemFromTraceForId(final Trace trace,
            final SpecificationItemId id)
    {
        for (final LinkedSpecificationItem item : trace.getItems())
        {
            if (item.getId().equals(id))
            {
                return item;
            }
        }
        throw new AssertionError(
                "Could not find linked specification item with ID \"" + id.toString() + "\"");
    }

    @Test
    void testThreeLevelsOk() throws IOException
    {
        final Trace trace = traceItems( //
                this.grandParentBuilder.addNeedsArtifactType("b"), //
                this.parentBuilder.addNeedsArtifactType("ca").addCoveredId(grandParentId), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem grandParent = getItemFromTraceForId(trace, grandParentId);
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasNoDefects(trace), //
                () -> assertTraceContainsNoDefectIds(trace), //
                () -> assertTraceSize(trace, 3), //
                () -> assertItemCoveredShallow(grandParent), //
                () -> assertItemDeepCoverageStatus(grandParent, DeepCoverageStatus.COVERED), // ,
                () -> assertItemCoversArtifactTypes(grandParent, "b"), //
                () -> assertItemHasNoUncoveredArtifactTypes(grandParent),
                () -> assertItemCoveredShallow(parent), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.COVERED), // ,
                () -> assertItemCoversArtifactTypes(parent, "ca"), //
                () -> assertItemHasNoUncoveredArtifactTypes(parent),
                () -> assertItemCoveredShallow(child), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId));
    }

    @Test
    void testTwoLevelsWithOneNeededArtifactTypeMissing() throws IOException
    {
        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType("ca").addNeedsArtifactType("cb"), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, parentId), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(child), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemCoversArtifactTypes(parent, "ca"), //
                () -> assertItemDoesNotCoverArtifactTypes(parent, "cb"));
    }

    // [itest->dsn~tracing.link-cycle~1]
    @Test
    void testSelfCycle() throws IOException
    {
        final Trace trace = traceItems( //
                this.childABuilder.addNeedsArtifactType("any").addCoveredId(childAId));
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, childAId), //
                () -> assertTraceSize(trace, 1), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.CYCLE));
    }

    // [itest->dsn~tracing.link-cycle~1]
    @Test
    void testTwoLevelsWithTwoLevelCycle() throws IOException
    {
        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType("ca").addCoveredId(childAId), //
                this.childABuilder.addNeedsArtifactType("b").addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, parentId, childAId), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(parent), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(child), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.CYCLE), //
                () -> assertItemDefect(child, true), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemCoversArtifactTypes(parent, "ca"), //
                () -> assertItemDoesNotCoverAnyArtifactTypes(parent));
    }
}