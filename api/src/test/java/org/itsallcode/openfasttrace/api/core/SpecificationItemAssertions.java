package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

final class SpecificationItemAssertions
{
    private SpecificationItemAssertions()
    {
        // prevent instantiation
    }

    public static void assertItemHasNoUncoveredArtifactTypes(final LinkedSpecificationItem item)
    {
        assertThat(nameItem(item) + "'s uncovered artifact types", item.getUncoveredArtifactTypes(),
                empty());
    }

    protected static String nameItem(final LinkedSpecificationItem item)
    {
        return item.getId().toString();
    }

    public static void assertItemHasNoCoveredArtifactTypes(final LinkedSpecificationItem item)
    {
        assertThat(nameItem(item) + "'s covered artifact types", item.getCoveredArtifactTypes(),
                empty());
    }

    public static void assertItemDoesNotCoverArtifactTypes(final LinkedSpecificationItem item,
            final String... artifactTypes)
    {
        assertThat(nameItem(item) + "'s uncovered artifact types", item.getUncoveredArtifactTypes(),
                containsInAnyOrder(artifactTypes));
    }

    public static void assertItemHasCoveredArtifactTypes(final LinkedSpecificationItem item,
            final String... artifactTypes)
    {
        assertThat(nameItem(item) + "'s covered artifact types", item.getCoveredArtifactTypes(),
                containsInAnyOrder(artifactTypes));
    }

    public static void assertItemHasUncoveredArtifactTypes(final LinkedSpecificationItem item,
            final String... artifactTypes)
    {
        assertThat(nameItem(item) + "'s uncovered artifact types", item.getUncoveredArtifactTypes(),
                containsInAnyOrder(artifactTypes));
    }

    public static void assertItemHasOvercoveredArtifactTypes(final LinkedSpecificationItem item,
            final String... artifactTypes)
    {
        assertThat(nameItem(item) + "'s over-covered artifact types",
                item.getOverCoveredArtifactTypes(), containsInAnyOrder(artifactTypes));
    }

    public static void assertItemCoversIds(final LinkedSpecificationItem item,
            final SpecificationItemId... ids)
    {
        assertThat(nameItem(item) + " covers IDs", item.getCoveredIds(), containsInAnyOrder(ids));
    }

    public static void assertItemDefect(final LinkedSpecificationItem item, final boolean defect)
    {
        assertThat(nameItem(item) + " is defect", item.isDefect(), equalTo(defect));
    }

    public static void assertItemDeepCoverageStatus(final LinkedSpecificationItem item,
            final DeepCoverageStatus status)
    {
        assertThat(nameItem(item) + " deep coverage", item.getDeepCoverageStatus(),
                equalTo(status));
    }

    public static void assertItemCoveredShallow(final LinkedSpecificationItem item,
            final boolean covered)
    {
        assertThat(nameItem(item) + " is covered shallow", item.isCoveredShallow(),
                equalTo(covered));
    }

    public static void assertItemOutgoingLinkCount(final LinkedSpecificationItem item,
            final int count)
    {
        assertThat(item.countOutgoingLinks(), equalTo(count));
    }

    public static void assertItemOutgoingBadLinkCount(final LinkedSpecificationItem item,
            final int count)
    {
        assertThat(item.countOutgoingBadLinks(), equalTo(count));
    }

    public static void assertItemIncomingLinkCount(final LinkedSpecificationItem item,
            final int count)
    {
        assertThat(item.countIncomingLinks(), equalTo(count));
    }

    public static void assertItemIncomingBadLinkCount(final LinkedSpecificationItem item,
            final int count)
    {
        assertThat(item.countIncomingBadLinks(), equalTo(count));
    }
}