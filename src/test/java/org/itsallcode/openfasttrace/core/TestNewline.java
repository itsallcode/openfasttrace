package org.itsallcode.openfasttrace.core;

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
import static org.junit.Assert.assertThat;

import org.itsallcode.openfasttrace.core.Newline;
import org.junit.Test;

public class TestNewline
{
    @Test
    public void testUnix()
    {
        assertThat(Newline.fromRepresentation("\n"), equalTo(Newline.UNIX));
    }

    @Test
    public void testWindows()
    {
        assertThat(Newline.fromRepresentation("\r\n"), equalTo(Newline.WINDOWS));
    }

    @Test
    public void testOldMac()
    {
        assertThat(Newline.fromRepresentation("\r"), equalTo(Newline.OLDMAC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknown()
    {
        Newline.fromRepresentation("unknown");
    }
}
