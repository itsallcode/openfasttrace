package org.itsallcode.openfasttrace.api.core;

/**
 * This enumeration represents the different kinds of deep coverage.
 */
public enum DeepCoverageStatus
{
    /**
     * This item and all its children are covered correctly.
     */
    COVERED(0),
    /**
     * This item or at least one of its children are not covered correctly
     */
    UNCOVERED(1),
    /**
     * This item is part of a link cycle, which means real coverage cannot be
     * evaluated
     */
    CYCLE(2);

    private final int badness;

    DeepCoverageStatus(final int badness)
    {
        this.badness = badness;
    }

    /**
     * Get the worst of two coverage statuses
     * 
     * @param a
     *            left status to compare
     * @param b
     *            right status to compare
     * @return worse of both provided statuses
     */
    public static DeepCoverageStatus getWorst(final DeepCoverageStatus a,
            final DeepCoverageStatus b)
    {
        return (b.badness > a.badness) ? b : a;
    }
}
