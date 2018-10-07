package org.itsallcode.openfasttrace;

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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterableOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.*;

import org.itsallcode.openfasttrace.importer.tag.config.PathConfig;
import org.junit.Test;

/**
 * This class implements a parameter object to control the settings of OFT's
 * export mode.
 */
public class TestImportSettings
{
    @Test
    public void testDefaultFilter()
    {
        assertThat(ImportSettings.createDefault().getFilters(),
                equalTo(FilterSettings.createAllowingEverything()));
    }

    @Test
    public void testDefaultPathConfigs()
    {
        assertThat(ImportSettings.createDefault().getPathConfigs(),
                emptyIterableOf(PathConfig.class));
    }

    @Test
    public void testBuildWithFilter()
    {
        final String[] expectedTags = { "a", "b" };
        final FilterSettings filter = new FilterSettings.Builder()
                .tags(new HashSet<>(Arrays.asList(expectedTags))).build();
        assertThat(new ImportSettings.Builder().filter(filter).build().getFilters().getTags(),
                containsInAnyOrder(expectedTags));
    }

    @Test
    public void testBuildWithPathConfigs()
    {
        final List<PathConfig> expectedPathConfigs = new ArrayList<>();
        final PathConfig.Builder builder = PathConfig.builder();
        final PathConfig expectedFirstPathConfig = builder.patternPathMatcher("src")
                .coveredItemArtifactType("a1").coveredItemNamePrefix("b1").tagArtifactType("c1")
                .build();
        expectedPathConfigs.add(expectedFirstPathConfig);
        assertThat(new ImportSettings.Builder().pathConfigs(expectedPathConfigs).build()
                .getPathConfigs().get(0), equalTo(expectedFirstPathConfig));
    }
}