package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.core.LinkStatus;

/**
 * {@link LinkStatus} used to filter SpecItems by bad link within OpenFastTrace UX.
 */
public enum WrongLinkType
{
    /**
     * PREDATED or OUTDATED reference.
     */
    WRONG_VERSION("version"),

    /**
     * Unknown ID.
     */
    ORPHANED("orphaned"),

    /**
     * Not needed coverage type.
     */
    UNWANTED("unwanted"),

    /**
     * Not relevant
     */
    NONE("");

    private final String text;

    /**
     * Tranform a {@link LinkStatus} to a WrongLinkType
     *
     * @param linkStatus The status to convert
     * @return This WrongLinkType
     */
    public static WrongLinkType toWrongLinkType( final LinkStatus linkStatus ) {
        return switch (linkStatus)
        {
            case PREDATED, OUTDATED -> WRONG_VERSION;
            case ORPHANED, AMBIGUOUS -> ORPHANED;
            case UNWANTED -> UNWANTED;
            default -> NONE;
        };
    }

    WrongLinkType(final String text) {
        this.text = text;
    }

    public boolean isValid() {
        return this != NONE;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return text;
    }

} // WrongLinkType
