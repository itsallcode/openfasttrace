package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TestNewline
{
    @Test
    void testUnix()
    {
        assertThat(Newline.fromRepresentation("\n"), equalTo(Newline.UNIX));
    }

    @Test
    void testWindows()
    {
        assertThat(Newline.fromRepresentation("\r\n"), equalTo(Newline.WINDOWS));
    }

    @Test
    void testOldMac()
    {
        assertThat(Newline.fromRepresentation("\r"), equalTo(Newline.OLDMAC));
    }

    @Test
    void testUnknownThrowsException()
    {
        assertThrows(IllegalArgumentException.class, () -> Newline.fromRepresentation("unknown"));
    }
}