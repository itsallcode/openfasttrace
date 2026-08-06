package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestLocatedSpecificationItemId
{
    private static final SpecificationItemId ID = SpecificationItemId.parseId("req~item~1");
    private static final SourceRange RANGE = range(1, 0, 1, 10);
    private static final SourceRange ARTIFACT_TYPE_RANGE = range(1, 0, 1, 3);
    private static final SourceRange NAME_RANGE = range(1, 4, 1, 8);
    private static final SourceRange REVISION_RANGE = range(1, 9, 1, 10);

    @Test
    void testBuildsLocatedIdWithAllRanges()
    {
        final LocatedSpecificationItemId locatedId = LocatedSpecificationItemId.builder().id(ID).range(RANGE)
                .artifactTypeRange(ARTIFACT_TYPE_RANGE).nameRange(NAME_RANGE).revisionRange(REVISION_RANGE).build();

        assertAll(
                () -> assertThat(locatedId.getId(), equalTo(ID)),
                () -> assertThat(locatedId.getRange(), equalTo(RANGE)),
                () -> assertThat(locatedId.getArtifactTypeRange(), equalTo(Optional.of(ARTIFACT_TYPE_RANGE))),
                () -> assertThat(locatedId.getNameRange(), equalTo(Optional.of(NAME_RANGE))),
                () -> assertThat(locatedId.getRevisionRange(), equalTo(Optional.of(REVISION_RANGE))));
    }

    @Test
    void testReturnsEmptyOptionalsForGeneratedIdComponents()
    {
        final LocatedSpecificationItemId locatedId = LocatedSpecificationItemId.builder().id(ID).build();

        assertAll(
                () -> assertThat(locatedId.getRange(), equalTo(null)),
                () -> assertThat(locatedId.getArtifactTypeRange(), equalTo(Optional.empty())),
                () -> assertThat(locatedId.getNameRange(), equalTo(Optional.empty())),
                () -> assertThat(locatedId.getRevisionRange(), equalTo(Optional.empty())));
    }

    @Test
    void testRequiresAnId()
    {
        final LocatedSpecificationItemId.Builder builder = LocatedSpecificationItemId.builder();

        assertThrows(NullPointerException.class, builder::build);
    }

    private static SourceRange range(final int startLine, final int startColumn, final int endLine,
            final int endColumn)
    {
        return new SourceRange(new SourcePosition(startLine, startColumn), new SourcePosition(endLine, endColumn));
    }

    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(LocatedSpecificationItemId.class).verify();
    }
}
