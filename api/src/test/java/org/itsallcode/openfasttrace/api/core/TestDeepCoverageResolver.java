package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.openfasttrace.api.core.SampleArtifactTypes.*;

import org.junit.jupiter.api.Test;

class TestDeepCoverageResolver
{
    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    void testResolve_Covered()
    {
        final LinkedSpecificationItem item = item(REQ, "item", IMPL);
        final LinkedSpecificationItem coveringItem = item(IMPL, "implementation");
        item.addLinkToItemWithStatus(coveringItem, LinkStatus.COVERED_SHALLOW);
        assertThat(DeepCoverageResolver.resolve(item, false), equalTo(DeepCoverageStatus.COVERED));
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    void testResolve_MissingCoverage()
    {
        final LinkedSpecificationItem item = item(REQ, "item");
        final LinkedSpecificationItem coveringItem = item(IMPL, "implementation", IMPL, UMAN);
        item.addLinkToItemWithStatus(coveringItem, LinkStatus.COVERED_SHALLOW);
        assertThat(DeepCoverageResolver.resolve(item, false), equalTo(DeepCoverageStatus.UNCOVERED));
    }

    // [utest->dsn~tracing.deep-coverage~1]
    @Test
    void testResolve_MissingApprovedStatusIfOnlyApprovedItemsAccepted()
    {
        final LinkedSpecificationItem item = itemWithStatus(REQ, "item", ItemStatus.PROPOSED);
        assertThat(DeepCoverageResolver.resolve(item, false), equalTo(DeepCoverageStatus.COVERED));
        assertThat(DeepCoverageResolver.resolve(item, true), equalTo(DeepCoverageStatus.UNCOVERED));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testResolve_CycleIfSelfLinkExists()
    {
        final LinkedSpecificationItem item = item(REQ, "item");
        item.addLinkToItemWithStatus(item, LinkStatus.COVERED_SHALLOW);
        assertThat(DeepCoverageResolver.resolve(item, false), equalTo(DeepCoverageStatus.CYCLE));
    }

    // [utest->dsn~tracing.link-cycle~1]
    @Test
    void testResolve_DeepCycle()
    {
        final LinkedSpecificationItem item = item(REQ, "item");
        final LinkedSpecificationItem coveringItem = item(IMPL, "implementation");
        item.addLinkToItemWithStatus(coveringItem, LinkStatus.COVERED_SHALLOW);
        coveringItem.addLinkToItemWithStatus(item, LinkStatus.COVERED_SHALLOW);
        assertThat(DeepCoverageResolver.resolve(item, false), equalTo(DeepCoverageStatus.CYCLE));
    }

    private static LinkedSpecificationItem item(final String artifactType, final String name,
            final String... needsArtifactTypes)
    {
        return itemWithStatus(artifactType, name, ItemStatus.APPROVED, needsArtifactTypes);
    }

    private static LinkedSpecificationItem itemWithStatus(final String artifactType,
            final String name, final ItemStatus status, final String... needsArtifactTypes)
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(artifactType, name, 1)
                .status(status);
        for (final String needsArtifactType : needsArtifactTypes)
        {
            builder.addNeedsArtifactType(needsArtifactType);
        }
        return new LinkedSpecificationItem(builder.build());
    }
}
