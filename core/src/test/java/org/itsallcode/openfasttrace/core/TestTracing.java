package org.itsallcode.openfasttrace.core;

import static org.itsallcode.openfasttrace.core.SpecificationItemAssertions.*;
import static org.itsallcode.openfasttrace.testutil.core.TraceAssertions.*;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTracing
{
    private static final String CHILD_B_ARTIFACT_TYPE = "cb";
    private static final String PARENT_ARTIFACT_TYPE = "b";
    private static final String CHILD_A_ARTIFACT_TYPE = "ca";
    private static final SpecificationItemId grandParentId = SpecificationItemId.parseId("a~a~1");
    private static final SpecificationItemId parentId = SpecificationItemId.parseId("b~b~1");
    private static final SpecificationItemId childAId = SpecificationItemId.parseId("ca~ca~1");
    private Builder grandParentBuilder;
    private Builder parentBuilder;
    private Builder childABuilder;

    @BeforeEach
    void beforeEach()
    {
        this.grandParentBuilder = item().id(grandParentId);
        this.parentBuilder = item().id(parentId);
        this.childABuilder = item().id(childAId);
    }

    // [utest->dsn~tracing.deep-coverage~1]
    // [utest->dsn~tracing.needed-coverage-status~1]
    @Test
    void testTwoLevelsOk()
    {

        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType(CHILD_A_ARTIFACT_TYPE), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasNoDefects(trace), //
                () -> assertTraceContainsNoDefectIds(trace), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(parent, true), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.COVERED), //
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemHasCoveredArtifactTypes(parent, CHILD_A_ARTIFACT_TYPE), //
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

    // [utest->dsn~tracing.deep-coverage~1]
    // [utest->dsn~tracing.needed-coverage-status~1]
    @Test
    void testThreeLevelsOk() throws IOException
    {
        final Trace trace = traceItems( //
                this.grandParentBuilder.addNeedsArtifactType(PARENT_ARTIFACT_TYPE), //
                this.parentBuilder.addNeedsArtifactType(CHILD_A_ARTIFACT_TYPE)
                        .addCoveredId(grandParentId), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem grandParent = getItemFromTraceForId(trace, grandParentId);
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasNoDefects(trace), //
                () -> assertTraceContainsNoDefectIds(trace), //
                () -> assertTraceSize(trace, 3), //
                () -> assertItemCoveredShallow(grandParent, true), //
                () -> assertItemDeepCoverageStatus(grandParent, DeepCoverageStatus.COVERED), // ,
                () -> assertItemHasCoveredArtifactTypes(grandParent, PARENT_ARTIFACT_TYPE), //
                () -> assertItemHasNoUncoveredArtifactTypes(grandParent),
                () -> assertItemCoveredShallow(parent, true), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.COVERED), // ,
                () -> assertItemHasCoveredArtifactTypes(parent, CHILD_A_ARTIFACT_TYPE), //
                () -> assertItemHasNoUncoveredArtifactTypes(parent),
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId));
    }

    // [utest->dsn~tracing.deep-coverage~1]
    // [utest->dsn~tracing.needed-coverage-status~1]
    @Test
    void testTwoLevelsWithOneNeededArtifactTypeMissing() throws IOException
    {
        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType(CHILD_A_ARTIFACT_TYPE)
                        .addNeedsArtifactType(CHILD_B_ARTIFACT_TYPE), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, parentId), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.COVERED), //
                () -> assertItemDefect(child, false), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemHasCoveredArtifactTypes(parent, CHILD_A_ARTIFACT_TYPE), //
                () -> assertItemDoesNotCoverArtifactTypes(parent, CHILD_B_ARTIFACT_TYPE));
    }

    // [utest->dsn~tracing.link-cycle~1]
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

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testTwoLevelsWithTwoLevelCycle() throws IOException
    {
        final Trace trace = traceItems( //
                this.parentBuilder.addNeedsArtifactType(CHILD_A_ARTIFACT_TYPE)
                        .addCoveredId(childAId), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, parentId, childAId), //
                () -> assertTraceSize(trace, 2), //
                () -> assertItemCoveredShallow(parent, true), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.CYCLE), //
                () -> assertItemDefect(child, true), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemHasCoveredArtifactTypes(parent, CHILD_A_ARTIFACT_TYPE));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testTwoLevelsWithThreeLevelCycle() throws IOException
    {
        final Trace trace = traceItems( //
                this.grandParentBuilder.addNeedsArtifactType(PARENT_ARTIFACT_TYPE)
                        .addCoveredId(childAId), //
                this.parentBuilder.addNeedsArtifactType(CHILD_A_ARTIFACT_TYPE)
                        .addCoveredId(grandParentId), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem grandParent = getItemFromTraceForId(trace, grandParentId);
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, grandParentId, parentId, childAId), //
                () -> assertTraceSize(trace, 3), //
                () -> assertItemCoveredShallow(grandParent, true), //
                () -> assertItemDeepCoverageStatus(grandParent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(parent, true), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.CYCLE), //
                () -> assertItemDefect(child, true), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemHasCoveredArtifactTypes(parent, CHILD_A_ARTIFACT_TYPE));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testTwoLevelsWithThreeLevelCycleAndWrongCoverageInLevelThree() throws IOException
    {
        final Trace trace = traceItems( //
                this.grandParentBuilder.addNeedsArtifactType(PARENT_ARTIFACT_TYPE)
                        .addCoveredId(childAId), //
                this.parentBuilder.addNeedsArtifactType(CHILD_B_ARTIFACT_TYPE)
                        .addCoveredId(grandParentId), //
                this.childABuilder.addCoveredId(parentId));
        final LinkedSpecificationItem grandParent = getItemFromTraceForId(trace, grandParentId);
        final LinkedSpecificationItem parent = getItemFromTraceForId(trace, parentId);
        final LinkedSpecificationItem child = getItemFromTraceForId(trace, childAId);
        assertAll(() -> assertTraceHasDefects(trace), //
                () -> assertTraceContainsDefectIds(trace, grandParentId, parentId, childAId), //
                () -> assertTraceSize(trace, 3), //
                () -> assertItemCoveredShallow(grandParent, true), //
                () -> assertItemDeepCoverageStatus(grandParent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(parent, false), //
                () -> assertItemDeepCoverageStatus(parent, DeepCoverageStatus.CYCLE), //
                () -> assertItemCoveredShallow(child, true), //
                () -> assertItemDeepCoverageStatus(child, DeepCoverageStatus.CYCLE), //
                () -> assertItemDefect(child, true), //
                () -> assertItemCoversIds(child, parentId), //
                () -> assertItemHasNoCoveredArtifactTypes(parent), //
                () -> assertItemHasNoCoveredArtifactTypes(parent));
    }
}
