package org.itsallcode.openfasttrace.api.core;

import java.util.Locale;

/**
 * This enumeration represents the different statuses of an item.
 */
// [impl->dsn~specification-item~4]
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
    REJECTED,
    /**
     * The item declares an unknown status.
     */
    UNKNOWN;

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
        final ItemStatus status = parseOrUnknown(text);
        if (status != UNKNOWN)
        {
            return status;
        }
        throw new IllegalArgumentException(
                "Unrecognized specification item status: \"" + text + "\"");
    }

    /**
     * Parses the given text and returns the matching {@link ItemStatus} or {@link #UNKNOWN}.
     *
     * @param text
     *            the text to parse, e.g. {@code "APPROVED"}.
     * @return the matching {@link ItemStatus}, or {@link #UNKNOWN} when no known status matches
     */
    public static ItemStatus parseOrUnknown(final String text)
    {
        for (final ItemStatus value : values())
        {
            if (value != UNKNOWN && text.equalsIgnoreCase(value.toString()))
            {
                return value;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
