package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSpecificationItem
{
    private static final SpecificationItemId ID = SpecificationItemId.parseId("req~item~1");
    private static final SpecificationItemId COVERED_ID = SpecificationItemId.parseId("feat~covered~2");
    private static final SpecificationItemId DEPEND_ON_ID = SpecificationItemId.parseId("req~dependency~3");

    // [utest->dsn~specification-item~3]
    @Test
    void testBuilderAcceptsLocatedIds()
    {
        final SpecificationItem item = SpecificationItem.builder().id(locatedId(ID))
                .addCoveredId(locatedId(COVERED_ID)).addDependOnId(locatedId(DEPEND_ON_ID)).build();

        assertAll(
                () -> assertThat(item.getId(), equalTo(ID)),
                () -> assertThat(item.getCoveredIds(), contains(COVERED_ID)),
                () -> assertThat(item.getDependOnIds(), contains(DEPEND_ON_ID)));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testPreservesCompatibilityForUnlocatedIds()
    {
        final SpecificationItem item = SpecificationItem.builder().id(ID).addCoveredId(COVERED_ID)
                .addDependOnId(DEPEND_ON_ID).build();

        assertAll(
                () -> assertThat(item.getId(), equalTo(ID)),
                () -> assertThat(item.getCoveredIds(), contains(COVERED_ID)),
                () -> assertThat(item.getDependOnIds(), contains(DEPEND_ON_ID)));
    }

    private static LocatedSpecificationItemId locatedId(final SpecificationItemId id)
    {
        return LocatedSpecificationItemId.builder().id(id).range(
                new SourceRange(new SourcePosition(0, 0), new SourcePosition(0, id.toString().length()))).build();
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass(SpecificationItem.class).verify();
    }
}
