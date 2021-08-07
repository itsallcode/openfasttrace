package org.itsallcode.openfasttrace.api.core;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

    private DeepCoverageStatus(final int badness)
    {
        this.badness = badness;
    }

    /**
     * Get the worse of two coverage status
     * 
     * @param a
     *            left status to compare
     * @param b
     *            right status to compare
     * @return worse of both provided status
     */
    public static DeepCoverageStatus getWorst(final DeepCoverageStatus a,
            final DeepCoverageStatus b)
    {
        return (b.badness > a.badness) ? b : a;
    }
}