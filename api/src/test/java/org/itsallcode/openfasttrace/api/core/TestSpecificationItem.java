package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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

    // [utest->dsn~specification-item~3]
    @Test
    void testToBuilderPreservesAllValues()
    {
        final SpecificationItem item = SpecificationItem.builder().id(locatedId(ID)).title("Title")
                .description("Description").rationale("Rationale").comment("Comment").status(ItemStatus.DRAFT)
                .location(Location.create("file.md", 7)).addCoveredId(locatedId(COVERED_ID))
                .addDependOnId(locatedId(DEPEND_ON_ID)).addNeedsArtifactType("impl").addTag("important")
                .forwards(true).build();

        assertThat(item.toBuilder().build(), equalTo(item));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testToBuilderDoesNotModifyOriginalItem()
    {
        final SpecificationItem item = SpecificationItem.builder().id(ID).addCoveredId(COVERED_ID).build();

        final SpecificationItem copy = item.toBuilder().addCoveredId(DEPEND_ON_ID).build();

        assertAll(
                () -> assertThat(item.getCoveredIds(), contains(COVERED_ID)),
                () -> assertThat(copy, not(equalTo(item))),
                () -> assertThat(copy.getCoveredIds(), contains(COVERED_ID, DEPEND_ON_ID)));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testBuilderReplacesCoveredIds()
    {
        final SpecificationItem item = SpecificationItem.builder().id(ID).addCoveredId(DEPEND_ON_ID)
                .coveredIds(List.of(COVERED_ID)).build();

        assertThat(item.getCoveredIds(), contains(COVERED_ID));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testBuilderReplacesDependencyIds()
    {
        final SpecificationItem item = SpecificationItem.builder().id(ID).addDependOnId(COVERED_ID)
                .dependOnIds(List.of(DEPEND_ON_ID)).build();

        assertThat(item.getDependOnIds(), contains(DEPEND_ON_ID));
    }

    // [utest->dsn~located-specification-item-id-storage~1]
    @Test
    void testLocatedCoveredIdsAreImmutable()
    {
        final SpecificationItem item = SpecificationItem.builder().id(locatedId(ID))
                .addCoveredId(locatedId(COVERED_ID)).addDependOnId(locatedId(DEPEND_ON_ID)).build();

        final List<LocatedSpecificationItemId> immutableList = item.getLocatedCoveredIds();
        final LocatedSpecificationItemId locatedId = locatedId(ID);
        assertThrows(UnsupportedOperationException.class, () -> immutableList.add(locatedId));
    }

    // [utest->dsn~located-specification-item-id-storage~1]
    @Test
    void testLocatedDependOnIdsAreImmutable()
    {
        final SpecificationItem item = SpecificationItem.builder().id(locatedId(ID))
                .addCoveredId(locatedId(COVERED_ID)).addDependOnId(locatedId(DEPEND_ON_ID)).build();

        final List<LocatedSpecificationItemId> immutableList = item.getLocatedDependOnIds();
        final LocatedSpecificationItemId locatedId = locatedId(ID);
        assertThrows(UnsupportedOperationException.class, () -> immutableList.add(locatedId));
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
