package org.itsallcode.openfasttrace.core;

public enum ItemStatus
{
    APPROVED, PROPOSED, DRAFT, REJECTED;

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
