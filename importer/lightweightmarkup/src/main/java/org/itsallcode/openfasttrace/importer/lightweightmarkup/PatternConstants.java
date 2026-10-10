package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

/**
 * Common regular expression constants used across lightweight markup formats.
 */
public final class PatternConstants
{
    /** Matches valid artifact types (e.g., "req", "test"). */
    public static final String ARTIFACT_TYPE = "\\p{Alpha}+";
    /** Matches valid bullet points ("+", "*", "-"). */
    public static final String BULLETS = "[+*-]";
    // [impl->dsn~md.tags-format~1]
    /** Matches valid tag names. */
    public static final String TAG_PATTERN = "\\p{Aplha}\\w*";
    /** Matches zero to three whitespace characters. */
    public static final String UP_TO_3_WHITESPACES = "\\s{0,3}";
    // [impl->dsn~md.requirement-references~1]
    /** Matches a requirement reference after a bullet. Capture group 1 holds the ID. */
    public static final String REFERENCE_AFTER_BULLET = UP_TO_3_WHITESPACES
            + PatternConstants.BULLETS + "(?:.*\\W)?" //
            + "(" + SpecificationItemId.ID_PATTERN + ")" //
            + "(?:\\W.*)?";

    private PatternConstants()
    {
        // not instantiable
    }
}
