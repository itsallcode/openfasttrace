package org.itsallcode.openfasttrace.importer;

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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.itsallcode.openfasttrace.FilterSettings;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TestImportFilterSettings
{
    @Test
    public void testFilterUnsetIfEmpty()
    {
        assertFilterSet(new FilterSettings.Builder().build(), false);
    }

    @Test
    public void testBuilder()
    {
        final String[] expectedArtifactTypes = { "foo", "bar" };
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .artifactTypes(new HashSet<>(Arrays.asList(expectedArtifactTypes))) //
                .build();
        assertThat(filterSettings.getArtifactTypes(), containsInAnyOrder(expectedArtifactTypes));
        assertFilterSet(filterSettings, true);
    }

    private void assertFilterSet(final FilterSettings filterSettings, final boolean set)
    {
        assertThat(filterSettings.isAnyCriteriaSet(), equalTo(set));
    }

    @Test
    public void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(FilterSettings.class).verify();
    }
}
