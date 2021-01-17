package org.itsallcode.openfasttrace.testutil;

/*-
 * #%L
 * OpenFastTrace Test utilities
 * %%
 * Copyright (C) 2016 - 2020 itsallcode.org
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

public class CompareAssertions
{
    public static <T> void testComparatorConsistentWithEquals(Comparator<? super T> comparator, T o,
            T smaller, T equal, T greater, boolean nullValueSupported)
    {
        testComparator(comparator, o, smaller, equal, greater, nullValueSupported);
        testConsistencyWithEquals(comparator, o, smaller, equal, greater);
    }

    public static <T> void testComparator(Comparator<? super T> comparator, T o, T smaller, T equal,
            T greater, boolean nullValueSupported)
    {
        assertAsymmetry(comparator, o, smaller, greater);
        assertSymmetry(comparator, o, equal);
        assertSignum(comparator, o, smaller, equal, greater);
        assertTransitivity(comparator, o, smaller, greater);

        assertEquals(0, comparator.compare(o, o));

        assertNullValue(comparator, o, nullValueSupported);
    }

    private static <T> void assertNullValue(Comparator<? super T> comparator, T o,
            boolean nullValueSupported)
    {
        try
        {
            comparator.compare(o, null);
            assertTrue(nullValueSupported, "No NullPointerException but null value not supported!");
        }
        catch (final NullPointerException ex)
        {
            assertFalse(nullValueSupported,
                    "NullPointerException thrown but null value supported!!");
        }
    }

    private static <T> void assertAsymmetry(Comparator<? super T> comparator, T o, T smaller,
            T greater)
    {
        assertTrue(comparator.compare(o, smaller) > 0);
        assertTrue(comparator.compare(smaller, o) < 0);

        assertTrue(comparator.compare(o, greater) < 0);
        assertTrue(comparator.compare(greater, o) > 0);
    }

    private static <T> void assertSymmetry(Comparator<? super T> comparator, T o, T equal)
    {
        assertEquals(0, comparator.compare(o, equal));
        assertEquals(0, comparator.compare(equal, o));
    }

    private static <T> void assertSignum(Comparator<? super T> comparator, T o, T smaller, T equal,
            T greater)
    {
        assertSignum(comparator, o, smaller);
        assertSignum(comparator, o, equal);
        assertSignum(comparator, o, greater);
    }

    private static <T> void assertSignum(Comparator<? super T> comparator, T o1, T o2)
    {
        assertEquals(Integer.signum(comparator.compare(o1, o2)),
                -Integer.signum(comparator.compare(o2, o1)));
    }

    private static <T> void assertTransitivity(Comparator<? super T> comparator, T o, T smaller,
            T greater)
    {
        // when
        assertTrue(comparator.compare(smaller, o) < 0);
        assertTrue(comparator.compare(o, greater) < 0);

        // then
        assertTrue(comparator.compare(smaller, greater) < 0);
    }

    private static <T> void testConsistencyWithEquals(Comparator<? super T> comparator, T o,
            T smaller, T equal, T greater)
    {
        assertEquals(o.equals(smaller), comparator.compare(o, smaller) == 0);
        assertEquals(o.equals(equal), comparator.compare(o, equal) == 0);
        assertEquals(o.equals(greater), comparator.compare(o, greater) == 0);
    }

    public static <T extends Comparable<? super T>> void testComparableConsistentWithEquals(T o,
            T smaller, T equal, T greater)
    {
        testComparatorConsistentWithEquals(Comparator.naturalOrder(), o, smaller, equal, greater,
                false);
    }

    public static <T extends Comparable<? super T>> void testComparable(T o, T smaller, T equal,
            T greater)
    {
        testComparator(Comparator.naturalOrder(), o, smaller, equal, greater, false);
    }
}
