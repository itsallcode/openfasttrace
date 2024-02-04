package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nl.jqno.equalsverifier.EqualsVerifier;

class SpecificationItemIdTest
{

    @ParameterizedTest(name = "Parsing of id ''{0}'' succeeds")
    @CsvSource(value =
    {
            "type~name~42, type, name, 42",
            "type~name-with-dash~42, type, name-with-dash, 42",
            "type~name.with.dot~42, type, name.with.dot, 42",
            "type~name-trailing-~42, type, name-trailing-, 42",
    })
    void parsingValidIdsSucceeds(final String id, final String expectedType, final String expectedName,
            final int expectedRevision)
    {
        final SpecificationItemId parsedId = parseId(id);
        assertAll(
                () -> assertThat(parsedId.getArtifactType(), equalTo(expectedType)),
                () -> assertThat(parsedId.getName(), equalTo(expectedName)),
                () -> assertThat(parsedId.getRevision(), equalTo(expectedRevision)));
    }

    @ParameterizedTest(name = "Parsing of id ''{0}'' fails")
    @CsvSource(value =
    {
            "ty.pe~name~42",
            "ty-pe~name~42",
            "ty.pe~name~42",
            "type~.name~42",
            "type~name-with-trailing-dot.~42",
            "type~name~rev",
            "typeÄ~name~42",
            "type~na.-me~42",
            "~name~42",
            "type~~42",
            "type~name~"
    })
    void parsingIllegalIdsFails(final String id)
    {
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> parseId(id));
        assertThat(exception.getMessage(),
                equalTo("String \"" + id + "\" cannot be parsed to a specification item ID"));
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass(SpecificationItemId.class).verify();
    }

    @Test
    void hasToString()
    {
        assertThat(parseId("type~name~42").toString(), equalTo("type~name~42"));
    }

    @Test
    void toRevisionWildcard()
    {
        final SpecificationItemId id = parseId("type~name~42").toRevisionWildcard();
        assertThat(id.getRevision(), equalTo(SpecificationItemId.REVISION_WILDCARD));
        assertThat(id.toString(), equalTo("type~name~" + SpecificationItemId.REVISION_WILDCARD));
    }

    private SpecificationItemId parseId(final String id)
    {
        return new SpecificationItemId.Builder(id).build();
    }
}
