package org.itsallcode.openfasttrace.api.core;

/**
 * Resolves the recursive deep coverage status of linked specification items.
 */
final class DeepCoverageResolver
{
    private DeepCoverageResolver()
    {
        // Prevent instantiation.
    }

    /**
     * Resolve the deep coverage status of a linked specification item.
     * 
     * @param item
     *            item from which to start the resolution
     * @param onlyAcceptApprovedItemStatus
     *            if true, only accept items with status "approved" as coverage
     * @return "covered" if the item is covered, "uncovered" if it is not
     *         covered, "cycle" if the item is part of a cycle
     */
    // [impl->dsn~tracing.deep-coverage~1]
    static DeepCoverageStatus resolve(final LinkedSpecificationItem item,
            final boolean onlyAcceptApprovedItemStatus)
    {
        return getDeepCoverageStatusEndRecursionStartingAt(item, item.getId(),
                DeepCoverageStatus.COVERED, onlyAcceptApprovedItemStatus);
    }

    // [impl->dsn~tracing.link-cycle~1]
    private static DeepCoverageStatus getDeepCoverageStatusEndRecursionStartingAt(
            final LinkedSpecificationItem item, final SpecificationItemId startId,
            final DeepCoverageStatus worstStatusSeen, final boolean onlyAcceptApprovedItemStatus)
    {
        DeepCoverageStatus status = worstStatusSeen;
        status = adjustDeepCoverageStatusIfApprovedRequired(item, onlyAcceptApprovedItemStatus,
                status);
        for (final LinkedSpecificationItem incomingItem : item.getIncomingItems())
        {
            if (incomingItem.getId().equals(startId))
            {
                return DeepCoverageStatus.CYCLE;
            }
            else
            {
                final DeepCoverageStatus otherStatus = getDeepCoverageStatusEndRecursionStartingAt(
                        incomingItem, startId, status, onlyAcceptApprovedItemStatus);
                if (otherStatus == DeepCoverageStatus.CYCLE)
                {
                    return DeepCoverageStatus.CYCLE;
                }
                status = DeepCoverageStatus.getWorst(status, otherStatus);
            }
        }
        if (status == DeepCoverageStatus.COVERED && !item.isCoveredShallow())
        {
            return DeepCoverageStatus.UNCOVERED;
        }
        else
        {
            return status;
        }
    }

    private static DeepCoverageStatus adjustDeepCoverageStatusIfApprovedRequired(
            final LinkedSpecificationItem item, final boolean onlyAcceptApprovedItemStatus,
            final DeepCoverageStatus deepCoveredStatus)
    {
        return (onlyAcceptApprovedItemStatus && deepCoveredStatus == DeepCoverageStatus.COVERED
                && item.getStatus() != ItemStatus.APPROVED)
                        ? DeepCoverageStatus.UNCOVERED
                        : deepCoveredStatus;
    }
}
