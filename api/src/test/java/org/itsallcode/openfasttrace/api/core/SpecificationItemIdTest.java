package org.itsallcode.openfasttrace.api.core;

/*-
 * #%L
 * OpenFastTrace API
 * %%
 * Copyright (C) 2016 - 2021 itsallcode.org
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
import static org.hamcrest.Matchers.equalTo;
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
    void parsingValidIdsSucceeds(String id, String expectedType, String expectedName, int expectedRevision)
    {
        final SpecificationItemId parsedId = parseId(id);
        assertThat(parsedId.getArtifactType(), equalTo(expectedType));
        assertThat(parsedId.getName(), equalTo(expectedName));
        assertThat(parsedId.getRevision(), equalTo(expectedRevision));
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
            "type~nameÄ~42",
            "type~na.-me~42",
            "~name~42",
            "type~~42",
            "type~name~"
    })
    void parsingIllegalIdsFails(String id)
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

    private SpecificationItemId parseId(String id)
    {
        return new SpecificationItemId.Builder(id).build();
    }
}
