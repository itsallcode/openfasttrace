package org.itsallcode.openfasttrace.api.core;

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
