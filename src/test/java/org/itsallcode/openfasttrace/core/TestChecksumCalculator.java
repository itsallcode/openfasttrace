package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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

import org.itsallcode.openfasttrace.importer.ChecksumCalculator;
import org.junit.Test;

public class TestChecksumCalculator
{
    @Test
    public void testCalculateCrc32OfEmptyString()
    {
        assertThat(ChecksumCalculator.calculateCrc32(""), equalTo(0L));
    }

    @Test
    public void testCalculateCrc32OfSimpleString()
    {
        assertThat(ChecksumCalculator.calculateCrc32("abcd"), equalTo(3984772369L));
    }

    @Test
    public void testCalculateCrc32OfUtf8String()
    {
        assertThat(ChecksumCalculator.calculateCrc32("äöüÖÄÜß"), equalTo(2866547662L));
    }
}
