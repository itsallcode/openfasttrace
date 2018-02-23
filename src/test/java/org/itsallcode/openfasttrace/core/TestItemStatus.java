package org.itsallcode.openfasttrace.core;

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
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestItemStatus
{

    @Test
    public void testParseString()
    {
        assertThat(ItemStatus.parseString("approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("Approved"), equalTo(ItemStatus.APPROVED));
        assertThat(ItemStatus.parseString("dRaft"), equalTo(ItemStatus.DRAFT));
        assertThat(ItemStatus.parseString("PROPOSED"), equalTo(ItemStatus.PROPOSED));
        assertThat(ItemStatus.parseString("ReJeCtEd"), equalTo(ItemStatus.REJECTED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownStringThrowsException()
    {
        ItemStatus.parseString("Unkown");
    }
}
