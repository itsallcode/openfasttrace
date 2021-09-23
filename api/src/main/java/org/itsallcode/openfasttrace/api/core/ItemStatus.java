package org.itsallcode.openfasttrace.api.core;

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

/**
 * This enumeration represents the different statuses of an item.
 */
public enum ItemStatus
{
    /**
     * The item is approved.
     */
    APPROVED,
    /**
     * The item is proposed.
     */
    PROPOSED,
    /**
     * The item is a draft.
     */
    DRAFT,
    /**
     * The item is rejected.
     */
    REJECTED;

    /**
     * Parses the given text and return the matching {@link ItemStatus}.
     * 
     * @param text
     *            the text to parse, e.g. {@code "APPROVED"}.
     * @return the matching {@link ItemStatus}.
     * @throws IllegalArgumentException
     *             in case no matching {@link ItemStatus} is found.
     */
    public static ItemStatus parseString(final String text)
    {
        for (final ItemStatus value : values())
        {
            if (text.equalsIgnoreCase(value.toString()))
            {
                return value;
            }
        }
        throw new IllegalArgumentException(
                "Unrecognized specification item status: \"" + text + "\"");
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
