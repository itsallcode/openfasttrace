package org.itsallcode.openfasttrace.core;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

public final class SpecificationItemAssertions
{
    private SpecificationItemAssertions()
    {
        // prevent instantiation
    }

    public static void assertItemHasNoUncoveredArtifactTypes(final LinkedSpecificationItem item)
    {
        assertThat(item + " has no uncovered artifact types", item.getUncoveredArtifactTypes(),
                empty());
    }

    public static void assertItemDoesNotCoverAnyArtifactTypes(final LinkedSpecificationItem item)
    {
        assertThat(item + " has no uncovered artifact types", item.getUncoveredArtifactTypes(),
                empty());
    }

    public static void assertItemDoesNotCoverArtifactTypes(final LinkedSpecificationItem item,
            final String... artifactTypes)
    {
        assertThat(item + " has uncovered artifact types", item.getUncoveredArtifactTypes(),
                contains(artifactTypes));
    }

    public static void assertItemCoversArtifactTypes(final LinkedSpecificationItem item,
            final String artifactTypes)
    {
        assertThat(item + " coveres artifact types", item.getCoveredArtifactTypes(),
                contains(artifactTypes));
    }

    public static void assertItemCoversIds(final LinkedSpecificationItem item,
            final SpecificationItemId... ids)
    {
        assertThat(item.getId() + " covers IDs", item.getCoveredIds(), contains(ids));
    }

    public static void assertItemDefect(final LinkedSpecificationItem item, final boolean defect)
    {
        assertThat(item.getId() + " is defect", item.isDefect(), equalTo(defect));
    }

    public static void assertItemDeepCoverageStatus(final LinkedSpecificationItem item,
            final DeepCoverageStatus status)
    {
        assertThat(item.getId() + " deep coverage", item.getDeepCoverageStatus(), equalTo(status));
    }

    public static void assertItemCoveredShallow(final LinkedSpecificationItem item)
    {
        assertThat(item.getId() + " is covered shallow", item.isCoveredShallow(), equalTo(true));
    }
}