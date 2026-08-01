package org.itsallcode.openfasttrace.api.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.HashSet;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestFilterSettings
{
    @Test
    void testFilterUnsetIfEmpty()
    {
        assertFilterSet(FilterSettings.builder().build(), false);
    }

    @Test
    void testBuilder()
    {
        final String[] expectedArtifactTypes = { "foo", "bar" };
        final FilterSettings filterSettings =  FilterSettings.builder() //
                .artifactTypes(new HashSet<>(Arrays.asList(expectedArtifactTypes))) //
                .build();
        assertThat(filterSettings.getArtifactTypes(), containsInAnyOrder(expectedArtifactTypes));
        assertFilterSet(filterSettings, true);
    }

    @Test
    void testBuilderWithStatuses()
    {
        final ItemStatus[] expectedStatuses = { ItemStatus.APPROVED, ItemStatus.DRAFT };
        final FilterSettings filterSettings = FilterSettings.builder() //
                .wantedStatuses(new HashSet<>(Arrays.asList(expectedStatuses))) //
                .build();
        assertThat(filterSettings.getWantedStatuses(), containsInAnyOrder(expectedStatuses));
        assertFilterSet(filterSettings, true);
    }

    private void assertFilterSet(final FilterSettings filterSettings, final boolean set)
    {
        assertThat(filterSettings.isAnyCriteriaSet(), equalTo(set));
    }

    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(FilterSettings.class).verify();
    }
}
