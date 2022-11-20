package org.itsallcode.openfasttrace.report.plaintext;

/**
 * Interface for text formatters.
 */
interface TextFormatter {
    /**
     * Format a text span that represents a good result.
     * @param text text span to be formatted
     * @return formatted text
     */
    public String formatOk(final String text);

    /**
     * Format a text span that represents a bad result.
     *
     * @param text text span to be formatted
     * @return formatted text
     */
    public String formatNotOk(final String text);

    /**
     * Format a text span that represents a strongly emphasized text.
     *
     * @param text text span to be formatted
     * @return formatted text
     */
    public String formatStrong(final String text);
}