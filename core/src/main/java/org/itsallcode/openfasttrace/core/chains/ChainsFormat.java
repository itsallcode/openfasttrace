package org.itsallcode.openfasttrace.core.chains;

import java.util.Arrays;
import java.util.Locale;

/**
 * Output format for tracing chains.
 */
// [impl->dsn~cli.chains.output-format~1]
public enum ChainsFormat
{
    /** Linear chain list with grouped terminal leaves. */
    CHAINS("chains"),
    /** Pairwise graph transitions in CSV format. */
    CSV("csv"),
    /** Mermaid.js flowchart graph. */
    MERMAID("mermaid");

    private final String name;

    ChainsFormat(final String name)
    {
        this.name = name;
    }

    /**
     * Get the format name.
     * 
     * @return format name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Check if a format name is supported.
     * 
     * @param format
     *            format name to check
     * @return true if supported, false otherwise
     */
    public static boolean isSupported(final String format)
    {
        if (format == null)
        {
            return false;
        }
        final String lower = format.toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).anyMatch(f -> f.name.equals(lower));
    }

    /**
     * Parse a format from a string.
     * 
     * @param format
     *            format name
     * @return matching ChainsFormat
     * @throws IllegalArgumentException
     *             if format is not supported
     */
    public static ChainsFormat fromString(final String format)
    {
        if (format == null)
        {
            throw new IllegalArgumentException("Chains format cannot be null");
        }
        final String lower = format.toLowerCase(Locale.ROOT);
        for (final ChainsFormat f : values())
        {
            if (f.name.equals(lower))
            {
                return f;
            }
        }
        throw new IllegalArgumentException("Unsupported chains format: '" + format + "'");
    }
}
